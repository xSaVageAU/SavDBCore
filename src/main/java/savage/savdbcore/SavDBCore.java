package savage.savdbcore;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Savs DB Core - Database and Redis library for Fabric server mods.
 * 
 * This mod provides:
 * This mod provides:
 * - MySQL, PostgreSQL, and SQLite storage with HikariCP connection pooling
 * - Async executor for database operations
 * - JSON config loader utilities
 * 
 * This is a library mod and does not add any gameplay features on its own.
 */
public class SavDBCore implements ModInitializer {
	public static final String MOD_ID = "savdbcore";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Savs DB Core initialized - providing database and Redis utilities");
	}
}