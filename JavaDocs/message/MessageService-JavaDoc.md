### `MessageService.java`

**1. 概述 (Overview)**

  * **完整路径:** `cn.drcomo.corelib.message.MessageService`
  * **核心职责:** 一个功能全面的消息管理和发送服务。它整合了语言文件加载、多级占位符解析（包括自定义占位符、内部占位符和 PAPI 占位符）、颜色代码翻译以及多种消息发送渠道（聊天、ActionBar、Title），为插件提供了一个强大而灵活的本地化消息解决方案。

**2. 如何实例化 (Initialization)**

  * **核心思想:** 这是一个高度集成的服务类，它的实例化依赖于核心库的多个其他组件。你需要首先创建好 `DebugUtil`, `YamlUtil`, 和 `PlaceholderAPIUtil` 的实例，然后将它们与插件实例、语言文件路径和可选的消息键前缀一同传入构造函数。
  * **构造函数:** `public MessageService(Plugin plugin, DebugUtil logger, YamlUtil yamlUtil, PlaceholderAPIUtil placeholderUtil, String langConfigPath, String keyPrefix)`
  * **代码示例:**
    ```java
    // 在你的子插件 onEnable() 方法中:
    Plugin myPlugin = this;
    DebugUtil myLogger = new DebugUtil(myPlugin, DebugUtil.LogLevel.INFO);
    YamlUtil myYamlUtil = new YamlUtil(myPlugin, myLogger);
    PlaceholderAPIUtil myPapiUtil = new PlaceholderAPIUtil(myPlugin, "myplugin");
    // (在此处为 myPapiUtil 注册你的 PAPI 占位符...)

    // 定义语言文件的路径和消息键的前缀
    // 这会加载 plugins/YourPlugin/languages/zh_CN.yml 文件
    String languageFilePath = "languages/zh_CN";
    // 所有通过 get/parse 等方法获取的消息键都会自动加上此前缀
    String messageKeyPrefix = "messages.my_plugin."; 

    // 实例化 MessageService
    MessageService messageService = new MessageService(
        myPlugin,
        myLogger,
        myYamlUtil,
        myPapiUtil,
        languageFilePath,
        messageKeyPrefix
    );

    // 实例化后，你可以注册内部占位符 {key:args}
    // 示例：注册一个 {money} 占位符，显示玩家余额
    // 假设你已经有了一个 EconomyProvider 实例 eco
    messageService.registerInternalPlaceholder("money", (player, args) -> {
        if (player == null) return "N/A";
        return eco.format(eco.getBalance(player));
    });

    myLogger.info("消息服务已启动。");
    ```

