# Test-double (java-e2e): Redis（真 testcontainer）

外部儲存依賴（kind=store，engine=redis）的替身配置。store 與 api 不同：**真品即替身**
（kind-constants store.yml `testability_defaults.double: real-product`），不是 in-process stub。
以 **Testcontainers 真起一個 `redis:7-alpine` container**，並把它的連線設定覆寫進 SUT，
測試側再開一條後門 redis client 直讀直寫。是 kind-constants `store.yml` `red_execute` 的
java-e2e 具體實現 SSOT，由 RED_PREHANDLING_HOOK 佈建。

## 需要的測試套件（Maven；scope=test）

red-execute 用此替身／其 handler（external-store-builder／external-store-verifier）前，
須確認專案 pom.xml `<dependencies>` 已含下列；缺者由 red-execute 自動補進（scope=test）：

- `org.springframework.boot:spring-boot-starter-data-redis` — 後門 `StringRedisTemplate`／`RedisTemplate`（Lettuce）；spring-boot BOM 管理，**version 可省**。若不想拉整個 starter，改用 `io.lettuce:lettuce-core`（同樣 BOM 管理）。
- `org.testcontainers:testcontainers` — `GenericContainer` 起真 redis；由 spring-boot-testcontainers 依賴帶入的 testcontainers BOM 管理，**version 可省**。
- `org.awaitility:awaitility` — external-store-verifier 的 bounded-wait 輪詢；由 spring-boot-dependencies BOM 管理，**version 可省**。

（api variant 的 in-process WireMock 走 `org.wiremock:wiremock` 顯式 version；store 走真 container，套件與版本管理處不同，見上。）

## 何時需要

isa.yml 內有 kind=store、engine=redis 的外部依賴 custom（handler `external-store-builder`
或 `external-store-verifier`）時，per registry entry 一個真 redis container（同 entry 的
A 預存與 D 驗證共用同一 container 與同一後門 client）。

## @TestConfiguration（每個 redis 依賴一個真 container）

放 `src/test/java/${BASE_PACKAGE}/config/{{Dep}}StoreConfiguration.java`（`{{Dep}}` 由 entry.name 轉 PascalCase）。
redis 的 kind-constants `container_by_engine.redis.service_connection: true`，故用 `@ServiceConnection`
一行接線，Spring Boot 自動把 container 的 host／port 覆寫進 SUT 的 redis 連線設定，
免手寫 `sut_property_overrides`：

```java
package ${BASE_PACKAGE}.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
public class {{Dep}}StoreConfiguration {

    // 真 redis container（kind-constants store.yml container_by_engine.redis）；
    // 整個 test JVM 共用一個；port 6379 由 Testcontainers 對外映射為隨機 port。
    @Bean
    @ServiceConnection(name = "redis")
    GenericContainer<?> {{dep}}Redis() {
        return new GenericContainer<>(DockerImageName.parse("redis:7-alpine"))
            .withExposedPorts(6379);
    }
}
```

於 `CucumberSpringConfiguration` 掛上：`@Import({{Dep}}StoreConfiguration.class)`。
`@ServiceConnection(name = "redis")` 讓 Spring Boot 覆寫 SUT 的 `spring.data.redis.*` 連線設定
指向此 container，SUT 與後門 client 因此連同一顆真 redis。

## 後門 client（測試側直連，不經 SUT API）

handler step def 以 `@Autowired StringRedisTemplate` 注入後門 client。此 template 由
spring-boot-starter-data-redis 自動裝配，連線設定已被上面 `@ServiceConnection` 導向同一 container，
故測試後門與 SUT 讀寫的是同一顆 redis：

```java
@Autowired StringRedisTemplate {{dep}}Redis;   // 後門直讀直寫，不經 SUT 的 HTTP API
```

## 每情境重置（必做）

container 跨情境共用，須每情境清空 keyspace，否則前一情境預存的 key 汙染下一情境。
於 `DatabaseCleanupHook.@Before`（或等效 hook）加：

```java
{{dep}}Redis.getConnectionFactory().getConnection().serverCommands().flushDb();
```

## 填空 slot

- `{{Dep}}`／`{{dep}}`：registry entry.name 轉 PascalCase／camelCase。
- container image／port：取 kind-constants `store.yml` `container_by_engine.redis`（`redis:7-alpine`、6379）；偏離才依 entry.testability_overrides 調整。

## Forbidden

- 不得 stub／mock redis——store 恆為 real-product，一律真 container。
- 不得硬編 host／port（Testcontainers 映射隨機 port，一律走 `@ServiceConnection` 自動覆寫）。
- 一個 entry 的 A／D 共用同一 container 與同一後門 `StringRedisTemplate`，不另起第二顆。
- 忘記 `flushDb()` → 情境間互相汙染，屬測試設計缺陷。

## 其他 engine（差異要點，不完整展開）

同 store kind、非 redis engine 時，仍是「真 container ＋ 後門直連 client」同一套路，只換
image／client／讀寫 API／service_connection（取 kind-constants `store.yml` `container_by_engine`）。
各 engine 差異：

- **s3 / minio**（`minio/minio`、port 9000、`service_connection: false`）：
  無 `@ServiceConnection`，須 `@DynamicPropertySource` 手動覆寫 entry.wiring.sut_property_overrides
  指向 container endpoint。後門 client 用 `software.amazon.awssdk:s3` 的 `S3Client`；
  A 面 `putObject(bucket, key, body)` 預存物件，D 面 `getObject(bucket, key)` 讀取斷言。
  套件：`org.testcontainers:minio` ＋ `software.amazon.awssdk:s3`（scope=test）。
- **mongodb**（`mongo:7`、port 27017、`service_connection: true`）：
  `@ServiceConnection` 一行接線。後門 client 用 `MongoTemplate`；A 面 `insert(doc, collection)`，
  D 面 `findById`／`find(query, collection)` 斷言。套件：`org.springframework.boot:spring-boot-starter-data-mongodb` ＋ `org.testcontainers:mongodb`。
- **elasticsearch**（`elasticsearch:8.15.0`、port 9200、`service_connection: true`）：
  `@ServiceConnection` 接線。後門 client 用 `ElasticsearchClient`；A 面 `index(...)`，D 面 `get(...)`／`search(...)`。
  **index refresh 延遲**：truth collections 標 `refresh_lag: true` 者，D 面 external-store-verifier
  的 bounded-wait timeout 須拉長（refresh 非同步生效）。套件：`org.springframework.boot:spring-boot-starter-data-elasticsearch` ＋ `org.testcontainers:elasticsearch`。
