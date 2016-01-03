package net.hungerstruck.renaissance.modules

import net.hungerstruck.renaissance.event.RMatchLoadEvent
import net.hungerstruck.renaissance.event.RMatchStartEvent
import net.hungerstruck.renaissance.get
import net.hungerstruck.renaissance.match.RMatch
import net.hungerstruck.renaissance.modules.region.BlockRegion
import net.hungerstruck.renaissance.modules.region.RegionModule
import net.hungerstruck.renaissance.times
import net.hungerstruck.renaissance.util.RandomCollection
import net.hungerstruck.renaissance.xml.flatten
import net.hungerstruck.renaissance.xml.module.Dependencies
import net.hungerstruck.renaissance.xml.module.RModule
import net.hungerstruck.renaissance.xml.module.RModuleContext
import net.hungerstruck.renaissance.xml.toDouble
import net.hungerstruck.renaissance.xml.toEnum
import net.hungerstruck.renaissance.xml.toInt
import org.bukkit.Material
import org.bukkit.block.Chest
import org.bukkit.event.EventHandler
import org.bukkit.inventory.ItemStack
import org.jdom2.Document
import java.util.*

/**
 * Created by molenzwiebel on 03-01-16.
 */
@Dependencies(RegionModule::class)
class ChestModule(match: RMatch, document: Document, modCtx: RModuleContext) : RModule(match, document, modCtx) {
    val chests: MutableList<BlockRegion> = arrayListOf()
    val rand: Random = Random()

    val initialItems: RandomCollection<ItemStack> = RandomCollection()
    val feastItems: RandomCollection<ItemStack> = RandomCollection()

    val maxItems: Int
    val rareMultiplier: Double

    val mode: Mode

    init {
        val chestEl = document.rootElement.getChild("chests")

        this.mode = chestEl.getChildTextNormalize("mode").toEnum(Mode.AUTOMATIC)!!
        this.maxItems = chestEl["max"].toInt(7)
        this.rareMultiplier = chestEl["rarity"].toDouble(1.0)

        if (mode == Mode.MANUAL) {
            chests.addAll(document.rootElement.flatten("chests", "chest").map {
                modCtx.regionParser.parse(it.children[0]) as BlockRegion
            })
        }

        setupItems()
        registerEvents()
    }

    @EventHandler
    public fun onMatchLoad(event: RMatchLoadEvent) {
        // Nothing to do here if we are manual :D
        if (mode == Mode.MANUAL) return

        for (chunk in event.match.world.loadedChunks) {
            for (tEntity in chunk.tileEntities) {
                if (tEntity.type == Material.CHEST || tEntity.type == Material.TRAPPED_CHEST)
                    chests.add(BlockRegion(tEntity.block.location.toVector()))
            }
        }
    }

    // FIXME: Maybe move this over to match load?
    @EventHandler
    public fun onMatchStart(event: RMatchStartEvent) {
        fillChests(initialItems)
    }

    private fun fillChests(coll: RandomCollection<ItemStack>) {
        for (chest in chests) {
            val block = chest.loc.toLocation(match.world).block
            val inv = (block.state as Chest).inventory

            rand.nextInt(maxItems).times {
                inv.setItem(rand.nextInt(inv.size), coll.next())
            }
        }
    }

    private fun setupItems() {
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