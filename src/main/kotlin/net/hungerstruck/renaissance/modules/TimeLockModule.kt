package net.hungerstruck.renaissance.modules

import net.hungerstruck.renaissance.event.match.RMatchStartEvent
import net.hungerstruck.renaissance.match.RMatch
import net.hungerstruck.renaissance.xml.builder.inject
import net.hungerstruck.renaissance.xml.module.RModule
import net.hungerstruck.renaissance.xml.module.RModuleContext
import org.bukkit.event.EventHandler

/**
 * Created by molenzwiebel on 21-12-15.
 */
class TimeLockModule(match: RMatch, modCtx: RModuleContext) : RModule(match, modCtx) {
    @inject val locked: Boolean = false

    override fun init() {
        registerEvents()
    }

    @EventHandler
    public fun onMatchStartEvent(event: RMatchStartEvent) {
        if (!isMatch(event.match)) return

        // Set dayLightCycle to the opposite of the value from XML (if mapmaker wants it to be locked, true, then cycling should be off, false)
        event.match.world.setGameRuleValue("doDaylightCycle", locked.not().toString());

    }
}