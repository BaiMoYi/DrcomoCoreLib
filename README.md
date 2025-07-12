---
title: DrcomoCoreLib JavaDocs
---

# **DrcomoCoreLib API 文档查询规则**

当接收到与 DrcomoCoreLib 开发相关的用户请求时，严格遵循以下规则，将所需功能映射到对应的API文档，并基于该文档提供解决方案。  

## **API文档查询规则**

---

### 日志记录
- **功能描述**：实现或管理控制台日志输出，如 `info`, `warn`, `error`，或动态设置日志级别。  
- **查询文档**：[查看](./JavaDocs/util/DebugUtil-JavaDoc.md)


### 配置文件读写 (YAML)
- **功能描述**：对 `.yml` 文件进行加载、重载、保存、读写键值、复制默认配置、获取配置节等操作。  
- **查询文档**：[查看](./JavaDocs/config/YamlUtil-JavaDoc.md)


### 文本颜色处理
- **功能描述**：翻译（`&` → `§`）、转换（`&#RRGGBB`）或剥离字符串中的 Minecraft 颜色代码。  
- **查询文档**：[查看](./JavaDocs/color/ColorUtil-JavaDoc.md)


### 发送消息与文本本地化
- **功能描述**：发送游戏内消息（聊天、ActionBar、Title），解析多层级占位符（自定义、PAPI、内部），管理多语言文件。  
- **查询文档**：[查看](./JavaDocs/message/MessageService-JavaDoc.md)


### 物品NBT数据操作
- **功能描述**：在 `ItemStack` 上附加、读取、修改、删除或批量保留自定义数据标签。  
- **前置查询**：[查看](./JavaDocs/nbt/NbtKeyHandler-JavaDoc.md)（NBT键名安全策略）  
- **核心查询**：[查看](./JavaDocs/nbt/NBTUtil-JavaDoc.md)（具体NBT操作API）


### PlaceholderAPI (PAPI) 集成
- **功能描述**：注册自定义PAPI占位符（如 `%myplugin_level%`）或解析含PAPI占位符的字符串。  
- **查询文档**：[查看](./JavaDocs/hook/placeholder/PlaceholderAPIUtil-JavaDoc.md)


### 动态条件判断
- **功能描述**：解析并计算包含PAPI占位符、逻辑运算符（`&&`, `||`）和比较运算符（`>=`, `==`, `STR_CONTAINS`）的条件表达式。  
- **查询文档**：[查看](./JavaDocs/hook/placeholder/parse/PlaceholderConditionEvaluator-JavaDoc.md)  
- **关联查询**：[查看](./JavaDocs/hook/placeholder/parse/ParseException-JavaDoc.md)（表达式解析异常处理）


### 经济系统交互 (Vault / PlayerPoints)
- **功能描述**：查询玩家余额、扣款、存款、格式化货币等操作。  
- **查询文档 1 (接口)**：[查看](./JavaDocs/hook/economy/EconomyProvider-JavaDoc.md)（通用经济接口）  
- **查询文档 2 (实现)**：  
  - Vault 对接：[查看](./JavaDocs/hook/economy/provider/VaultEconomyProvider-JavaDoc.md)  
  - PlayerPoints 对接：[查看](./JavaDocs/hook/economy/provider/PlayerPointsEconomyProvider-JavaDoc.md)  
- **查询文档 3 (结果)**：[查看](./JavaDocs/hook/economy/EconomyResponse-JavaDoc.md)（经济操作返回对象）


### 数学公式计算
- **功能描述**：计算字符串形式的数学表达式（支持变量）。  
- **查询文档**：[查看](./JavaDocs/math/FormulaCalculator-JavaDoc.md)


### 音效管理
- **功能描述**：从配置文件加载音效并通过键名（key）播放。  
- **查询文档**：[查看](./JavaDocs/sound/SoundManager-JavaDoc.md)


### **GUI 创建与交互**
- **功能描述**：构建交互式菜单、定义特定槽位的点击行为、管理GUI的打开与关闭、获取点击事件的详细信息或执行安全的GUI辅助操作。
- **前置概念查询**：
    * [查看](./JavaDocs/gui/interfaces/ClickAction-JavaDoc.md) (理解定义 **“做什么”** 的回调)
    * [查看](./JavaDocs/gui/interfaces/SlotPredicate-JavaDoc.md) (理解定义 **“在哪里生效”** 的条件)
- **核心逻辑查询 (事件分发)**：[查看](./JavaDocs/gui/GuiActionDispatcher-JavaDoc.md) (用于注册 `ClickAction` 与 `SlotPredicate` 的组合)
- **关联查询 (会话管理)**：[查看](./JavaDocs/gui/GUISessionManager-JavaDoc.md) (用于打开、关闭、验证玩家的GUI会话)
- **关联查询 (数据载体)**：[查看](./JavaDocs/gui/ClickContext-JavaDoc.md) (用于在回调中获取点击类型、玩家等上下文信息)
- **关联查询 (辅助工具)**：[查看](./JavaDocs/gui/GuiManager-JavaDoc.md) (用于安全播放音效、清理光标、检查危险点击等)

### 数据库操作 (SQLite)
- **功能描述**：连接管理 SQLite 数据库，初始化表结构，执行增删改查（CRUD）、事务处理。  
- **查询文档**：[查看](./JavaDocs/database)
