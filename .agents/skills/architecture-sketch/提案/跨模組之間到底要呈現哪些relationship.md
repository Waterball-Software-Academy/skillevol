# 提案：跨模組之間到底要呈現哪些 relationship

> 狀態：提案（brainstorm 收斂版，已採納，進入迭代）。
> 對象：`specs/truth/architecture/架構互動.html`（ELK 互動架構圖）。
> 來源 raw idea：`specs/truth/architecture/RAW-IDEAS.md`（2026-06-21）。

## 1. 問題

- 目前跨模組只畫了 critical dependency（呼叫／資料流）。
- Clean Architecture 的「**邊界（boundary）**」這層關係沒被呈現。
- 具體：`Mutator` 介面在「核心引擎」與「變異子」兩模組各畫一次（mirror、重複），看不出「實作跨界指向抽象」的依賴反轉。

## 2. 硬約束：一處原則（No Mirror）

- 每個 **class / interface 只有唯一 home 模組**，不得在多處複製。
- 跨模組共享一律以 **edge** 表達，不是複製節點。
- 例：`Mutator` 只住核心引擎；變異子模組刪掉該盒，`«Mutator»×10` 拉一條 realize 線越界指過去。任何共享型別／聚合根同此處理。
- 模型改動：class 標 `home` 模組；跨模組關係是 edge。

## 3. 跨模組關係分類（語意不同，呈現也要不同）

| 類型 | 意義 | Clean Arch 解讀 |
|---|---|---|
| **realize / implements** | 具象 → 介面（跨模組） | **邊界本體**；依賴指向抽象 |
| **call-through-abstraction** | 呼叫對方的介面 / port / 聚合根 | 健康的依賴反轉（DIP ok） |
| **direct-to-concrete (smell)** | 直接呼叫對方的具象實作 | 邊界**洩漏** / 耦合壞味道 |
| **data-flow / 讀型別** | 次要資料或型別依賴 | 弱關係 |

## 4. 該畫 / 不該畫

**該畫**（具「架構意義」才畫）：
1. realize（跨模組具象→介面）。
2. 依賴對方的**公開抽象**（interface / port / 聚合根）。
3. critical path 上的真實呼叫。

**不該畫**（只會變噪音）：
4. 順手的 util import、re-export、傳遞依賴、同型別被多處讀取。

**反直覺但關鍵**：
5. `direct-to-concrete` 的 smell 線**要畫、且要顯眼**（紅）。使用者正是想一眼看到「誰戳穿邊界」。→ 線的顏色 = 架構健康度。

## 5. 最具 HCI 的呈現

### 5.1 邊 LOD（隨展開聚合，與節點展開同一心智模型）
- 兩邊收合 → 一條 module→module 線，標 `realize ×N`。
- 一邊展開 → 展開側內部類別 → 對方模組盒（代表整包）。
- 兩邊展開 → 才 fan out 成 class→class 精準線。

### 5.2 Edge bundling
- realize 多條時先匯到介面盒旁的共用 port，再扇入；視覺像「一束線收進介面」。
- ELK 的 port + `edgeRouting=ORTHOGONAL` 原生支援。
- （本圖把 10 個具象收斂成單一 `«Mutator»×10` 盒，故 realize 天然只有一條，先免爆量。）

### 5.3 視覺語彙（不靠顏色硬背）
- realize → **空心三角箭頭**（UML），指向介面。
- call → 實心箭頭。
- smell（direct-to-concrete）→ 紅虛線。
- 一眼分辨「實作 / 呼叫 / 壞味道」。

### 5.4 邊界輕量視覺化
- 給「核心」模組（核心引擎、基因組型別）淡底色／一圈；外圈模組另一色。
- 配合 realize 線全部**指向內**，Clean Architecture 的環自然浮現，不必畫真同心圓。

### 5.5 佈局即論述
- ELK 用 realize／依賴方向當分層依據 → 被指最多的核心被排到中心側。
- 不用讀字，看佈局就知道「誰是核心、誰依賴誰」。

## 6. 加碼：圖即架構守門員
- 一旦某模組對另一模組出現紅色 `direct-to-concrete`，就是 DIP 被破壞的告警。
- 圖不只描述現狀，還能持續**驗收邊界**。

## 7. 對現有實作（架構互動.html）的改動清單
1. 資料模型：移除 mirror 的 `Mutator`（只留核心引擎那顆）。
2. 跨模組 edge 標 `kind ∈ {realize, call, smell, dataflow}`。
3. realize / smell / call / 內部 各自 marker 與配色（空心三角／紅虛線／實心／灰）。
4. 核心模組底色 zoning（核心引擎、基因組）。
5. 邊 LOD：收合聚合、展開 fan out（realize 目前單條，先確保正確）。

## 8. 開放問題
- realize 邊在「兩邊都展開且有多條」時的 bundle 策略（本圖暫以單盒規避）。
- 「公開抽象」如何在資料標記（哪些 class 是 module 的 public surface）。
- smell 線來源：人工標，或日後接 import 分析自動偵測。
