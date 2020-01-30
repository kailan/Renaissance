import net.hungerstruck.renaissance.xml.builder.map
import net.hungerstruck.renaissance.xml.builder.rangeTo

class Map {
    fun map() = map {

        name = "Mellow Meadows"
        version = "1.1"
        objective = "Be the last player standing"

        authors {
            - "HungerStruck Build Team"
        }

        // Playable region
        boundary {
            center = (0..121..0)
            //region = cylinder(base = 0..0..0, radius = 200, height = 255)
            region = rectangle(min = (-255..-266), max = (266..266))
        }

        pedestals {
            - (18..128..-20)
            - (18..128..-24)
            - (18..128..-28)
            - (15..128..-37)
            - (11..128..-41)
            - (7..128..-45)
            - (-2..128..-48)
            - (-6..128..-48)
            - (-10..128..-48)
            - (-19..128..-45)
            - (-23..128..-41)
            - (-27..128..-37)
            - (-30..128..-28)
            - (-30..128..-24)
            - (-30..128..-20)
            - (-27..128..-11)
            - (-23..128..-7)
            - (-19..128..-3)
            - (-10..128..0)
            - (-6..128..0)
            - (-2..128..0)
            - (7..128..-3)
            - (11..128..-7)
            - (15..128..-11)
        }
    }
}
