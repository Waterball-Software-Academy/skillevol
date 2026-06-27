# Raw Idea
用 /Users/johnnypan/Projects/skill-演進式-研究/.agents/skills/skillevol-form-eval
  幫 /Users/johnnypan/Projects/skill-演進式-研究/.agents/skills/skillevol-run-eval
  寫 eval 吧

  寫的過程要充分地與我互動，先規劃一個最小成本逐步驗證計畫
  一個點一個點驗證
  如果我說「不」那就可能代表有假設錯了，修正驗證計畫，再繼續。

  驗證計畫要 歸檔，每次修正計劃的 iterative process 都要記錄進度（現階段在哪，下一步在哪）到某處

  好比先開一個 .skillevol 資料夾，裡面放一個 .gitignore 裡面寫 ** (把自己資料夾從內部 ignore 掉自己）
  然後下一層放 skill name, 好比 .skillevol/skillevol-run-eval 就代表 skillevol-run-eval的 workspace
  底下再放 define-evals 代表工作內容是建立 定義 evals 。然後才放計畫檔 working-plan.md。i.e.,  .skillevol/skillevol-run-eval/define-evals/working-plan.md。

  裡面要先規劃你判斷下的逐步驗證計畫，最重要思想就是別產一堆之後使用者覺得整個很不對勁，感覺也不對，對用戶來說要一次 reject 一堆東西很痛苦，所以寧願逐步驗證、訪談，也不要失去驗證力。。

# Skill Proposal

`skillevol-define-evals` 是一支「協作流程」skill：它規範怎麼跟使用者一步步、可靠地把某個目標 skill 的 eval 定義出來。以下是整體提案。

## 這個 skill 要解決的問題

在這套演進式 skill 的世界裡，eval 是一切自主迭代的前提。沒有一組可信的 golden eval，agent 改了 rules、scripts、SOP 之後根本分不清自己是變好還是變壞，迭代就退化成在噪音裡亂走。eval 扮演的是 fitness oracle——適應度的尺、reward 的訊號。

但這把尺本身很難定義，而且定義它的過程充滿假設。被測 skill 的 inputs／outputs 長怎樣、一個 eval unit 的 before/ 該裝什麼、互動斷點怎麼答、產出怎麼判分——每一個都是可能猜錯的環節；對結構複雜、或本身就在「跑別的 skill」的 meta skill 來說，錯的機率更高。

真正要避免的失敗模式是這樣：agent 一股腦把整棵 eval/ 樹產出來、整包交給使用者，使用者卻發現從根上的框架就理解錯了。這時使用者得「一次 reject 一大堆」，這件事非常痛苦；更糟的是，這種痛苦會誘使雙方為了省事而勉強接受一個其實有瑕疵的 eval，於是 eval 的驗證力當場崩潰——一把校歪的尺比沒有尺更危險，因為它會讓後續所有迭代忠實地往錯的方向最佳化。

`skillevol-define-evals` 就是為了堵住這個失敗模式而存在。它把「定義 eval」從一次性的傾倒，改造成一段**階段性、可驗證、有紀錄的協作**：每一個根本假設都在任何依賴它的 artifact 被寫出來之前，先用最低成本跟使用者對齊。

## 核心精神：逐步驗證，絕不傾倒

整個流程只有一條鐵律——最小成本、逐點驗證、一次只推進一個點。

每個驗證點都先把「我打算這樣理解／這樣做」攤成一個具體、可回答的東西（一張嵌套圖、一份單一 unit 的草案），交給使用者確認，confirm 之後才動手產出對應的 artifact。使用者說「不」不是挫敗，而是訊號：代表某個上游假設錯了。這時不硬幹、不繞過，而是回頭定位是哪個假設、修正計畫、把這次修正記進歷史，然後再往下。

寧可多花幾輪訪談與確認，也不要為了快而失去驗證力。判斷標準很簡單：使用者永遠不應該被迫一次否決一大堆東西。只要某個產出讓使用者覺得「整個很不對勁」，那就代表前面有個更根本的點沒先驗——流程的任務，就是讓那個點提早、單獨、便宜地浮現。

## 工作區與進度歸檔

因為這段協作會跨多輪對話、可能被 conversation compact，所以「現在卡在哪、怎麼走到這裡」必須落成檔案，既能還原進度也能被稽核。

在專案根開一個 scratch 工作區 `.skillevol/`，裡面放一個內容為 `**` 的 `.gitignore`，讓這個工作區把自己整包 ignore 掉、不進版控（它是過程的暫存腦，不是產物）。工作區下依「被測 skill／工作項目」分層，例如為 `skillevol-run-eval` 定義 eval 的工作就落在 `.skillevol/skillevol-run-eval/define-evals/working-plan.md`。

`working-plan.md` 是這段協作的單一真相，連貫地寫成一份計畫而非散落便條，至少承載四件事：

- **目標**：要為哪個 skill、用 `skillevol-form-eval` 的 form 建出怎樣的一組 eval。
- **方法（鐵律）**：上面那條「逐點驗證、不傾倒」的承諾，提醒每一輪都照它走。
- **驗證點清單**：依「最根本、最可能錯」排序的待驗假設，每點標明它要對齊什麼、目前狀態（待確認／已確認／未開始）。
- **進度與修正歷史**：「現階段在哪、下一步在哪」隨每次推進更新；每一次因為使用者說「不」而修正計畫，都在修正歷史記下日期、原本錯在哪、改成什麼。這份歷史本身就是這套方法可靠性的證據。

## 與 skillevol-form-eval 的分工

`skillevol-form-eval` 與 `skillevol-define-evals` 是「形」與「怎麼可靠抵達那個形」的關係：前者規範一個 eval **該長什麼樣**，後者規範**怎麼跟使用者一步步把它定義對**。`define-evals` 產出的每一個 artifact 都必須是 form-eval-conformant 的，因此整段協作從頭到尾都受 `skillevol-form-eval` 的整體流程約束。

具體而言，協作要產出並始終守住 form-eval 的這些形狀：eval 放在**被測 skill 的 package 底下** `<skill>/eval/`，內含 `shared/expect.md`（橫切判準）、`dev/`（迭代用、可見）與選用的 `holdout/`（最終 gate、對 agent 隱藏）。每個 unit 是一個可獨立重現的實驗：`before/` 是 drop 進去就成立的 CWD、同時也是算檔案變更的 diff base；`prompt.md` 是開場那一句 user 輸入；互動型 unit 另有 `user.md` 作為模擬使用者的 answer key；`expect.md` 只管行為與時序——以 `## Provenance` 標明這個 unit 釘哪條行為、`## Run` 把過程拆成若干 one-turn（每個 turn 只驗它的 tool calls 與 assistant message，不放 file diff）、必要時以 `## Cross-turn` 驗 turn 之間的順序、gate 與活性；檔案的最終結果不寫進 expect，而是由 `after/` 這份真實快照 imply（before/ 到 after/ 的 diff 就是預期產出，比對交 judge、語意等價即可、非 byte-exact）。所有語意判準一律用 spec-by-example 的 0.0／0.3／0.7／1.0 四錨點配具體片段。

還有一條跨工具的硬規則：當某個 unit 的 `before/` 需要內嵌一個被評的 target skill 當 fixture 時，那個 skill 要同時複製到 `before/.agents/skills/<skill>` 與 `before/.claude/skills/<skill>` 兩處，讓這個 unit 無論用 Cursor（讀 `.agents`）還是 Claude（讀 `.claude`）都跑得起來。

## 階段性驗證流程

流程把 eval 的定義拆成一串驗證點，依 working-backward 的精神排序——最根本、最可能整套錯的先驗，因為它一旦錯，後面所有東西都白做。每個點都遵守同一個節拍：先把假設攤給使用者確認，confirm 後才產出該點的 artifact，產出後更新進度，再進下一點。典型的點序如下：

1. **形狀**：對齊「這個 skill 的一次 inputs→outputs 到底長怎樣」與「一個 eval unit 的 before/ 該裝什麼」。這是最根本的一層；對 meta 或複雜 skill，先畫一張嵌套圖讓使用者點頭，勝過任何後續細節。
2. **可跑骨架**：對齊讓 eval 真的跑得起來的真實依賴——target skill 怎麼被載入、互動斷點由誰回答（由跑測者依該 unit 的 `user.md` 自答、不轉交真人）、用哪個 judge、第一個拿來當 target 的對象是誰。骨架不成立，寫再多 unit 都是空中樓閣。
3. **第一個最小 happy unit 的設計**：挑一個 outcome 極不模糊的案（清楚該 pass），先只把它的輸入、預期行為、預期終態設計出來、跟使用者確認，還不動手寫檔。
4. **寫出 happy unit**：把這一個 unit 的檔（`before/`、`prompt.md`、選用 `user.md`、`expect.md`、`after/`）實際寫出來，給使用者看「單一一個 unit」確認 form 與內容都對，再往下。
5. **負向 unit**：補上該 fail 的案、邊界輸入、以及「不污染 fixture」這類負向把關——這些才是 eval 真正的驗證力來源，乾淨輸入永遠測不到。
6. **holdout**：補上對 agent 隱藏、只在最終 gate 才跑的 unit，用來偵測 agent 是不是只背了可見答案、其實沒真的 generalize。
7. **shared**：把跨 unit 共用的橫切判準上抽到 `shared/expect.md`，個別 unit 只留本情境差異、繼承 shared。

這個點序是預設骨幹而非教條：對齊過程中若使用者在某點說「不」，就可能要新增、拆分或重排後續的點——這正是流程要支援的，也正是 working-plan 修正歷史要記錄的。

## 互動契約

每個驗證點的呈現都要具體到可被一句話 confirm 或 reject：給一張圖、一份草案、一個 yes/no 的選擇，而不是抽象描述「我會做 X」。遇到使用者說「不」，就把它當成「上游有個假設錯了」，回頭定位是哪一個、修正計畫、在修正歷史誠實記下「原本以為…、其實是…」，再重新呈現。任何 artifact 都只在它所屬的點被確認之後才產出；每一輪推進後，`working-plan.md` 的「現階段／下一步」都要保持與實況同步。

## 完成定義

當被測 skill 擁有一組 form-eval-conformant 的 `eval/`（`shared/expect.md` 加上若干 `dev/` unit、必要時加 `holdout/`），其中每一個 unit 都可追溯到一個被使用者確認過的驗證點，且 `working-plan.md` 完整反映了從假設到確認、含每次修正的軌跡，這次 eval 定義就算完成。產出的這組 eval 接著就能交給 `skillevol-run-eval` 逐 unit 執行評分、由 `skillevol-run-benchmark` 跑整組並彙總——這套階段性協作的價值，最終就兌現在「一把雙方都信得過、且知道它為何可信的尺」上。

