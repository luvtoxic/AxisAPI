package me.luvtoxic.axisAPI

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory

object AxisUtils {

    /**
     * Broadcast a formatted message using Adventure API.
     * @param message The broadcast message.
     */
    fun broadcast(message: String) {
        val component = LegacyComponentSerializer.legacy('&').deserialize(message)
        Bukkit.broadcast(component)
    }

    /**
     * Run a task asynchronously.
     * @param task The task to run.
     */
    fun runAsync(task: () -> Unit) {
        Bukkit.getScheduler().runTaskAsynchronously(AxisAPI.instance, task)
    }

    /**
     * Run a task synchronously.
     * @param task The task to run.
     */
    fun runSync(task: () -> Unit) {
        Bukkit.getScheduler().runTask(AxisAPI.instance, task)
    }

    /**
     * Schedule a repeating task.
     * @param delay Delay in ticks.
     * @param period Period between executions.
     * @param task The task to run.
     * @return Task ID for cancellation.
     */
    fun scheduleRepeatingTask(delay: Long, period: Long, task: () -> Unit): Int {
        return Bukkit.getScheduler().scheduleSyncRepeatingTask(AxisAPI.instance, task, delay, period)
    }

    /**
     * Schedule a delayed task.
     * @param delay Delay in ticks.
     * @param task Task to run.
     */
    fun scheduleDelayedTask(delay: Long, task: () -> Unit) {
        Bukkit.getScheduler().runTaskLater(AxisAPI.instance, task, delay)
    }

    /**
     * Teleport a player to a location asynchronously.
     * @param player The player to teleport.
     * @param location The target location.
     */
    fun teleportAsync(player: Player, location: Location) {
        player.teleportAsync(location).thenAccept { success ->
            if (!success) {
                sendMessage(player, "&cFailed to teleport to the specified location.")
            }
        }.exceptionally { throwable ->
            sendMessage(player, "&cAn error occurred while teleporting.")
            throwable.printStackTrace()
            null
        }
    }

    /**
     * Send a formatted message to a player with color support.
     * @param player The player to send the message to.
     * @param message The message to send.
     */
    fun sendMessage(player: Player, message: String) {
        val component = LegacyComponentSerializer.legacy('&').deserialize(message)
        player.sendMessage(component)
    }

    /**
     * Get all online players as a list.
     * @return List of all online players.
     */
    fun getOnlinePlayers(): List<Player> {
        return Bukkit.getOnlinePlayers().toList()
    }

    /**
     * Execute a command as the console.
     * @param command The command to execute.
     */
    fun executeConsoleCommand(command: String) {
        runSync {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command)
        }
    }

    /**
     * Create a simple Inventory for a player.
     * @param size Size of the Inventory.
     * @param title Title on the Inventory.
     * @return The Inventory instance.
     */
    fun createInventory(size: Int, title: String): Inventory {
        require(size in 9..54 && size % 9 == 0) { "Inventory size must be a multiple of 9 between 9 and 54." }
        return Bukkit.createInventory(null, size, LegacyComponentSerializer.legacy('&').deserialize(title))
    }

    /**
     * Get the player's current ping.
     * @param player The player whose ping to fetch.
     * @return The player's ping, or -1 if unavailable.
     */
    fun getPlayerPing(player: Player): Int {
        return try {
            val craftPlayer = player.javaClass.getMethod("getHandle").invoke(player)
            val pingField = craftPlayer.javaClass.getDeclaredField("ping")
            pingField.isAccessible = true
            pingField.getInt(craftPlayer)
        } catch (e: Exception) {
            -1
        }
    }

    /**
     * Format a location into a readable string.
     * @param location The location to format.
     * @return Formatted location string.
     */
    fun formatLocation(location: Location): String {
        return "World: ${location.world?.name}, X: ${location.blockX}, Y: ${location.blockY}, Z: ${location.blockZ}"
    }
}
