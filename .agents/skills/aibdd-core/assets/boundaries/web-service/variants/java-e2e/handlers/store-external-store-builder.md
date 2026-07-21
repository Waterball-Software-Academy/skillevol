# Handler template (java-e2e): external-store-builder

kind-constants `store.yml` `red_execute` A 面（外部儲存版 entity_setup）的具體 Java 填空模版：
以測試側後門 client **直寫真 store**，讓 SUT 執行前指定資料已存在。engine=redis 用
**Spring `StringRedisTemplate` 的後門寫入**（`opsForValue().set(key, value)`），
truth 有宣告 TTL 才帶過期。需 [`../test-doubles/redis.md`](../test-doubles/redis.md) 佈建的真
container 與後門 `StringRedisTemplate` bean。

store 與 api 不同：這是**真 testcontainer**（redis:7-alpine），不是 in-process 替身；後門寫入
的與 SUT 讀取的是同一顆真 redis（見 test-doubles/redis.md 的 `@ServiceConnection` 接線）。

step def 放 `steps/<function-package-slug>/external_store_builder/{{Dep}}StoreBuilderSteps.java`，
`@Autowired` 注入 redis.md 的 `StringRedisTemplate` bean。

## 對應句式（store；matcher 取 kind-constants store.yml format 主幹）

- 預存資料：`儲存 "…" 已存在 …, with JSON:`（Given，json）— 見 store.yml `isa_step_templates` 的
  A（`外部儲存已存在資料`），format `^儲存 "(?P<store>[^"]+)" 已存在 (?P<target>[^ ]+), with JSON:$`。

## 模版

```java
package ${BASE_PACKAGE}.steps.{{package_slug}}.external_store_builder;

import ${BASE_PACKAGE}.steps.ScenarioContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;

public class {{Dep}}StoreBuilderSteps {

    // truth 約定檔（convention.md keyspaces）：target 別名 → key pattern（colon 分隔慣例）
    // 例："使用者session" → "session:{userId}"；佔位變數對得上 feature 資料別名
    private static final String KEY_PATTERN = "{{key_pattern}}";
    // truth 宣告 TTL 才帶（業務語意需要過期時）；未宣告則留 null，永不過期
    private static final Duration TTL = {{ttl_or_null}};   // 如 Duration.ofMinutes(30)，或 null

    @Autowired ScenarioContext scenarioContext;
    @Autowired StringRedisTemplate {{dep}}Redis;
    @Autowired ObjectMapper objectMapper;

    @Given("儲存 {string} 已存在 {{target}}, with JSON:")
    public void 已存在target(String store, String json) throws Exception {
        // json 依 truth value_schema 填空（不得發明約定檔未宣告的欄位）；
        // key 由 KEY_PATTERN 帶入 json 中的別名值解析而成
        String key = resolveKey(json);
        // 後門直寫真 redis（不經 SUT 的 HTTP API）
        {{dep}}Redis.opsForValue().set(key, json);
        if (TTL != null) {
            {{dep}}Redis.expire(key, TTL);
        }
        // red_execute 有宣告 export 供後續引用的 key 才寫回 ScenarioContext
        scenarioContext.getIds().put("{{export_alias}}", key);
    }

    private String resolveKey(String json) throws Exception {
        // 從 json 取出 KEY_PATTERN 佔位變數對應的別名值，代入 pattern
        var node = objectMapper.readTree(json);
        return KEY_PATTERN.replace("{{{placeholder}}}", node.get("{{placeholder_field}}").asText());
    }
}
```

## 填空 slot

- `{{Dep}}`／`{{dep}}`：entry.name → PascalCase／camelCase；`{{package_slug}}`：function package slug（snake_case）。
- `{{target}}`：kind-constants store.yml format 的 target 具名參數字面（保留為 Cucumber 表達式常量或 regex 群組）。
- `{{key_pattern}}`：truth 約定檔（redis keyspaces）該 target 的 key pattern（colon 分隔，佔位變數對得上 feature 別名）。
- `{{ttl_or_null}}`：truth 有宣告 TTL 才填 `Duration.of…`，否則 `null`（不發明過期語意）。
- `{{placeholder}}`／`{{placeholder_field}}`：key pattern 佔位變數名 → json 內對應欄位。
- `{{export_alias}}`：red_execute 有宣告 export 才寫回 `ScenarioContext.ids`；無宣告則整段移除。

## Forbidden（承 kind-constants store.yml `red_execute.forbidden`）

- 不得經 SUT 的 HTTP API 建狀態——只走後門 `StringRedisTemplate`（那才是 A 面；經 API 是 operation-invoke 的事）。
- 只寫 truth 約定檔（value_schema／keyspaces）宣告過的 key／欄位，不發明。
- TTL 只在 truth 宣告時才帶（業務有過期語意）；未宣告不擅自加過期。
- 一個 entry 的 A／D 共用 redis.md 的同一真 container 與同一後門 `StringRedisTemplate` bean。
- 不 stub／mock redis——store 恆 real-product，讀寫真 container。
