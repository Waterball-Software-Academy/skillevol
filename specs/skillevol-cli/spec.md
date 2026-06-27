實作 .agents/skills/skillevol-cli 這個 skill
by /skillevol-define-evals 

skillevol 就是一個可以演進 skill 的工具
有一堆mutators 
但 anyway 他們都會用到 .skillevol 這個 workspace

我想寫兩個 cli under this skill
第一個是 setup 創建 .skillevol （內涵 ** 的 gitignore)，可能會被多個 skill 用到
第二個是 reset ，就是把 .skillevol 中除了 gitignore 以外的 skill package全部刪掉

1. 針對這兩者去做 dev benchmark
2. 針對其他會用到的 skills 盤一下，哪些可以改成去使用此 skill 
3. 在實作 skill 的時候要 derive script by /skillevol-derive-script 來去建立 cli command scripts