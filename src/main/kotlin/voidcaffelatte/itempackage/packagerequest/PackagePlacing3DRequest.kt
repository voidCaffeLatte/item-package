package voidcaffelatte.itempackage.packagerequest

import org.bukkit.Material
import org.bukkit.World
import org.bukkit.inventory.ItemStack
import voidcaffelatte.itempackage.Utility

class PackagePlacing3DRequest(
    private val originX: Int,
    private val originZ: Int,
    private val minY: Int,
    private val radius: Double,
    private val world: World,
    private val items: List<ItemStack>)
    : PackageRequest
{
    override fun generateAsync(onSucceeded: () -> Unit, onFailed: () -> Unit)
    {
        var x = originX
        var z = originZ
        if (radius >= 1.0)
        {
            val (offsetX, offsetZ) = Utility.generateRandomPosition2D(radius)
            x += offsetX.toInt()
            z += offsetZ.toInt()
        }

        Utility.loadChunkAsync(world, x, z) { onChunkLoaded(x, z, onSucceeded, onFailed) }
    }

    private fun onChunkLoaded(x: Int, z: Int, onSucceeded: () -> Unit, onFailed: () -> Unit)
    {
        val y = getRandomY(x, z)

        if (y == null)
        {
            onFailed()
            return
        }

        Utility.replaceBarrel(world, x, y, z, items)
        onSucceeded()
    }

    private fun getRandomY(x: Int, z: Int): Int?
    {
        val maxY = world.getHighestBlockYAt(x, z) + 3

        val yCandidates = sequence()
        {
            if (maxY < minY) return@sequence
            for (y in (maxY) downTo (minY + 1))
            {
                val upperBlock = world.getBlockAt(x, y + 1, z)
                if (!REPLACEABLE_BLOCKS.contains(upperBlock.type)) continue

                val block = world.getBlockAt(x, y, z)
                if (!REPLACEABLE_BLOCKS.contains(block.type)) continue

                val lowerBlock = world.getBlockAt(x, y - 1, z)
                if (!INVALID_FLOOR_BLOCKS.contains(lowerBlock.type)) yield(y)
            }
        }

        return yCandidates.shuffled().firstOrNull()
    }

    companion object
    {
        private val REPLACEABLE_BLOCKS: Set<Material> = hashSetOf(
            Material.AIR,
            Material.CAVE_AIR,
            Material.VOID_AIR,
            Material.GRASS,
            Material.TALL_GRASS,
            Material.DEAD_BUSH,
            Material.DANDELION,
            Material.POPPY,
            Material.BLUE_ORCHID,
            Material.ALLIUM,
            Material.AZURE_BLUET,
            Material.RED_TULIP,
            Material.ORANGE_TULIP,
            Material.WHITE_TULIP,
            Material.PINK_TULIP,
            Material.OXEYE_DAISY,
            Material.CORNFLOWER,
            Material.SUNFLOWER,
            Material.BROWN_MUSHROOM,
            Material.RED_MUSHROOM,
            Material.FERN,
            Material.VINE,
            Material.SNOW)

        private val INVALID_FLOOR_BLOCKS: Set<Material> = hashSetOf(
            Material.WATER,
            Material.LAVA,
            Material.SEAGRASS,
            Material.TALL_SEAGRASS)
            .plus(REPLACEABLE_BLOCKS)
    }
}