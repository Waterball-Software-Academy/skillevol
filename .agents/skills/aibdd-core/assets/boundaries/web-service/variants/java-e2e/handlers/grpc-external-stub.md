# Handler template (java-e2e): external-stub

kind-constants `grpc.yml` `red_execute` B 面（樁）的具體 Java 填空模版：佈建 in-process grpcmock
讓外部 rpc 對指定請求回傳指定回應。用 **grpcmock 的 Java DSL `stubFor(unaryMethod(...)...)`**。
需 [`../test-doubles/grpcmock.md`](../test-doubles/grpcmock.md) 佈建的替身 server bean。

step def 放 `steps/<function-package-slug>/external_stub/{{Dep}}StubSteps.java`，
`@Autowired` 注入 grpcmock.md 的 `GrpcMock` bean。

## 對應句式（grpc；matcher 取 kind-constants grpc.yml format 主幹）

- 成功／業務結果體：`外部服務 "…" 對 … 回應, with JSON:`（Given，json；response message 由 proto JSON mapping 帶入）— 主模版，見下。

**與 api 的形態一致**：換 HTTP operation 為 gRPC rpc、換 WireMock 為 grpcmock、換 HTTP body 為 proto message。
「外部業務否決」（如簽核 rejected）是**同一 rpc 換回應 message 內容**，用主模版填否決 payload 即可；
gRPC 錯誤形態（grpc-status／trailer）**不在測試範圍**（承 grpc.yml red_execute.forbidden），故無 api 的「錯誤狀態變體」。

## 模版

```java
package ${BASE_PACKAGE}.steps.{{package_slug}}.external_stub;

import io.cucumber.java.en.Given;
import org.grpcmock.GrpcMock;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.protobuf.util.JsonFormat;
// proto truth 編譯產生的 generated 類別：service stub 與 response message
import ${DEP_PROTO_PACKAGE}.{{ServiceGrpc}};
import ${DEP_PROTO_PACKAGE}.{{ResponseMessage}};

import static org.grpcmock.GrpcMock.stubFor;
import static org.grpcmock.GrpcMock.unaryMethod;

public class {{Dep}}StubSteps {

    @Autowired GrpcMock grpcMock;   // grpcmock.md 佈建的 {{dep}}GrpcMock bean

    @Given("外部服務 {string} 對 {{rpc}} 回應, with JSON:")
    public void 對rpc回應(String boundary, String responseJson) throws Exception {
        // responseJson 依 proto response message truth 填空（不得發明 truth 外欄位）
        {{ResponseMessage}}.Builder builder = {{ResponseMessage}}.newBuilder();
        JsonFormat.parser().ignoringUnknownFields().merge(responseJson, builder);

        stubFor(unaryMethod({{ServiceGrpc}}.get{{Rpc}}Method())
            .willReturn(builder.build()));
    }
}
```

## 填空 slot

- `{{Dep}}`／`{{dep}}`／`{{package_slug}}`：同 interaction-verifier.md。
- `{{rpc}}`：kind-constants format 的 rpc 具名參數字面（Cucumber 表達式常量或 regex 群組）。
- `{{ServiceGrpc}}`／`{{Rpc}}`：proto truth 該 service 的 generated `*Grpc` 類別與 rpc 方法（如 `get<Rpc>Method()`）。
- `{{ResponseMessage}}`：proto truth 該 rpc 的 response message 型別；`${DEP_PROTO_PACKAGE}` 為 codegen 的 java package。

## Forbidden（承 kind-constants grpc.yml `red_execute.forbidden`）

- 不真連外——只對本地 in-process grpcmock 替身 `stubFor` 佈樁。
- response message 只填 proto response message truth 宣告過的欄位，不發明。
- 不得佈建／斷言 grpc-status／trailer；streaming rpc 順序語意範圍外。
- 一個 entry 的 B／E 共用 grpcmock.md 的同一 `GrpcMock` bean。
- proto stub／message 類別由 truth proto 編譯產生，不手寫。
