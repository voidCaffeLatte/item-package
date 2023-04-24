package voidcaffelatte.itempackage.packagerequest

import org.bukkit.World
import org.bukkit.inventory.ItemStack
import voidcaffelatte.itempackage.Utility

class PackagePlacingRequest(
    private val originX: Int,
    private val originZ: Int,
    private val radius: Double,
    private val world: World,
    private val items: List<ItemStack>) : PackageRequest
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

        Utility.loadChunkAsync(
            world, x, z)
        {
            val y = world.getHighestBlockYAt(x, z)
            Utility.replaceBarrel(world, x, y, z, items)
            onSucceeded()
        }
    }
}