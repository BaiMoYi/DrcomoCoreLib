### `NBTUtil.java`

**1. 概述 (Overview)**

  * **完整路径:** `cn.drcomo.corelib.nbt.NBTUtil`
  * **核心职责:** 一个基于 `tr7zw.item-nbt-api` 的通用 NBT 工具类，它封装了对 `ItemStack` NBT 数据进行增、删、改、查的各种操作。通过结合 `NbtKeyHandler` 策略，它能够以一种安全、带命名空间的方式来管理插件私有的物品数据，并提供了对多种 NBT 数据类型的深拷贝支持。

**2. 如何实例化 (Initialization)**

  * **核心思想:** `NBTUtil` 的设计强制要求开发者提供一个 `NbtKeyHandler` 实现，以确保所有 NBT 操作都是带前缀、避免冲突的。实例化时，需要传入你为插件定制的 `NbtKeyHandler` 和一个 `DebugUtil` 实例用于日志记录。
  * **构造函数:** `public NBTUtil(NbtKeyHandler handler, DebugUtil logger)`
  * **代码示例:**
    ```java
    // 在你的子插件 onEnable() 方法中:
    Plugin myPlugin = this;
    DebugUtil myLogger = new DebugUtil(myPlugin, DebugUtil.LogLevel.INFO);

    // 1. 先创建 NbtKeyHandler 的实现
    final String nbtPrefix = myPlugin.getName().toLowerCase() + "_"; // 定义一个对你的插件唯一的 NBT 前缀
    NbtKeyHandler myNbtKeyHandler = new NbtKeyHandler() {
        @Override
        public boolean isValidKey(String fullKey) { 
            return fullKey != null && fullKey.startsWith(nbtPrefix); 
        }

        @Override
        public String addPrefix(String key) { 
            return nbtPrefix + key; 
        }

        @Override
        public String removePrefix(String fullKey) { 
            return isValidKey(fullKey) ? fullKey.substring(nbtPrefix.length()) : fullKey; 
        }
    };

    // 2. 用创建好的 handler 和 logger 初始化 NBTUtil
    NBTUtil nbtUtil = new NBTUtil(myNbtKeyHandler, myLogger);

    myLogger.info("NBT 工具已初始化。");

    // 现在你可以使用 nbtUtil 来安全地操作物品的 NBT 了
    // ItemStack item = new ItemStack(Material.DIAMOND_SWORD);
    // ItemStack modifiedItem = nbtUtil.setString(item, "owner_uuid", player.getUniqueId().toString());
    // player.getInventory().addItem(modifiedItem);
    ```

