# 本 Skill Family 目的

- 能夠以終為始地去開發 Skill，先定義好 evaluation benchmark 再讓 AI 去自我迭代直到通過 evaluation benchmark，這樣我們人就可以把精力放在每個 skill 的 evaluation 就好了。

# Quick start

## 1. 下載 skill

- git clone https://github.com/Waterball-Software-Academy/skillevol

## 2. 本 Skill 的入口是哪一個？其實只要用這個就好

1. 只要使用 skillevol-loop 就好。
2. 用法，use case 有以下幾個：
    1. 全新 skill：
        1. 範例：
            
            ```yaml
            /skillevol-loop 我想創建一個新的 Skill。
            
            這個 Skill 的功能是：在程式開發之前，先用 Mermaid 繪製出類別圖。當類別圖繪製好之後，
            
            它會跟我互動，在技術細節上徵求我的同意；直到我 Confirm 之後，它才會開始進行開發。
            ```
            
        2. 成果：
            1. AI 會創建你要的 skill package，然後 AI 會先透過 `/skillevol-define-evals，`來邊訪談你，邊幫你定義該 skill 的 `eval/` 測試案例要怎麼測。
            2. eval 定義完之後會撰寫在 `<skill-package>/eval/` 資料夾之中，參考 Eval 結構長怎樣？ 
            3. eval 定義完之後，AI 會開始寫 skill，寫完之後他會進行 `/skillevol-run-eval` 來去在 skillevol 沙盒工作區中執行 eval。
    2. 既有 skill 要優化
        1. 時機：
            1. 通常是在某個對話中，你用了自己的某個 skill，但產出似乎不如你所願。
            2. 你分析此 skill 的 eval，發現不夠完善，想要優化。
        
        2. 範例：
            
            ```yaml
            /skillevol-loop 我想優化既有的 /plan-with-class-diagram 這個 Skill。
            
            目前的問題是：有時候它在使用者只是先確認類別圖時，就直接往下開始施工了。
            
            我希望它一定要先把 class diagram 畫出來、等我確認圖，再另外問我要不要開始實作。
            ```
            
        3. 成果：
            1. AI 會先分析目前這個 skill 的 eval 能不能真的抓到你說的問題。
            2. 如果 eval 不夠，AI 會先幫你補 eval，然後先跑出 red gate，證明這個問題現在真的會被測到。
            3. 等你確認 red gate 沒問題之後，AI 才會開始持續調整 skill，直到 dev benchmark 綠掉，最後再跑 holdout。
        

# Eval 結構長怎樣？

## 直接參考

這個 skill 自己就是透過 eval 開發出來的。有幾個參考，點底下參考連結後參考其中的 `eval` 下的組成即可：

1. 簡單好懂： https://github.com/Waterball-Software-Academy/skillevol/tree/main/.agents/skills/skillevol-derive-rules
2. 相較複雜的案例：https://github.com/Waterball-Software-Academy/skillevol/tree/main/.agents/skills/skillevol-run-eval

## 結構及解說

先講最常見的長相：

```text
eval/
  shared/
    expect.md
  dev/
    <case-name>/
      before/
      prompt.md
      expect.md
      user.md     # optional
      after/      # optional
  holdout/
    <case-name>/
      before/
      prompt.md
      expect.md
      user.md     # optional
      after/      # optional
```

1. `dev/`
    1. 這是平常拿來反覆迭代的 benchmark。
    2. 你在演化 skill 的過程中，AI 主要就是反覆跑這一區。
2. `holdout/`
    1. 這是最後放行用的 benchmark。
    2. 它的角色比較像期末考，不是每一輪都拿來當調參工具。
3. `shared/expect.md`
    1. 放橫切、共用的判準。
    2. 每個 case 自己的 `expect.md` 只要補這個 case 特有的要求就好。
4. `<case-name>/before/`
    1. 這是這個 eval case 開始前的工作區初始狀態。
    2. target skill 實際會在這份初始狀態上工作。
5. `<case-name>/prompt.md`
    1. 這就是丟給 target skill 的起始使用者需求。
    2. 你可以把它想成「這一題的題目本體」。
6. `<case-name>/expect.md`
    1. 這裡定義這題要怎麼評分、哪些行為算 pass、哪些不算。
    2. 它是 oracle，不是給 target skill 看的提示。
7. `<case-name>/user.md`
    1. 這是 optional。
    2. 如果這個 skill 在執行途中可能會停下來問使用者問題，就可以用這個檔案來模擬後續使用者回覆。
8. `<case-name>/after/`
    1. 這也是 optional。
    2. 當這題需要檢查檔案終態時，就會放這個 case 跑完之後應該長什麼樣子。

# Skillevol 沙盒工作區解釋

Skillevol 會在 repo 根目錄底下開一個 `.skillevol/` 工作區。

這個工作區不是正式產物，也不是 skill 本體，而是 AI 在演化過程中的 scratch space。

所以它通常會有一個 `.skillevol/.gitignore`，內容是 `**`，意思是整包沙盒都不要進版控。

你可以把它理解成：skill 正文、eval fixture 這些是正式資產；`.skillevol/` 則是這一輪執行過程中的工作筆記、報告與暫存工作區。

常見會看到幾種東西：

1. `.skillevol/<target-skill>/define-evals/working-plan.md`
    1. 當 AI 正在幫某個 skill 定義 eval，計畫會寫在這裡。
2. `.skillevol/<target-skill>/loop/working-plan.md`
    1. 當 AI 正在用 eval 驅動某個 skill 持續演化，工作計畫會寫在這裡。
