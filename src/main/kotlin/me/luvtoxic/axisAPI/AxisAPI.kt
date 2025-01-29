package me.luvtoxic.axisAPI

import org.bukkit.plugin.java.JavaPlugin

class AxisAPI : JavaPlugin() {

    override fun onEnable() {
        logger.info("AxisAPI has been enabled")
    }

    override fun onDisable() {
        logger.info("AxisAPI has now been disabled")
    }

    companion object {
        lateinit var instance: AxisAPI
            private set
    }

    init {
        instance = this
    }
}
