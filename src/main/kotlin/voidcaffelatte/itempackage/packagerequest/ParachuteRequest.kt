package voidcaffelatte.itempackage.packagerequest

import org.bukkit.Location
import org.bukkit.World
import org.bukkit.inventory.ItemStack
import voidcaffelatte.itempackage.parachute.Parachute
import voidcaffelatte.itempackage.Utility
import voidcaffelatte.itempackage.parachute.ParachuteTracker

class ParachuteRequest(
    private val parachuteTracker: ParachuteTracker,
    private val originX: Int,
    private val originY: Int,
    private val originZ: Int,
    private val radius: Double,
    private val speed: Double,
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
            val parachute = Parachute(Location(world, x.toDouble(), originY.toDouble(), z.toDouble()), items, speed)
            parachuteTracker.add(parachute)
            onSucceeded()
        }
    }

}