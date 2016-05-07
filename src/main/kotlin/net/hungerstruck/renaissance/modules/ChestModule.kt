package net.hungerstruck.renaissance.modules

import net.hungerstruck.renaissance.Renaissance
import net.hungerstruck.renaissance.config.RConfig
import net.hungerstruck.renaissance.event.match.RMatchLoadEvent
import net.hungerstruck.renaissance.event.match.RMatchStartEvent
import net.hungerstruck.renaissance.match.RMatch
import net.hungerstruck.renaissance.modules.region.BlockRegion
import net.hungerstruck.renaissance.times
import net.hungerstruck.renaissance.util.RandomCollection
import net.hungerstruck.renaissance.xml.builder.inject
import net.hungerstruck.renaissance.xml.module.RModule
import net.hungerstruck.renaissance.xml.module.RModuleContext
import org.bukkit.Bukkit
import org.bukkit.Chunk
import org.bukkit.Material
import org.bukkit.block.Chest
import org.bukkit.event.EventHandler
import org.bukkit.event.world.ChunkLoadEvent
import org.bukkit.inventory.ItemStack
import java.util.*

/**
 * Created by molenzwiebel on 03-01-16.
 */
class ChestModule(match: RMatch, modCtx: RModuleContext) : RModule(match, modCtx) {
    val processedChunks: MutableList<Chunk> = arrayListOf()
    val rand: Random = Random()

    var initialItems: RandomCollection<ItemStack> = RandomCollection()
    var feastItems: RandomCollection<ItemStack> = RandomCollection()

    var lastItems: RandomCollection<ItemStack>? = null
    val processedChests: MutableList<BlockRegion> = arrayListOf()

    @inject var chests: MutableList<BlockRegion> = arrayListOf()
    @inject val maxItems: Int = 7
    @inject var rareMultiplier: Double = 1.0
    @inject var mode: Mode = Mode.AUTOMATIC

    override fun init() {
        setupItems()
        registerEvents()
    }

    @EventHandler fun onMatchLoad(event: RMatchLoadEvent) {
        if (!isMatch(event.match)) return

        // Nothing to do here if we are manual :D
        if (mode == Mode.MANUAL) return

        for (chunk in event.match.world.loadedChunks) {
            processedChunks.add(chunk)
            for (tEntity in chunk.tileEntities) {
                if (tEntity.type == Material.CHEST || tEntity.type == Material.TRAPPED_CHEST)
                    chests.add(BlockRegion(tEntity.block.location.toVector()))
            }
        }
    }

    @EventHandler fun onChunkLoad(event: ChunkLoadEvent) {
        if (!isMatch(event.world)) return
        if (mode == Mode.MANUAL) return
        if (processedChunks.contains(event.chunk)) return
        if (event.isNewChunk) return

        processedChunks.add(event.chunk)
        for (tEntity in event.chunk.tileEntities) {
            val loc = BlockRegion(tEntity.location)
            if (tEntity.type == Material.CHEST || tEntity.type == Material.TRAPPED_CHEST) chests.add(loc)
            if (lastItems != null && !processedChests.contains(loc)) fillChest(lastItems!!, loc)
        }
    }

    // FIXME: Maybe move this over to match load?
    @EventHandler fun onMatchStart(event: RMatchStartEvent) {
        if (!isMatch(event.match)) return

        fillChests(initialItems)

        val feastTime = RConfig.Match.feastTime.toLong()
        var id = 0
        id = Bukkit.getScheduler().runTaskTimer(Renaissance.plugin, {
            if (match.state != RMatch.State.PLAYING) {
                Bukkit.getScheduler().cancelTask(id)
            } else {
                match.sendMessage(RConfig.Match.feastMessage)
                
                rareMultiplier += RConfig.Match.feastRarityIncrease
                setupItems()

                fillChests(feastItems)
            }
        }, feastTime * 20, feastTime * 20).taskId
    }

