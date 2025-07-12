### `PlaceholderConditionEvaluator.java`

**1. 概述 (Overview)**

  * **完整路径:** `cn.drcomo.corelib.hook.placeholder.parse.PlaceholderConditionEvaluator`
  * **核心职责:** 一个轻量级的、专门用于解析和求值布尔条件表达式的引擎。它能够处理包含 PAPI 占位符、数学运算、逻辑运算符（`&&`, `||`）以及多种比较运算符（如 `==`, `>`, `STR_CONTAINS`）的复杂条件字符串，并返回一个布尔结果。这在配置文件中定义动态触发条件（例如，在什么条件下执行某个动作）的场景下极为有用。

**2. 如何实例化 (Initialization)**

  * **核心思想:** 该评估器依赖于多个核心库组件，其实例化过程完美体现了“依赖注入”模式。你需要先创建好 `DebugUtil` 和 `PlaceholderAPIUtil` 的实例，然后将它们连同你的插件实例一起，作为构造函数的参数传入。
  * **构造函数:** `public PlaceholderConditionEvaluator(JavaPlugin pluginInstance, DebugUtil debugger, PlaceholderAPIUtil placeholderUtil)`
  * **代码示例:**
    ```java
    // 在你的子插件 onEnable() 方法中，确保已初始化好依赖项:
    JavaPlugin myPlugin = this;
    DebugUtil myLogger = new DebugUtil(myPlugin, DebugUtil.LogLevel.INFO);
    PlaceholderAPIUtil myPapiUtil = new PlaceholderAPIUtil(myPlugin, "myplugin");
    // (此处应有 myPapiUtil 的占位符注册代码)

    // 现在，实例化条件评估器
    PlaceholderConditionEvaluator conditionEvaluator = new PlaceholderConditionEvaluator(
        myPlugin,
        myLogger,
        myPapiUtil
    );

    myLogger.info("条件表达式评估器已准备就绪。");
    ```
 
**3. 公共API方法 (Public API Methods)**

  * #### `calculateMathExpression(String expression)`

      * **返回类型:** `double`
      * **功能描述:** 计算一个纯数学表达式字符串。它内部委托给 `FormulaCalculator` 执行。
      * **参数说明:**
          * `expression` (`String`): 待计算的数学表达式，例如 `"5 * (2 + %player_level%)"`。
      * **使用示例:**
        ```java
        // 注意：此方法在计算前不会解析 PAPI 占位符。
        // 你需要先手动解析。
        String rawExpression = "100 + %player_level% * 5";
        String parsedExpression = myPapiUtil.parse(player, rawExpression); // 假设玩家10级，结果为 "100 + 10 * 5"
        double result = conditionEvaluator.calculateMathExpression(parsedExpression); // result = 150.0
        ```

  * #### `containsMathExpression(String expression)`

      * **返回类型:** `boolean`
      * **功能描述:** 判断一个字符串中是否包含了数学运算符或函数，可用于初步判断是否需要进行数学计算。
      * **参数说明:**
          * `expression` (`String`): 要检查的字符串。

  * #### `checkAllLines(Player player, List<String> lines)`

      * **返回类型:** `boolean`
      * **功能描述:** **同步**地检查一个字符串列表中的所有条件行。只有当所有行都评估为 `true` 时，才返回 `true`（逻辑与 `AND` 的关系）。
      * **参数说明:**
          * `player` (`Player`): PAPI 占位符的上下文玩家。
          * `lines` (`List<String>`): 包含条件表达式的字符串列表。

  * #### `parse(Player player, String expression)`

      * **返回类型:** `boolean`
      * **功能描述:** **同步**地解析并评估单个条件表达式字符串。这是该类的核心同步执行方法。
      * **参数说明:**
          * `player` (`Player`): PAPI 占位符的上下文玩家。
          * `expression` (`String`): 单行条件表达式，例如 `"%player_level% >= 10 && %vault_eco_balance% > 1000"`。
      * **使用示例:**
        ```java
        String condition = "'%player_world_name%' == 'world_nether'";
        try {
            boolean isInNether = conditionEvaluator.parse(player, condition);
            if (isInNether) {
                player.sendMessage("你现在在地狱！");
            }
        } catch (ParseException e) {
            myLogger.error("条件表达式解析失败: " + condition, e);
        }
        ```

  * #### `parseAndEvaluateAsync(String expression, Player player)`

      * **返回类型:** `CompletableFuture<Boolean>`
      * **功能描述:** **异步**地解析并评估单个条件表达式。此方法会将解析和计算任务提交到 Bukkit 的异步线程池中执行，避免在主线程上进行耗时操作（特别是当占位符解析涉及IO时），最终通过 `CompletableFuture` 返回结果。
      * **参数说明:**
          * `expression` (`String`): 单行条件表达式。
          * `player` (`Player`): PAPI 占位符的上下文玩家。
      * **使用示例:**
        ```java
        String asyncCondition = "%some_slow_papi_placeholder% == 'expected_value'";
        conditionEvaluator.parseAndEvaluateAsync(asyncCondition, player).thenAccept(result -> {
            // 这个回调会在主线程中执行，可以安全地操作 Bukkit API
            if (result) {
                player.sendMessage("异步条件满足！");
            }
        });
        ```

  * #### `checkAllLinesAsync(Player player, List<String> lines)`

      * **返回类型:** `CompletableFuture<Boolean>`
      * **功能描述:** **异步**地、串行地检查一个列表中的所有条件行。它会逐个异步评估每个条件，一旦有任何一个条件为 `false`，就会立即返回 `false`，实现短路效果。
      * **参数说明:**
          * `player` (`Player`): PAPI 占位符的上下文玩家。
          * `lines` (`List<String>`): 包含条件表达式的字符串列表。

