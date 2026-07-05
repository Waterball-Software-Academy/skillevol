# Raw idea

SKILL 在演進了好一陣子之後，可能會需要重構。

有幾個可能問題
1. Rule 檔案的內聚力不夠，請參考 specs/003-步驟規則內聚性

2. SOP 裡面的控制流程「絕對性」不夠：
    - 指令含糊，看到這步驟時，到底要做什麼？每個指令的絕對性要明確。SOP 中不可以有任何參考資料。
    - 控制流程含糊：SOP 中應該要多使用 FOR EACH / IF / ELSE / ELSE IF / GO-TO 等等控制流程子，來去把執行流程，配合 TODO LIST TOOLing 機制來實現。不准漏步驟。

3. SOP 的抽象層級不一致，最外層之 SOP 裝著太多混雜的關注點。SOP 應該只嚴格呈現步驟指令、步驟產出之統合稱呼、控制流程。（影響人的閱讀，人應該深入淺出，先從 SOP 就秒懂流程，然後再從 rule 去深究規則細節）

4. 詞彙認知循序漸進不足：SKILL 上層過早暴露 skill-specific 術語，讀者還沒理解要做什麼，就被迫先理解 protocol label、gate id、oracle、provenance、mutation 等詞。SKILL 應先用一般使用者的基礎通用詞彙與基礎句構建立操作直覺；必要術語才在首次提到時快速解釋、在當步載入規則檔案，或直接放棄改用流程句。

# 問題細膩定義

我詮釋你在講的問題是：skill 演進久了之後，失效點通常不是「內容不夠多」，而是 instruction surface 的責任錯位。SOP、RuleFile、TemplateFile、Sub-SOP、working-plan、eval oracle 各自應該承載不同職責；一旦這些職責混在一起，agent 表面上讀到很多指令，實際上卻無法判斷下一步應做什麼、哪條規則在當下有約束力、產物要落在哪裡、何時必須停止等使用者確認。

核心問題可拆成三種互相放大的失真。

1. Step-Rule Cohesion 失真
   RuleFile 被當成全域背景或保險提醒廣播到多個 step，而不是只掛在真正需要它的 step 上。結果是 agent 每一步都帶著太多非局部約束，反而分不清當下應遵守哪條規則。判準不是「這條 rule 有沒有道理」，而是「移除這條 rule 後，該 step 是否更容易產生錯誤 artifact、錯誤判斷或錯誤執行方式」。

2. SOP Control Absoluteness 失真
   SOP 用敘述句、提醒句、原則句描述流程，但沒有把流程寫成可執行的狀態機。agent 看到某步時，仍要自行猜測要 READ、THINK、WRITE、ASK、DELEGATE、STOP、GO TO 哪個動作，也不知道 IF / ELSE / FOR EACH 的分支條件與回邊。長流程尤其會在 conversation compact 後失去位置，因為 TODO tooling、working-plan 與 phase gate 沒有被納入控制流程。

3. SOP Abstraction Level 失真
   最外層 SOP 同時塞入流程、原因、rubric、例外、模板骨架、委派 payload、歷史脈絡與防呆提醒。人類無法先從 SOP 秒懂流程，agent 也無法把 SOP 當作唯一主控線。SOP 應只呈現三類資訊：當步指令、當步產物的統合名稱、控制流程轉移。判斷準則放 RuleFile，固定產物骨架放 TemplateFile，有序子程序放 Sub-SOP，跨 turn 狀態放 working-plan。

因此，這個問題的本質不是「把 skill 寫得更完整」，而是把 skill 改成可執行、可評分、可恢復的程序結構。

良好的 skill 應滿足以下判準：

1. 每個 SOP step 都有單一主動詞，且能直接對應到工具行為或明確內省動作。
2. 每個 step 都能說清楚輸入、產物、下一個 gate；不能只說「處理」「分析」「確認」這種不可驗證動詞。
3. SOP 中的分支必須明寫 IF / ELSE IF / ELSE / FOR EACH / GO TO / STOP；不得把分支藏在自然語言解釋裡。
4. SOP 不展開規則內容、rubric、範例或模板全文；只命名當步消費的規則集合、模板集合或子程序。
5. RuleFile 只承載當步必要的無序原子規則；若一條規則只是背景知識、前序已決定的事、或跨太多 concern 的提醒，就不應貼在該 step。
6. TemplateFile 只承載固定產物骨架；不得把行為規則塞進 template，也不得把輸出骨架寫在 RuleFile。
7. Sub-SOP 只承載有順序依賴、可獨立執行的子程序；不得把幾條無序禁令包成 Sub-SOP。
8. 長流程必須同時有工具化 TODO 與落盤 working-plan，讓中斷、壓縮、resume 後仍能還原 phase、active gate、failure provenance 與下一個驗證點。
9. eval oracle 必須是法官，不是參考資料；缺少可執行 eval 時，不得先改 skill 正文，應先導向 define-evals。
10. 若 skill 自己正在被 eval，不得讀取自己的 self-eval golden、expect、after 或報告來猜答案；只能讀目標 skill、目標 eval、使用者需求與合法 working state。
11. skill-specific 詞彙必須按 progressive disclosure 出現；上層 SKILL 應優先使用一般詞彙與可執行句構，專門詞只能在必要時首次解釋、當步載入規則檔案，或被刪除改成流程描述。

用這套定義來看 `/skillevol-loop`，現版已經有重要的 oracle 與 mutation 規則，但主 SOP 仍混入太多判斷細節與巢狀說明；它需要被改成一條更硬的 loop state machine：先建立 state carrier，再判斷 eval bootstrap / RCA / red gate / mutation / benchmark / final gate，任何 gate 未被確認前都不得越權到下一階段。
