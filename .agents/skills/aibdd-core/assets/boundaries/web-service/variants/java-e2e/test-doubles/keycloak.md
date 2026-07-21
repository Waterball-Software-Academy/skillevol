# Test-double (java-e2e): Keycloak（真 testcontainer；real-product override）

身分依賴（kind=identity）的 **real-product override** 配置。identity 的
kind-constants `identity.yml` `testability_defaults.double` **預設是 generic-stub（bypass
in-process，免 container）**——預設路徑不需要本檔，A 面直接用 `JwtHelper` 在測試 JVM 內簽
測試 token（見 [`../handlers/identity-identity-builder.md`](../handlers/identity-identity-builder.md)）。

**只有 per-dependency override 為 `real-product` 時才用本檔**：以 Testcontainers 真起一個
Keycloak container，由 truth 檔（realm-export.json）import realm，A 面改成向真 IdP
建 user＋取真 token。是 identity.yml `red_execute.prehandling_template` real-product 分支的
java-e2e 具體實現 SSOT，由 RED_PREHANDLING_HOOK 佈建。

## 何時需要（bypass 免此檔）

- **bypass（預設）**：不需本檔、不需 container、不需下列 testcontainers 套件；用 JJWT 簽測試 token。
  受保真度天花板限制（只測 SUT 授權邏輯，不測 SSO 整合）。
- **real-product（override）**：entry 的 `testability_overrides` 明確選 real-product（須記錄選擇理由）時，
  才起真 Keycloak container，保真高、慢；A 面走真 IdP grant 取真 token。

## 需要的測試套件（Maven；scope=test）

**bypass 用不到本節**——bypass 只需 JJWT（`io.jsonwebtoken:jjwt-*`，java-e2e starter 已內含），
無額外 container 套件。

real-product 才需下列（缺者由 red-execute 自動補進，scope=test）：

- `org.testcontainers:testcontainers` — `GenericContainer` 起真 Keycloak container；由
  spring-boot-testcontainers 帶入的 testcontainers BOM 管理，**version 可省**。
- Keycloak container 二擇一：
  - `com.github.dasniko:testcontainers-keycloak` — 專用 `KeycloakContainer`（含 `withRealmImportFile(...)`
    ／`getAuthServerUrl()` 便利 API，對得上 identity.yml prehandling_template）；**需顯式 version**（非 BOM 管理）。
  - 或退回泛用 `org.testcontainers:testcontainers` 的 `GenericContainer`（自行 `withExposedPorts(8080)`
    ＋掛 realm import volume），不引專用套件時用。
- `io.jsonwebtoken:jjwt-*` — bypass 才需；real-product 取真 token，不用測試簽章器。

（api variant 的 in-process WireMock 走 `org.wiremock:wiremock` 顯式 version；identity real-product
走真 container，套件與版本管理處不同，見上。）

## @TestConfiguration（每個 identity real-product 依賴一個真 container）

放 `src/test/java/${BASE_PACKAGE}/config/{{Dep}}IdentityConfiguration.java`（`{{Dep}}` 由 entry.name 轉 PascalCase）。
container image／port／env 取 kind-constants `identity.yml` `container_real_product`
（`keycloak/keycloak:26.0`、port 8080、`KEYCLOAK_ADMIN`/`KEYCLOAK_ADMIN_PASSWORD`），
realm import 由 truth 檔（realm-export.json）掛載：

```java
package ${BASE_PACKAGE}.config;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@TestConfiguration(proxyBeanMethods = false)
public class {{Dep}}IdentityConfiguration {

    // 真 Keycloak container（identity.yml container_real_product）；整個 test JVM 共用一個。
    // realm import 由 truth 檔掛載（identity.yml realm_import：realm-export.json）。
    static final KeycloakContainer SERVER =
        new KeycloakContainer("keycloak/keycloak:26.0")
            .withRealmImportFile("{{truth_file}}");   // truth realm-export.json（classpath 或路徑）
    static { SERVER.start(); }

    /** 覆寫 SUT 的 issuer-uri 類 config key（entry.wiring.sut_property_overrides；real-product 必填）。 */
    @DynamicPropertySource
    static void overrideSutProperties(DynamicPropertyRegistry registry) {
        registry.add("{{sut_property}}",
            () -> SERVER.getAuthServerUrl() + "/realms/{{realm}}");
    }
}
```

於 `CucumberSpringConfiguration` 掛上：`@Import({{Dep}}IdentityConfiguration.class)`。
Keycloak 無 Spring Boot `@ServiceConnection` 自動接線，故 issuer-uri 走 `@DynamicPropertySource`
手動覆寫 `sut_property_overrides`（real-product 時必填，見 rules/kind-identity.md）。

## A 面改成向真 IdP 取真 token（real-product）

real-product 時 identity-builder 不再用 `JwtHelper` 簽測試 token，而是向真 Keycloak 走
grant 取真 token（identity.yml `red_execute.stepdef_constants.token_source`：real-product＝
password／client-credentials grant）：以 admin client 依 DataTable 的 roles／claims 建 user
（或 realm import 已含），再打 token endpoint 取真 access token，export `{{alias}}.uid`／
`{{alias}}.token` 寫回 `ScenarioContext`（export_vars 同 identity.yml A，不變）。憑證／client
secret 不落 truth 檔（rules/kind-identity.md：credential 不管理）。

## 每情境重置（必做）

container 跨情境共用。realm import 的固定身分無狀態污染問題；但情境中動態建的 user 須清理，
否則跨情境殘留。於 `DatabaseCleanupHook.@Before`（或等效 hook）以 admin client 刪除本情境建的 user
（或每情境用唯一 username 命名空間避免碰撞）。`ScenarioContext.clear()` 仍照常清 ids／memo。

## 填空 slot

- `{{Dep}}`／`{{dep}}`：registry entry.name 轉 PascalCase／camelCase。
- `{{truth_file}}`：truth realm export（realm-export.json）的路徑／classpath 位置（identity.yml realm_import）。
- `{{realm}}`：realm export 宣告的 realm 名。
- `{{sut_property}}`：entry.wiring.sut_property_overrides 的 issuer-uri 類 config key（real-product 必填）。
- container image／port／env：取 identity.yml `container_real_product`；偏離才依 entry.testability_overrides 調整。

## Forbidden（承 identity.yml `red_execute.forbidden`）

- 不得測 redirect flow／token 過期／signature 驗證（保真度天花板範圍外）——real-product 亦不涵蓋 redirect flow（E2E 層外）。
- 不得在 step 句中出現 IdP URL 等連線細節（連線走 config 覆寫，不入句）。
- 憑證／client secret 不落 truth 檔（credential 不管理）。
- roles／claims 只用 truth realm 宣告過的角色與 claim，不發明。
- bypass 免 container：real-product override 才引本檔的 testcontainers／Keycloak 套件，預設路徑不引。
