package savage.savdbcore.storage;

import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Base interface for database storage implementations.
 * Provides connection lifecycle management.
 */
public abstract class SqlStorage {
    public HikariDataSource dataSource; // Public so subclasses in other packages can access
    protected final String tablePrefix;

    public SqlStorage(String tablePrefix) {
        this.tablePrefix = tablePrefix;
    }

    /**
     * Set up the HikariCP data source with database-specific configuration.
     * Must be implemented by subclasses (MySQL, SQLite, PostgreSQL).
     */
    protected abstract void setupDataSource();

    /**
     * Initialize the database connection and create necessary tables.
     * Call this during mod initialization.
     */
    public void initialize() {
        setupDataSource();
    }

    /**
     * Get a connection from the pool.
     * @return A database connection
     * @throws SQLException if connection fails
     */
    public Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new SQLException("DataSource not initialized. Call initialize() first.");
        }
        return dataSource.getConnection();
    }

    /**
     * Close the connection pool and release resources.
     * Call this during mod shutdown.
     */
    public void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

    /**
     * Check if the data source is initialized and not closed.
     * @return true if ready for use
     */
    public boolean isReady() {
        return dataSource != null && !dataSource.isClosed();
    }

    public String getTablePrefix() {
        return tablePrefix;
    }
}
