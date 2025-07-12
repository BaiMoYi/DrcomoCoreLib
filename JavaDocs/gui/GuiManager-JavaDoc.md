### `GuiManager.java`

**1. 概述 (Overview)**

  * **完整路径:** `cn.drcomo.corelib.gui.GuiManager`
  * **核心职责:** 提供与 GUI 操作相关的辅助能力，包括危险点击检测、光标清理以及安全播放音效；

**2. 如何实例化 (Initialization)**

  * **核心思想:** 这是一个<em>可实例化</em>的服务类，每个子插件应为其创建独立实例，并通过构造函数注入 `DebugUtil` 以获得符合自身日志级别的输出。
  * **构造函数:** `public GuiManager(DebugUtil logger)`
  * **代码示例:**
    ```java
    // 在你的子插件 onEnable() 方法中
    DebugUtil logger = new DebugUtil(this, DebugUtil.LogLevel.DEBUG);

    GuiManager guiManager = new GuiManager(logger);
    ```

**3. 公共API方法 (Public API Methods)**

  * #### `boolean isDangerousClick(ClickType click)`

    * **功能描述:**
      判断一次 Bukkit `ClickType` 是否属于**危险交互**，若命中则应在 GUI 层面立即拦截，防止非法放置、物品复制或丢失等问题。
    * **参数说明:**
      * `click` (`ClickType`) — 触发事件的点击类型；传入 `null` 时同样视为危险操作。
    * **返回值:**
      * `true` — 危险点击，需拦截；
      * `false` — 安全点击，可继续处理。
    * **判定逻辑:**
      依次匹配下列任一条件即返回 `true`：
        - `click.isShiftClick()`     // Shift-单击或 Shift-双击
        - `click.isKeyboardClick()`   // 数字键、Q 键等快捷键操作
        - `click.isCreativeAction()`  // 创意模式特有点击
        - `click == ClickType.DOUBLE_CLICK` // 双击
        - `click == ClickType.SWAP_OFFHAND` // 主/副手物品交换
        - `click == ClickType.CONTROL_DROP` // Ctrl + Q 丢弃
        - `click == ClickType.NUMBER_KEY`  // 数字键替换槽位
        - `click == ClickType.DROP`     // Q 键丢弃
        - `click == ClickType.UNKNOWN`   // 未知类型（保险起见必拦截）

  * #### `void clearCursor(Player player, InventoryClickEvent event)`

      * **功能描述:** 清空玩家鼠标光标上的物品堆，更新背包视图；若出现异常将记录到日志。
      * **参数说明:**
          * `player` (`Player`): 目标玩家。
          * `event` (`InventoryClickEvent`): 原始事件对象。

  * #### `void safePlaySound(Player player, Sound sound, float volume, float pitch)`

      * **功能描述:** 在指定玩家位置播放音效；如播放失败（版本兼容或其他异常），异常将被捕获并记录。
      * **参数说明:**
          * `player` (`Player`): 目标玩家。
          * `sound` (`Sound`): 音效枚举。
          * `volume` (`float`): 音量 (0.0 ~ 1.0)。
          * `pitch` (`float`): 音调 (0.5 ~ 2.0)。

**4. 注意事项 (Cautions)**
  * **光标清理边界：**
    - 清理光标物品时应先判断业务场景，避免误删玩家重要物品。
    - 建议只在自定义 GUI 区域内调用 clearCursor，玩家背包操作应放行。

  * **危险点击检测局限：**
    - isDangerousClick 仅基于 ClickType 判断，部分特殊操作需业务层二次校验。
    - 对于复杂交互（如拖拽、批量操作），建议结合事件类型和业务逻辑综合判断。

  * **批量物品处理建议：**
    - 大量物品操作建议分批处理，避免主线程卡顿。
    - 物品发放失败时应有兜底逻辑（如自动掉落）。

  * **与业务层协作：**
    - GuiManager 只做通用处理，具体业务规则应由调用方实现。

----- 