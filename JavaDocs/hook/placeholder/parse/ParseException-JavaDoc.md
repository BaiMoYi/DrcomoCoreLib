### `ParseException.java`

**1. 概述 (Overview)**

  * **完整路径:** `cn.drcomo.corelib.hook.placeholder.parse.ParseException`
  * **核心职责:** 一个自定义的受检异常（`Checked Exception`），专门用于在 `PlaceholderConditionEvaluator` 解析条件表达式的过程中，当遇到语法错误或其他解析问题时抛出。

**2. 如何实例化 (Initialization)**

  * **核心思想:** 这个异常类通常由库的内部（主要是 `PlaceholderConditionEvaluator` 的解析器部分）在检测到错误时实例化并抛出。开发者主要负责在调用 `parse()` 方法时，使用 `try-catch` 语句来捕获并处理它。
  * **构造函数:** `public ParseException(String message)`
  * **代码示例:**
    ```java
    // 开发者通常是捕获此异常，而不是创建它。
    // PlaceholderConditionEvaluator conditionEvaluator = ...;
    String invalidExpression = "%player_level% >> 10"; // ">>" 不是一个有效的比较运算符

    try {
        conditionEvaluator.parse(player, invalidExpression);
    } catch (ParseException e) {
        // 捕获到解析异常
        getLogger().severe("条件表达式 '" + invalidExpression + "' 存在语法错误: " + e.getMessage());
        // 在这里可以进行错误处理，比如通知管理员检查配置文件
    }
    ```

**3. 公共API方法 (Public API Methods)**

  * 本类继承自 `java.lang.Exception`，除了构造函数外，没有自己新增的公共方法。开发者可以像处理标准 Java 异常一样，通过 `getMessage()` 方法获取错误详情。

