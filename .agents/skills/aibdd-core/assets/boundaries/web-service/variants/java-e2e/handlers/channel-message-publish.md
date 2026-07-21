# Handler template (java-e2e): message-publish

kind-constants `channel.yml` `red_execute` C 面（inbound）的具體 Java 填空模版：測試側 producer
往 channel 發一則測試訊息，**驅動 SUT 消費**。用 **spring-kafka 的 `KafkaTemplate`**（真 broker，
非 stub）。需 [`../test-doubles/kafka.md`](../test-doubles/kafka.md) 佈建的真 testcontainer broker。

step def 放 `steps/<function-package-slug>/channel/{{Dep}}PublishSteps.java`，
`@Autowired` 注入 kafka.md 的 `KafkaTemplate<String, String>` bean。

## 對應句式（channel；matcher 取 kind-constants channel.yml format 主幹）

- inbound 發訊：`channel "…" 收到一則 {{eventType}} 訊息, with JSON:`（When，json；作為前置事件注入時可用 Given）— 主模版，見下。

**注意**：本句只負責「把訊息投進 channel」。SUT 消費後的結果斷言屬 D／E 面且恆帶 eventually，
禁在本句做——本 handler 不查任何消費結果、不打 SUT 端點。

## 模版

```java
package ${BASE_PACKAGE}.steps.{{package_slug}}.channel;

import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.kafka.support.KafkaHeaders;

public class {{Dep}}PublishSteps {

    // truth 檔宣告的 topic 名（channel → broker topic/queue）
    private static final String TOPIC = "{{topic}}";

    @Autowired KafkaTemplate<String, String> kafkaTemplate;

    @When("channel {string} 收到一則 {{eventType}} 訊息, with JSON:")
    public void 收到一則訊息(String channel, String payloadJson) {
        // payloadJson 依 payload_ref schema truth 填空（不得發明 truth 外欄位）
        // key／headers 依 truth 檔宣告；truth 未宣告者不填
        kafkaTemplate.send(MessageBuilder
            .withPayload(payloadJson)
            .setHeader(KafkaHeaders.TOPIC, TOPIC)
            .setHeader(KafkaHeaders.KEY, "{{message_key}}")   // truth 有宣告 key 才留此行
            .build());
        kafkaTemplate.flush();   // 確保投遞完成，讓 SUT 有機會消費（後續斷言恆 eventually）
    }
}
```

## 填空 slot

- `{{Dep}}`／`{{dep}}`：registry entry.name → PascalCase／camelCase；`{{package_slug}}`：function package slug（snake_case）。
- `{{eventType}}`：kind-constants format 的 eventType 具名參數字面（保留為 Cucumber 表達式常量或 regex 群組）。
- `{{topic}}`：truth 檔該 channel 對應的 broker topic／queue 名。
- `{{message_key}}`：truth 檔 key 表達式（如 orderId）對得上 feature 資料別名；truth 未宣告 key 則整行移除。
- headers：truth 檔在契約語意需要時才宣告；未宣告不填。

## Forbidden（承 kind-constants channel.yml `red_execute.forbidden`）

- 不得以 SUT 的 HTTP API 替代發訊（發訊只走測試側 producer；打 SUT 端點是 api_call 的事）。
- payload 只填 payload_ref schema truth 宣告過的欄位，不發明。
- 不得斷言訊息順序／exactly-once／重投遞（範圍外判別式）。
- 本 handler 只負責投訊入 channel，不查消費結果——結果斷言屬 D／E＋eventually。