**3. 公共API方法 (Public API Methods)**

  * #### `getInt(ItemStack item, String key, int def)`

      * **返回类型:** `int`
      * **功能描述:** 从物品的 NBT 中安全地读取一个整数值。
      * **参数说明:**
          * `item` (`ItemStack`): 要读取的物品。
          * `key` (`String`): NBT 键名（**无需**手动加前缀，方法内部会通过 `NbtKeyHandler` 处理）。
          * `def` (`int`): 如果物品为 `null`、键不存在或发生任何读取异常时，返回的默认值。
      * **使用示例:**
        ```java
        int level = nbtUtil.getInt(item, "level", 1);
        ```

  * #### `setInt(ItemStack item, String key, int val)`

      * **返回类型:** `ItemStack`
      * **功能描述:** 向物品的 NBT 中写入一个整数值。**重要：** 此方法遵循 NBT-API 的不可变性设计，它会返回一个包含修改后 NBT 的**全新 `ItemStack` 克隆**，原始的 `item` 对象不会被改变。你必须用此方法的返回值来更新你的物品变量。
      * **参数说明:**
          * `item` (`ItemStack`): 要修改的物品。
          * `key` (`String`): NBT 键名（无需加前缀）。
          * `val` (`int`): 要写入的整数值。
      * **使用示例:**
        ```java
        ItemStack sword = player.getInventory().getItemInMainHand();
        // 错误的做法: nbtUtil.setInt(sword, "damage", 100); (这行代码的效果被丢弃了)
        // 正确的做法:
        sword = nbtUtil.setInt(sword, "damage", 100);
        player.getInventory().setItemInMainHand(sword);
        ```

  * #### `getString(ItemStack item, String key, String def)`

      * **返回类型:** `String`
      * **功能描述:** 从物品的 NBT 中安全地读取一个字符串值。
      * **参数说明:**
          * `item` (`ItemStack`): 目标物品。
          * `key` (`String`): NBT 键名（无需加前缀）。
          * `def` (`String`): 默认值。
      * **使用示例:**
        ```java
        String crafterName = nbtUtil.getString(item, "crafter", "未知工匠");
        ```

  * #### `setString(ItemStack item, String key, String val)`

      * **返回类型:** `ItemStack`
      * **功能描述:** 向物品的 NBT 中写入一个字符串值。同样返回一个新的克隆物品。
      * **参数说明:**
          * `item` (`ItemStack`): 目标物品。
          * `key` (`String`): NBT 键名（无需加前缀）。
          * `val` (`String`): 要写入的字符串值。

  * #### `hasKey(ItemStack item, String key)`

      * **返回类型:** `boolean`
      * **功能描述:** 检查物品的 NBT 中是否存在指定的键（方法内部已自动处理前缀）。
      * **参数说明:**
          * `item` (`ItemStack`): 目标物品。
          * `key` (`String`): NBT 键名（无需加前缀）。

  * #### `removeKey(ItemStack item, String key)`

      * **返回类型:** `ItemStack`
      * **功能描述:** 从物品的 NBT 中移除一个键值对。同样返回一个新的克隆物品。
      * **参数说明:**
          * `item` (`ItemStack`): 目标物品。
          * `key` (`String`): 要移除的 NBT 键名（无需加前缀）。

  * #### `scanPluginNBTKeys(ItemStack item)`

      * **返回类型:** `Set<String>`
      * **功能描述:** 扫描一个物品的所有 NBT 键，并返回一个只包含那些符合本插件前缀（由 `NbtKeyHandler` 定义）的、**完整的键名**（即包含前缀）的集合。
      * **参数说明:**
          * `item` (`ItemStack`): 要扫描的物品。

  * #### `hasPluginKey(ItemStack item, String customKey)`

      * **返回类型:** `boolean`
      * **功能描述:** `hasKey` 的别名，语义上更清晰地表示正在检查一个属于本插件的自定义键。
      * **参数说明:**
          * `item` (`ItemStack`): 目标物品。
          * `customKey` (`String`): 自定义键名（不含前缀）。

  * #### `checkPluginKeys(ItemStack item, Set<String> customKeys)`

      * **返回类型:** `Set<String>`
      * **功能描述:** 检查一个自定义键集合中，哪些键实际存在于物品的 NBT 中。返回的是**存在**的、**带前缀**的完整键名集合。
      * **参数说明:**
          * `item` (`ItemStack`): 目标物品。
          * `customKeys` (`Set<String>`): 一个包含待检查的、不带前缀的自定义键的集合。

  * #### `batchPreserve(ItemStack src, ItemStack dst, Set<String> customKeys)`

      * **返回类型:** `ItemStack`
      * **功能描述:** 批量地将源物品（`src`）中指定的 NBT 数据，深拷贝到目标物品（`dst`）中。这在物品合成、修复或升级后需要保留特定自定义数据（如绑定信息、特殊属性）的场景中非常有用。它返回修改后的 `dst` 物品的克隆。
      * **参数说明:**
          * `src` (`ItemStack`): NBT 数据的来源物品。
          * `dst` (`ItemStack`): 要接收 NBT 数据的目标物品。
          * `customKeys` (`Set<String>`): 一个包含所有需要保留的、不带前缀的自定义键的集合。
      * **使用示例:**
        ```java
        // 在铁砧合成事件中，保留神器的 "soulbound" 标签
        ItemStack originalItem = event.getInventory().getItem(0);
        ItemStack resultItem = event.getResult();
        if (nbtUtil.hasKey(originalItem, "soulbound")) {
            Set<String> keysToKeep = new HashSet<>();
            keysToKeep.add("soulbound");
            resultItem = nbtUtil.batchPreserve(originalItem, resultItem, keysToKeep);
            event.setResult(resultItem);
        }
        ```

  * #### `preserveSingle(ItemStack src, ItemStack dst, String customKey)`

      * **返回类型:** `ItemStack`
      * **功能描述:** `batchPreserve` 的便捷版本，用于仅保留单个 NBT 键。
      * **参数说明:**
          * `src` (`ItemStack`): 源物品。
          * `dst` (`ItemStack`): 目标物品。
          * `customKey` (`String`): 要保留的单个自定义键（不带前缀）。

  * #### `cleanupInvalidKeys(Set<String> keys)`

      * **返回类型:** `Set<String>`
      * **功能描述:** 这是一个辅助工具方法，用于从一个给定的键集合中，筛选出所有不符合本插件前缀策略的"无效"键。
      * **参数说明:**
          * `keys` (`Set<String>`): 一个包含完整键名（带前缀）的集合。

  * #### `addPrefix(String customKey)`

      * **返回类型:** `String`
      * **功能描述:** 直接调用 `NbtKeyHandler` 的 `addPrefix` 方法，为自定义键添加插件前缀。
      * **参数说明:**
          * `customKey` (`String`): 自定义键名（不含前缀）。

  * #### `removePrefix(String fullKey)`

      * **返回类型:** `String`
      * **功能描述:** 直接调用 `NbtKeyHandler` 的 `removePrefix` 方法，从完整键中移除插件前缀。
      * **参数说明:**
          * `fullKey` (`String`): 完整键名（含前缀）。

  * #### `toRawString(ItemStack item)`

      * **返回类型:** `String`
      * **功能描述:** 获取目标物品完整 NBT 数据的字符串表示（SNBT 格式），便于日志输出和人工比对。
      * **参数说明:**
          * `item` (`ItemStack`): 目标物品。
      * **返回格式说明:**
          * 返回值为标准 SNBT（Stringified Named Binary Tag）格式，类似 JSON，但字段顺序、类型与 Minecraft NBT 规范一致。
          * 例如：`{id:"minecraft:diamond_sword",Count:1b,tag:{display:{Name:"\"神器\""},CustomModelData:1234}}`
          * 若物品无 NBT 或异常，返回 "{}"。
      * **典型用途:**
          * 适合直接打印日志、人工比对、与 NBTExplorer/NBT Exporter 等工具输出对照。

  * #### `getRawCompound(ItemStack item)`

      * **返回类型:** `Object` (运行时实际为 `ReadWriteNBT`)
      * **功能描述:** 获取物品的原始 NBT Compound 对象，以供高级自定义读取/写入或序列化操作。
      * **参数说明:**
          * `item` (`ItemStack`): 目标物品。
      * **返回格式说明:**
          * 返回值为 NBT-API 的 `ReadWriteNBT` 实例，可直接调用其 API 进行 NBT 结构的遍历、读取、写入、序列化等操作。
          * 例如：`compound.getString("id")`、`compound.toString()`（同样为 SNBT 格式）。
          * 若物品无 NBT 或异常，返回 `null`。
      * **典型用途:**
          * 适合插件开发者进行复杂 NBT 结构处理、批量操作、或自定义导出。

  * #### `fromRawString(String nbtString)`

      * **返回类型:** `ItemStack`
      * **功能描述:** 将 SNBT/JSON 字符串反序列化为 ItemStack；若解析失败抛出 `ParseException`。
      * **返回格式说明:** 解析遵循 NBT-API `NBT.parseNBT` 规范，支持标准 SNBT，如 `{id:"minecraft:stone",Count:1b}`。

  * #### `getAllNbt(ItemStack item)` / `setAllNbt(ItemStack item, Map<String,Object> nbtMap)`

      * **返回类型:** `Map<String,Object>` / `ItemStack`
      * **功能描述:** 递归读取或批量写入物品全部 NBT 数据；Map 支持嵌套 `Map` 与 `List` 结构。
      * **返回格式说明:**
          * Map 的键为原始 NBT 键名，值类型映射 Java 基础类型、`byte[]/int[]/long[]`、`Map`、`List`（List 支持 String/Number/Compound）。

  * #### `getRaw(ItemStack, String)` / `setRaw(ItemStack,String,Object)`

      * **返回类型:** `Object` / `ItemStack`
      * **功能描述:** 直接读取/写入原生 NBT 键，不自动添加前缀；支持全部基础类型及数组。

  * #### `toPrettyString(ItemStack item)`

      * **返回类型:** `String`
      * **功能描述:** 输出带缩进的美化 SNBT，方便人工阅读。

  * #### `exportPluginNbt(ItemStack)` / `importPluginNbt(ItemStack,String)`

      * **返回类型:** `String` / `ItemStack`
      * **功能描述:** 仅导出/导入符合当前 `NbtKeyHandler` 前缀的自定义 NBT 区域，便于数据迁移与备份。
      * **返回格式说明:** 同 SNBT；导入时仅处理带前缀键。
