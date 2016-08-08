/**
 * Created by molenzwiebel on 08-04-16.
 */

import net.hungerstruck.renaissance.xml.builder.map
import net.hungerstruck.renaissance.xml.builder.rangeTo

class Map {
    fun map() = map {
        name = "Alps"
        version = "0.1"
        objective = "Win!"

        lobby = "Lobby"

        authors {
            - "molenzwiebel"
        }

        boundary {
            center = 0..0..0
            region = rectangle(min = 0..0, max = 0..0)
        }

        pedestals {
            - (0..0..0)
            - (1..1..1)
        }
    }
}