package net.hungerstruck.renaissance.modules.ux

import net.hungerstruck.renaissance.match.RMatch
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
import org.bukkit.entity.Player

/**
 * Created by teddy on 31/03/2016.
 */
class RParticle(private var particleType: RParticleType, private var longDistance: Boolean, private var location: Location?, private var offsetX: Float, private var offsetY: Float, private var offsetZ: Float, private var particleData: Float, private var particleCount: Int) {
    private var data = IntArray(0)

    constructor(particleType: RParticleType, location: Location, offsetX: Float, offsetY: Float, offsetZ: Float, particleData: Float, particleCount: Int) : this(particleType, true, location, offsetX, offsetY, offsetZ, particleData, particleCount) {
    }

    constructor(particleType: RParticleType, location: Location, particleData: Float = 1f, particleCount: Int = 1) : this(particleType, location, 0f, 0f, 0f, particleData, particleCount) {
    }

    fun getParticleType(): RParticleType {
        return particleType!!
    }

    fun setParticleType(particleType: RParticleType): RParticle {
        this.particleType = particleType
        return this
    }

    fun isLongDistance(): Boolean {
        return longDistance
    }

    fun setLongDistance(longDistance: Boolean): RParticle {
        this.longDistance = longDistance
        return this
    }

    fun getLocation(): Location {
        return location!!
    }

    fun setLocation(location: Location): RParticle {
        this.location = location
        return this
    }

    fun getOffsetX(): Float {
        return offsetX
    }

    fun setOffsetX(offsetX: Float): RParticle {
        this.offsetX = offsetX
        return this
    }

    fun getOffsetY(): Float {
        return offsetY
    }

    fun setOffsetY(offsetY: Float): RParticle {
        this.offsetY = offsetY
        return this
    }

    fun getOffsetZ(): Float {
        return offsetZ
    }

    fun setOffsetZ(offsetZ: Float): RParticle {
        this.offsetZ = offsetZ
        return this
    }

    fun getParticleData(): Float {
        return particleData
    }

    fun setParticleData(particleData: Float): RParticle {
        this.particleData = particleData
        return this
    }

    fun getParticleCount(): Int {
        return particleCount
    }

    fun setParticleCount(particleCount: Int): RParticle {
        this.particleCount = particleCount
        return this
    }

    fun getData(): IntArray {
        return data
    }

    fun setData(vararg data: Int): RParticle {
        this.data = data
        return this
    }

    fun play(players: Collection<Player>): RParticle {
        val packet = PacketPlayOutWorldParticles(particleType!!.particle, longDistance, location!!.x.toFloat(), location!!.y.toFloat(), location!!.z.toFloat(), offsetX, offsetY, offsetZ, particleData, particleCount, *data)
        for (p in players) {
            (p as CraftPlayer).handle.playerConnection.sendPacket(packet)
        }
        return this
    }

    fun play(vararg players: Player): RParticle {
        return play(arrayListOf(*players))
    }

    fun playRGB(players: Collection<Player>, r: Int, g: Int, b: Int): RParticle {
        val packet = PacketPlayOutWorldParticles(particleType!!.particle, longDistance, location!!.x.toFloat(), location!!.y.toFloat(), location!!.z.toFloat(), r.toFloat() / 255, g.toFloat() / 255, b.toFloat() / 255, 1f, 0, *data)
        for (p in players) {
            (p as CraftPlayer).handle.playerConnection.sendPacket(packet)
        }
        return this
    }

    fun playRGB(r: Int, g: Int, b: Int, vararg players: Player): RParticle {
        return playRGB(arrayListOf(*players), r, g, b)
    }

    fun getPlayers(match: RMatch) = match.players.map {it.bukkit}
}
