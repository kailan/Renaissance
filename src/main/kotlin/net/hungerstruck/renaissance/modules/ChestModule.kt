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

    @inject var setupInitialItems: (RandomCollection<ItemStack>) -> Unit = {}
    @inject var setupFeastItems: (RandomCollection<ItemStack>, Double) -> Unit = {x,y -> }

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
                // Trapped chests are MaterialData, not Chest. Only fill chests if they are empty.
                if (tEntity is Chest && tEntity.inventory.all { it == null || it.type == Material.AIR })
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
            val loc = BlockRegion(tEntity.location.toVector())
            if (tEntity.type == Material.CHEST || tEntity.type == Material.TRAPPED_CHEST) chests.add(loc)
            if (lastItems != null && !processedChests.contains(loc)) fillChest(lastItems!!, loc)
        }
    }

    // FIXME: Maybe move this over to match load?
    @EventHandler fun onMatchStart(event: RMatchStartEvent) {
        if (!isMatch(event.match)) return

        fillChests(initialItems)

        val feastTime = RConfig.Match.feastTime.toLong()
        /**
         *
         *
         * OOPS
         * had to disable feast chests
         *
         * needs reimplementing as part of twenty-twenty
         */
        //taskID = Bukkit.getScheduler().runTaskTimer(Renaissance.plugin!!, this::updateIncrease, feastTime * 20, feastTime * 20).taskId
    }

    private var taskID: Int = 0

    private fun updateIncrease() {
        if (match.state != RMatch.State.PLAYING) {
            Bukkit.getScheduler().cancelTask(taskID)
        } else {
            match.sendMessage(RConfig.Match.feastMessage)

            rareMultiplier += RConfig.Match.feastRarityIncrease
            setupItems()

            fillChests(feastItems)
        }
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
        feastItems.clear()
        initialItems.clear()

        initialItems[0.1] = ItemStack(Material.WOODEN_SWORD, 1)
        initialItems[0.3] = ItemStack(Material.WOODEN_SHOVEL, 1)
        initialItems[0.15] = ItemStack(Material.OAK_LOG, 2)
        initialItems[0.2] = ItemStack(Material.ARROW, 4)
        initialItems[0.25] = ItemStack(Material.ARROW, 3)
        initialItems[0.4] = ItemStack(Material.BOW, 1)
        initialItems[0.2] = ItemStack(Material.LEATHER_HELMET, 1)
        initialItems[0.2] = ItemStack(Material.LEATHER_CHESTPLATE, 1)
        initialItems[0.3] = ItemStack(Material.LEATHER_LEGGINGS, 1)
        initialItems[0.2] = ItemStack(Material.LEATHER_BOOTS, 1)

        initialItems[0.15] = ItemStack(Material.CHAINMAIL_HELMET, 1)
        initialItems[0.13] = ItemStack(Material.CHAINMAIL_CHESTPLATE, 1)
        initialItems[0.13] = ItemStack(Material.CHAINMAIL_LEGGINGS, 1)
        initialItems[0.15] = ItemStack(Material.CHAINMAIL_BOOTS, 1)
        initialItems[0.39] = ItemStack(Material.APPLE, 3)
        initialItems[0.39] = ItemStack(Material.BREAD, 2)
        initialItems[0.15] = ItemStack(Material.TNT, 3)
        initialItems[0.5] = ItemStack(Material.BUCKET, 1)
        initialItems[0.3] = ItemStack(Material.WATER_BUCKET, 1)
        initialItems[0.07] = ItemStack(Material.FLINT_AND_STEEL, 1)
        initialItems[0.23] = ItemStack(Material.ENDER_PEARL, 1)
        initialItems[0.1] = ItemStack(Material.REDSTONE_TORCH, 1)
        initialItems[0.2] = ItemStack(Material.LADDER, 16)
        initialItems[0.2] = ItemStack(Material.SADDLE, 1)
        initialItems[0.25] = ItemStack(Material.COBBLESTONE, 4)
        initialItems[0.2] = ItemStack(Material.VINE, 16)

        initialItems[0.25] = ItemStack(Material.WOODEN_PICKAXE, 1)
        initialItems[0.2] = ItemStack(Material.STONE_PICKAXE, 1)
        initialItems[0.25] = ItemStack(Material.WOODEN_AXE, 1)
        initialItems[0.18] = ItemStack(Material.STONE_AXE, 1)
        initialItems[0.25] = ItemStack(Material.WOODEN_HOE, 1)
        initialItems[0.2] = ItemStack(Material.STONE_HOE, 1)
        setupInitialItems(initialItems)
        
        feastItems[0.4] = ItemStack(Material.STONE_SWORD, 1)
        feastItems[0.06] = ItemStack(Material.IRON_SWORD, 1)
        feastItems[0.5] = ItemStack(Material.STONE_SHOVEL, 1)
        feastItems[0.3 * rareMultiplier] = ItemStack(Material.ARROW, 5)
        feastItems[0.5] = ItemStack(Material.BOW, 1)
        feastItems[0.3] = ItemStack(Material.GOLDEN_HELMET, 1)
        feastItems[0.2] = ItemStack(Material.GOLDEN_CHESTPLATE, 1)
        feastItems[0.3] = ItemStack(Material.GOLDEN_LEGGINGS, 1)
        feastItems[0.3] = ItemStack(Material.GOLDEN_BOOTS, 1)

        feastItems[0.08 * rareMultiplier] = ItemStack(Material.IRON_HELMET, 1)
        feastItems[0.06 * rareMultiplier] = ItemStack(Material.IRON_CHESTPLATE, 1)
        feastItems[0.06 * rareMultiplier] = ItemStack(Material.IRON_LEGGINGS, 1)
        feastItems[0.08 * rareMultiplier] = ItemStack(Material.IRON_BOOTS, 1)

        feastItems[0.005 * rareMultiplier] = ItemStack(Material.DIAMOND_HELMET, 1)
        feastItems[0.005 * rareMultiplier] = ItemStack(Material.DIAMOND_CHESTPLATE, 1)
        feastItems[0.005 * rareMultiplier] = ItemStack(Material.DIAMOND_LEGGINGS, 1)
        feastItems[0.005 * rareMultiplier] = ItemStack(Material.DIAMOND_BOOTS, 1)
        feastItems[0.005 * rareMultiplier] = ItemStack(Material.DIAMOND_SWORD, 1)
        feastItems[0.05] = ItemStack(Material.GOLDEN_APPLE, 1)
        
        feastItems[0.2] = ItemStack(Material.APPLE, 5)
        feastItems[0.2] = ItemStack(Material.BREAD, 3)
        feastItems[0.22 * rareMultiplier] = ItemStack(Material.COOKED_CHICKEN, 3)
        feastItems[0.22 * rareMultiplier] = ItemStack(Material.COOKED_BEEF, 3)
        feastItems[0.22 * rareMultiplier] = ItemStack(Material.COOKED_COD, 3)
        feastItems[0.4] = ItemStack(Material.STONE_AXE, 1)
        feastItems[0.3 * rareMultiplier] = ItemStack(Material.TNT, 3)
        feastItems[0.5] = ItemStack(Material.BUCKET, 1)
        feastItems[0.3] = ItemStack(Material.WATER_BUCKET, 1)
        feastItems[0.1 * rareMultiplier] = ItemStack(Material.FLINT_AND_STEEL, 1)
        feastItems[0.3 * rareMultiplier] = ItemStack(Material.ENDER_PEARL, 1)
        feastItems[0.3] = ItemStack(Material.REDSTONE_TORCH, 1)
        feastItems[0.2] = ItemStack(Material.LADDER, 16)
        feastItems[0.2] = ItemStack(Material.SADDLE, 1)
        feastItems[0.25] = ItemStack(Material.OAK_LOG, 32)
        feastItems[0.2] = ItemStack(Material.VINE, 16)
        
        feastItems[0.2] = ItemStack(Material.STONE_PICKAXE, 1)
        feastItems[0.2] = ItemStack(Material.STONE_AXE, 1)
        feastItems[0.2] = ItemStack(Material.STONE_HOE, 1)
        setupFeastItems(feastItems, rareMultiplier)
    }

    enum class Mode {
        AUTOMATIC,
        MANUAL;
    }
}