package cn.drcomo.corelib.database;

import org.bukkit.plugin.Plugin;
import java.sql.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.File;
import java.io.InputStream;

/**
 * SQLite 数据库工具，管理连接、初始化表结构以及
 * 基础 CRUD 操作。
 * <p>
 * 请在构造时传入所需的插件实例、相对路径
 * 和初始化脚本列表。
 * </p>
 */
public class SQLiteDB {

    private final Plugin plugin;
    private final String dbFilePath;
    private final List<String> initScripts;
    private Connection connection;

    /**
     * 构造方法。
     *
     * @param plugin       Bukkit 插件实例，由调用者提供
     * @param relativePath 数据库文件相对于插件数据目录的路径
     * @param scripts      初始化或升级表结构的 SQL 脚本路径列表
     */
    public SQLiteDB(Plugin plugin, String relativePath, List<String> scripts) {
        this.plugin = plugin;
        File file = new File(plugin.getDataFolder(), relativePath);
        this.dbFilePath = file.getAbsolutePath();
        this.initScripts = scripts != null ? scripts : new ArrayList<>();
    }

    /**
     * 打开数据库连接。
     * 如果所在目录不存在将会自动创建。
     *
     * @throws SQLException 连接或连接配置失败时抛出
     */
    public void connect() throws SQLException {
        File file = new File(dbFilePath);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
        this.connection = DriverManager.getConnection("jdbc:sqlite:" + dbFilePath);
        this.connection.setAutoCommit(false);
    }

    /**
     * 回滚当前交易并关闭数据库连接。
     * 如果连接已被关闭将会被忽略。
     */
    public void disconnect() {
        if (connection != null) {
            try {
                if (!connection.getAutoCommit()) {
                    connection.rollback();
                }
            } catch (SQLException ignored) {
            }
            try {
                connection.close();
            } catch (SQLException ignored) {
            }
            connection = null;
        }
    }

    /**
     * 执行构造器传入的 SQL 脚本，用于初始化或升级 schema。
     * 不存在的脚本将被空过。
     *
     * @throws SQLException 执行 SQL 语句时出现错误
     * @throws IOException  脚本读取失败时抛出
     */
    public void initializeSchema() throws SQLException, IOException {
        for (String path : initScripts) {
            try (InputStream in = plugin.getResource(path)) {
                if (in == null) {
                    continue;
                }
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
                    String sql = reader.lines().collect(Collectors.joining("\n"));
                    for (String statement : sql.split(";")) {
                        String trimmed = statement.trim();
                        if (!trimmed.isEmpty()) {
                            try (PreparedStatement ps = connection.prepareStatement(trimmed)) {
                                ps.executeUpdate();
                            }
                        }
                    }
                }
            }
        }
        if (!connection.getAutoCommit()) {
            connection.commit();
        }
    }

    /**
     * 执行 INSERT/UPDATE/DELETE 语句。
     *
     * @param sql    带有 '?' 下标位置符的 SQL 语句
     * @param params 语句中下标的参数列表
     * @return 变更的行数
     * @throws SQLException 执行失败时抛出
     */
    public int executeUpdate(String sql, Object... params) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            setParams(ps, params);
            int rows = ps.executeUpdate();
            if (!connection.getAutoCommit()) {
                connection.commit();
            }
            return rows;
        }
    }

    /**
     * 基于指定 SQL 进行单行查询。
     *
     * @param sql     带下标的 SQL 语句
     * @param handler 给定如何由 ResultSet 构造实例
     * @param params  语句中下标的参数
     * @param <T>     返回对象类型
     * @return 如果无结果则为 {@code null}
     * @throws SQLException 执行期间失败时抛出
     */
    public <T> T queryOne(String sql, ResultSetHandler<T> handler, Object... params) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            setParams(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? handler.handle(rs) : null;
            }
        }
    }

    /**
     * 基于指定 SQL 查询多行数据。
     *
     * @param sql     带下标的 SQL 语句
     * @param handler 从 ResultSet 构造实体的回调
     * @param params  语句中下标的参数
     * @param <T>     返回的实体类型
     * @return 查询的结果列表，从不为 {@code null}
     * @throws SQLException 执行期间失败时抛出
     */
    public <T> List<T> queryList(String sql, ResultSetHandler<T> handler, Object... params) throws SQLException {
        List<T> list = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            setParams(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(handler.handle(rs));
                }
            }
        }
        return list;
    }

    /**
     * 在单个事务中执行多个更新操作。
     * 当出现异常时不会提交并全部回滚。
     *
     * @param callback 事务中需要执行的逻辑
     * @throws SQLException 事务中的数据操作失败时抛出
     */
    public void transaction(SQLRunnable callback) throws SQLException {
        boolean old = connection.getAutoCommit();
        connection.setAutoCommit(false);
        try {
            callback.run(this);
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(old);
        }
    }

    private void setParams(PreparedStatement ps, Object... params) throws SQLException {
        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
        }
    }

    /**
     * 结果集处理器。
     *
     * @param <T> 转换成的类型
     */
    public interface ResultSetHandler<T> {
        /**
         * 从 {@link ResultSet} 解析一行数据并转成实体。
         *
         * @param rs JDBC 结果集
         * @return 转换后的实体
         * @throws SQLException 解析过程出现错误时抛出
         */
        T handle(ResultSet rs) throws SQLException;
    }

    /**
     * 事务回调。
     */
    public interface SQLRunnable {
        /**
         * 在事务中执行的逻辑。
         *
         * @param db 当前数据库工具
         * @throws SQLException SQL 操作失败时抛出
         */
        void run(SQLiteDB db) throws SQLException;
    }
}
