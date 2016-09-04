package net.hungerstruck.renaissance.modules

import net.hungerstruck.renaissance.match.RMatch
import net.hungerstruck.renaissance.xml.builder.inject
import net.hungerstruck.renaissance.xml.module.RModule
import net.hungerstruck.renaissance.xml.module.RModuleContext
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.entity.TNTPrimed
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityExplodeEvent

class TNTSettingsModule(match: RMatch, modCtx: RModuleContext) : RModule(match, modCtx) {
    @inject val blockDamage = false
    @inject val instantIgnite = false
    @inject val `yield`: Float = -1.0f

    override fun init() {
        registerEvents()
    }

    @EventHandler
    fun onEntityExplodeEvent(event: EntityExplodeEvent) {
        if (!isMatch(event.world)) return
        if (!(event.getEntity() is TNTPrimed)) return;

        // TODO: Underwater block explosions

        if(!blockDamage)
            event.blockList().clear()

        if(`yield` != -1.0f)
            event.`yield` = `yield`.toFloat()

    }

    @EventHandler
    fun onTNTPlaceEvent(event: BlockPlaceEvent) {
        if (!isMatch(event.world)) return
        if (!event.block.type.equals(Material.TNT)) return
        if (instantIgnite) {
            event.block.type = Material.AIR
            event.block.world.spawnEntity(Location(event.block.world, event.block.x + 0.5, event.block.y + 0.5, event.block.z + 0.5), EntityType.PRIMED_TNT)
        }
    }
}