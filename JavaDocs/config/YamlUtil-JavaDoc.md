### `YamlUtil.java`

**1. 概述 (Overview)**

  * **完整路径:** `cn.drcomo.corelib.config.YamlUtil`
  * **核心职责:** 一个强大的 YAML 配置文件管理工具类。它封装了 Bukkit 插件开发中与 `*.yml` 文件交互的几乎所有常见操作，包括：自动创建插件数据文件夹、从 JAR 中复制默认配置、加载/重载/保存配置、以及提供一系列带默认值的便捷读取方法，并集成了详细的日志记录。

**2. 如何实例化 (Initialization)**

  * **核心思想:** `YamlUtil` 被设计为每个插件持有一个实例。它需要 `Plugin` 实例来定位数据文件夹和资源，需要 `DebugUtil` 实例来输出操作日志。一旦实例化，它就可以管理该插件的所有 `.yml` 配置文件。
  * **构造函数:** `public YamlUtil(Plugin plugin, DebugUtil logger)`
  * **代码示例:**
    ```java
    // 在你的子插件 onEnable() 方法中:
    Plugin myPlugin = this;
    DebugUtil myLogger = new DebugUtil(myPlugin, DebugUtil.LogLevel.INFO);

    // 实例化 YamlUtil
    YamlUtil yamlUtil = new YamlUtil(myPlugin, myLogger);

    // --- 使用 YamlUtil ---

    // 1. 确保某个子目录存在
    yamlUtil.ensureDirectory("playerdata");

    // 2. 从 JAR 包的 resources 目录下复制默认配置
    // 假设你的 JAR 中有 /resources/config.yml 和 /resources/messages.yml
    // 这行代码会将它们复制到 /plugins/YourPlugin/ 目录下（如果文件尚不存在）
    myYamlUtil.copyDefaults("", ""); // 第一个参数是JAR内目录，第二个是插件数据文件夹内目录

    // 3. 加载一个配置文件到内存缓存
    yamlUtil.loadConfig("config"); // 加载 config.yml

    // 4. 读取配置项（如果不存在，会使用默认值写入并保存）
    boolean featureEnabled = yamlUtil.getBoolean("config", "features.auto-heal.enabled", true);
    String welcomeMessage = yamlUtil.getString("messages", "welcome", "&a欢迎您, %player_name%!");

    myLogger.info("YAML 配置工具已初始化，并加载了默认配置。");
    ```

**3. 公共API方法 (Public API Methods)**

  * #### `ensureFolderAndCopyDefaults(String resourceFolder, String relativePath)`

      * **返回类型:** `void`
      * **功能描述:** 若插件数据文件夹内某目标目录不存在，则创建该目录，并从 JAR 内指定资源文件夹复制其全部文件及层级结构到该目录，实现一次性批量初始化。（通常用于**首次使用该插件**）
      * **参数说明:**
          * `resourceFolder` (`String`): JAR 内资源文件夹路径，例如 `"templates"` 或 `"assets/lang"`。
          * `relativePath` (`String`): 数据文件夹内目标目录，相对插件根目录，空字符串表示根目录。

  * #### `ensureDirectory(String relativePath)`

      * **返回类型:** `void`
      * **功能描述:** 确保在插件的数据文件夹下，指定的相对路径目录存在。如果不存在，会自动创建。
      * **参数说明:**
          * `relativePath` (`String`): 相对于插件数据文件夹的路径，例如 `"data"` 或 `"logs/archive"`。

  * #### `copyDefaults(String resourceFolder, String relativePath)`

      * **返回类型:** `void`
      * **功能描述:** 从插件 JAR 内指定的资源文件夹（包含其子目录）复制所有 `.yml` 文件到插件数据文件夹中的目标目录，并保留原有目录层级。仅当目标文件不存在时才会执行复制。
      * **参数说明:**
          * `resourceFolder` (`String`): JAR 包内的源文件夹路径（如 `"config"`，空字符串表示 JAR 根目录）。
          * `relativePath` (`String`): 插件数据文件夹内的目标文件夹路径，可为空字符串表示插件根目录。

  * #### `copyYamlFile(String resourcePath, String relativePath)`

      * **返回类型:** `void`
      * **功能描述:** 复制插件 JAR 内指定的单个 `.yml` 文件到插件数据文件夹的目标目录，若目标文件已存在则跳过。
      * **参数说明:**
          * `resourcePath` (`String`): 资源文件在 JAR 内的完整路径，例如 `"config/example.yml"`。
          * `relativePath` (`String`): 插件数据文件夹内的目标目录，相对插件根目录，空字符串表示根目录。

  * #### `loadConfig(String fileName)`

      * **返回类型:** `void`
      * **功能描述:** 加载一个指定的 `.yml` 文件，并将其内容解析为一个 `YamlConfiguration` 对象，缓存在内存中。
      * **参数说明:**
          * `fileName` (`String`): 文件名，**不**包含 `.yml` 后缀。

  * #### `reloadConfig(String fileName)`

      * **返回类型:** `void`
      * **功能描述:** 从磁盘重新加载指定的配置文件，覆盖内存中的旧缓存。
      * **参数说明:**
          * `fileName` (`String`): 文件名（不含.yml）。

  * #### `saveConfig(String fileName)`

      * **返回类型:** `void`
      * **功能描述:** 将内存中缓存的指定配置对象，保存回磁盘上的 `.yml` 文件。
      * **参数说明:**
          * `fileName` (`String`): 文件名（不含.yml）。

  * #### `getConfig(String fileName)`

      * **返回类型:** `YamlConfiguration`
      * **功能描述:** 获取一个已加载的 `YamlConfiguration` 实例。如果该配置尚未被加载，此方法会先自动调用 `loadConfig`。
      * **参数说明:**
          * `fileName` (`String`): 文件名（不含.yml）。

  * #### `getString(String fileName, String path, String def)`

      * **返回类型:** `String`
      * **功能描述:** 从指定配置文件中读取一个字符串。如果路径不存在，会将 `def`（默认值）写入该路径，保存文件，然后返回 `def`。
      * **参数说明:**
          * `fileName` (`String`): 文件名。
          * `path` (`String`): YAML 中的路径，例如 `"database.host"`。
          * `def` (`String`): 默认值。

  * #### `getInt`, `getBoolean`, `getDouble`, `getLong`, `getStringList`

      * **功能描述:** 与 `getString` 类似，分别用于读取整数、布尔值、双精度浮点数、长整数和字符串列表，都支持写入默认值。

  * #### `setValue(String fileName, String path, Object value)`

      * **返回类型:** `void`
      * **功能描述:** 在指定的配置中设置一个路径的值，并立即保存到磁盘。
      * **参数说明:**
          * `fileName` (`String`): 文件名。
          * `path` (`String`): 路径。
          * `value` (`Object`): 要设置的值。

  * #### `contains(String fileName, String path)`

      * **返回类型:** `boolean`
      * **功能描述:** 检查指定配置中是否包含某个路径。
      * **参数说明:**
          * `fileName` (`String`): 文件名。
          * `path` (`String`): 路径。

  * #### `getKeys(String fileName, String path)`

      * **返回类型:** `Set<String>`
      * **功能描述:** 获取指定路径下的所有直接子节点的键（keys）。
      * **参数说明:**
          * `fileName` (`String`): 文件名。
          * `path` (`String`): 路径。

  * #### `getSection(String fileName, String path)`

      * **返回类型:** `ConfigurationSection`
      * **功能描述:** 获取指定路径对应的整个配置节（`ConfigurationSection`）。
      * **参数说明:**
          * `fileName` (`String`): 文件名。
          * `path` (`String`): 路径。
