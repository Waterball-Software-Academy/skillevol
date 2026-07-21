# Handler template (java-e2e): identity-builder

kind-constants `identity.yml` `red_execute` A 面（seed 身分）的具體 Java 填空模版：測試執行前
讓身分 `{{alias}}`（含角色／claims）就緒，供後續以其名義操作。對應 `isa_step_templates` 的
A（`已存在身分`），format `^已存在身分 "(?P<alias>[^"]+)", with table:$`（data_table）。

**預設 bypass（in-process，免 container）**：用 java-e2e.md 宣告的 `JwtHelper.generateToken(userId)`
在測試 JVM 內直接簽一枚測試 token，依 `datatable_parameters`（roles／claims）備 claims，再把
`{{alias}}.uid`（後續 api_call 的 UID 引用）與 `{{alias}}.token`（Authorization header 素材）
export 寫回 `ScenarioContext`。**免額外 container 套件**——只需 JJWT（`io.jsonwebtoken:jjwt-*`，
java-e2e starter 已內含），不需 test-doubles/keycloak.md 的 testcontainers 套件。

**保真度天花板（承 identity.yml，必讓使用者知情）**：bypass 只測「SUT 的授權邏輯」，
永遠不測「SSO 整合本身」——token parsing／signature／audience／scope／過期皆測不到；
SSO redirect 流程面為範圍外。real-product override（真 Keycloak container）改走
[`../test-doubles/keycloak.md`](../test-doubles/keycloak.md)，A 面改成向真 IdP 建 user＋取真 token。

step def 放 `steps/<function-package-slug>/identity_builder/{{Dep}}IdentitySteps.java`，
`@Autowired` 注入 `ScenarioContext` 與 java-e2e.md 的 `JwtHelper` bean。

## 對應句式（identity；matcher 取 kind-constants identity.yml format 主幹 alias）

- seed 身分：`已存在身分 "…", with table:`（Given，data_table）— 主模版，見下。
  DataTable 欄位僅 `roles`（逗號分隔角色）與 `claims`（額外 claims JSON），皆 optional，
  且只可用 truth 宣告過的角色與 claim（不得發明 realm truth 外的角色／claim key）。

## 模版（bypass；in-process 簽 token）

```java
package ${BASE_PACKAGE}.steps.{{package_slug}}.identity_builder;

import ${BASE_PACKAGE}.cucumber.JwtHelper;
import ${BASE_PACKAGE}.cucumber.ScenarioContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class {{Dep}}IdentitySteps {

    @Autowired ScenarioContext scenarioContext;
    @Autowired JwtHelper jwtHelper;
    @Autowired ObjectMapper objectMapper;

    @Given("已存在身分 {string}, with table:")
    public void 已存在身分(String alias, DataTable table) throws Exception {
        Map<String, String> row = table.asMaps().isEmpty() ? Map.of() : table.asMaps().get(0);

        // uid：以 alias 當測試身分的穩定 subject（後續 api_call 以此 UID 引用）
        String uid = alias;

        // roles（optional）：逗號分隔；只可用 truth 宣告過的角色，不發明
        String rolesRaw = row.get("roles");
        List<String> roles = (rolesRaw == null || rolesRaw.isBlank())
            ? List.of()
            : Arrays.stream(rolesRaw.split(",")).map(String::trim).filter(s -> !s.isBlank()).toList();

        // claims（optional）：額外 claims JSON；只可用 truth 宣告過的 claim key，不發明
        String claimsRaw = row.get("claims");
        Map<String, Object> claims = (claimsRaw == null || claimsRaw.isBlank())
            ? Map.of()
            : objectMapper.readValue(claimsRaw, Map.class);

        // in-process 簽測試 token（bypass；免 container）。roles／claims 併入 token payload。
        String token = jwtHelper.generateToken(uid, roles, claims);

        // export_vars（承 identity.yml A 的 export_vars）：寫回 ScenarioContext
        scenarioContext.getIds().put(alias + ".uid", uid);       // {{alias}}.uid → 後續 api_call UID 引用
        scenarioContext.getMemo().put(alias + ".token", token);  // {{alias}}.token → Authorization header 素材
    }
}
```

## JwtHelper 依 roles／claims 擴充（bypass 專用）

java-e2e.md 宣告的 `JwtHelper.generateToken(userId)` 只帶 subject。identity bypass 需帶
roles／claims 時，於 `JwtHelper` 加一個多載（維持原簽章不動），用 JJWT builder 的
`.claim(key, value)` 併入——**只併 truth 宣告過的角色／claim，不發明欄位**：

```java
public String generateToken(String userId, List<String> roles, Map<String, Object> claims) {
    var builder = Jwts.builder().subject(userId)
        .issuedAt(Date.from(Instant.now()))
        .expiration(Date.from(Instant.now().plus(expireHours, ChronoUnit.HOURS)));
    if (roles != null && !roles.isEmpty()) builder.claim("roles", roles);  // claim key 依 truth 約定
    if (claims != null) claims.forEach(builder::claim);
    return builder.signWith(secretKey).compact();
}
```

（claim key 名如 `roles`／`scope`／`aud` 皆須對得上 truth realm 約定；SUT 讀哪個 claim
判角色，就寫哪個，不擅自改名。）

## 填空 slot

- `{{Dep}}`／`{{dep}}`：registry entry.name 轉 PascalCase／camelCase；`{{package_slug}}`：function package slug（snake_case）。
- `{{alias}}`：kind-constants identity.yml format 的 alias 具名參數字面（保留為 Cucumber 表達式常量或 regex 群組）。
- roles／claims 欄位值：只可用 truth（realm export／身分約定）宣告過的角色與 claim key，不發明。
- export 鍵 `{{alias}}.uid`／`{{alias}}.token`：承 identity.yml A 的 `export_vars`，寫回 `ScenarioContext`。

## Forbidden（承 kind-constants identity.yml `red_execute.forbidden` 與保真度天花板）

- 不得測 redirect flow／token 過期／signature 驗證（保真度天花板範圍外）——bypass 只測 SUT 授權邏輯。
- 不得在句中／step 內出現 IdP URL 等連線細節（bypass 無 IdP 連線）。
- bypass 模式不得宣稱涵蓋 SSO 整合；handoff 須標注保真等級（bypass），補位是 BDD 層外的真 IdP 整合測試。
- roles／claims 只用 truth 宣告過的角色與 claim key，不發明 realm truth 外欄位。
- export 只寫 identity.yml `export_vars` 宣告的 `{{alias}}.uid`／`{{alias}}.token`，不擅自多塞。
- bypass 免 container：不引 testcontainers／Keycloak 套件（那是 real-product override 才需，見 test-doubles/keycloak.md）。
