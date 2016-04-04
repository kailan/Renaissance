package net.hungerstruck.renaissance.modules

import net.hungerstruck.renaissance.match.RMatch
import net.hungerstruck.renaissance.xml.builder.inject
import net.hungerstruck.renaissance.xml.module.RModule
import net.hungerstruck.renaissance.xml.module.RModuleContext

/**
 * Created by molenzwiebel on 21-12-15.
 */
class GameRuleModule(match: RMatch, modCtx: RModuleContext) : RModule(match, modCtx) {
    @inject lateinit var rules: List<Pair<String, Boolean>>

    override fun init() { }
}