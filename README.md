# Savs DB Core

A Fabric library mod providing database and Redis utilities for Minecraft server-side mods.

## Features

- **Multi-Database Support**: MySQL, PostgreSQL, and SQLite with HikariCP connection pooling
- **Redis Pub/Sub**: Lettuce-based Redis client for cross-server communication
- **Async Utilities**: Thread pool executor for async database operations
- **Config Management**: JSON configuration loading and saving with Gson
- **JAR-in-JAR Bundling**: All dependencies bundled - no separate downloads needed

## For Mod Developers

### Adding to Your Project

Add to your `build.gradle`:

```gradle
repositories {
    mavenCentral()
    mavenLocal() // For local development
}

dependencies {
    modImplementation "savage.savdbcore:savdbcore:1.0.0"
    include "savage.savdbcore:savdbcore:1.0.0"
    
    // Compile-time only (provided by savdbcore at runtime)
    compileOnly "com.zaxxer:HikariCP:5.1.0"
    compileOnly "com.github.ben-manes.caffeine:caffeine:3.1.6" // If using Caffeine
}
```

### Usage Example

```java
import savage.savdbcore.storage.*;
import savage.savdbcore.config.DBCoreConfig;
import java.sql.Connection;

// Configure storage
DBCoreConfig.StorageConfig config = new DBCoreConfig.StorageConfig();
config.poolSize = 10;
config.connectionTimeout = 30000;
config.idleTimeout = 600000;

// Create SQLite storage
SqliteStorage storage = new SqliteStorage(
    "my-mod",           // Mod ID
    "data.sqlite",      // Database filename
    "mymod_",          // Table prefix
    config
);

// Initialize
storage.initialize();

// Use connection
try (Connection conn = storage.getConnection()) {
    // Your SQL queries here
    PreparedStatement stmt = conn.prepareStatement("SELECT * FROM mymod_table");
    ResultSet rs = stmt.executeQuery();
    // ...
}

// Shutdown when done
storage.shutdown();
```

### Redis Example

```java
import savage.savdbcore.redis.RedisManager;
import savage.savdbcore.config.DBCoreConfig;

// Configure Redis
DBCoreConfig.RedisConfig config = new DBCoreConfig.RedisConfig();
config.enabled = true;
config.host = "localhost";
config.port = 6379;
config.channel = "my-channel";

// Create manager
RedisManager redis = new RedisManager(config);

// Set message handler
redis.setMessageHandler(message -> {
    System.out.println("Received: " + message);
});

// Connect
redis.connect();

// Publish messages
redis.publish(new MyMessage("Hello", "World"));

// Shutdown when done
redis.shutdown();
```

## Included Dependencies

All dependencies are bundled via JAR-in-JAR:

- HikariCP 5.1.0 (Connection Pooling)
- MySQL Connector/J 8.4.0
- PostgreSQL JDBC 42.7.3
- SQLite JDBC 3.46.0.0
- Lettuce Core 6.3.0.RELEASE (Redis Client)
- Reactor Core 3.5.11
- Reactive Streams 1.0.4
- Caffeine 3.1.8 (Caching)

## For Server Owners

This is a **library mod** - it doesn't add any gameplay features on its own. It's automatically bundled inside mods that use it (like Savs-Common-Economy), so you don't need to download it separately.

## Building

```bash
./gradlew build
```

The built JAR will be in `build/libs/`.

## Publishing to Maven Local

For local development:

```bash
./gradlew publishToMavenLocal
```

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Mods Using SavDBCore

- [Savs-Common-Economy](https://github.com/xSaVageAU/Savs-Common-Economy) - Economy system for Fabric servers

## Version Compatibility

- Minecraft: 1.21.10
- Fabric Loader: 0.18.1+
- Java: 21+