**3. 公共API方法 (Public API Methods)**

  * #### `reloadLanguages()`

      * **返回类型:** `void`
      * **功能描述:** 重新从磁盘加载语言文件，并刷新内存中的消息缓存。当需要热重载配置文件时（例如，通过指令重载插件），应调用此方法以确保消息是最新的。
      * **参数说明:** 无。
      * **使用示例:**
        ```java
        // 在你的插件重载指令的执行逻辑中
        public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
            if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
                // ... 其他重载逻辑 ...
                messageService.reloadLanguages();
                sender.sendMessage("语言文件已重新加载。");
                return true;
            }
            return false;
        }
        ```

  * #### `getRaw(String key)`

      * **返回类型:** `String`
      * **功能描述:** 从缓存中获取指定键的原始、未经任何处理的消息字符串。如果设置了 `keyPrefix`，会自动应用。如果找不到键，会返回 `null` 并在后台记录一条警告。
      * **参数说明:**
          * `key` (`String`): 消息的键。
      * **使用示例:**
        ```java
        // 假设 messages.my_plugin.raw_key 在文件中定义为 "&c原始文本"
        String rawText = messageService.getRaw("raw_key");
        // rawText 的值将是 "&c原始文本"
        ```

  * #### `get(String key, Object... args)`

      * **返回类型:** `String`
      * **功能描述:** 获取原始消息，并立即使用 `String.format` 方法进行格式化。这适用于包含 `%s`, `%d` 等标准 Java 格式化占位符的消息。
      * **参数说明:**
          * `key` (`String`): 消息的键。
          * `args` (`Object...`): 传递给 `String.format` 的参数，用于替换消息中的 `%s`, `%d` 等。
      * **使用示例:**
        ```java
        // yml中: 'player_joined: "%s 加入了服务器，在线人数: %d"'
        String formattedMsg = messageService.get("player_joined", player.getName(), Bukkit.getOnlinePlayers().size());
        // formattedMsg -> "Steve 加入了服务器，在线人数: 15"
        ```

  * #### `parse(String key, Player player, Map<String, String> custom)`

      * **返回类型:** `String`
      * **功能描述:** 对指定键的消息执行完整的、多阶段的占位符解析流程。解析顺序为：1. 自定义占位符 (`%key%`) -\> 2. 内部占位符 (`{key:args}`) -\> 3. PAPI 占位符。这是最常用的消息获取和处理方法。
      * **参数说明:**
          * `key` (`String`): 消息的键。
          * `player` (`Player`): PAPI 和部分内部占位符所需的上下文玩家，可以为 `null`。
          * `custom` (`Map<String, String>`): 一个包含自定义占位符及其替换值的 Map。键是不带 `%` 的占位符名称。
      * **使用示例:**
        ```java
        // yml中: 'welcome: "&a欢迎, %rank% %player_name%！你的余额是: {money}。"'
        Map<String, String> customPlaceholders = new HashMap<>();
        customPlaceholders.put("rank", "[VIP]");

        String welcomeMsg = messageService.parse("welcome", player, customPlaceholders);
        // 最终消息可能是: "欢迎, [VIP] Steve！你的余额是: $1,234.56。"
        ```

  * #### `getList(String key)`

      * **返回类型:** `List<String>`
      * **功能描述:** 从语言文件中获取一个字符串列表，通常用于发送多行消息（如 a）。
      * **参数说明:**
          * `key` (`String`): 消息列表在 YAML 文件中的键。
      * **使用示例:**
        ```java
        // yml中:
        // help_info:
        //   - "&e/cmd help - 显示帮助"
        //   - "&e/cmd info - 查看信息"
        List<String> helpLines = messageService.getList("help_info");
        ```

  * #### `parseList(String key, Player player, Map<String, String> custom)`

      * **返回类型:** `List<String>`
      * **功能描述:** 获取一个消息列表，并对列表中的每一行字符串都执行完整的、多阶段的占位符解析。
      * **参数说明:**
          * `key` (`String`): 消息列表的键。
          * `player` (`Player`): 上下文玩家。
          * `custom` (`Map<String, String>`): 自定义占位符 Map。
      * **使用示例:**
        ```java
        // yml中:
        // player_stats:
        //   - "&6玩家: %player_name%"
        //   - "&c生命值: %player_health%/%player_max_health%"
        //   - "&b余额: {money}"
        List<String> parsedStats = messageService.parseList("player_stats", player, Collections.emptyMap());
        ```

  * #### `registerInternalPlaceholder(String key, BiFunction<Player, String, String> resolver)`

      * **返回类型:** `void`
      * **功能描述:** 注册一个内部占位符，格式为 `{key}` 或 `{key:args}`。这些占位符由 `MessageService` 内部处理，不依赖 PAPI，性能更高，且能与 `MessageService` 的其他功能（如 `args`）紧密集成。
      * **参数说明:**
          * `key` (`String`): 内部占位符的键（不带花括号）。
          * `resolver` (`BiFunction<Player, String, String>`): 一个处理函数，它接收一个 `Player` 对象和一个 `String` 类型的 `args`（即占位符中冒号后面的内容），并返回最终要显示的字符串。
      * **使用示例:**
        ```java
        // 注册一个 {item_name:main_hand} 占位符
        messageService.registerInternalPlaceholder("item_name", (player, args) -> {
            if (player == null) return "未知物品";
            if ("main_hand".equalsIgnoreCase(args)) {
                ItemStack item = player.getInventory().getItemInMainHand();
                return item != null && item.hasItemMeta() ? item.getItemMeta().getDisplayName() : "空手";
            }
            return "无效参数";
        });
        ```

  * #### `send(Player player, String key)`

      * **返回类型:** `void`
      * **功能描述:** 解析（不带自定义占位符）并向指定玩家发送单条消息。这是一个便捷方法。
      * **参数说明:**
          * `player` (`Player`): 接收消息的玩家。
          * `key` (`String`): 消息的键。
      * **使用示例:**
        ```java
        messageService.send(player, "action_completed_successfully");
        ```

  * #### `send(CommandSender target, String key, Map<String, String> custom)`

      * **返回类型:** `void`
      * **功能描述:** 解析（带自定义占位符）并向指定目标（玩家或控制台）发送单条消息。这是最通用的单条消息发送方法。
      * **参数说明:**
          * `target` (`CommandSender`): 消息接收者。
          * `key` (`String`): 消息的键。
          * `custom` (`Map<String, String>`): 自定义占位符 Map。
      * **使用示例:**
        ```java
        Map<String, String> data = new HashMap<>();
        data.put("item_name", "钻石剑");
        messageService.send(sender, "item_given", data);
        ```

  * #### `sendList(CommandSender target, String key)`

      * **返回类型:** `void`
      * **功能描述:** 解析（不带自定义占位符）并向目标发送一个消息列表。
      * **参数说明:**
          * `target` (`CommandSender`): 消息接收者。
          * `key` (`String`): 消息列表的键。
      * **使用示例:**
        ```java
        messageService.sendList(player, "plugin_help_menu");
        ```

  * #### `sendList(CommandSender target, String key, Map<String, String> custom)`

      * **返回类型:** `void`
      * **功能描述:** 解析（带自定义占位符）并向目标发送一个消息列表。
      * **参数说明:**
          * `target` (`CommandSender`): 消息接收者。
          * `key` (`String`): 消息列表的键。
          * `custom` (`Map<String, String>`): 自定义占位符 Map。

  * #### `broadcast(String key)`

      * **返回类型:** `void`
      * **功能描述:** 解析并向全服所有在线玩家广播一条消息。PAPI 占位符将无法针对特定玩家解析，因此请使用不依赖玩家的占位符。
      * **参数说明:**
          * `key` (`String`): 消息的键。

  * #### `broadcast(String key, Map<String, String> custom, String permission)`

      * **返回类型:** `void`
      * **功能描述:** 解析并向全服拥有指定权限的玩家广播一条消息。消息会为每个玩家单独解析，因此可以使用玩家相关的 PAPI 占位符。
      * **参数说明:**
          * `key` (`String`): 消息的键。
          * `custom` (`Map<String, String>`): 自定义占位符 Map。
          * `permission` (`String`): 玩家需要拥有的权限节点。

  * #### `broadcastList(String key, Map<String, String> custom, String permission)`

      * **返回类型:** `void`
      * **功能描述:** 解析并向全服拥有指定权限的玩家广播一个消息列表。
      * **参数说明:**
          * `key` (`String`): 消息列表的键。
          * `custom` (`Map<String, String>`): 自定义占位符 Map。
          * `permission` (`String`): 权限节点。

  * #### `sendRaw(CommandSender target, String rawMessage)`

      * **返回类型:** `void`
      * **功能描述:** 直接发送一条原始的、未经任何消息键查找和占位符解析的字符串消息。此方法仍然会自动处理颜色代码。
      * **参数说明:**
          * `target` (`CommandSender`): 消息接收者。
          * `rawMessage` (`String`): 要发送的原始消息文本。

  * #### `sendRawList(CommandSender target, List<String> rawMessages)`

      * **返回类型:** `void`
      * **功能描述:** 直接发送一个原始字符串列表，每行都会被处理颜色代码。
      * **参数说明:**
          * `target` (`CommandSender`): 消息接收者。
          * `rawMessages` (`List<String>`): 要发送的原始消息列表。

  * #### `sendOptimizedChat(Player player, List<String> messages)`

      * **返回类型:** `void`
      * **功能描述:** 优化地向玩家发送多行聊天消息，实际上是 `sendColorizedList` 的别名。
      * **参数说明:**
          * `player` (`Player`): 目标玩家。
          * `messages` (`List<String>`): 已经解析和处理好颜色的消息列表。

  * #### `sendStagedActionBar(Player player, List<String> messages)`

      * **返回类型:** `void`
      * **功能描述:** 逐条地向玩家发送 ActionBar 消息。由于 ActionBar 会被新的消息覆盖，这个方法的效果是快速连续地显示列表中的每一条消息。
      * **参数说明:**
          * `player` (`Player`): 目标玩家。
          * `messages` (`List<String>`): 要在 ActionBar 上显示的消息列表。

  * #### `sendStagedTitle(Player player, List<String> titles, List<String> subtitles)`

      * **返回类型:** `void`
      * **功能描述:** 逐条地向玩家发送 Title/Subtitle 组合。
      * **参数说明:**
          * `player` (`Player`): 目标玩家。
          * `titles` (`List<String>`): 主标题列表。
          * `subtitles` (`List<String>`): 副标题列表。

  * #### `storeMessage(Object context, String key, Map<String, String> custom)`

      * **返回类型:** `void`
      * **功能描述:** 解析一条消息，但并不立即发送，而是将其存储在一个与特定上下文对象关联的临时列表中。
      * **参数说明:**
          * `context` (`Object`): 关联的上下文对象，例如一个 `UUID` 或一个 `Player` 对象。
          * `key` (`String`): 消息的键。
          * `custom` (`Map<String, String>`): 自定义占位符。

  * #### `storeMessageList(Object context, String key, Map<String, String> custom)`

      * **返回类型:** `void`
      * **功能描述:** 解析一个消息列表，并将其所有行存储到指定的上下文中。
      * **参数说明:**
          * `context` (`Object`): 上下文对象。
          * `key` (`String`): 消息列表的键。
          * `custom` (`Map<String, String>`): 自定义占位符。

  * #### `hasMessages(Object context)`

      * **返回类型:** `boolean`
      * **功能描述:** 检查指定的上下文中是否存储了任何消息。
      * **参数说明:**
          * `context` (`Object`): 上下文对象。

  * #### `countMessages(Object context)`

      * **返回类型:** `int`
      * **功能描述:** 计算指定上下文中存储的消息数量。
      * **参数说明:**
          * `context` (`Object`): 上下文对象。

  * #### `sendContext(Object context, Player player, String channel)`

      * **返回类型:** `void`
      * **功能描述:** 将指定上下文中存储的所有消息，通过指定的渠道（"chat", "actionbar", "title"）发送给玩家，并在发送后清除该上下文的消息。
      * **参数说明:**
          * `context` (`Object`): 上下文对象。
          * `player` (`Player`): 接收消息的玩家。
          * `channel` (`String`): 发送渠道。

  * #### `sendContextFailures(Object context, Player player)`

      * **返回类型:** `void`
      * **功能描述:** 发送上下文中的消息，通常用于表示失败信息。当前实现等同于通过 "chat" 渠道发送。
      * **参数说明:**
          * `context` (`Object`): 上下文对象。
          * `player` (`Player`): 目标玩家。

  * #### `sendContextSuccesses(Object context, Player player)`

      * **返回类型:** `void`
      * **功能描述:** 发送上下文中的消息，通常用于表示成功信息。当前实现等同于通过 "chat" 渠道发送。
      * **参数说明:**
          * `context` (`Object`): 上下文对象。
          * `player` (`Player`): 目标玩家。

