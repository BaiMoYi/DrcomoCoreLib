### `SoundManager.java`

**1. 概述 (Overview)**

  * **完整路径:** `cn.drcomo.corelib.sound.SoundManager`
  * **核心职责:** 一个健壮的音效管理类，用于从指定的 YAML 配置文件中加载、缓存和播放音效。它允许开发者将音效的定义（音效名称、音量、音调）与代码逻辑分离，并通过一个简单的键来触发播放，同时支持全局音量控制。
  > 自 v1.1 起，已支持带命名空间的自定义音效（如 `glacia_sounds:samus.ice_beam`）

**2. 如何实例化 (Initialization)**

  * **核心思想:** `SoundManager` 的设计遵循"依赖注入"和"配置优先"的原则。实例化时，需要提供 `Plugin`, `YamlUtil`, `DebugUtil` 等核心依赖，并且明确指定用于存储音效配置的 `*.yml` 文件名，以及全局音量调节参数。
  * **构造函数:** `public SoundManager(Plugin plugin, YamlUtil yamlUtil, DebugUtil logger, String configName, float volumeMultiplier, boolean warnOnMissingKeys)`
  * **代码示例:**
    ```java
    // 在你的子插件 onEnable() 方法中:
    Plugin myPlugin = this;
    DebugUtil myLogger = new DebugUtil(myPlugin, DebugUtil.LogLevel.INFO);
    YamlUtil myYamlUtil = new YamlUtil(myPlugin, myLogger);

    // 假设你的音效配置存储在 'sounds.yml' 文件中
    String soundConfigFileName = "sounds"; // 无需 .yml 后缀

    SoundManager soundManager = new SoundManager(
        myPlugin,
        myYamlUtil,
        myLogger,
        soundConfigFileName, // 告诉管理器去哪里找音效配置
        1.0f,                // 全局音量倍率，1.0f 代表不增不减
        true                 // 如果播放一个不存在的音效键，在控制台打印警告
    );

    // 在你的插件数据文件夹里，需要有一个 'sounds.yml' 文件，内容格式如下：
    // level_up: 'ENTITY_PLAYER_LEVELUP-1.0-1.2'
    // craft_success: 'BLOCK_ANVIL_USE-0.8-1.5'
    // ice_beam: 'glacia_sounds:samus.ice_beam'          # 仅名称，默认音量/音调皆为 1.0
    // crimson_loop: 'minecraft:ambient.crimson_forest.loop-0.5' # 指定音量，音调默认为 1.0
    // amethyst_chime: 'minecraft:block.amethyst_block.chime-0.1-2' # 指定音量与音调
    // 格式: 'Name[-Volume][-Pitch]'，Volume 与 Pitch 可选

    // 最重要的一步：实例化后，必须调用 loadSounds() 来加载配置
    soundManager.loadSounds();

    myLogger.info("音效管理器已加载 " + soundManager.getCachedSoundCount() + " 个音效。");
    ```

