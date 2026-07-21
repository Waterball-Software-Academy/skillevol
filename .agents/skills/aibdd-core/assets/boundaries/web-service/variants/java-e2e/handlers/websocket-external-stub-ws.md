# Handler template (java-e2e): external-stub-ws（client 型）

kind-constants `websocket.yml` `isa_step_templates` 的 **B 面**（handler `external-stub-ws`）具體 Java 填空模版：
**applies_when `entry.sut_role == client`**——SUT 是 ws client、連向外部 feed；替身建連後依腳本
`webSocket.send(frameJson)` 推送 frame（frames[direction=in]，SUT 視角收）。用 in-process okhttp
**MockWebServer**（WireMock 3 不支援 ws，不得選用）。需 [`../test-doubles/mockwebserver.md`](../test-doubles/mockwebserver.md)
佈建的 feed server bean 與 `FeedRecorder`（DynamicPropertyRegistry 覆寫 SUT feed URL 指向 MockWebServer）。

step def 放 `steps/<function-package-slug>/external_stub/{{Dep}}FeedSteps.java`，
`@Autowired` 注入 mockwebserver.md 的 `FeedRecorder` bean。

## 對應句式（websocket；matcher 取 kind-constants websocket.yml format 主幹）

- 推送 frame：`feed "…" 將推送 …, with JSON:`（Given，json）— frames[direction=in] 的 payload 由 JSON 帶入。

## 模版

```java
package ${BASE_PACKAGE}.steps.{{package_slug}}.external_stub;

import ${BASE_PACKAGE}.config.FeedRecorder;
import io.cucumber.java.en.Given;
import okhttp3.WebSocket;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

public class {{Dep}}FeedSteps {

    @Autowired FeedRecorder {{dep}}FeedRecorder;

    @Given("feed {string} 將推送 {{event}}, with JSON:")
    public void feed將推送(String boundary, String frameJson) {
        // 等 SUT client 連上替身（伺服端 socket 就緒）；bounded-wait，timeout 必填
        await().atMost(Duration.ofSeconds(5)).pollInterval(Duration.ofMillis(100))
            .until(() -> {{dep}}FeedRecorder.serverSocket() != null);
        WebSocket server = {{dep}}FeedRecorder.serverSocket();
        assertThat(server).as("feed 尚未與 SUT 建立 ws 連線").isNotNull();
        // frameJson 依 frames[direction=in] payload schema truth 填空（不得發明 truth 外欄位）
        server.send(frameJson);
    }
}
```

## 填空 slot

- `{{Dep}}`／`{{dep}}`：entry.name → PascalCase／camelCase；`{{package_slug}}`：function package slug（snake_case）。
- `{{event}}`：kind-constants format 的 event 具名參數字面（保留為 Cucumber 表達式常量或 regex 群組）。
- `frameJson`：frames[direction=in] 該 event 的 payload；只填 truth 宣告欄位。

## Forbidden（承 kind-constants websocket.yml `red_execute.forbidden`）

- 不真連外——只對本地 in-process MockWebServer feed 推送 frame。
- 不得選 WireMock 當 ws 替身（不支援 WebSocket）。
- frame payload 只填 frames[direction=in] truth 宣告過的欄位，不發明。
- 連線建立等待為 bounded-wait（timeout 必填）；不得測連線重試／心跳／重連（範圍外）。
- 一個 entry 的 B／E 共用 mockwebserver.md 的同一 feed／`FeedRecorder` bean。
- 本 handler 僅 client 型（sut_role==client）適用；server 型改用 message-publish（C）。
