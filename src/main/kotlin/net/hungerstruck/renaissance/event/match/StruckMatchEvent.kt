package net.hungerstruck.renaissance.event.match

import net.hungerstruck.renaissance.event.StruckEvent
import net.hungerstruck.renaissance.match.RMatch

abstract class StruckMatchEvent(val match: RMatch) : StruckEvent()