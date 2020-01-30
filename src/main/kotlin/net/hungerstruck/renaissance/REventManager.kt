package net.hungerstruck.renaissance

import org.apache.commons.io.FileUtils
import org.bukkit.entity.Player
import java.io.File
import java.util.*

/**
 * Manages special cases for events (such as playtests, tournaments etc).
 *
 * Created by molenzwiebel on 23-04-16.
 */
class REventManager {
    private val forcedSpectators: MutableList<UUID>

    constructor() {
        forcedSpectators = try {
            val contents = FileUtils.readFileToString(File("forced-spectators.txt"))
            ArrayList(contents.split("\n").map { UUID.fromString(it) })
        } catch (ignored: Exception) {
            ArrayList()
        }
    }

    fun addForcedSpectator(id: UUID) = forcedSpectators.add(id)
    fun removeForcedSpectator(id: UUID) = forcedSpectators.remove(id)

    fun isForcedSpectator(id: UUID) = forcedSpectators.contains(id)
    fun isForcedSpectator(p: Player) = forcedSpectators.contains(p.uniqueId)

    fun getForcedSpectators() = forcedSpectators
}