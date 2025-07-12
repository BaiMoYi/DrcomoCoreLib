### `DrcomoCoreLib.java`

**1. 概述 (Overview)**

  * **完整路径:** `cn.drcomo.corelib.DrcomoCoreLib`
  * **核心职责:** 这是 DrcomoCoreLib 插件库的主类，继承自 `org.bukkit.plugin.java.JavaPlugin`。它主要负责在服务器启动和关闭时，加载和卸载本核心库，并向控制台输出必要的信息。它本身不提供任何业务功能，其主要价值在于通过源码中的 `showUsageExample()` 方法，为开发者提供一个清晰、完整的库使用范例。

**2. 如何实例化 (Initialization)**

  * **核心思想:** 该类由 Bukkit/Spigot 服务器在插件加载时自动实例化，开发者**不应也无需**手动创建其实例。开发者需要关注的是如何在自己的插件中，正确地实例化和使用本库提供的其他工具类。
  * **构造函数:** `public DrcomoCoreLib()`
  * **代码示例:**
    ```java
    // 该类由服务器自动管理，开发者不应手动实例化。
    // 正确的做法是在你的子插件中，参考其内部的 showUsageExample() 方法来初始化其他工具。
    // 以下是如何在你的插件中获取对 DrcomoCoreLib 实例的引用（虽然通常没必要）：
    DrcomoCoreLib coreLib = (DrcomoCoreLib) Bukkit.getPluginManager().getPlugin("DrcomoCoreLib");

    if (coreLib != null) {
        // 你可以获取到实例，但通常不会直接与它交互。
        // 它的价值在于作为一个加载入口和提供用法示例。
        getLogger().info("成功获取到 DrcomoCoreLib 实例。");
    }
    ```

**3. 公共API方法 (Public API Methods)**

  * #### `onEnable()`

      * **返回类型:** `void`
      * **功能描述:** 插件启用时由服务器调用的方法。它会初始化一个供库内部使用的 `DebugUtil`，并打印一条详细的启动信息，向使用者说明此库的性质和基本用法。
      * **参数说明:** 无。

  * #### `onDisable()`

      * **返回类型:** `void`
      * **功能描述:** 插件禁用时由服务器调用的方法。它会打印一条卸载信息。
      * **参数说明:** 无。