3. `.skillevol/skillevol-run-eval/run-evals/<phase>/<unit>/`
    1. 這是某次單一 eval run 的外層 sandbox。
    2. 裡面通常會看到 `observation.md`、`judge-input.md`、`eval-report.md` 這種執行痕跡。

為什麼要有這層沙盒？因為這樣正式的 eval fixture、正式的 skill package，和每一輪執行中的暫存狀態才能分開。

這樣你在看 repo 的時候，也比較容易分清楚：

1. 哪些是你真正要 version control 的東西。
2. 哪些只是這一次 run 的中間產物。

# 我們的 Eval 優勢在哪？

我覺得這套 eval 比較強的地方，是它不是只在看 skill 用完之後的 snapshot 對不對。

它其實是在驗證「這個 skill 有沒有用對的方法完成這件事」。

1. 不是只看 `after/`
    1. 一般測法很容易只看最後產物像不像 golden snapshot。
    2. 但我們這套 eval 會同時看 `Tool calls`、`Assistant message`、`after/`，如果是多輪互動還會看 `event trace`。
    3. 所以不是只有結果對，連過程也要對。
2. 可以驗證互動型 skill
    1. 有些 skill 本來就不是一口氣做完，中途會有必要 clarify。
    2. 這時候 eval 不只是允許它問，而是可以明確驗證：它有沒有在該問的時候問、問的題目對不對、回答之後有沒有繼續往下走。
    3. `user.md` 在這裡很重要，因為它讓 eval 可以模擬後續使用者回覆，而不是只測單輪 happy path。
3. 可以驗證時序與 gate
    1. 很多 skill 的 correctness，不只在於最後有沒有產出檔案，而在於順序有沒有守住。
    2. 例如：該先 clarify 才能寫、該先 confirm 架構才能施工、答完上一題之前不能偷跑下一步。
    3. 這些東西單靠 snapshot 很難測，但用 turn-by-turn 的 expect 與 cross-turn 規則就能測。
4. 對 multi-turn 行為比較有判別力
    1. 我們可以驗證每個 `ASK` 後面是不是有對應的 `ANSWER`。
    2. 也可以驗證同一題是不是被重複問、整個 run 最後會不會正常終止。
    3. 換句話說，eval 不只看「有沒有問」，還看「這個互動有沒有走完」。
5. 不會把 oracle 簡化成死板的 byte-exact 比對
    1. `after/` 的重點比較偏向語意等價，不一定是每個字都一模一樣。
    2. 這讓 eval 比較不會因為表面差異就誤殺合理解，也比較能容納真正有設計空間的 skill。
6. 能把失敗定位得更清楚
    1. 因為它不是只有一張最終快照，所以 fail 的時候比較容易知道是卡在 tool call、互動 gate、時序，還是檔案終態。
    2. 這對後面要用 `/skillevol-loop` 去補 red gate、做 RCA、再做 mutation 很重要。

簡單講，我們這套 eval 測的不只是「有沒有做出來」，而是「有沒有以正確的互動方式、正確的步驟順序、正確的邊界控制把它做出來」。

# Skill Family Table

由上到下就是由高層到低層。`form` 類型放最下面，`derive` 放在 `form` 之上。

| 層級 | Skill | 在做什麼 |
| --- | --- | --- |
| L5 | `skillevol-loop` | 整個 skill 演化流程的總 orchestrator。用 eval 當 oracle，先補測試與 red gate，再持續 mutation 到 benchmark 通過。 |
| L4 | `skillevol-define-evals` | 幫目標 skill 定義 eval benchmark，讓一個 skill 先有可驗證的尺。 |
| L4 | `skillevol-run-benchmark` | 跑某個 skill 的整組 benchmark，包含 dev 與 holdout，並彙總結果。 |
| L4 | `skillevol-cli` | 管理 `.skillevol/` 沙盒工作區的 setup 與 reset。 |
| L3 | `skillevol-run-eval` | 跑單一 eval unit，收集 observation，並產出 eval-report。 |
| L3 | `skillevol-eval-judge` | 根據 observation、expect 與 after 來判讀單一 eval run 的 pass / fail。 |
| L2 | `skillevol-derive-rules` | 把指定 SOP 步驟中的無序原子規定抽成 RuleFile，並補回 reference。 |
| L2 | `skillevol-derive-script` | 把可一起自動化的步驟群抽成 ScriptFile，並讓 parent 步改成 script invoke。 |
| L2 | `skillevol-derive-subsop` | 把指定步內嵌的有序子程序抽成 Sub-SOP，並讓 parent 步改成 invoke reference。 |
| L2 | `skillevol-derive-template` | 把固定形狀的輸出骨架抽成 TemplateFile，並補回 reference。 |
| L1 | `skillevol-form-eval` | 規範 Skill 的 `eval/` 要怎麼寫。 |
| L1 | `skillevol-form-frontmatter` | 規範 Skill 的 frontmatter 要怎麼寫。 |
| L1 | `skillevol-form-purpose` | 規範 Skill 的 `Purpose` 部位要怎麼寫。 |
| L1 | `skillevol-form-rule-file` | 規範 Skill 的 RuleFile 要怎麼寫。 |
| L1 | `skillevol-form-script` | 規範 Skill 的 ScriptFile 要怎麼寫。 |
| L1 | `skillevol-form-sop` | 規範 Skill 的 `SOP` 部位要怎麼寫。 |
| L1 | `skillevol-form-subsop` | 規範 Skill 的 Sub-SOP 要怎麼寫。 |
| L1 | `skillevol-form-template-file` | 規範 Skill 的 TemplateFile 要怎麼寫。 |