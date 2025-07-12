### `GUISessionManager.java`

**1. 概述 (Overview)**

  * **完整路径:** `cn.drcomo.corelib.gui.GUISessionManager`
  * **核心职责:** 统一管理所有由子插件创建的 GUI 会话。负责在玩家打开界面时记录会话、在关闭时注销，并提供会话验证与批量清理能力。

**2. 如何实例化 (Initialization)**

  * **核心思想:** 该管理器遵循控制反转原则，构造时需要传入主插件实例、调试工具以及可选的 `MessageService`。它本身不注册事件，事件应由外部绑定。
  * **构造函数:** `public GUISessionManager(Plugin plugin, DebugUtil debug, MessageService messageService)`
  * **代码示例:**
    ```java
    Plugin plugin = this; // 你的插件主类
    DebugUtil logger = new DebugUtil(plugin, DebugUtil.LogLevel.INFO);
    MessageService msgSvc = null; // 如无需要可传入 null

    GUISessionManager manager = new GUISessionManager(plugin, logger, msgSvc);
    ```

**3. 公共API方法 (Public API Methods)**

  * #### `openSession(Player player, String sessionId, GUICreator creator)`
 
      * **返回类型:** `boolean`
      * **功能描述:** 创建并打开一个新的 GUI，会自动登记会话并在之后的验证中使用。
      * **参数说明:**
          * `player` (`Player`): 目标玩家。
          * `sessionId` (`String`): 会话标识，由调用方保证唯一。
          * `creator` (`GUICreator`): 用于构建界面的回调。

  * #### `boolean isSessionInventoryClick(Player player, InventoryClickEvent event)`

      * **功能描述:** 基于会话登记的 Inventory 与 `event.getClickedInventory()` 比较，精准判断点击是否发生在自定义 GUI 区域。
      * **返回值:** 在当前会话 GUI 区域返回 `true`。
      * **使用场景:** 需严格区分 GUI 与玩家背包时调用。

  * #### `boolean isPlayerInventoryClick(Player player, InventoryClickEvent event)`

      * **功能描述:** 判断点击是否发生在玩家背包区域（非自定义 GUI）。
      * **返回值:** 点击背包区域时返回 `true`。

  * #### `closeSession(Player player)`

      * **返回类型:** `void`
      * **功能描述:** 关闭并注销指定玩家的当前会话。
      * **参数说明:**
          * `player` (`Player`): 目标玩家。

  * #### `closeAllSessions()`

      * **返回类型:** `void`
      * **功能描述:** 关闭并注销所有已记录的会话。
      * **参数说明:** 无。

  * #### `getCurrentSessionId(Player player)`

      * **返回类型:** `String`
      * **功能描述:** 获取玩家当前会话的标识，如果不存在则返回 `null`。
      * **参数说明:**
          * `player` (`Player`): 目标玩家。

  * #### `hasSession(Player player)`

      * **返回类型:** `boolean`
      * **功能描述:** 判断指定玩家是否存在活跃会话。
      * **参数说明:**
          * `player` (`Player`): 目标玩家。

  * #### `validateSessionInventory(Player player, Inventory inv)`

      * **返回类型:** `boolean`
      * **功能描述:** 检查给定界面是否属于玩家当前会话，用于拦截非法或过期的操作。
      * **参数说明:**
          * `player` (`Player`): 目标玩家。
          * `inv` (`Inventory`): 要验证的界面实例。

  * #### `void flushOnDisable()`

      * **功能描述:** 插件关闭 (`onDisable`) 时同步回收所有仍在会话中的 GUI 物品并返还给玩家；若背包已满则自然掉落。
      * **使用场景:** 保证插件关闭时不会因调度器停止而导致物品丢失。

**4. 注意事项 (Cautions)**

  * **会话生命周期管理：**
    - InventoryCloseEvent 可能多次触发，必须通过会话状态或 Set<UUID> 做幂等保护，防止重复发放或回收物品。
    - 会话状态应与玩家实际界面保持同步，避免因异步/延迟导致状态错乱。

  * **物品安全回收：**
    - 插件关闭（onDisable）时，务必调用 `flushOnDisable()`，同步返还所有会话内物品，防止调度器失效导致物品丢失。
    - flushOnDisable 只应在主线程调用，且需确保所有玩家会话都被正确遍历。

  * **幂等性与重入风险：**
    - 任何涉及物品返还、会话关闭的操作都必须保证只执行一次，典型做法是用会话状态标记或锁机制防止重复。
    - 注意 finally 块释放锁，避免死锁或资源泄漏。
