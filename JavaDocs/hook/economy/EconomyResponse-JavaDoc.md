### `EconomyResponse.java`

**1. 概述 (Overview)**

  * **完整路径:** `cn.drcomo.corelib.hook.economy.EconomyResponse`
  * **核心职责:** 一个简单的数据对象（DTO），用于封装经济操作（如存款、取款）的返回结果。它清晰地标示了操作是否成功，并在失败时提供了具体的错误信息。

**2. 如何实例化 (Initialization)**

  * **核心思想:** 这个类通常由经济提供者（`EconomyProvider`的实现类）在执行操作后内部创建并返回。开发者主要负责消费这个对象，而不是创建它。不过，也可以通过其公共构造函数或静态工厂方法来手动创建。
  * **构造函数:** `public EconomyResponse(boolean success, String errorMessage)`
  * **代码示例:**
    ```java
    // 通常，你不需要自己创建 EconomyResponse，而是从 EconomyProvider 的方法中获取它。
    EconomyResponse response = economyProvider.withdraw(player, 100.0);

    // 然后检查它的状态
    if (response.success) {
        player.sendMessage("支付成功！");
    } else {
        player.sendMessage("支付失败: " + response.errorMessage);
    }

    // 你也可以使用静态工厂方法来创建实例，这在编写测试或模拟实现时很有用。
    EconomyResponse successResponse = EconomyResponse.ok();
    EconomyResponse failureResponse = EconomyResponse.fail("余额不足");
    ```

**3. 公共API方法 (Public API Methods)**

  * #### `ok()`

      * **返回类型:** `EconomyResponse`
      * **功能描述:** 一个静态工厂方法，用于快速创建一个表示“操作成功”的 `EconomyResponse` 实例。其 `success` 字段为 `true`，`errorMessage` 为 `null`。
      * **参数说明:** 无。

  * #### `fail(String msg)`

      * **返回类型:** `EconomyResponse`
      * **功能描述:** 一个静态工厂方法，用于快速创建一个表示“操作失败”的 `EconomyResponse` 实例。其 `success` 字段为 `false`。
      * **参数说明:**
          * `msg` (`String`): 描述失败原因的错误信息。

