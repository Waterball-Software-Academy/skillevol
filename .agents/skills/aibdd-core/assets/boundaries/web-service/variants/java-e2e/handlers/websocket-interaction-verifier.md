# Handler template (java-e2e): interaction-verifier（兩型共用）

kind-constants `websocket.yml` `isa_step_templates` 的 **E 面**（handler `interaction-verifier`）具體 Java 填空模版：
驗證觀測點**最終收到**符合 matcher 的 frame（eventually）。**client／server 兩型共用**——差別只在觀測緩衝來源：

- client 型（sut_role==client）：SUT 送給 feed 的 frame，收在 mockwebserver.md 的 `FeedRecorder.received()`。
- server 型（sut_role==server）：SUT 送給測試 client 的 frame，收在 mockwebserver.md 的 `WsTestClient.received()`。

以 **awaitility bounded-wait** 輪詢觀測 queue，直到出現符合 matcher 的 frame；只斷言 payload 宣告欄位。
需 [`../test-doubles/mockwebserver.md`](../test-doubles/mockwebserver.md) 佈建的觀測緩衝 bean。

step def 放 `steps/<function-package-slug>/external_stub/{{Dep}}InteractionSteps.java`，
`@Autowired` 注入 mockwebserver.md 的觀測 bean（依型擇一）。

## 對應句式（websocket；matcher 取 kind-constants websocket.yml 的 format 主幹）

- 收到 frame：`ws "…" 最終應收到 … frame, with table:`（Then，data_table）— frames[direction=out] 的 payload 欄位。

## 模版

```java
package ${BASE_PACKAGE}.steps.{{package_slug}}.external_stub;

// client 型注入 FeedRecorder；server 型注入 WsTestClient——擇一（見下方註解）
import ${BASE_PACKAGE}.config.FeedRecorder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Then;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import static org.awaitility.Awaitility.await;

public class {{Dep}}InteractionSteps {

    // DataTable 中文表頭 → frames[direction=out] payload 屬性（僅 truth 宣告欄位；不得發明）
    private static final Map<String, String> FIELD_TO_PROPERTY = Map.of(
        {{field_header}}, "{{schema_property}}"   // 每欄一筆
    );

    private final ObjectMapper mapper = new ObjectMapper();

    // client 型：@Autowired FeedRecorder {{dep}}FeedRecorder;（觀測 queue = {{dep}}FeedRecorder.received()）
    // server 型：@Autowired WsTestClient {{dep}}TestClient;（觀測 queue = {{dep}}TestClient.received()）
    @Autowired FeedRecorder {{dep}}FeedRecorder;

    @Then("ws {string} 最終應收到 {{event}} frame, with table:")
    public void 最終應收到frame(String boundary, DataTable table) {
        Map<String, String> expected = table.asMaps().get(0);
        BlockingQueue<String> received = {{dep}}FeedRecorder.received();  // server 型改 {{dep}}TestClient.received()
        // E 正面：bounded-wait（eventually）；timeout 必填，不無界等待
        await().atMost(Duration.ofSeconds(5)).pollInterval(Duration.ofMillis(200))
            .until(() -> received.stream().anyMatch(frame -> matches(frame, expected)));
    }

    private boolean matches(String frameJson, Map<String, String> expected) {
        try {
            JsonNode node = mapper.readTree(frameJson);
            for (var e : expected.entrySet()) {
                String prop = FIELD_TO_PROPERTY.get(e.getKey());
                if (prop == null) {
                    throw new AssertionError("欄位「" + e.getKey() + "」未在 frames payload truth 宣告");
                }
                JsonNode v = node.get(prop);
                if (v == null || !v.asText().equals(e.getValue())) return false;
            }
            return true;
        } catch (Exception ex) {
            return false;  // 非 JSON 或解析失敗 → 此 frame 不符，繼續輪詢
        }
    }
}
```

## 填空 slot

- `{{Dep}}`／`{{dep}}`：entry.name → PascalCase／camelCase；`{{package_slug}}`：function package slug（snake_case）。
- `{{event}}`：kind-constants format 的 event 具名參數字面（保留為 Cucumber 表達式常量或 regex 群組）。
- `FIELD_TO_PROPERTY`：DataTable 各中文表頭 → frames[direction=out] payload 屬性；只列 truth 宣告欄位。
- 觀測 bean 依型擇一：client 型 `FeedRecorder.received()`；server 型 `WsTestClient.received()`。

## Forbidden（承 kind-constants websocket.yml `red_execute.forbidden`）

- 只查本地觀測緩衝（in-process feed 的 `FeedRecorder` 或測試 client 的 `WsTestClient`）——不真連外。
- 只斷言 frames[direction=out] truth 宣告過的欄位；DataTable 出現未宣告欄位 → 停下回報（擲錯），不硬斷。
- E 面 timeout 必填（bounded-wait／eventually）；**不得無界等待**。
- 不得測連線重試／心跳／重連協定語意（範圍外）。
- 不得在此 handler 內發 MockMvc／打 SUT 自身 HTTP 端點；本 handler 只查觀測到的 ws frame。
