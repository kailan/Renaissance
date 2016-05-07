package net.hungerstruck.renaissance.settings

import me.anxuiz.settings.Setting
import me.anxuiz.settings.SettingBuilder
import me.anxuiz.settings.SettingCallbackManager
import me.anxuiz.settings.SettingRegistry
import me.anxuiz.settings.bukkit.PlayerSettings
import me.anxuiz.settings.types.BooleanType
import me.anxuiz.settings.types.EnumType

object Settings {
    val SCOREBOARD_OPTIONS : Setting = SettingBuilder().name("Scoreboard").alias("sb").summary("Information scoreboard").type(BooleanType()).defaultValue(true).get()
    val BLOOD_OPTIONS : Setting =  SettingBuilder().name("Blood").alias("b").summary("Blood particles when you get hit").type(BooleanType()).defaultValue(true).get()
    val SPECTATOR_OPTIONS : Setting = SettingBuilder().name("spectators").alias("s").summary("View spectators when spectating").type(BooleanType()).defaultValue(false).get()

    fun register() {
        val registry = PlayerSettings.getRegistry()
        val callbacks = PlayerSettings.getCallbackManager()
        registry.register(SCOREBOARD_OPTIONS)
        registry.register(BLOOD_OPTIONS)
        registry.register(SPECTATOR_OPTIONS)

        callbacks.addCallback(SCOREBOARD_OPTIONS, ScoreboardChangeCallback())
    }
}