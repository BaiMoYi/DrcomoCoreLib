### `FormulaCalculator.java`

**1. 概述 (Overview)**

  * **完整路径:** `cn.drcomo.corelib.math.FormulaCalculator`
  * **核心职责:** 一个基于 `exp4j` 库的强大静态数学公式计算工具。它能够解析和计算复杂的文本格式的数学表达式，支持变量、标准运算符以及一系列预定义的数学函数（如 `min`, `max`, `pow`, `sqrt`, `sin` 等）。

**2. 如何实例化 (Initialization)**

  * **核心思想:** 这是一个完全静态的工具类，所有方法都应通过类名直接调用。它不应该被实例化。
  * **构造函数:** `private FormulaCalculator()` (私有构造函数，禁止外部实例化)
  * **代码示例:**
    ```java
    // 这是一个静态工具类，请直接通过类名调用其方法。

    // 示例1：计算简单表达式
    try {
        double result = FormulaCalculator.calculate("3 * (log(100) + 5^2)"); // 3 * (2 + 25) = 81
        System.out.println("计算结果: " + result); // 输出 81.0
    } catch (IllegalArgumentException e) {
        // 处理表达式语法错误
    }

    // 示例2：使用变量进行计算
    Map<String, Double> variables = new HashMap<>();
    variables.put("x", 10.0);
    variables.put("y", 5.0);
    try {
        // 表达式中的变量名不需要用 % 或 {} 包裹
        double resultWithVars = FormulaCalculator.calculate("sqrt(x^2 + y^2)", variables);
        System.out.println("带变量计算结果: " + resultWithVars);
    } catch (IllegalArgumentException e) {
        // 处理错误
    }
    ```

**3. 公共API方法 (Public API Methods)**

  * #### `calculate(String formula)`

      * **返回类型:** `double`
      * **功能描述:** 计算一个不含变量的数学公式字符串。
      * **参数说明:**
          * `formula` (`String`): 要计算的数学表达式。
      * **抛出异常:** `IllegalArgumentException` - 如果公式语法无效或无法计算。

  * #### `calculate(String formula, Map<String, Double> variables)`

      * **返回类型:** `double`
      * **功能描述:** 计算一个包含变量的数学公式字符串。
      * **参数说明:**
          * `formula` (`String`): 含有变量名的数学表达式。
          * `variables` (`Map<String, Double>`): 一个 `Map`，其中键是变量名，值是对应的双精度数值。

  * #### `containsMathExpression(String formula)`

      * **返回类型:** `boolean`
      * **功能描述:** 快速检查一个字符串是否包含任何数学运算符（`+`, `-`, `*`, `/` 等）或预定义的数学函数名。
      * **参数说明:**
          * `formula` (`String`): 要检查的字符串。

  * #### `getAvailableFunctions()`

      * **返回类型:** `String[]`
      * **功能描述:** 获取所有当前计算器支持的自定义函数名称数组（例如 "min", "max", "clamp", "lerp" 等）。
      * **参数说明:** 无。

  * #### `validateFormula(String formula)`

      * **返回类型:** `boolean`
      * **功能描述:** 验证一个公式字符串的语法是否正确，但并不执行实际的计算。如果语法有效则返回 `true`，否则返回 `false`。
      * **参数说明:**
          * `formula` (`String`): 要验证的公式字符串。

