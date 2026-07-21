# Handler template (java-e2e): identity external-stub（B 特例：SUT 自驗 JWT）

kind-constants `identity.yml` 的 **B 特例**：當 SUT **自己驗 JWT 簽章**（自驗 issuer／
公鑰）時，須 stub 出 JWKS／introspection endpoint，讓 SUT 取到公鑰能驗過測試 token 的簽章。
identity.yml 對此**不另立句型**——直接**複用 api kind 的 external-stub 契約句式**
（`isa_step_templates` B 註記：boundary＝entry.name，騎 api 形態）。

**本 handler 是薄封裝，不重造機制**：JWKS／introspection 也是一條 outbound HTTP endpoint，
與 api external-stub 完全同構，故**直接複用 [`api-external-stub.md`](./api-external-stub.md) 的
in-process WireMock `stubFor(...)` 機制**與其 test-double
[`../test-doubles/wiremock.md`](../test-doubles/wiremock.md) 佈建的 `WireMockServer` bean，
不另起替身。

## 何時走此 B 特例（而非預設 bypass）

- 預設是 **bypass in-process**（見 [`identity-identity-builder.md`](./identity-identity-builder.md)）：
  SUT 直接吃 `JwtHelper` 簽的測試 token，不對外取公鑰驗簽——**此時完全不需要本 handler**。
- 只有當 SUT 的 security config 會**向 IdP 拉 JWKS／打 introspection** 才驗 token 時，才需本 B 特例：
  用 WireMock stub 出那條 JWKS／introspection endpoint，回傳與測試 token 簽章相容的公鑰 JSON。

## 複用方式（照 api external-stub，只換 urlPath 與 body）

沿用 api-external-stub.md 的 `stubFor(...)` 主模版，差異只在填空：

- **boundary＝entry.name**（identity registry entry 名，如 IdP／Keycloak entry）。
- **urlPath** 填 JWKS 或 introspection endpoint（如 `/.well-known/jwks.json`、
  `/realms/{{realm}}/protocol/openid-connect/certs`，或 introspection 的 `/token/introspect`）——
  路徑取 truth（OIDC discovery／realm export）宣告的實際 endpoint，不自推。
- **body** 回 JWKS 公鑰 JSON（JWK Set：`{"keys":[{...}]}`），公鑰須與 identity-builder 簽測試 token
  用的金鑰對相容（bypass 的 HMAC 對稱金鑰情境通常不觸此路；本 B 特例常見於 SUT 走 RS256／
  公私鑰驗簽時，測試側改用非對稱金鑰對簽 token，並把公鑰放進此 JWKS）。

```java
package ${BASE_PACKAGE}.steps.{{package_slug}}.external_stub;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.cucumber.java.en.Given;
import org.springframework.beans.factory.annotation.Autowired;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class {{Dep}}JwksStubSteps {

    // truth（OIDC discovery／realm export）宣告的 JWKS/introspection endpoint
    private static final String JWKS_PATH = "{{jwks_path}}";   // 如 /realms/{{realm}}/protocol/openid-connect/certs

    @Autowired WireMockServer wireMock;   // 複用 test-doubles/wiremock.md 佈建的同一 server bean

    @Given("外部服務 {string} 對 {{operation}} 回應")
    public void 對JWKS回應(String boundary, String jwksJson) {
        // jwksJson 回 JWK Set 公鑰 JSON；公鑰須與 identity-builder 簽 token 的金鑰對相容
        wireMock.stubFor(request("GET", urlPathEqualTo(JWKS_PATH))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody(jwksJson)));
    }
}
```

（句式常量／`{{operation}}`／status 語意一律以 api-external-stub.md 為準；本檔只註明
identity 情境下 boundary／urlPath／body 該填什麼，不重述 WireMock 機制。）

## 填空 slot

- `{{Dep}}`／`{{dep}}`／`{{package_slug}}`／`{{operation}}`：同 [`api-external-stub.md`](./api-external-stub.md)。
- `{{jwks_path}}`：truth（OIDC discovery／realm export）宣告的 JWKS／introspection endpoint path，不自推。
- `{{realm}}`：real-product／truth 宣告的 realm 名（若 endpoint 含 realm 段）。

## Forbidden（承 identity.yml 保真度天花板 ＋ api external-stub 的 forbidden）

- 不真連外——只對本地 in-process WireMock 替身 `stubFor`（複用 test-doubles/wiremock.md 同一 server）。
- 不重造替身機制——一律複用 api external-stub 的 `WireMockServer` bean 與 `stubFor(...)`，不另起第二個 server。
- JWKS body 只放與測試簽章相容的公鑰，不發明 truth 外的 endpoint／欄位；endpoint path 取 truth，不自推。
- 仍守 identity 天花板：stub JWKS 只讓 SUT 驗得過**測試簽章**，不等於驗證真 SSO 整合；handoff 標注保真等級。
