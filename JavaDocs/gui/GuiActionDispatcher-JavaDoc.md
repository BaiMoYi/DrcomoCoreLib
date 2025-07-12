### `GuiActionDispatcher.java`

**1. 概述 (Overview)**

  * **完整路径:** `cn.drcomo.corelib.gui.GuiActionDispatcher`
  * **核心职责:** 在验证会话和拦截危险操作后，将玩家的点击事件分发到已注册的回调函数。

**2. 如何实例化 (Initialization)**

  * **核心思想:** 分发器需要外部提供 `DebugUtil`、`GUISessionManager` 与 `GuiManager` 实例，以遵循控制反转原则。
  * **构造函数:** `public GuiActionDispatcher(DebugUtil debug, GUISessionManager sessions, GuiManager guiManager)`

**3. 公共API方法 (Public API Methods)**

  * #### `register(String sessionId, SlotPredicate where, ClickAction action)`

      * **返回类型:** `void`
      * **功能描述:** 为指定会话注册一个点击回调及其槽位过滤条件。
      * **参数说明:**
          * `sessionId` (`String`): 会话标识。
          * `where` (`SlotPredicate`): 槽位判断条件。
          * `action` (`ClickAction`): 点击回调。

  * #### `unregister(String sessionId)`

      * **返回类型:** `void`
      * **功能描述:** 移除指定会话的所有回调。
      * **参数说明:**
          * `sessionId` (`String`): 会话标识。

  * #### `handleClick(ClickContext ctx, InventoryClickEvent event)`

      * **返回类型:** `void`
      * **功能描述:** 统一处理事件，验证会话并分派到符合条件的回调。
      * **参数说明:**
          * `ctx` (`ClickContext`): 点击上下文。
          * `event` (`InventoryClickEvent`): 原始事件。

**4. 内部结构 (Internal Structure)**
  * **ActionRegistry:**
    - 内部静态类，维护每个会话的槽位与回调映射。
    - 提供 `add(SlotPredicate, ClickAction)` 注册方法与 `dispatch(ClickContext)` 分发方法。
    - 仅在分发器内部使用，对外透明。

**5. 注意事项 (Cautions)**
  * **回调注册与注销：**
    - 注册回调时需确保 sessionId 唯一，避免覆盖其他会话的回调。
    - 注销时应彻底移除所有相关回调，防止内存泄漏或回调残留。

  * **分发器与会话/回调解耦：**
    - 分发器只负责事件分发，不应持有业务状态，所有业务逻辑应通过回调注入。
    - 回调实现应避免依赖分发器内部状态，保持解耦。

  * **回调异常处理与分发死链：**
    - 回调执行时已内置 try/catch，防止单个回调异常影响整个分发流程。
    - 若回调链中存在死链（如未注册或已失效），有日志提示并安全跳过。

**6. 使用示例 (Usage Example)**

```java
public class MyListener implements Listener {
    private final GuiActionDispatcher dispatcher;
    private final GUISessionManager sessionMgr;
    private final GuiManager guiManager;

    public MyListener(Plugin plugin) {
        DebugUtil logger = new DebugUtil(plugin, DebugUtil.LogLevel.INFO);
        this.sessionMgr = new GUISessionManager(plugin, logger, null);
        this.guiManager = new GuiManager(logger);
        this.dispatcher = new GuiActionDispatcher(logger, sessionMgr, guiManager);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        ClickContext ctx = ClickContext.from(event, sessionMgr);
        dispatcher.handleClick(ctx, event);
    }
}
```
