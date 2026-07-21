# Handler template (java-e2e): external-store-verifier

kind-constants `store.yml` `red_execute` D 面（外部儲存版狀態驗證）的具體 Java 填空模版：
以測試側後門 client **bounded-wait 輪詢直讀真 store**，驗證 SUT 執行後指定資料最終呈現指定內容。
engine=redis 用 **Spring `StringRedisTemplate` 的後門讀取**（`opsForValue().get(key)`）＋
**awaitility 輪詢 CAS 比對**（compare-and-assert，反覆讀到相符或 timeout）。需
[`../test-doubles/redis.md`](../test-doubles/redis.md) 佈建的真 container 與後門 `StringRedisTemplate` bean。

store 與 api 不同：SUT 對真 redis 的寫入可能非同步生效，故 D 面**不是一次讀，是 bounded-wait
輪詢直到相符或 timeout**（timeout 必填，見下 Forbidden）。

step def 放 `steps/<function-package-slug>/external_store_verifier/{{Dep}}StoreVerifierSteps.java`，
`@Autowired` 注入 redis.md 的 `StringRedisTemplate` bean。

## 對應句式（store；matcher 取 kind-constants store.yml 的 format 主幹）

- 最終存在指定內容：`儲存 "…" 的 … 最終應存在, with JSON:`（Then，json）— 見 store.yml
  `isa_step_templates` 的 D（`外部儲存最終應存在`），format
  `^儲存 "(?P<store>[^"]+)" 的 (?P<target>[^ ]+) 最終應存在, with JSON:$`。

## 模版

```java
package ${BASE_PACKAGE}.steps.{{package_slug}}.external_store_verifier;

import ${BASE_PACKAGE}.steps.ScenarioContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Then;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

public class {{Dep}}StoreVerifierSteps {

    // truth 約定檔（convention.md keyspaces）：target 別名 → key pattern（colon 分隔慣例）
    private static final String KEY_PATTERN = "{{key_pattern}}";
    // bounded-wait timeout（必填）；truth 該 collection 標 refresh_lag 者拉長（如 elasticsearch refresh）
    private static final Duration TIMEOUT = Duration.ofSeconds({{timeout_seconds}});   // 預設 5；refresh_lag → 更長

    @Autowired ScenarioContext scenarioContext;
    @Autowired StringRedisTemplate {{dep}}Redis;
    @Autowired ObjectMapper objectMapper;

    @Then("儲存 {string} 的 {{target}} 最終應存在, with JSON:")
    public void 最終應存在(String store, String expectedJson) throws Exception {
        JsonNode expected = objectMapper.readTree(expectedJson);
        String key = resolveKey(expected);
        // D 面 bounded-wait：輪詢後門讀（GET）→ CAS 比對，反覆到相符或 timeout；不無界等待
        await().atMost(TIMEOUT).pollInterval(Duration.ofMillis(200)).untilAsserted(() -> {
            String actualJson = {{dep}}Redis.opsForValue().get(key);
            assertThat(actualJson).as("儲存 key「%s」尚未存在", key).isNotNull();
            JsonNode actual = objectMapper.readTree(actualJson);
            // 只斷言 truth 約定檔宣告過的欄位（保守約束；未載明 value 不斷）
            expected.fieldNames().forEachRemaining(field ->
                assertThat(actual.get(field))
                    .as("欄位「%s」內容不符", field)
                    .isEqualTo(expected.get(field)));
        });
    }

    private String resolveKey(JsonNode expected) {
        // 從 expected 取 KEY_PATTERN 佔位變數對應別名值代入；或引用 A 面 export 至 ScenarioContext.ids 的 key
        return KEY_PATTERN.replace("{{{placeholder}}}", expected.get("{{placeholder_field}}").asText());
    }
}
```

## 填空 slot

- `{{Dep}}`／`{{dep}}`：entry.name → PascalCase／camelCase；`{{package_slug}}`：function package slug（snake_case）。
- `{{target}}`：kind-constants store.yml format 的 target 具名參數字面（保留為 Cucumber 表達式常量或 regex 群組）。
- `{{key_pattern}}`：truth 約定檔（redis keyspaces）該 target 的 key pattern。
- `{{timeout_seconds}}`：bounded-wait timeout；預設 5，truth collection 標 `refresh_lag: true` 者拉長。
- `{{placeholder}}`／`{{placeholder_field}}`：key pattern 佔位變數名 → json 內對應欄位；或以 A 面 export 至 `ScenarioContext.ids` 的 key 直取。

## Forbidden（承 kind-constants store.yml `red_execute.forbidden`）

- 不得經 SUT 的 HTTP API 驗狀態——只走後門 `StringRedisTemplate` 讀真 container（那才是 D 面；查 API 回應是 operation-response-verify 的事）。
- 只斷言 truth 約定檔（value_schema／keyspaces）宣告過的 key／欄位；expected 出現未宣告欄位 → 停下回報，不硬斷；真相未載明的 value 內容不斷（只斷存在性／宣告欄位）。
- D 面 timeout 必填（bounded-wait）；**不得無界等待**，不得 `Thread.sleep`。
- 不發明每 engine 專用斷言語彙——一律「後門 GET ＋ awaitility CAS 輪詢」同一套路。
- 一個 entry 的 A／D 共用 redis.md 的同一真 container 與同一後門 `StringRedisTemplate` bean。
- 不 stub／mock redis——store 恆 real-product，讀真 container。
