### `VaultEconomyProvider.java`

**1. 概述 (Overview)**

  * **完整路径:** `cn.drcomo.corelib.hook.economy.provider.VaultEconomyProvider`
  * **核心职责:** 作为 `EconomyProvider` 接口的具体实现，用于桥接 `Vault` 插件。它使得上层应用可以通过一套标准接口与任何被 `Vault` 支持的经济插件（如 EssentialsX, CMI 等）进行交互。

**2. 如何实例化 (Initialization)**

  * **核心思想:** 在确认服务器上 `Vault` 插件及其经济服务均可用时，实例化此类。**务必通过依赖注入传入已初始化的 `DebugUtil`**，避免在类内部创建新实例。
  * **构造函数:** `public VaultEconomyProvider(Plugin plugin, DebugUtil logger)`
  * **代码示例:**
    ```java
    // 在你的子插件 onEnable() 方法中:
    Plugin myPlugin = this;
    DebugUtil myLogger = new DebugUtil(myPlugin, DebugUtil.LogLevel.INFO);
    EconomyProvider myEconomyProvider = null;

    // 检查 Vault 是否可用
    if (Bukkit.getPluginManager().isPluginEnabled("Vault")) {
        myLogger.info("检测到 Vault 插件，正在尝试挂钩经济服务...");
        myEconomyProvider = new VaultEconomyProvider(
            myPlugin,
            myLogger
        );
        if (myEconomyProvider.isEnabled()) {
            myLogger.info("成功连接到 Vault 经济服务: " + myEconomyProvider.getName());
        } else {
            myLogger.warn("Vault 虽已安装，但未找到注册的 Economy 服务。请确保安装了如 EssentialsX 等经济核心插件。");
        }
    } else {
        myLogger.warn("未安装 Vault 插件。");
    }
    ```

**3. 公共API方法 (Public API Methods)**

  * #### `isEnabled()`

      * **返回类型:** `boolean`
      * **功能描述:** 检查 `Vault` 插件是否可用并且是否有一个经济服务提供者（Economy Service Provider）已经向 `Vault` 注册。
      * **参数说明:** 无。

  * #### `getName()`

      * **返回类型:** `String`
      * **功能描述:** 返回由 `Vault` 挂钩的底层经济插件的名称。
      * **参数说明:** 无。

  * #### `hasBalance(Player player, double amount)`

      * **返回类型:** `boolean`
      * **功能描述:** 通过 `Vault` API 检查玩家的余额是否足够。
      * **参数说明:**
          * `player` (`Player`): 目标玩家。
          * `amount` (`double`): 需要检查的金额。

  * #### `withdraw(Player player, double amount)`

      * **返回类型:** `EconomyResponse`
      * **功能描述:** 通过 `Vault` 从玩家账户扣款，并将 `Vault` 的返回结果包装成 `EconomyResponse`。
      * **参数说明:**
          * `player` (`Player`): 目标玩家。
          * `amount` (`double`): 要扣除的金额。

  * #### `deposit(Player player, double amount)`

      * **返回类型:** `EconomyResponse`
      * **功能描述:** 通过 `Vault` 向玩家账户存款，并将 `Vault` 的返回结果包装成 `EconomyResponse`。
      * **参数说明:**
          * `player` (`Player`): 目标玩家。
          * `amount` (`double`): 要存入的金额。

  * #### `getBalance(Player player)`

      * **返回类型:** `double`
      * **功能描述:** 通过 `Vault` 获取玩家的当前余额。
      * **参数说明:**
          * `player` (`Player`): 目标玩家。

  * #### `format(double amount)`

      * **返回类型:** `String`
      * **功能描述:** 使用 `Vault` 底层经济插件提供的格式化方法，将金额转换为显示字符串。
      * **参数说明:**
          * `amount` (`double`): 要格式化的金额。

