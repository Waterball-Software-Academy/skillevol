# Handler template (java-e2e): interaction-verifier

kind-constants `grpc.yml` `red_execute` E 面（互動驗證）的具體 Java 填空模版：驗證 SUT 對外部
rpc 的呼叫（次數／訊息欄位／未呼叫），用 **in-process grpcmock 的 Java DSL `verifyThat(...)`**。
需 [`../test-doubles/grpcmock.md`](../test-doubles/grpcmock.md) 佈建的替身 server bean。

step def 放 `steps/<function-package-slug>/external_stub/{{Dep}}InteractionSteps.java`，
`@Autowired` 注入 grpcmock.md 的 `GrpcMock` bean。

## 對應句式（grpc；matcher 取 kind-constants grpc.yml 的 format 主幹）

- 次數：`外部服務 "…" 的 … 應被呼叫 {int} 次` — 主模版，見下。
- 反面：`外部服務 "…" 的 … 不應被呼叫` — 變體 B。

（次數句式亦覆蓋「被呼叫一次」；訊息欄位斷言以 `withRequest(...)` 收斂到指定 message，見主模版註解。）

## 模版（次數主變體）

```java
package ${BASE_PACKAGE}.steps.{{package_slug}}.external_stub;

import io.cucumber.java.en.Then;
import org.grpcmock.GrpcMock;
import org.springframework.beans.factory.annotation.Autowired;

// proto truth 編譯產生的 service stub
import ${DEP_PROTO_PACKAGE}.{{ServiceGrpc}};

import java.time.Duration;

import static org.grpcmock.GrpcMock.calledMethod;
import static org.grpcmock.GrpcMock.times;
import static org.grpcmock.GrpcMock.verifyThat;
import static org.awaitility.Awaitility.await;

public class {{Dep}}InteractionSteps {

    @Autowired GrpcMock grpcMock;   // grpcmock.md 佈建的 {{dep}}GrpcMock bean

    @Then("外部服務 {string} 的 {{rpc}} 應被呼叫 {int} 次")
    public void 應被呼叫次數(String boundary, int times) {
        // E 正面：bounded-wait（同步外呼立即滿足；非同步則等到 timeout）；verifyThat 失敗擲 AssertionError。
        // 需斷言送出的訊息欄位時，於 calledMethod(...) 後接 .withRequest(msg)（只斷 proto truth 宣告欄位）。
        await().atMost(Duration.ofSeconds(5)).pollInterval(Duration.ofMillis(200))
            .untilAsserted(() -> verifyThat(
                calledMethod({{ServiceGrpc}}.get{{Rpc}}Method()),
                times(times)));
    }
}
```

## 變體 B（反面：`… 不應被呼叫`）

反面**不無界等待**：以觀察窗（kind-constants red_execute 的 window 預設，如 2s）等滿後斷言 0 次。
```java
@Then("外部服務 {string} 的 {{rpc}} 不應被呼叫")
public void 不應被呼叫(String boundary) {
    // 觀察窗滿之前若曾出現即失敗；滿窗後仍為 0 才通過（不無界輪詢）
    await().during(Duration.ofSeconds(2)).atMost(Duration.ofSeconds(3))
        .untilAsserted(() -> verifyThat(
            calledMethod({{ServiceGrpc}}.get{{Rpc}}Method()),
            times(0)));
}
```

## 填空 slot

- `{{Dep}}`／`{{dep}}`：entry.name → PascalCase／camelCase；`{{package_slug}}`：function package slug（snake_case）。
- `{{rpc}}`：kind-constants format 的 rpc 具名參數字面（保留為 Cucumber 表達式常量或 regex 群組）。
- `{{ServiceGrpc}}`／`{{Rpc}}`：proto truth 該 service 的 generated `*Grpc` 類別與 rpc 方法（如 `get<Rpc>Method()`）；`${DEP_PROTO_PACKAGE}` 為 codegen 的 java package。
- 訊息欄位斷言：`.withRequest(...)` 之 message 只填 proto request message truth 宣告欄位。

## Forbidden（承 kind-constants grpc.yml `red_execute.forbidden`）

- 不真連外部平台——只查本地 in-process grpcmock 替身（`verifyThat`）。
- 只斷言 proto request message truth 宣告過的欄位；出現未宣告欄位 → 停下回報，不硬斷。
- **不得斷言 grpc-status／trailer**；streaming rpc 順序語意範圍外。
- E 正面 timeout 必填（bounded-wait）；反面以觀察窗判定，不無界等待。
- 不得在此 handler 內打 SUT 自身端點；本 handler 只查替身收到的 outbound rpc。
- proto stub／message 類別由 truth proto 編譯產生，不手寫。
