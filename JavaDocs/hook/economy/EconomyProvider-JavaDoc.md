### `EconomyProvider.java`

**1. 概述 (Overview)**

  * **完整路径:** `cn.drcomo.corelib.hook.economy.EconomyProvider`
  * **核心职责:** 定义了一个标准的经济服务提供者接口。它的目的是抽象不同经济插件（如 Vault, PlayerPoints）的操作，为上层应用提供一个统一的、与具体经济插件无关的交互方式。任何希望接入 DrcomoCoreLib 经济体系的插件，都需要提供此接口的实现。

**2. 如何实例化 (Initialization)**

  * **核心思想:** 这是一个接口 (`interface`)，不能被直接实例化。开发者应该实例化其具体的实现类，如 `VaultEconomyProvider` 或 `PlayerPointsEconomyProvider`，或者创建自己的实现类。
  * **构造函数:** 无 (接口没有构造函数)。
  * **代码示例:**
    ```java
    // 这是一个接口，不能直接实例化。
    // 你应该根据服务器上安装的经济插件，来选择并实例化一个具体的实现。

    // 示例：优先使用 Vault，如果 Vault 不可用，则尝试 PlayerPoints
    EconomyProvider economyProvider = null;

    Plugin myPlugin = this;
    DebugUtil myLogger = new DebugUtil(myPlugin, DebugUtil.LogLevel.INFO);

    if (Bukkit.getPluginManager().isPluginEnabled("Vault")) {
        // 实例化 Vault 的经济提供者
        economyProvider = new VaultEconomyProvider(myPlugin, myLogger.getLevel());
        myLogger.info("已挂钩到 Vault 经济服务。");
    } else if (Bukkit.getPluginManager().isPluginEnabled("PlayerPoints")) {
        // 实例化 PlayerPoints 的经济提供者
        economyProvider = new PlayerPointsEconomyProvider(myPlugin, myLogger.getLevel());
        myLogger.info("已挂钩到 PlayerPoints 经济服务。");
    } else {
        myLogger.warn("未找到受支持的经济插件，经济功能将不可用。");
    }

    // 后续就可以通过 economyProvider 接口来操作经济，无需关心具体实现
    if (economyProvider != null && economyProvider.isEnabled()) {
        // ...
    }
    ```

**3. 公共API方法 (Public API Methods)**

  * #### `isEnabled()`

      * **返回类型:** `boolean`
      * **功能描述:** 判断当前经济服务是否已成功初始化且可用。在调用任何经济操作前，都应先检查此项。
      * **参数说明:** 无。

  * #### `getName()`

      * **返回类型:** `String`
      * **功能描述:** 获取底层经济插件的名称，例如 "Vault" 或 "PlayerPoints"。
      * **参数说明:** 无。

  * #### `hasBalance(Player player, double amount)`

      * **返回类型:** `boolean`
      * **功能描述:** 检查指定玩家的账户余额是否足够支付指定金额。
      * **参数说明:**
          * `player` (`Player`): 要检查的玩家。
          * `amount` (`double`): 要检查的金额。

  * #### `withdraw(Player player, double amount)`

      * **返回类型:** `EconomyResponse`
      * **功能描述:** 从指定玩家的账户中扣除指定数额的货币。
      * **参数说明:**
          * `player` (`Player`): 要扣款的玩家。
          * `amount` (`double`): 要扣除的金额。

  * #### `deposit(Player player, double amount)`

      * **返回类型:** `EconomyResponse`
      * **功能描述:** 向指定玩家的账户中存入指定数额的货币。
      * **参数说明:**
          * `player` (`Player`): 要存款的玩家。
          * `amount` (`double`): 要存入的金额。

  * #### `getBalance(Player player)`

      * **返回类型:** `double`
      * **功能描述:** 获取指定玩家当前的账户余额。如果经济服务不可用，应返回 0。
      * **参数说明:**
          * `player` (`Player`): 要查询的玩家。

  * #### `format(double amount)`

      * **返回类型:** `String`
      * **功能描述:** 将一个数字金额格式化为对用户友好的显示字符串，通常会包含货币单位或特定的格式，例如 "$1,234.56" 或 "500 点券"。
      * **参数说明:**
          * `amount` (`double`): 要格式化的金额。

