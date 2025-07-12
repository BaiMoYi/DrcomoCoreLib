### `PlayerPointsEconomyProvider.java`

**1. 概述 (Overview)**

  * **完整路径:** `cn.drcomo.corelib.hook.economy.provider.PlayerPointsEconomyProvider`
  * **核心职责:** 作为 `EconomyProvider` 接口的具体实现，专门用于对接 `PlayerPoints` 插件。它将所有标准的经济操作（查询余额、存/取款等）翻译成对 `PlayerPoints` API 的调用。

**2. 如何实例化 (Initialization)**

  * **核心思想:** 在确认服务器上已安装并启用了 `PlayerPoints` 插件后，实例化此类。**务必通过依赖注入传入已初始化的 `DebugUtil`**，避免在类内部创建新实例。
  * **构造函数:** `public PlayerPointsEconomyProvider(Plugin plugin, DebugUtil logger)`
  * **代码示例:**
    ```java
    // 在你的子插件 onEnable() 方法中:
    Plugin myPlugin = this;
    DebugUtil myLogger = new DebugUtil(myPlugin, DebugUtil.LogLevel.INFO);
    EconomyProvider myEconomyProvider = null;

    // 检查 PlayerPoints 是否可用
    if (Bukkit.getPluginManager().isPluginEnabled("PlayerPoints")) {
        myLogger.info("检测到 PlayerPoints 插件，正在尝试挂钩...");
        myEconomyProvider = new PlayerPointsEconomyProvider(
            myPlugin,
            myLogger // 直接注入已有的 DebugUtil 实例
        );
        if (myEconomyProvider.isEnabled()) {
            myLogger.info("成功连接到 PlayerPoints API: " + myEconomyProvider.getName());
        } else {
            myLogger.warn("无法连接到 PlayerPoints API，经济功能将不可用。");
        }
    } else {
        myLogger.warn("未安装 PlayerPoints 插件。");
    }
    ```

**3. 公共API方法 (Public API Methods)**

  * #### `isEnabled()`

      * **返回类型:** `boolean`
      * **功能描述:** 检查 `PlayerPoints` 插件是否可用并且其 API 是否已成功连接。
      * **参数说明:** 无。

  * #### `getName()`

      * **返回类型:** `String`
      * **功能描述:** 返回所挂钩的经济插件名称，即 "PlayerPoints"。
      * **参数说明:** 无。

  * #### `hasBalance(Player player, double amount)`

      * **返回类型:** `boolean`
      * **功能描述:** 调用 `PlayerPoints` API 检查玩家的点券是否不少于指定数量。
      * **参数说明:**
          * `player` (`Player`): 目标玩家。
          * `amount` (`double`): 需要检查的点券数量。

  * #### `withdraw(Player player, double amount)`

      * **返回类型:** `EconomyResponse`
      * **功能描述:** 从玩家账户中扣除指定数量的点券。如果成功，返回成功的 `EconomyResponse`；如果余额不足或服务不可用，则返回失败的响应。
      * **参数说明:**
          * `player` (`Player`): 目标玩家。
          * `amount` (`double`): 要扣除的点券数量（会被向上取整为整数）。

  * #### `deposit(Player player, double amount)`

      * **返回类型:** `EconomyResponse`
      * **功能描述:** 向玩家账户中增加指定数量的点券。
      * **参数说明:**
          * `player` (`Player`): 目标玩家。
          * `amount` (`double`): 要增加的点券数量（会被向上取整为整数）。

  * #### `getBalance(Player player)`

      * **返回类型:** `double`
      * **功能描述:** 获取玩家当前的点券余额。
      * **参数说明:**
          * `player` (`Player`): 目标玩家。

  * #### `format(double amount)`

      * **返回类型:** `String`
      * **功能描述:** 将点券数量格式化为字符串，例如 "100 点数"。
      * **参数说明:**
          * `amount` (`double`): 要格式化的点券数量。

