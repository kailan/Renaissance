package net.hungerstruck.renaissance.modules

import net.hungerstruck.renaissance.event.match.RMatchLoadEvent
import net.hungerstruck.renaissance.match.RMatch
import net.hungerstruck.renaissance.xml.builder.inject
import net.hungerstruck.renaissance.xml.module.Dependencies
import net.hungerstruck.renaissance.xml.module.RModule
import net.hungerstruck.renaissance.xml.module.RModuleContext
import org.bukkit.event.EventHandler

@Dependencies(TimeLockModule::class)
class TimeSetModule(match: RMatch, modCtx: RModuleContext) : RModule(match, modCtx) {
    @inject val value: Long = 0

    override fun init() {
        registerEvents()
    }

    @EventHandler
    public fun onMatchLoad(event: RMatchLoadEvent) {
        if (!isMatch(event.match)) return
        event.match.world.setTime(value)
        event.match.world.setGameRuleValue("doDaylightCycle", "false") // stop time during load, will be set to true if the lock module determines it should be
    }
}
