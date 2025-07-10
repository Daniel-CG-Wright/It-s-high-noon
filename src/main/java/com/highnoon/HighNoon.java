package com.highnoon;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HighNoon implements ModInitializer {
	public static final String MOD_ID = "high_noon";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Hello Fabric world!");

		DuelManager.init();        // registers event listeners
		CommandRegistrationCallback.EVENT.register(DuelCommands::register);
		ServerLivingEntityEvents.ALLOW_DEATH.register(DuelManager::onPlayerDeath);
		// load persisted stats
		// StatsManager.loadAll();
	}
}