package savage.savdbcore.storage;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.fabricmc.loader.api.FabricLoader;
import savage.savdbcore.config.DBCoreConfig;

import java.io.File;
import java.nio.file.Path;

/**
 * SQLite storage implementation using HikariCP connection pooling.
 * Uses file-based database with WAL mode for better concurrency.
 */
public class SqliteStorage extends SqlStorage {
    private final File databaseFile;
    private final DBCoreConfig.StorageConfig config;

    /**
     * Create SQLite storage with custom database file location.
     * @param databaseFile The SQLite database file
     * @param tablePrefix Table prefix for all tables
     * @param config Storage configuration
     */
    public SqliteStorage(File databaseFile, String tablePrefix, DBCoreConfig.StorageConfig config) {
        super(tablePrefix);
        this.databaseFile = databaseFile;
        this.config = config;
        
        // Ensure parent directory exists
        if (databaseFile.getParentFile() != null) {
            databaseFile.getParentFile().mkdirs();
        }
    }

    /**
     * Create SQLite storage in the mod's config directory.
     * @param modId The mod ID (used for directory name)
     * @param databaseName The database file name (e.g., "data.sqlite")
     * @param tablePrefix Table prefix for all tables
     * @param config Storage configuration
     */
    public SqliteStorage(String modId, String databaseName, String tablePrefix, DBCoreConfig.StorageConfig config) {
        this(getDefaultDatabaseFile(modId, databaseName), tablePrefix, config);
    }

    private static File getDefaultDatabaseFile(String modId, String databaseName) {
        Path configDir = FabricLoader.getInstance().getConfigDir().resolve(modId);
        configDir.toFile().mkdirs();
        return configDir.resolve(databaseName).toFile();
    }

    @Override
    protected void setupDataSource() {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl("jdbc:sqlite:" + databaseFile.getAbsolutePath());
        hikariConfig.setDriverClassName("org.sqlite.JDBC");
        
        // SQLite-specific optimizations
        hikariConfig.addDataSourceProperty("journal_mode", "WAL");
        hikariConfig.addDataSourceProperty("synchronous", "NORMAL");
        
        // Apply pool settings
        hikariConfig.setMaximumPoolSize(config.poolSize);
        hikariConfig.setConnectionTimeout(config.connectionTimeout);
        hikariConfig.setIdleTimeout(config.idleTimeout);
        
        this.dataSource = new HikariDataSource(hikariConfig);
    }

    public File getDatabaseFile() {
        return databaseFile;
    }
}
