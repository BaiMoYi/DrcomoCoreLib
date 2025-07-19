### `ClickContext.java`

**1. 概述 (Overview)**

  * **完整路径:** `cn.drcomo.corelib.gui.ClickContext`
  * **核心职责:** 作为 GUI 点击事件的统一数据载体，封装玩家、会话及点击相关信息。

**2. 如何实例化 (Initialization)**

  * **核心思想:** 该类是 Java 17 `record`，应通过其静态方法 `from(InventoryClickEvent, GUISessionManager)` 创建。
  * **代码示例:**
    ```java
    ClickContext ctx = ClickContext.from(event, sessionManager);
    dispatcher.handleClick(ctx, event);
    ```

**3. 公共API方法 (Public API Methods)**

  * #### `from(InventoryClickEvent e, GUISessionManager sessionMgr)`

      * **返回类型:** `ClickContext`
      * **功能描述:** 根据事件与会话管理器构建新的上下文实例。
      * **参数说明:**
          * `e` (`InventoryClickEvent`): 原始事件。
          * `sessionMgr` (`GUISessionManager`): 会话管理器。

  * #### `isShift()`

      * **返回类型:** `boolean`
      * **功能描述:** 判断点击是否为 Shift + 点击。内部委托 `ClickTypeUtil.isShift(clickType)` 实现。
      * **无上下文时推荐：** 直接使用 `ClickTypeUtil.isShift(type)`。

  * #### `isLeftClick()`

      * **返回类型:** `boolean`
      * **功能描述:** 判断是否按下左键。内部委托 `ClickTypeUtil.isLeftClick(clickType)` 实现。
      * **无上下文时推荐：** 直接使用 `ClickTypeUtil.isLeftClick(type)`。

  * #### `isRightClick()`

      * **返回类型:** `boolean`
      * **功能描述:** 判断是否按下右键。内部委托 `ClickTypeUtil.isRightClick(clickType)` 实现。
      * **无上下文时推荐：** 直接使用 `ClickTypeUtil.isRightClick(type)`。

  * #### `isMiddleClick()`

      * **返回类型:** `boolean`
      * **功能描述:** 判断是否按下中键。内部委托 `ClickTypeUtil.isMiddleClick(clickType)` 实现。
      * **无上下文时推荐：** 直接使用 `ClickTypeUtil.isMiddleClick(type)`。

  * #### `isNumberKey()`

      * **返回类型:** `boolean`
      * **功能描述:** 判断点击是否来自数字键。内部委托 `ClickTypeUtil.isNumberKey(clickType)` 实现。
      * **无上下文时推荐：** 直接使用 `ClickTypeUtil.isNumberKey(type)`。

  * #### `isDrop()`

      * **返回类型:** `boolean`
      * **功能描述:** 判断是否按下丢弃键。内部委托 `ClickTypeUtil.isDrop(clickType)` 实现。
      * **无上下文时推荐：** 直接使用 `ClickTypeUtil.isDrop(type)`。

  * #### `isControlDrop()`

      * **返回类型:** `boolean`
      * **功能描述:** 判断是否按下 Ctrl+丢弃。内部委托 `ClickTypeUtil.isControlDrop(clickType)` 实现。
      * **无上下文时推荐：** 直接使用 `ClickTypeUtil.isControlDrop(type)`。

  * #### `isSwapOffhand()`

      * **返回类型:** `boolean`
      * **功能描述:** 判断是否与副手物品交换。内部委托 `ClickTypeUtil.isSwapOffhand(clickType)` 实现。
      * **无上下文时推荐：** 直接使用 `ClickTypeUtil.isSwapOffhand(type)`。

  * #### `isKeyboardTriggerClick()`

      * **返回类型:** `boolean`
      * **功能描述:** 判断是否为快捷键操作点击（Shift、数字键、副手交换、双击、窗口边缘）。内部委托 `ClickTypeUtil.isKeyboardTriggerClick(clickType)` 实现。
      * **无上下文时推荐：** 直接使用 `ClickTypeUtil.isKeyboardTriggerClick(type)`。

  * #### `isDangerous()`

      * **返回类型:** `boolean`
      * **功能描述:** 判断该点击是否被视为危险操作。内部委托 `ClickTypeUtil.isDangerous(clickType)` 实现。
      * **无上下文时推荐：** 直接使用 `ClickTypeUtil.isDangerous(type)`。

**4. 注意事项 (Cautions)**
  * **上下文数据构造边界：**
    - ClickContext 必须通过 from() 工厂方法构造，确保所有字段完整、类型安全。
    - 构造时应校验事件和会话管理器非空，防止 NPE。
  * **数据一致性与扩展性：**
    - 上下文数据应与实际事件保持同步，避免因异步或多线程导致数据失效。
    - 如需扩展上下文字段，建议通过 record 新增字段并保持兼容性。
  * **点击类型安全获取：**
    - 获取点击类型时应判空，防止 ClickType 为 null 导致异常。
  * **多线程访问安全：**
    - ClickContext 不应跨线程长期保存；如需异步处理，应在回调前提取所需不可变数据。
    - 避免在异步线程直接操作 Bukkit API。
  * **持久化与缓存：**
    - 不要将 ClickContext 本身缓存或序列化；如确有需要，仅存储业务必需的最小字段。

**5. 槽位与容器类型注意事项 (Slot & Container Type Cautions)**

- 服务端不同版本对 `slot` 的含义不同，`clickedInventory` 可能为 `null`，工作台、盔甲架、展示框等有自定义槽位编号。建议使用 `InventoryView#convertSlot(int)` 统一转化，通过 `InventoryType`/`Holder` 判断容器类型，务必区分顶部容器与玩家背包。
