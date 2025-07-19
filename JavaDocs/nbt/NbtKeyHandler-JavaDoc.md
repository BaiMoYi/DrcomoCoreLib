### `NbtKeyHandler.java`

**1. 概述 (Overview)**

  * **完整路径:** `cn.drcomo.corelib.nbt.NbtKeyHandler`
  * **核心职责:** 定义了一个 NBT 键（key）的前缀策略接口。它的作用是规范化插件在向物品的 NBT（Named Binary Tag）中读写数据时，如何管理自己的键名，以避免与其他插件发生冲突。

**2. 如何实例化 (Initialization)**

  * **核心思想:** 这是一个接口，不能直接实例化。开发者需要在使用 `NBTUtil` 之前，创建这个接口的一个匿名内部类或具名实现类。在实现中，你需要定义一个对你的插件而言独一无二的前缀。
  * **构造函数:** 无。
  * **代码示例:**
    ```java
    // 在你的子插件中，创建一个 NbtKeyHandler 的实现。
    // 通常，你可以在插件主类或一个专门的工具类中创建它。

    // 简单的匿名内部类实现：
    final String nbtPrefix = "myawesomeplugin_"; // 定义一个独特的、最好是插件名的前缀

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
            if (isValidKey(fullKey)) {
                return fullKey.substring(nbtPrefix.length());
            }
            return fullKey;
        }
    };

    // 然后，你可以用这个 handler 去初始化 NBTUtil
    // DebugUtil logger = ...;
    // NBTUtil nbtUtil = new NBTUtil(myNbtKeyHandler, logger);
    ```

**3. 公共API方法 (Public API Methods)**

  * #### `isValidKey(String fullKey)`

      * **返回类型:** `boolean`
      * **功能描述:** 判断一个完整的 NBT 键名是否属于本插件（即是否拥有正确的自定义前缀）。
      * **参数说明:**
          * `fullKey` (`String`): 从物品 NBT 中读取到的完整键名。

  * #### `addPrefix(String key)`

      * **返回类型:** `String`
      * **功能描述:** 为一个自定义的、不带前缀的键名，添加上插件的专属前缀，生成一个完整的、可以安全写入 NBT 的键名。
      * **参数说明:**
          * `key` (`String`): 用户自定义的键名，例如 `"item_level"`。

  * #### `removePrefix(String fullKey)`

      * **返回类型:** `String`
      * **功能描述:** 从一个完整的、带有前缀的键名中，移除前缀，返回用户自定义的那部分。
      * **参数说明:**
          * `fullKey` (`String`): 带有前缀的完整键名。

