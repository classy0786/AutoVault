package com.autovault;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AutoVault implements ModInitializer {
    public static final String MOD_ID = "autovault";
    public static final Logger LOGGER = 
        LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("AutoVault Mod Loaded!");
    }
}
