package net.hungerstruck.renaissance.modules.ux

import net.minecraft.server.v1_8_R3.EnumParticle

/**
 * Created by teddy on 31/03/2016.
 */
enum class RParticleType private constructor(val id: Int, val particle: EnumParticle) {
    EXPLODE(0, EnumParticle.EXPLOSION_NORMAL),
    LARGE_EXPLOSION(1, EnumParticle.EXPLOSION_LARGE),
    HUGE_EXPLOSION(2, EnumParticle.EXPLOSION_HUGE),
    FOREWORK_SPARK(3, EnumParticle.FIREWORKS_SPARK),
    BUBBLE(4, EnumParticle.WATER_BUBBLE),
    SPLASH(5, EnumParticle.WATER_SPLASH),
    WAKE(6, EnumParticle.WATER_WAKE),
    SUSPEND(7, EnumParticle.SUSPENDED),
    DEATH_SUSPEND(8, EnumParticle.SUSPENDED_DEPTH),
    CRIT(9, EnumParticle.CRIT),
    MAGIC_CRIT(10, EnumParticle.CRIT_MAGIC),
    SMOKE(11, EnumParticle.SMOKE_NORMAL),
    LARGE_SMOKE(12, EnumParticle.SMOKE_LARGE),
    SPELL(13, EnumParticle.SPELL),
    INSTANT_SPELL(14, EnumParticle.SPELL_INSTANT),
    MOB_SPELL(15, EnumParticle.SPELL_MOB),
    MOB_SPELL_AMBIENT(16, EnumParticle.SPELL_MOB_AMBIENT),
    WITCH_MAGIC(17, EnumParticle.SPELL_WITCH),
    DRIP_WATER(18, EnumParticle.DRIP_WATER),
    DRIP_LAVA(19, EnumParticle.DRIP_LAVA),
    ANGRY_VILLAGER(20, EnumParticle.VILLAGER_ANGRY),
    HAPPY_VILLAGER(21, EnumParticle.VILLAGER_HAPPY),
    TOWN_AURA(22, EnumParticle.TOWN_AURA),
    NOTE(23, EnumParticle.NOTE),
    PORTAL(24, EnumParticle.PORTAL),
    ENCHANTMENT_TABLE(25, EnumParticle.ENCHANTMENT_TABLE),
    FLAME(26, EnumParticle.FLAME),
    LAVA(27, EnumParticle.LAVA),
    FOOT_STEP(28, EnumParticle.FOOTSTEP),
    CLOUD(29, EnumParticle.CLOUD),
    RED_DUST(30, EnumParticle.REDSTONE),
    SNOWBALL_POOF(31, EnumParticle.SNOWBALL),
    SNOW_SHOVEL(32, EnumParticle.SNOW_SHOVEL),
    SLIVE(33, EnumParticle.SLIME),
    HEART(34, EnumParticle.HEART),
    BARRIER(35, EnumParticle.BARRIER),
    ICON_CRACK(36, EnumParticle.ITEM_CRACK),
    BLOCK_CRACK(37, EnumParticle.BLOCK_CRACK),
    BLOCK_DUST(38, EnumParticle.BLOCK_DUST),
    DROPLET(39, EnumParticle.WATER_DROP),
    TAKE(40, EnumParticle.ITEM_TAKE),
    MOB_APPEARANCE(41, EnumParticle.MOB_APPEARANCE)
}