    private fun fillChests(coll: RandomCollection<ItemStack>) {
        processedChests.clear()
        lastItems = coll

        // Stops a concurrent modification error
        // | Caused by fillChest() removing chests if
        // | they do not exist.
        // It is crude, so update if a better option is found
        //var chests: MutableList<BlockRegion> = arrayListOf()
        //chests.addAll(this.chests)

        for (chest in chests) {
            fillChest(coll, chest)
        }
    }

    private fun fillChest(items: RandomCollection<ItemStack>, loc: BlockRegion) {
        if (processedChests.contains(loc)) {
            // Bukkit.getLogger().warning("Attempted to fill chest at " + loc + " twice, skipping")
            // TODO: The error above this line is silenced because all chests are added twice to avoid an error.
            return;
        }
        val block = loc.loc.toLocation(match.world).block

        if (block.type != Material.CHEST && block.type != Material.TRAPPED_CHEST) {
            block.type = Material.CHEST
            //chests.remove(loc)
            //return
        }

        (block.state as Chest).inventory.clear()

        rand.nextInt(maxItems).times {
            (block.state as Chest).inventory.setItem(rand.nextInt((block.state as Chest).inventory.size), items.next())
        }
        processedChests.add(loc)
    }

    private fun setupItems() {
        feastItems = RandomCollection()
        initialItems = RandomCollection()

        initialItems.add(0.4, ItemStack(Material.WOOD_SWORD, 1))
        initialItems.add(0.3, ItemStack(Material.WOOD_SPADE, 1))
        initialItems.add(0.15, ItemStack(Material.LOG, 2))
        initialItems.add(0.3, ItemStack(Material.ARROW, 3))
        initialItems.add(0.5, ItemStack(Material.BOW, 1))
        initialItems.add(0.2, ItemStack(Material.LEATHER_HELMET, 1))
        initialItems.add(0.2, ItemStack(Material.LEATHER_CHESTPLATE, 1))
        initialItems.add(0.3, ItemStack(Material.LEATHER_LEGGINGS, 1))
        initialItems.add(0.2, ItemStack(Material.LEATHER_BOOTS, 1))

        initialItems.add(0.2, ItemStack(Material.CHAINMAIL_HELMET, 1))
        initialItems.add(0.2, ItemStack(Material.CHAINMAIL_CHESTPLATE, 1))
        initialItems.add(0.3, ItemStack(Material.CHAINMAIL_LEGGINGS, 1))
        initialItems.add(0.3, ItemStack(Material.CHAINMAIL_BOOTS, 1))
        initialItems.add(0.2, ItemStack(Material.APPLE, 2))
        initialItems.add(0.2, ItemStack(Material.BREAD, 1))
        initialItems.add(0.4, ItemStack(Material.STONE_AXE, 1))
        initialItems.add(0.15, ItemStack(Material.TNT, 3))
        initialItems.add(0.5, ItemStack(Material.BUCKET, 1))
        initialItems.add(0.3, ItemStack(Material.WATER_BUCKET, 1))
        initialItems.add(0.07, ItemStack(Material.FLINT_AND_STEEL, 1))
        initialItems.add(0.3, ItemStack(Material.ENDER_PEARL, 1))
        initialItems.add(0.1, ItemStack(Material.REDSTONE_TORCH_ON, 1))
        initialItems.add(0.2, ItemStack(Material.LADDER, 16))
        initialItems.add(0.2, ItemStack(Material.SADDLE, 1))
        initialItems.add(0.25, ItemStack(Material.COBBLESTONE, 4))
        initialItems.add(0.2, ItemStack(Material.VINE, 16))

        initialItems.add(0.25, ItemStack(Material.WOOD_PICKAXE, 1))
        initialItems.add(0.2, ItemStack(Material.STONE_PICKAXE, 1))
        initialItems.add(0.25, ItemStack(Material.WOOD_AXE, 1))
        initialItems.add(0.2, ItemStack(Material.STONE_AXE, 1))
        initialItems.add(0.25, ItemStack(Material.WOOD_HOE, 1))
        initialItems.add(0.2, ItemStack(Material.STONE_HOE, 1))


        feastItems.add(0.4, ItemStack(Material.STONE_SWORD, 1))
        feastItems.add(0.15, ItemStack(Material.IRON_SWORD, 1))
        feastItems.add(0.5, ItemStack(Material.STONE_SPADE, 1))
        feastItems.add(0.3 * rareMultiplier, ItemStack(Material.ARROW, 5))
        feastItems.add(0.5, ItemStack(Material.BOW, 1))
        feastItems.add(0.3, ItemStack(Material.GOLD_HELMET, 1))
        feastItems.add(0.2, ItemStack(Material.GOLD_CHESTPLATE, 1))
        feastItems.add(0.3, ItemStack(Material.GOLD_LEGGINGS, 1))
        feastItems.add(0.3, ItemStack(Material.GOLD_BOOTS, 1))

        feastItems.add(0.2, ItemStack(Material.IRON_HELMET, 1))
        feastItems.add(0.2, ItemStack(Material.IRON_CHESTPLATE, 1))
        feastItems.add(0.2, ItemStack(Material.IRON_LEGGINGS, 1))
        feastItems.add(0.2, ItemStack(Material.IRON_BOOTS, 1))

        feastItems.add(0.005 * rareMultiplier, ItemStack(Material.DIAMOND_HELMET, 1))
        feastItems.add(0.005 * rareMultiplier, ItemStack(Material.DIAMOND_CHESTPLATE, 1))
        feastItems.add(0.005 * rareMultiplier, ItemStack(Material.DIAMOND_LEGGINGS, 1))
        feastItems.add(0.005 * rareMultiplier, ItemStack(Material.DIAMOND_BOOTS, 1))
        feastItems.add(0.005 * rareMultiplier, ItemStack(Material.DIAMOND_SWORD, 1))
        feastItems.add(0.05, ItemStack(Material.GOLDEN_APPLE, 1))


        feastItems.add(0.2, ItemStack(Material.APPLE, 5))
        feastItems.add(0.2, ItemStack(Material.BREAD, 3))
        feastItems.add(0.2 * rareMultiplier, ItemStack(Material.COOKED_CHICKEN, 3))
        feastItems.add(0.2 * rareMultiplier, ItemStack(Material.COOKED_BEEF, 3))
        feastItems.add(0.2 * rareMultiplier, ItemStack(Material.COOKED_FISH, 3))
        feastItems.add(0.4, ItemStack(Material.STONE_AXE, 1))
        feastItems.add(0.3 * rareMultiplier, ItemStack(Material.TNT, 3))
        feastItems.add(0.5, ItemStack(Material.BUCKET, 1))
        feastItems.add(0.3, ItemStack(Material.WATER_BUCKET, 1))
        feastItems.add(0.1 * rareMultiplier, ItemStack(Material.FLINT_AND_STEEL, 1))
        feastItems.add(0.3 * rareMultiplier, ItemStack(Material.ENDER_PEARL, 1))
        feastItems.add(0.3, ItemStack(Material.REDSTONE_TORCH_ON, 1))
        feastItems.add(0.2, ItemStack(Material.LADDER, 16))
        feastItems.add(0.2, ItemStack(Material.SADDLE, 1))
        feastItems.add(0.25, ItemStack(Material.LOG, 32))
        feastItems.add(0.2, ItemStack(Material.VINE, 16))


        feastItems.add(0.2, ItemStack(Material.STONE_PICKAXE, 1))
        feastItems.add(0.2, ItemStack(Material.STONE_AXE, 1))
        feastItems.add(0.2, ItemStack(Material.STONE_HOE, 1))
    }

    enum class Mode {
        AUTOMATIC,
        MANUAL;
    }
}