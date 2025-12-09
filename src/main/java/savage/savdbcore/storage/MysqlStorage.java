package savage.savdbcore.storage;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import savage.savdbcore.config.DBCoreConfig;

/**
 * MySQL storage implementation using HikariCP connection pooling.
 */
public class MysqlStorage extends SqlStorage {
    private final String host;
    private final int port;
    private final String database;
    private final String user;
    private final String password;
    private final DBCoreConfig.StorageConfig config;

    public MysqlStorage(String host, int port, String database, String user, String password, String tablePrefix, DBCoreConfig.StorageConfig config) {
        super(tablePrefix);
        this.host = host;
        this.port = port;
        this.database = database;
        this.user = user;
        this.password = password;
        this.config = config;
    }

    @Override
    protected void setupDataSource() {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database);
        hikariConfig.setUsername(user);
        hikariConfig.setPassword(password);
        hikariConfig.setDriverClassName("com.mysql.cj.jdbc.Driver");
        
        // MySQL-specific optimizations
        hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        hikariConfig.addDataSourceProperty("useServerPrepStmts", "true");
        hikariConfig.addDataSourceProperty("useLocalSessionState", "true");
        hikariConfig.addDataSourceProperty("rewriteBatchedStatements", "true");
        hikariConfig.addDataSourceProperty("cacheResultSetMetadata", "true");
        hikariConfig.addDataSourceProperty("cacheServerConfiguration", "true");
        hikariConfig.addDataSourceProperty("elideSetAutoCommits", "true");
        hikariConfig.addDataSourceProperty("maintainTimeStats", "false");

        // Apply pool settings
        hikariConfig.setMaximumPoolSize(config.poolSize);
        hikariConfig.setConnectionTimeout(config.connectionTimeout);
        hikariConfig.setIdleTimeout(config.idleTimeout);

        this.dataSource = new HikariDataSource(hikariConfig);
    }
}
