# Test-double (java-e2e): Kafka（真 testcontainer broker）

channel 依賴（kind=channel）的替身配置。channel broker 是**有狀態中介**，故不 stub broker——
以 **真 Kafka testcontainer**（`apache/kafka-native:3.8.0`）起真品 broker，讓 SUT 對真 broker 消費／發布。
是 kind-constants `channel.yml` `red_execute` 的 java-e2e 具體實現 SSOT，由 RED_PREHANDLING_HOOK 佈建。

與 api 的 in-process WireMock（免 Docker、in-process）不同：channel 需真 container，
`DynamicPropertyRegistry` 覆寫 `spring.kafka.bootstrap-servers` 指向 container，
C 面 test producer（`KafkaTemplate`）與 E 面 test consumer（`@KafkaListener`）都對此真 broker 運作。

## 需要的測試套件（Maven；scope=test）

red-execute 用此替身／其 handler（message-publish／interaction-verifier）前，須確認專案 pom.xml
`<dependencies>` 已含下列；缺者由 red-execute 自動補進（scope=test）：

- `org.springframework.kafka:spring-kafka` — 測試側 `KafkaTemplate`（C 發訊）與 `@KafkaListener`（E test consumer）；由 spring-boot-dependencies BOM 管理，**version 可省**。
- `org.testcontainers:kafka` — `KafkaContainer` 真品 broker；由 spring-boot-dependencies BOM 管理，**version 可省**。
- `org.awaitility:awaitility` — interaction-verifier 的 bounded-wait；由 spring-boot-dependencies BOM 管理，**version 可省**。

（其他 variant 的等效套件管理處與座標，見各 variant 的 test-double 檔。）

## 何時需要

isa.yml 內有 kind=channel 的依賴 custom（handler `message-publish` 或 `interaction-verifier`）時，
per registry entry 一個真 Kafka testcontainer（同 entry 的 C 發訊與 E 查驗共用同一 broker）。

## @TestConfiguration（每個 channel 依賴一個真 broker container）

放 `src/test/java/${BASE_PACKAGE}/config/{{Dep}}ChannelConfiguration.java`（`{{Dep}}` 由 entry.name 轉 PascalCase）：

```java
package ${BASE_PACKAGE}.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
public class {{Dep}}ChannelConfiguration {

    // 真品 broker container（有狀態中介），整個 test JVM 共用一個
    static final KafkaContainer BROKER =
        new KafkaContainer(DockerImageName.parse("apache/kafka-native:3.8.0"));
    static { BROKER.start(); }

    /** 覆寫 SUT 的 broker 連線設定鍵指向真 container。 */
    @DynamicPropertySource
    static void overrideBootstrapServers(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", BROKER::getBootstrapServers);
    }

    /** C 面：供 message-publish handler 發測試訊息的 test producer。 */
    @Bean
    KafkaTemplate<String, String> kafkaTemplate(ProducerFactory<String, String> pf) {
        return new KafkaTemplate<>(pf);
    }

    /** E 面：test consumer，收 SUT 對 channel 發布的訊息進接收緩衝供斷言。 */
    @Bean
    {{Dep}}TestConsumer {{dep}}TestConsumer() { return new {{Dep}}TestConsumer(); }
}
```

test consumer 骨架（`@KafkaListener` 把 SUT 發布訊息收進緩衝，供 interaction-verifier 讀）：

```java
import org.springframework.kafka.annotation.KafkaListener;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class {{Dep}}TestConsumer {
    private final List<Map<String, String>> buffer = new CopyOnWriteArrayList<>();

    // topic 取 truth 檔該 outbound channel 對應 topic；payload 反序列化為欄位 map
    @KafkaListener(topics = "{{topic}}", groupId = "test-verifier")
    void onMessage(String payload) { buffer.add(parse(payload)); }

    public List<Map<String, String>> received() { return buffer; }
    void clear() { buffer.clear(); }
}
```

於 `CucumberSpringConfiguration` 掛上：`@Import({{Dep}}ChannelConfiguration.class)`。

## Topic 預建（避免 flaky）

truth 檔宣告的 topic／queue 應預建，避免 auto-create 時序差異造成 flaky。以 `NewTopic` bean 或
container 起後建 topic 皆可；outbound channel 的 topic 需在 test consumer 註冊前存在。

## 每情境重置（必做）

真 broker 與 test consumer 緩衝跨情境共用，須每情境清空接收緩衝，否則前一情境收到的訊息汙染下一情境。
於 `DatabaseCleanupHook.@Before`（或等效 hook）加：`{{dep}}TestConsumer.clear();`。

## 填空 slot

- `{{Dep}}`／`{{dep}}`：registry entry.name 轉 PascalCase／camelCase。
- `{{topic}}`：truth 檔該 channel 對應的 broker topic／queue 名。

## 其他 broker

以 entry.broker 分流；差異要點（完整展開見對應 spring 模組）：

- **rabbitmq**（image `rabbitmq:3-management`）：C 面 test producer 改用 `RabbitTemplate`，
  E 面 test consumer 改用 `@RabbitListener`；`DynamicPropertyRegistry` 覆寫
  `spring.rabbitmq.host`／`spring.rabbitmq.port` 指向 `RabbitMQContainer`。
  套件改 `org.springframework.boot:spring-boot-starter-amqp` ＋ `org.testcontainers:rabbitmq`。
  其餘（真 container、bounded-wait 斷言、每情境清緩衝、只斷言 truth 宣告欄位）同 kafka。

## Forbidden

- 不得 stub broker——channel broker 是有狀態中介，一律用真 testcontainer 真品。
- 不得硬編 bootstrap-servers／host／port（一律取 container 的 `getBootstrapServers()` 等動態值）。
- 一個 entry 的 C／E 共用同一真 broker container，不另起第二個。
- 忘記清接收緩衝 → 情境間互相汙染，屬測試設計缺陷。
