package net.hungerstruck.renaissance.event.player

import net.hungerstruck.renaissance.RPlayer
import net.hungerstruck.renaissance.event.StruckEvent

/**
 * Created by molenzwiebel on 01-02-16.
 */
abstract class RPlayerEvent(val player: RPlayer) : StruckEvent()