### `ClickTypeUtil.java`

**1. 概述 (Overview)**

  * **完整路径:** `cn.drcomo.corelib.gui.ClickTypeUtil`
  * **核心职责:** 提供常用 `ClickType` 判定工具方法，允许在无 `ClickContext` 情况下快速判断点击行为（是否危险、是否快捷键触发等）。

**2. 使用方式 (Usage)**

  * **核心思想:** 纯静态工具类，<em>无需实例化</em>；直接通过类名调用。
  * **代码示例:**
    ```java
    // 在 InventoryClickEvent 监听器中
    if (ClickTypeUtil.isDangerous(event.getClick())) {
        event.setCancelled(true);
        player.sendMessage("§c该操作已被拦截！");
    }
    ```

**3. 公共API方法 (Public API Methods)**

  * #### `boolean isShift(ClickType type)`

      * **功能描述:** 判断是否为 Shift + 点击（左/右键均可）。
      * **参数说明:** `type` — 事件 ClickType，可为 null。
      * **返回值:** `true` 表示 Shift 点击。

  * #### `boolean isLeftClick(ClickType type)`

      * **功能描述:** 判断是否按下左键。

  * #### `boolean isRightClick(ClickType type)`

      * **功能描述:** 判断是否按下右键。

  * #### `boolean isMiddleClick(ClickType type)`

      * **功能描述:** 判断是否按下中键。

  * #### `boolean isNumberKey(ClickType type)`

      * **功能描述:** 判断点击是否由数字键触发。

  * #### `boolean isDrop(ClickType type)`

      * **功能描述:** 判断是否按下 Q 键丢弃。

  * #### `boolean isControlDrop(ClickType type)`

      * **功能描述:** 判断是否按下 Ctrl + Q 丢弃。

  * #### `boolean isSwapOffhand(ClickType type)`

      * **功能描述:** 判断是否与副手交换物品。

  * #### `boolean isKeyboardTriggerClick(ClickType type)`

      * **功能描述:** 聚合判断：Shift、数字键、副手交换、双击、窗口边缘点击任意命中即返回 `true`。

  * #### `boolean isDangerous(ClickType type)`

      * **功能描述:** 判断点击是否被视为"危险操作"；若 `type` 为 `null` 或满足以下任一条件即返回 `true`：
        - `type.isShiftClick()`
        - `type.isKeyboardClick()`
        - `type.isCreativeAction()`
        - `type == ClickType.DOUBLE_CLICK`
        - `type == ClickType.SWAP_OFFHAND`
        - `type == ClickType.CONTROL_DROP`
        - `type == ClickType.NUMBER_KEY`
        - `type == ClickType.DROP`
        - `type == ClickType.UNKNOWN`

**4. 注意事项 (Cautions)**
  * **空值处理：** 所有方法均对 `null` 进行安全判定，返回 `false`（或 `isDangerous` 返回 `true`）。
  * **跨版本兼容：** 不同 Minecraft 版本新增/移除 `ClickType` 枚举时，应及时复核危险判定逻辑。
  * **业务层补充：** 工具类仅基于 ClickType 判定；复杂交互（拖拽、批量移动）仍需业务层配合判断其他事件字段。

**5. 典型开发陷阱与边界条件 (Pitfalls & Edge Cases)**

- **主副手 / 盔甲架特殊交互**：`F` 键交换、盔甲架手持槽、装备槽可被玩家操作但常被遗漏。部分分支服务器（如 Mohist）对副手事件行为不同，需回退至手动检测 `hotbar button == 40`。
- **无容器情况下的快捷键**：玩家仅打开背包时仍能 Shift-Click / 数字键移动物品，需要单独判定 `clickedInventory == player.getInventory()`。 