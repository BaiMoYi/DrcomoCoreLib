### `ColorUtil.java`

**1. 概述 (Overview)**

  * **完整路径:** `cn.drcomo.corelib.color.ColorUtil`
  * **核心职责:** 一个静态工具类，专门用于处理 Minecraft 游戏内的文本颜色代码。它支持传统的 `&` 颜色代码，并能在兼容版本（1.16+）上将 `&#RRGGBB` 格式的十六进制颜色码转换为 Minecraft 支持的格式。

**2. 如何实例化 (Initialization)**

  * **核心思想:** 这是一个完全静态的工具类 (`Utility Class`)，所有方法都应通过类名直接调用。它不包含任何状态，因此**不能也不需要**被实例化。
  * **构造函数:** `private ColorUtil()` (私有构造函数，禁止外部实例化)
  * **代码示例:**
    ```java
    // 这是一个静态工具类，请直接通过类名调用其方法。
    // 无需创建实例。

    // 示例：
    String originalText = "&a你好, &#00FF00世界!";
    String translatedText = ColorUtil.translateColors(originalText);
    player.sendMessage(translatedText); // 将发送带有颜色的文本给玩家

    String textWithColor = "&c这是一个&l测试。";
    String strippedText = ColorUtil.stripColorCodes(textWithColor);
    // strippedText 的值为 "这是一个测试。"
    ```

**3. 公共API方法 (Public API Methods)**

  * #### `translateColors(String text)`

      * **返回类型:** `String`
      * **功能描述:** 将包含 `&` 颜色代码和 `&#RRGGBB` 十六进制颜色代码的字符串，翻译成 Minecraft 服务器可以识别并显示的彩色文本。对于不兼容十六进制颜色的旧版服务器，它会优雅地降级为最接近的传统颜色。
      * **参数说明:**
          * `text` (`String`): 包含待转换颜色代码的原始文本。
      * **使用示例:**
        ```java
        String message = "§c警告: &#FFD700您的金币不足！";
        String formattedMessage = ColorUtil.translateColors(message);
        Bukkit.broadcastMessage(formattedMessage);
        ```

  * #### `stripColorCodes(String text)`

      * **返回类型:** `String`
      * **功能描述:** 从给定的文本中移除所有类型的颜色代码（包括 `&`、`§` 以及 `&#RRGGBB` 格式），返回一个纯净、不带任何颜色格式的字符串。
      * **参数说明:**
          * `text` (`String`): 包含颜色代码的原始文本。
      * **使用示例:**
        ```java
        String coloredLore = "§a生命值: §c+10";
        String plainLore = ColorUtil.stripColorCodes(coloredLore);
        // plainLore 将会是 "生命值: +10"
        ```

