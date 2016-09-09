package net.hungerstruck.renaissance.modules

import com.google.common.collect.Sets
import net.hungerstruck.renaissance.match.RMatch
import net.hungerstruck.renaissance.xml.builder.inject
import net.hungerstruck.renaissance.xml.module.RModule
import net.hungerstruck.renaissance.xml.module.RModuleContext
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.EntityType
import org.bukkit.entity.Projectile
import org.bukkit.entity.TNTPrimed
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityExplodeEvent
import org.bukkit.event.entity.ExplosionPrimeEvent
import org.bukkit.event.entity.ProjectileLaunchEvent
import java.util.*

// TODO: Future plans: Link this up with future inventory module to allow creation of different projectiles as inventory items which can be given, along with update this module to affect all projectiles within a specified projectile item (as opposed to globally)
class ProjectileModule(match: RMatch, modCtx: RModuleContext) : RModule(match, modCtx) {

    @inject val underwaterVelocityModifier = 0.6F

    override fun init() {
        registerEvents()
    }

    @EventHandler
    fun onProjectileLaunch(event: ProjectileLaunchEvent) {
        event.entity.underwaterVelocityModifier = underwaterVelocityModifier
    }


}