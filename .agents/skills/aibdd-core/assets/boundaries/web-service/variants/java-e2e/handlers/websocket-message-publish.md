# Handler template (java-e2e): message-publish（server 型）

kind-constants `websocket.yml` `isa_step_templates` 的 **C 面**（handler `message-publish`）具體 Java 填空模版：
**applies_when `entry.sut_role == server`**——SUT 自帶 ws endpoint、是 server；測試側以 ws client fixture
連 SUT endpoint 送 frame（frames[direction=in]，SUT 視角收）。用 in-process okhttp `WebSocket`
測試 client（或 Spring `StandardWebSocketClient`；此模版取前者），無替身 server、無 container。
需 [`../test-doubles/mockwebserver.md`](../test-doubles/mockwebserver.md) 佈建的測試 client fixture bean（server 型節）。

step def 放 `steps/<function-package-slug>/external_stub/{{Dep}}PublishSteps.java`，
`@Autowired` 注入 mockwebserver.md 的 `WsTestClient` bean。

## 對應句式（websocket；matcher 取 kind-constants websocket.yml format 主幹）

- 送出 frame：`ws 連線 "…" 送出 …, with JSON:`（When，json）— frames[direction=in] 的 payload 由 JSON 帶入。

## 模版

```java
package ${BASE_PACKAGE}.steps.{{package_slug}}.external_stub;

import ${BASE_PACKAGE}.config.{{Dep}}TestClientConfiguration.WsTestClient;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

public class {{Dep}}PublishSteps {

    @Autowired WsTestClient {{dep}}TestClient;

    @When("ws 連線 {string} 送出 {{event}}, with JSON:")
    public void ws送出(String boundary, String frameJson) {
        // frameJson 依 frames[direction=in] payload schema truth 填空（不得發明 truth 外欄位）
        // fixture 建構時已連上 SUT endpoint（見 mockwebserver.md server 型節）
        {{dep}}TestClient.send(frameJson);
    }
}
```

## 填空 slot

- `{{Dep}}`／`{{dep}}`：entry.name → PascalCase／camelCase；`{{package_slug}}`：function package slug（snake_case）。
- `{{event}}`：kind-constants format 的 event 具名參數字面（保留為 Cucumber 表達式常量或 regex 群組）。
- `frameJson`：frames[direction=in] 該 event 的 payload；只填 truth 宣告欄位。

## Forbidden（承 kind-constants websocket.yml `red_execute.forbidden`）

- 只對本地 SUT endpoint 送 frame——不臆造 endpoint 路徑（取自 SUT ws endpoint truth）。
- frame payload 只填 frames[direction=in] truth 宣告過的欄位，不發明。
- 不得測連線重試／心跳／重連協定語意（範圍外）。
- 一個 entry 的 C／E 共用 mockwebserver.md 的同一 `WsTestClient` fixture，不另起第二個連線。
- 本 handler 僅 server 型（sut_role==server）適用；client 型改用 external-stub-ws（B）。
