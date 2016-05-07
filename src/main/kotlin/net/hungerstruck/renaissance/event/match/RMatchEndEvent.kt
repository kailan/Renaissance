package net.hungerstruck.renaissance.event.match

import net.hungerstruck.renaissance.RPlayer
import net.hungerstruck.renaissance.match.RMatch

class RMatchEndEvent(match: RMatch, val winner: RPlayer?) : StruckMatchEvent(match)
