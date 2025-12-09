package savage.savdbcore.storage;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import savage.savdbcore.config.DBCoreConfig;

/**
 * PostgreSQL storage implementation using HikariCP connection pooling.
 */
public class PostgresStorage extends SqlStorage {
    private final String host;
    private final int port;
    private final String database;
    private final String user;
    private final String password;
    private final DBCoreConfig.StorageConfig config;

    public PostgresStorage(String host, int port, String database, String user, String password, String tablePrefix, DBCoreConfig.StorageConfig config) {
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
        hikariConfig.setJdbcUrl("jdbc:postgresql://" + host + ":" + port + "/" + database);
        hikariConfig.setUsername(user);
        hikariConfig.setPassword(password);
        hikariConfig.setDriverClassName("org.postgresql.Driver");
        
        // Apply pool settings
        hikariConfig.setMaximumPoolSize(config.poolSize);
        hikariConfig.setConnectionTimeout(config.connectionTimeout);
        hikariConfig.setIdleTimeout(config.idleTimeout);
        
        this.dataSource = new HikariDataSource(hikariConfig);
    }
}