**3. 公共API方法 (Public API Methods)**

  * #### `loadSounds()`

      * **返回类型:** `void`
      * **功能描述:** 从构造时指定的 YAML 配置文件中读取所有音效定义，并将其解析、缓存到内存中，以备后续快速播放。这是在实例化之后必须调用的初始化方法。
      * **参数说明:** 无。

  * #### `reloadSounds()`

      * **返回类型:** `void`
      * **功能描述:** 清空现有音效缓存，并重新执行 `loadSounds()` 流程。用于在不重启服务器的情况下，热重载音效配置。
      * **参数说明:** 无。

  * #### `hasSound(String key)`

      * **返回类型:** `boolean`
      * **功能描述:** 检查缓存中是否存在指定键的音效。
      * **参数说明:**
          * `key` (`String`): 音效在配置文件中的键，例如 `"level_up"`。

  * #### `getCachedSoundCount()`

      * **返回类型:** `int`
      * **功能描述:** 获取当前已加载并缓存的音效总数。
      * **参数说明:** 无。

  * #### `getAvailableSoundKeys()`

      * **返回类型:** `Set<String>`
      * **功能描述:** 获取所有已成功加载的音效键的集合。
      * **参数说明:** 无。

  * #### `playSound(Player player, String key)`

      * **返回类型:** `void`
      * **功能描述:** 在指定玩家的位置，为该玩家播放一个预定义的音效。
      * **参数说明:**
          * `player` (`Player`): 播放音效的目标玩家。
          * `key` (`String`): 音效的键。
      * **使用示例:**
        ```java
        // 当玩家升级时
        soundManager.playSound(player, "level_up");
        ```

  * #### `playSoundAtLocation(Location loc, String key)`

      * **返回类型:** `void`
      * **功能描述:** 在世界中的一个特定位置播放音效，该位置附近的所有玩家都能听到。
      * **参数说明:**
          * `loc` (`Location`): 音效播放的中心位置。
          * `key` (`String`): 音效的键。

  * #### `playSoundInRadius(Location center, String key, double radius)`

      * **返回类型:** `void`
      * **功能描述:** 在指定中心点的特定半径范围内，为所有玩家播放音效。
      * **参数说明:**
          * `center` (`Location`): 音效播放的中心。
          * `key` (`String`): 音效的键。
          * `radius` (`double`): 听得见音效的最大半径（单位：方块）。

  * #### `playSoundFromString(Player player, String soundString)`

      * **返回类型:** `void`
      * **功能描述:** 直接根据一个音效定义字符串，为玩家播放音效，而无需预先在配置文件中定义。
      * **参数说明:**
          * `player` (`Player`): 目标玩家。
          * `soundString` (`String`): 格式为 `"Name[-Volume][-Pitch]"` 的字符串，例如 `"UI_BUTTON_CLICK-1.0-1.0"`、`"minecraft:block.amethyst_block.chime-0.1-2"`。

  * #### `playSoundFromStringInRadius(Location center, String soundString, double radius)`

      * **返回类型:** `void`
      * **功能描述:** 在指定位置一定半径内，根据字符串定义播放音效。
      * **参数说明:**
          * `center` (`Location`): 中心位置。
          * `soundString` (`String`): `"Name[-Volume][-Pitch]"` 格式的字符串。
          * `radius` (`double`): 半径（方块数）。

  * #### `playSound(String worldName, double x, double y, double z, String key)`

      * **返回类型:** `void`
      * **功能描述:** 通过世界名称和坐标在指定位置播放预定义音效。
      * **参数说明:**
          * `worldName` (`String`): 世界名称。
          * `x`, `y`, `z` (`double`): 坐标。
          * `key` (`String`): 音效键。

  * #### `playSoundInRadius(String worldName, double x, double y, double z, String key, double radius)`

      * **返回类型:** `void`
      * **功能描述:** 通过世界名称和坐标，在半径范围内播放预定义音效。
      * **参数说明:**
          * `worldName` (`String`): 世界名称。
          * `x`, `y`, `z` (`double`): 坐标。
          * `key` (`String`): 音效键。
          * `radius` (`double`): 半径（方块数）。

  * #### `playSoundFromString(String worldName, double x, double y, double z, String soundString)`

      * **返回类型:** `void`
      * **功能描述:** 通过世界名称和坐标，根据字符串定义播放音效。
      * **参数说明:**
          * `worldName` (`String`): 世界名称。
          * `x`, `y`, `z` (`double`): 坐标。
          * `soundString` (`String`): `"Name[-Volume][-Pitch]"` 格式的字符串。

  * #### `playSoundFromStringInRadius(String worldName, double x, double y, double z, String soundString, double radius)`

      * **返回类型:** `void`
      * **功能描述:** 通过世界名称和坐标，在半径范围内根据字符串定义播放音效。
      * **参数说明:**
          * `worldName` (`String`): 世界名称。
          * `x`, `y`, `z` (`double`): 坐标。
          * `soundString` (`String`): `"Name[-Volume][-Pitch]"` 格式的字符串。
          * `radius` (`double`): 半径（方块数）。

