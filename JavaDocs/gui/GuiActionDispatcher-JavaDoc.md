### `GuiActionDispatcher.java`

**1. 概述 (Overview)**

  * **完整路径:** `cn.drcomo.corelib.gui.GuiActionDispatcher`
  * **核心职责:** 在验证会话和拦截危险操作后，将玩家的点击事件分发到已注册的回调函数。

**2. 如何实例化 (Initialization)**

  * **核心思想:** 分发器需要外部提供 `DebugUtil` 与 `GUISessionManager` 实例，以遵循控制反转原则。
  * **构造函数:** `public GuiActionDispatcher(DebugUtil debug, GUISessionManager sessions)`

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


**4. 注意事项 (Cautions)**
  * **回调注册与注销：**
    - 注册回调时需确保 sessionId 唯一，避免覆盖其他会话的回调。
    - 注销时应彻底移除所有相关回调，防止内存泄漏或回调残留。

  * **分发器与会话/回调解耦：**
    - 分发器只负责事件分发，不应持有业务状态，所有业务逻辑应通过回调注入。
    - 回调实现应避免依赖分发器内部状态，保持解耦。

 * **回调异常处理与分发死链：**
    - 回调执行时必须 try/catch，防止单个回调异常影响整个分发流程。
    - 若回调链中存在死链（如未注册或已失效），应有日志提示并安全跳过。

  * **并发安全与性能：**
    - `handleClick` 在主线程高频调用，应保持 O(1) 判断，避免在分发过程中执行 IO 或数据库操作。
    - 回调中若需耗时任务，应使用调度器在异步线程处理，再返回主线程。

**4. 使用示例 (Usage Example)**

```java
public class MyListener implements Listener {
    private final GuiActionDispatcher dispatcher;
    private final GUISessionManager sessionMgr;

    public MyListener(Plugin plugin) {
        DebugUtil logger = new DebugUtil(plugin, DebugUtil.LogLevel.INFO);
        this.sessionMgr = new GUISessionManager(plugin, logger, null);
        this.dispatcher = new GuiActionDispatcher(logger, sessionMgr);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        ClickContext ctx = ClickContext.from(event, sessionMgr);
        dispatcher.handleClick(ctx, event);
    }
}
```

**5. 典型开发陷阱与注意事项 (Pitfalls & Cautions)**

| # | 场景 | 易错点 | 推荐做法 |
|---|------|--------|----------|
| 1 | **事件覆盖不全** | 仅监听 `InventoryClickEvent` 中的 "PICKUP_<something>", 遗漏 Shift-Click、数字键 (`KEY_NUMBER`)，拖拽 (`InventoryDragEvent`)、副手交换 (`SWAP_OFFHAND`)、丢弃键 (`DROP` / `CONTROL_DROP`) 等 | 建立 **统一交互调度层**：<br>- 对所有相关事件注册监听；<br>- 首先归一化为内部 `Action` 枚举，再转业务处理；<br>- 使用字符串比对或反射以兼容旧 API 中缺失的枚举值 |
| 3 | **权限 / 开关遗漏** | 忽略 bypass 权限或全局功能开关导致误封、体验异常 | 在调度层最前端插入 **权限 & 开关短路** 判断；支持热插拔（reload 时即时生效） |

**集成风险 (Integration Risks)**

| 维度 | 风险 | 缓解策略 |
|------|------|----------|
| **API 兼容性** | 枚举缺失、方法签名差异、NMS 变动 | 构建 `Compatibility` 工具类：<br>- 通过版本号分发实现；<br>- 对不可预知字段使用反射 + 缓存；<br>- 持续集成中加入多版本编译 & 运行测试 |
| **第三方插件冲突** | 背包保护、GUI 框架、MMO 物品类插件均可能修改同一事件 | 调整事件优先级（`LOWEST` / `MONITOR`）；避免 **直接**取消事件→先检测是否已取消；对于物品元数据，遵循 "不破坏但附加" 原则 |

**通用设计原则 (General Design Principles)**

| 原则 | 说明 |
|------|------|
| **集中式交互调度** | 所有物品交互事件在 _同一_ 入口汇总，避免分散监听带来的状态泄露。 |
| **最小权限原则** | 默认拒绝操作，仅在明确允许（白名单、权限节点）时放行。 |
| **幂等性** | 多次触发同一事件链不应导致物品重复扣除或奖励。 |
| **版本前向兼容** | 对未来新增交互（如 1.20 引入的新快捷槽）留有扩展点：统一 `enum` → `String` 转存；或在策略模式中注入新 `Action`。 |
