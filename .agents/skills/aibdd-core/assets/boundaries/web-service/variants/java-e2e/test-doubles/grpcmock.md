# Test-double (java-e2e): grpcmock（in-process）

外部 gRPC 依賴（kind=grpc）的替身配置。以 **in-process grpcmock server**（測試 JVM 內嵌，免 Docker）
攔截 SUT 的 outbound gRPC，並把替身 target（host:port）覆寫進 SUT 設定鍵
（entry.wiring.sut_property_overrides，如 grpc.client.<name>.address）。
是 kind-constants `grpc.yml` `red_execute` 的 java-e2e 具體實現 SSOT，由 RED_PREHANDLING_HOOK 佈建。

## 需要的測試套件（Maven；scope=test）

red-execute 用此替身／其 handler（external-stub／interaction-verifier）前，須確認專案 pom.xml
`<dependencies>` 已含下列；缺者由 red-execute 自動補進（scope=test）：

- `org.grpcmock:grpcmock-junit5` — in-process `GrpcMock` server 與 stub/verify DSL；**需顯式 version**（如 `0.13.0`，非 spring-boot BOM 管理）。
- `io.grpc:grpc-stub` / `io.grpc:grpc-protobuf` — grpc-java client stub 與 protobuf message；grpcmock 佈樁需 rpc 的 generated stub。**通常隨產品 gRPC client 已在 pom**，缺者補進（version 隨產品 grpc-java 或其 BOM）。
- `org.awaitility:awaitility` — interaction-verifier 的 bounded-wait；由 spring-boot-dependencies BOM 管理，**version 可省**。

proto descriptor 由 truth 檔（`${DEPENDENCIES_DIR}/<name>/` 的 proto）編譯產生：
gRPC client stub 與 message builder 由 protobuf-maven-plugin 對該 proto 執行 codegen 產出，
grpcmock 以這批 generated `*Grpc` service 定義掛載 rpc（即 in-process server 的 descriptor 來源）。
不得憑印象手寫 stub／message 類別。

（其他 variant 的等效套件管理處與座標，見各 variant 的 test-double 檔。）

## 何時需要

isa.yml 內有 kind=grpc 的外部依賴 custom（handler `external-stub` 或 `interaction-verifier`）時，
per registry entry 一個 in-process GrpcMock server（同 entry 的 B 佈樁與 E 查驗共用同一 server）。

## @TestConfiguration（每個 grpc 依賴一個 server bean）

放 `src/test/java/${BASE_PACKAGE}/config/{{Dep}}StubConfiguration.java`（`{{Dep}}` 由 entry.name 轉 PascalCase）：

```java
package ${BASE_PACKAGE}.config;

import org.grpcmock.GrpcMock;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@TestConfiguration(proxyBeanMethods = false)
public class {{Dep}}StubConfiguration {

    // in-process server，隨機 port（免 Docker）；整個 test JVM 共用一個。
    // proto descriptor 由 truth 檔編譯出的 generated stub 提供（佈樁時以 *Grpc 定義掛載 rpc）。
    static final GrpcMock SERVER = GrpcMock.grpcMock().build();
    static { SERVER.start(); }

    /** 覆寫 SUT 設定鍵（entry.wiring.sut_property_overrides 各鍵）指向替身 target（host:port）。 */
    @DynamicPropertySource
    static void overrideSutProperties(DynamicPropertyRegistry registry) {
        // 每個 sut_property_override 一行；鍵取自 registry entry.wiring.sut_property_overrides
        registry.add("{{sut_property}}", () -> "localhost:" + SERVER.getPort());
    }

    /** 供 handler step def 以 Java DSL 佈樁／查驗。 */
    @Bean
    GrpcMock {{dep}}GrpcMock() { return SERVER; }
}
```

於 `CucumberSpringConfiguration` 掛上：`@Import({{Dep}}StubConfiguration.class)`。

## 每情境重置（必做）

GrpcMock 跨情境共用，須每情境清樁與呼叫記錄，否則前一情境的樁／journal 汙染下一情境。
於 `DatabaseCleanupHook.@Before`（或等效 hook）加：`{{dep}}GrpcMock.resetAll();`。

## 填空 slot

- `{{Dep}}`／`{{dep}}`：registry entry.name 轉 PascalCase／camelCase。
- `{{sut_property}}`：entry.wiring.sut_property_overrides 每一鍵各一行 `registry.add(...)`。

## Forbidden

- 不得連真實外部服務——只用本地 in-process grpcmock 替身。
- 不得硬編 port（隨機 port，target 一律 `"localhost:" + SERVER.getPort()`）。
- 一個 entry 的 B／E 共用同一 `GrpcMock` bean，不另起第二個。
- 忘記 `resetAll()` → 情境間互相汙染，屬測試設計缺陷。
- proto stub／message 類別一律由 truth proto 編譯產生，不手寫。
