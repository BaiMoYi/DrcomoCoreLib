### `DebugUtil.java`

**1. 概述 (Overview)**

  * **完整路径:** `cn.drcomo.corelib.util.DebugUtil`
  * **核心职责:** 一个通用的调试日志工具，旨在为插件提供分级别的、带前缀的控制台日志输出。它允许开发者根据需要动态调整日志级别，从而在开发和生产环境中灵活地控制日志的详细程度。

**2. 如何实例化 (Initialization)**

  * **核心思想:** 每个插件都应创建并持有一个独立的 `DebugUtil` 实例。这种设计确保了日志输出的前缀是调用方插件的名称，从而使日志来源一目了然。实例化时需要传入插件主类实例和期望的初始日志级别。
  * **构造函数:** `public DebugUtil(Plugin plugin, LogLevel level)`
  * **代码示例:**
    ```java
    // 在你的子插件的 onEnable() 方法中:
    Plugin myPlugin = this; // 'this' 指向你的插件主类实例

    // 创建一个 DebugUtil 实例，初始日志级别设置为 DEBUG
    // 在开发阶段，使用 DEBUG 级别可以输出所有日志，方便调试。
    DebugUtil myLogger = new DebugUtil(myPlugin, DebugUtil.LogLevel.DEBUG);

    // 在生产环境中，你可能希望减少不必要的日志输出
    // DebugUtil productionLogger = new DebugUtil(myPlugin, DebugUtil.LogLevel.INFO);

    // 使用 myLogger 来输出日志
    myLogger.info("MyAwesomePlugin 已加载，日志系统初始化成功！");
    ```

**3. 公共API方法 (Public API Methods)**

  * #### `setLevel(LogLevel level)`

      * **返回类型:** `void`
      * **功能描述:** 动态设置日志记录器的输出级别。设置后，只有级别等于或高于新设定级别的日志才会被输出。
      * **参数说明:**
          * `level` (`LogLevel`): 新的日志级别，例如 `LogLevel.INFO`, `LogLevel.WARN`。

  * #### `getLevel()`

      * **返回类型:** `LogLevel`
      * **功能描述:** 获取当前日志记录器的输出级别。
      * **参数说明:** 无。

  * #### `debug(String msg)`

      * **返回类型:** `void`
      * **功能描述:** 输出一条 DEBUG 级别的日志。这通常用于记录详细的、仅在开发调试时需要关心的信息。
      * **参数说明:**
          * `msg` (`String`): 要输出的日志消息。

  * #### `info(String msg)`

      * **返回类型:** `void`
      * **功能描述:** 输出一条 INFO 级别的日志。用于记录插件运行过程中的常规信息，如插件加载、重载、核心功能执行等。
      * **参数说明:**
          * `msg` (`String`): 要输出的日志消息。

  * #### `warn(String msg)`

      * **返回类型:** `void`
      * **功能描述:** 输出一条 WARN 级别的日志。用于报告潜在的问题或非致命性错误，这些情况通常不会导致插件停止工作。
      * **参数说明:**
          * `msg` (`String`): 要输出的警告消息。

  * #### `error(String msg)`

      * **返回类型:** `void`
      * **功能描述:** 输出一条 ERROR 级别的日志。用于报告严重的错误，这些错误可能会影响插件的正常功能。
      * **参数说明:**
          * `msg` (`String`): 要输出的错误消息。

  * #### `error(String msg, Throwable t)`

      * **返回类型:** `void`
      * **功能描述:** 输出一条 ERROR 级别的日志，并附带一个异常的堆栈跟踪信息。这是捕获并报告异常时的首选方法。
      * **参数说明:**
          * `msg` (`String`): 对错误的描述信息。
          * `t` (`Throwable`): 捕获到的异常对象。

  * #### `log(LogLevel level, String message)`

      * **返回类型:** `void`
      * **功能描述:** 以指定的日志级别输出一条消息。这是一个更通用的日志记录方法。
      * **参数说明:**
          * `level` (`LogLevel`): 要使用的日志级别。
          * `message` (`String`): 要输出的日志消息。

  * #### `log(LogLevel level, String message, Throwable t)`

      * **返回类型:** `void`
      * **功能描述:** 以指定的日志级别输出一条消息，并附带异常的堆栈跟踪。
      * **参数说明:**
          * `level` (`LogLevel`): 要使用的日志级别。
          * `message` (`String`): 对错误的描述信息。
          * `t` (`Throwable`): 捕获到的异常对象。

