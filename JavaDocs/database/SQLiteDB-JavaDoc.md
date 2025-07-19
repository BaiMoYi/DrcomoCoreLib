### `SQLiteDB.java`

**1. 概述 (Overview)**

  * **完整路径:** `cn.drcomo.corelib.database.SQLiteDB`
  * **核心职责:** 管理 SQLite 连接、初始化表结构，并提供最基础的增删改查以及事务执行能力。它遵循零硬编码和控制反转原则，由调用方传入数据库路径和初始化脚本。

**2. 如何实例化 (Initialization)**

  * **核心思想:** 使用依赖注入的方式，将 `Plugin` 实例、相对路径以及初始化 SQL 脚本列表在构造时提供给该类。这样库本身不关心具体的文件位置与表结构。
  * **构造函数:** `public SQLiteDB(Plugin plugin, String relativePath, List<String> initScripts)`
  * **代码示例:**
    ```java
    Plugin myPlugin = this;
    List<String> scripts = Arrays.asList("schema.sql");
    SQLiteDB db = new SQLiteDB(myPlugin, "data/mydb.sqlite", scripts);
    db.connect();
    db.initializeSchema();
    ```

**3. 公共API方法 (Public API Methods)**

  * #### `connect()`

      * **返回类型:** `void`
      * **功能描述:** 打开数据库连接并关闭自动提交。如果路径父目录不存在会自动创建。
      * **参数说明:** 无

  * #### `disconnect()`

      * **返回类型:** `void`
      * **功能描述:** 回滚未提交的事务并关闭连接，忽略关闭过程中的异常。
      * **参数说明:** 无

  * #### `initializeSchema()`

      * **返回类型:** `void`
      * **功能描述:** 按顺序读取构造时提供的 SQL 脚本，以分号为界执行每条语句，用于建表或升级数据库结构。
      * **参数说明:** 无

  * #### `executeUpdate(String sql, Object... params)`

      * **返回类型:** `int`
      * **功能描述:** 使用 `PreparedStatement` 执行 INSERT、UPDATE 或 DELETE 语句并返回受影响的行数。
      * **参数说明:**
          * `sql` (`String`): 包含 `?` 占位符的 SQL 语句。
          * `params` (`Object...`): 填充占位符的参数列表。

  * #### `queryOne(String sql, ResultSetHandler<T> handler, Object... params)`

      * **返回类型:** `T`
      * **功能描述:** 执行查询并返回第一行结果，不存在记录时返回 `null`。
      * **参数说明:**
          * `sql` (`String`): 查询语句。
          * `handler` (`ResultSetHandler<T>`): 将 `ResultSet` 转为实体的回调。
          * `params` (`Object...`): 占位符参数。

  * #### `queryList(String sql, ResultSetHandler<T> handler, Object... params)`

      * **返回类型:** `List<T>`
      * **功能描述:** 执行查询并将所有结果转换为列表，永不返回 `null`。
      * **参数说明:** 同 `queryOne`

  * #### `transaction(SQLRunnable callback)`

      * **返回类型:** `void`
      * **功能描述:** 关闭自动提交后执行回调内的多次数据库更新，若发生异常则回滚，否则提交。
      * **参数说明:**
          * `callback` (`SQLRunnable`): 在事务中执行的逻辑。

**4. 内部接口 (Inner Interfaces)**

  * #### `ResultSetHandler<T>`

      * **核心职责:** 将 `ResultSet` 中的一行转换为目标类型 `T`。
      * **关键方法:** `T handle(ResultSet rs) throws SQLException`

  * #### `SQLRunnable`

      * **核心职责:** 在事务中执行自定义数据库操作。
      * **关键方法:** `void run(SQLiteDB db) throws SQLException`
