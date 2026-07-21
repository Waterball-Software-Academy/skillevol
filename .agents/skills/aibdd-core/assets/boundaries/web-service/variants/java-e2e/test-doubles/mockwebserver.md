# Test-double (java-e2e): MockWebServer（in-process，WebSocket）

WebSocket 依賴（kind=websocket）的替身配置。以 **in-process okhttp MockWebServer**（測試 JVM 內嵌，免 Docker）
扮演對端。WireMock 3 不支援 ws，不得選用。是 kind-constants `websocket.yml` `testability_defaults`
（`container_by_role`）的 java-e2e 具體實現 SSOT，由 RED_PREHANDLING_HOOK 佈建。

依 entry.sut_role 分流：

- **client 型（sut_role == client）**：SUT 是 ws client、連向外部 feed。替身用 MockWebServer
  enqueue 一個 WebSocket upgrade 扮 feed，連線建立後依腳本 `webSocket.send(frameJson)` 推送 frame。
  base URL 覆寫進 SUT 設定鍵（entry.wiring.sut_property_overrides）。見下方主節。
- **server 型（sut_role == server）**：SUT 自帶 ws endpoint、無替身；測試側以 ws client fixture
  連 SUT endpoint。見下方 ## server 型（測試 client fixture）。

## 需要的測試套件（Maven；scope=test）

red-execute 用此替身／其 handler（external-stub-ws／message-publish／interaction-verifier）前，
須確認專案 pom.xml `<dependencies>` 已含下列；缺者由 red-execute 自動補進（scope=test）：

- `com.squareup.okhttp3:mockwebserver` — in-process `MockWebServer`（支援 WebSocket upgrade）；**需顯式 version**（如 `4.12.0`，非 spring-boot BOM 管理）。
- `com.squareup.okhttp3:okhttp` — `WebSocket`／`WebSocketListener`／`Request`（client 型推送、server 型測試 client 皆用）；**需顯式 version**（與 mockwebserver 同版）。
- `org.awaitility:awaitility` — interaction-verifier 的 bounded-wait；由 spring-boot-dependencies BOM 管理，**version 可省**。

## 何時需要

isa.yml 內有 kind=websocket 的外部依賴 custom（handler `external-stub-ws`／`message-publish`／`interaction-verifier`）時：

- client 型：per registry entry 一個 in-process MockWebServer（同 entry 的 B 推送與 E 觀測共用同一 server）。
- server 型：無替身 server，per entry 一個測試 client fixture bean（C 送 frame 與 E 觀測共用同一連線）。

## client 型：@TestConfiguration（每個 websocket 依賴一個 feed server bean）

放 `src/test/java/${BASE_PACKAGE}/config/{{Dep}}FeedConfiguration.java`（`{{Dep}}` 由 entry.name 轉 PascalCase）：

```java
package ${BASE_PACKAGE}.config;

import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.MockResponse;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@TestConfiguration(proxyBeanMethods = false)
public class {{Dep}}FeedConfiguration {

    // in-process feed，隨機 port（免 Docker）；整個 test JVM 共用一個
    static final MockWebServer FEED = new MockWebServer();
    static {
        // enqueue 一個 WebSocket upgrade：連線建立後由 handler 依腳本推送 frame
        FEED.enqueue(new MockResponse().withWebSocketUpgrade(FeedRecorder.LISTENER));
    }

    /** 覆寫 SUT feed URL 設定鍵（entry.wiring.sut_property_overrides 各鍵）指向替身。 */
    @DynamicPropertySource
    static void overrideSutProperties(DynamicPropertyRegistry registry) {
        // 每個 sut_property_override 一行；鍵取自 registry entry.wiring.sut_property_overrides
        registry.add("{{sut_property}}", () -> FEED.url("/").toString().replaceFirst("^http", "ws"));
    }

    /** 供 handler step def 拿到 feed（B 推送、E 觀測共用）。 */
    @Bean
    MockWebServer {{dep}}Feed() { return FEED; }

    /** 供 handler 拿到「已建立的伺服端 WebSocket」以腳本化推送，以及觀測收到的 frame。 */
    @Bean
    FeedRecorder {{dep}}FeedRecorder() { return FeedRecorder.INSTANCE; }
}
```

`FeedRecorder` 持有伺服端 `WebSocket`（供 B 推送）與收到的 frame queue（供 E 觀測），
放 `src/test/java/${BASE_PACKAGE}/config/FeedRecorder.java`：

