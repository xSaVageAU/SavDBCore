package savage.savdbcore.config;

public class DBCoreConfig {
    public StorageConfig storage = new StorageConfig();
    public RedisConfig redis = new RedisConfig();

    public static class StorageConfig {
        public String type = "SQLITE"; // SQLITE, MYSQL, POSTGRESQL
        public String host = "localhost";
        public int port = 3306;
        public String database = "database";
        public String user = "root";
        public String password = "password";
        public String tablePrefix = "db_";
        public int poolSize = 10;
        public long connectionTimeout = 30000;
        public long idleTimeout = 600000;
    }

    public static class RedisConfig {
        public boolean enabled = false;
        public String host = "localhost";
        public int port = 6379;
        public String password = "";
        public String channel = "dbcore-updates";
        public boolean debugLogging = false;
    }

    public enum StorageType {
        SQLITE,
        MYSQL,
        POSTGRESQL
    }
}
