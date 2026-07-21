# Handler template (java-e2e): interaction-verifier

kind-constants `channel.yml` `red_execute` E 面（outbound；互動驗證）的具體 Java 填空模版：
test consumer 在 **bounded-wait 內收到 SUT 對 channel 發布、且符合 matcher 的訊息恰 times 則**
才通過（恆 eventually）。用 **spring-kafka `@KafkaListener` 的接收緩衝**＋**awaitility 有界輪詢**。
需 [`../test-doubles/kafka.md`](../test-doubles/kafka.md) 佈建的真 testcontainer broker 與 test consumer。

step def 放 `steps/<function-package-slug>/channel/{{Dep}}InteractionSteps.java`，
`@Autowired` 注入 kafka.md 的 test consumer（收 SUT 發布訊息的接收緩衝）。

## 對應句式（channel；matcher 取 kind-constants channel.yml 的 format 主幹）

- outbound 驗證：`channel "…" 最終應收到 {{times}} 則 {{eventType}} 訊息, with table:`（Then，data_table）— 主模版，見下。

E 面的觀測點是 **test consumer 的接收緩衝**，不是 broker 管理 API；非同步硬需求為 timeout 必填。

## 模版

```java
package ${BASE_PACKAGE}.steps.{{package_slug}}.channel;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Then;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

public class {{Dep}}InteractionSteps {

    // DataTable 中文表頭 → payload schema 屬性（僅 truth payload_ref 宣告欄位；不得發明）
    private static final Map<String, String> FIELD_TO_PROPERTY = Map.of(
        {{field_header}}, "{{schema_property}}"   // 每欄一筆
    );

    // kafka.md 佈建的 test consumer：@KafkaListener 把 SUT 發布訊息收進此緩衝
    @Autowired {{Dep}}TestConsumer consumer;

    @Then("channel {string} 最終應收到 {int} 則 {{eventType}} 訊息, with table:")
    public void 最終應收到訊息(String channel, int times, DataTable table) {
        Map<String, String> expected = table.asMaps().get(0);
        expected.keySet().forEach(header ->
            assertThat(FIELD_TO_PROPERTY.get(header))
                .as("欄位「%s」未在 payload_ref schema truth 宣告", header).isNotNull());
        // E 正面：bounded-wait 內符合 matcher 的訊息恰 times 則（timeout 必填，非同步硬需求）
        await().atMost(Duration.ofSeconds(5)).pollInterval(Duration.ofMillis(200))
            .untilAsserted(() -> {
                List<Map<String, String>> matched = consumer.received().stream()
                    .filter(msg -> expected.entrySet().stream().allMatch(e ->
                        expected.get(e.getKey())
                            .equals(msg.get(FIELD_TO_PROPERTY.get(e.getKey())))))
                    .toList();
                assertThat(matched).as("符合條件的訊息則數").hasSize(times);
            });
    }
}
```

## 填空 slot

- `{{Dep}}`／`{{dep}}`：registry entry.name → PascalCase／camelCase；`{{package_slug}}`：function package slug（snake_case）。
- `{{eventType}}`：kind-constants format 的 eventType 具名參數字面（保留為 Cucumber 表達式常量或 regex 群組）。
- `{{times}}`：由句面 `{int}` 帶入，代表符合 matcher 的訊息應收恰幾則。
- `FIELD_TO_PROPERTY`：DataTable 各中文表頭 → payload schema 屬性；只列 truth payload_ref 宣告欄位。

## Forbidden（承 kind-constants channel.yml `red_execute.forbidden`）

- 只斷言 payload_ref schema truth 宣告過的欄位；DataTable 出現未宣告欄位 → 停下回報，不硬斷。
- E 面 timeout 必填（bounded-wait），不得無界等待。
- 不得斷言訊息順序／exactly-once／重投遞（範圍外判別式）——只斷言符合 matcher 的則數與宣告欄位。
- 不得以 SUT 的 HTTP API／broker 管理 API 替代觀測；觀測點只有 test consumer 的接收緩衝。
- 不得在此 handler 內發訊或打 SUT 自身端點；本 handler 只查 test consumer 收到的 outbound。