```java
package ${BASE_PACKAGE}.config;

import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okhttp3.Response;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

public class FeedRecorder {

    public static final FeedRecorder INSTANCE = new FeedRecorder();

    // 伺服端 WebSocket（連線建立後由 listener 填入），B handler 用它 send frame
    private final AtomicReference<WebSocket> serverSocket = new AtomicReference<>();
    // SUT → feed 方向收到的 frame（E handler 觀測用）
    private final BlockingQueue<String> received = new LinkedBlockingQueue<>();

    public static final WebSocketListener LISTENER = INSTANCE.new Listener();

    public WebSocket serverSocket() { return serverSocket.get(); }
    public BlockingQueue<String> received() { return received; }

    /** 每情境清理：清 queue（連線由 SUT 側管理，情境間 SUT context 重啟時自然重連）。 */
    public void reset() { received.clear(); }

    class Listener extends WebSocketListener {
        @Override public void onOpen(WebSocket ws, Response resp) { serverSocket.set(ws); }
        @Override public void onMessage(WebSocket ws, String text) { received.add(text); }
    }
}
```

於 `CucumberSpringConfiguration` 掛上：`@Import({{Dep}}FeedConfiguration.class)`。

## server 型：測試 client fixture

server 型無替身 server。SUT 自帶 ws endpoint；測試側以 okhttp `WebSocket` fixture 連 SUT endpoint，
C（送 frame）與 E（觀測收到 frame）共用同一連線。放
`src/test/java/${BASE_PACKAGE}/config/{{Dep}}TestClientConfiguration.java`：

```java
package ${BASE_PACKAGE}.config;

import okhttp3.OkHttpClient;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@TestConfiguration(proxyBeanMethods = false)
public class {{Dep}}TestClientConfiguration {

    // @SpringBootTest(webEnvironment = RANDOM_PORT) 注入實際 port
    @Value("${local.server.port}") int port;

    // SUT endpoint 相對路徑（來自 SUT ws endpoint truth，不臆造）
    private static final String ENDPOINT_PATH = "{{endpoint_path}}";

    @Bean
    WsTestClient {{dep}}TestClient() {
        WsTestClient client = new WsTestClient();
        OkHttpClient http = new OkHttpClient();
        Request req = new Request.Builder()
            .url("ws://localhost:" + port + ENDPOINT_PATH)
            .build();
        http.newWebSocket(req, client.listener());
        return client;
    }

    public static class WsTestClient {
        private volatile WebSocket socket;
        private final BlockingQueue<String> received = new LinkedBlockingQueue<>();

        public void send(String frameJson) { socket.send(frameJson); }
        public BlockingQueue<String> received() { return received; }
        public void reset() { received.clear(); }

        WebSocketListener listener() {
            return new WebSocketListener() {
                @Override public void onOpen(WebSocket ws, Response r) { socket = ws; }
                @Override public void onMessage(WebSocket ws, String text) { received.add(text); }
            };
        }
    }
}
```

於 `CucumberSpringConfiguration` 掛上：`@Import({{Dep}}TestClientConfiguration.class)`。

## 每情境重置（必做）

替身／測試 client 跨情境共用，須每情境清 frame queue，否則前一情境收到的 frame 汙染下一情境。
於 `DatabaseCleanupHook.@Before`（或等效 hook）加：

- client 型：`{{dep}}FeedRecorder.reset();`
- server 型：`{{dep}}TestClient.reset();`

## 填空 slot

- `{{Dep}}`／`{{dep}}`：registry entry.name 轉 PascalCase／camelCase。
- `{{sut_property}}`（client 型）：entry.wiring.sut_property_overrides 每一鍵各一行 `registry.add(...)`。
- `{{endpoint_path}}`（server 型）：SUT ws endpoint truth 宣告的路徑，不臆造。

## Forbidden

- 不得連真實外部服務／真實對端——只用本地 in-process MockWebServer（client 型）或連本地 SUT endpoint（server 型）。
- 不得選 WireMock 當 ws 替身——WireMock 3 不支援 WebSocket。
- 不得硬編 port（client 型用 `FEED.url(...)`；server 型用 `${local.server.port}`）。
- 一個 entry 的 B／E（client 型）或 C／E（server 型）共用同一 fixture，不另起第二個連線。
- 不得測連線重試／心跳／重連協定語意（範圍外，承 websocket.yml red_execute）。
- 忘記 `reset()` → 情境間互相汙染，屬測試設計缺陷。
