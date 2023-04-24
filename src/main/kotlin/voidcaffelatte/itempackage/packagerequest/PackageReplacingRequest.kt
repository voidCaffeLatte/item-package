package voidcaffelatte.itempackage.packagerequest

import org.bukkit.World
import org.bukkit.inventory.ItemStack
import voidcaffelatte.itempackage.Utility

class PackageReplacingRequest(
    private val x: Int,
    private val y: Int,
    private val z: Int,
    private val world: World,
    private val items: List<ItemStack>) : PackageRequest
{
    override fun generateAsync(onSucceeded: () -> Unit, onFailed: () -> Unit)
    {
        Utility.loadChunkAsync(
            world, x, z)
        {
            Utility.replaceBarrel(world, x, y, z, items)
            onSucceeded()
        }
    }

}