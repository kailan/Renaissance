package net.hungerstruck.renaissance.modules

import net.hungerstruck.renaissance.match.RMatch
import net.hungerstruck.renaissance.xml.builder.inject
import net.hungerstruck.renaissance.xml.module.RModule
import net.hungerstruck.renaissance.xml.module.RModuleContext
import org.bukkit.entity.EntityType
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.ProjectileLaunchEvent

// TODO: Future plans: Link this up with future inventory module to allow creation of different projectiles as inventory items which can be given, along with update this module to affect all projectiles within a specified projectile item (as opposed to globally)
class ProjectileModule(match: RMatch, modCtx: RModuleContext) : RModule(match, modCtx) {
    @inject var underwaterVelocityModifier: Map<EntityType, Float> = mapOf()

    override fun init() {
        registerEvents()
    }

    @EventHandler
    fun onProjectileLaunch(event: ProjectileLaunchEvent) {
        if(underwaterVelocityModifier.containsKey(event.entity.type) && underwaterVelocityModifier[event.entity.type] != null)
            event.entity.underwaterVelocityModifier = underwaterVelocityModifier[event.entity.type]!!
    }


}