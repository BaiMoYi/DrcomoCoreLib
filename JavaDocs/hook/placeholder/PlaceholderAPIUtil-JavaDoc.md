### `PlaceholderAPIUtil.java`

**1. 概述 (Overview)**

  * **完整路径:** `cn.drcomo.corelib.hook.placeholder.PlaceholderAPIUtil`
  * **核心职责:** 一个集中式的 `PlaceholderAPI` (PAPI) 占位符管理器。它极大地简化了为插件注册自定义 PAPI 占位符的流程，并提供了参数解析和递归解析的实用工具，使开发者能以一种结构化、易于管理的方式来提供动态文本。

**2. 如何实例化 (Initialization)**

  * **核心思想:** 在插件的 `onEnable()` 方法中为你的插件创建一个全局唯一的 `PlaceholderAPIUtil` 实例。实例化时需要提供一个独特的“标识符”（identifier），这个标识符将作为你所有自定义占位符的前缀。
  * **构造函数:** `public PlaceholderAPIUtil(Plugin pluginInstance, String identifier)`
  * **代码示例:**
    ```java
    // 在你的子插件 onEnable() 方法中:
    Plugin myPlugin = this;
    PlaceholderAPIUtil papiUtil = null;

    if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
        // 建议使用插件名的小写形式作为标识符
        String myIdentifier = myPlugin.getName().toLowerCase();

        papiUtil = new PlaceholderAPIUtil(myPlugin, myIdentifier);

        // --- 实例化后，立即注册你的占位符 ---

        // 示例1：一个简单的无参数占位符 %myplugin_version%
        papiUtil.register("version", (player, rawArgs) -> {
            return myPlugin.getDescription().getVersion();
        });

        // 示例2：一个带参数的占位符 %myplugin_location_x% 或 %myplugin_location_y%
        papiUtil.register("location", (player, rawArgs) -> {
            if (player == null) return "N/A";
            switch (rawArgs.toLowerCase()) {
                case "x": return String.valueOf(player.getLocation().getBlockX());
                case "y": return String.valueOf(player.getLocation().getBlockY());
                case "z": return String.valueOf(player.getLocation().getBlockZ());
                default: return "无效坐标";
            }
        });

        // 示例3：一个使用 splitArgs 的多参数占位符 %myplugin_greet_Dragon_你好%
        papiUtil.register("greet", (player, rawArgs) -> {
            String[] args = PlaceholderAPIUtil.splitArgs(rawArgs); // 使用静态方法拆分
            if (args.length < 2) return "参数不足";
            // args[0] 是 "Dragon", args[1] 是 "你好"
            return args[1] + ", " + args[0] + "!"; // 输出 "你好, Dragon!"
        });

    } else {
        // 处理 PAPI 未安装的情况
    }
    ```

**3. 公共API方法 (Public API Methods)**

  * #### `register(String key, BiFunction<Player, String, String> resolver)`

      * **返回类型:** `void`
      * **功能描述:** 注册一个自定义占位符。这是该类的核心方法。
      * **参数说明:**
          * `key` (`String`): 占位符的键（`key`）。例如，对于占位符 `%myid_player_health%`，此处的 `key` 就是 `"player_health"`。
          * `resolver` (`BiFunction<Player, String, String>`): 一个处理函数，它接收一个 `Player` 对象和一个 `String` 类型的原始参数 `rawArgs`，并返回最终要显示的字符串。`rawArgs` 是占位符中 `key` 之后的所有内容。

* #### `registerSpecial(String key, Function<Player,String> resolver)`

    * **返回类型:** `void`  
    * **功能描述:** 无参数占位符的快捷注册方法。等同于  
      ```java
      register(key, (player, rawArgs) -> resolver.apply(player));
      ```  
      适用于只依赖玩家上下文，不需要额外参数的占位符。  
    * **参数说明:**
        * `key` (`String`): 占位符主键（不含 `%`），例如 `"prefix"` 对应 `%identifier_prefix%`  
        * `resolver` (`Function<Player,String>`): 接收 `Player` 返回要显示的字符串  
    * **使用示例:**
      ```java
      // 注册一个 %myid_prefix% 占位符，直接返回玩家前缀
      papiUtil.registerSpecial("prefix", player -> getPrefix(player));
      ```
      
* #### `parse(Player player, String text, Map<String,String> customPlaceholders)`

    * **返回类型:** `String`  
    * **功能描述:** 在进行 PAPI 占位符解析前，先对所有 `{key}` 形式的自定义占位符做一次批量替换，然后再递归调用原有 `parse(Player,String)` 解析所有 `%identifier_key_args%`。  
    * **参数说明:**
        * `player` (`Player`): 占位符的上下文玩家，可为 `null`  
        * `text` (`String`): 含有 `{}` 和/或 `%...%` 的待解析文本  
        * `customPlaceholders` (`Map<String,String>`): 自定义占位符映射，键对应 `{key}` 中的 `key`，值为替换内容  
    * **使用示例:**
      ```java
      Map<String,String> map = Map.of(
          "username", player.getName(),
          "score", String.valueOf(getScore(player))
      );
      String tpl = "玩家：{username}，分数：{score}，全局称号：%myid_title%";
      String result = papiUtil.parse(player, tpl, map);
      player.sendMessage(result);
      ```

  * #### `parse(Player player, String text)`

      * **返回类型:** `String`
      * **功能描述:** 递归解析一个包含PAPI占位符的文本，直到文本中不再包含任何占位符为止。它内部调用 `PlaceholderAPI.setPlaceholders`。推荐使用此方法来解析文本，以确保嵌套的占位符（即一个占位符的返回值是另一个占位符）也能被正确解析。
      * **参数说明:**
          * `player` (`Player`): 占位符的上下文玩家，可为 `null`。
          * `text` (`String`): 包含占位符的待解析文本。
      * **使用示例:**
        ```java
        String messageTemplate = "欢迎, %player_name%！你的位置是: %myplugin_location_x%, %myplugin_location_y%！";
        String parsedMessage = papiUtil.parse(player, messageTemplate);
        player.sendMessage(parsedMessage);
        ```

  * #### `splitArgs(String rawArgs)`

      * **返回类型:** `String[]`
      * **功能描述:** 一个静态的辅助方法，用于将占位符的原始参数字符串（`rawArgs`）按 `_` (下划线) 拆分成一个字符串数组。这对于处理包含多个参数的复杂占位符非常方便。
      * **参数说明:**
          * `rawArgs` (`String`): 从解析器（`resolver`）中获取的原始参数字符串。
      * **使用示例:**
        ```java
        // 在 register 方法的 resolver 内部使用：
        // 占位符: %myid_sum_10_20%
        papiUtil.register("sum", (player, rawArgs) -> {
            String[] args = PlaceholderAPIUtil.splitArgs(rawArgs); // -> ["10", "20"]
            if (args.length < 2) return "Error";
            int num1 = Integer.parseInt(args[0]);
            int num2 = Integer.parseInt(args[1]);
            return String.valueOf(num1 + num2); // 返回 "30"
        });
        ```

