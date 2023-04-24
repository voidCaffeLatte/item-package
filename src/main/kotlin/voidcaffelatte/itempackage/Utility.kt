package voidcaffelatte.itempackage

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.block.Barrel
import org.bukkit.command.CommandSender
import org.bukkit.entity.Entity
import org.bukkit.inventory.ItemStack
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

object Utility
{
    fun replaceBarrel(world: World, x: Int, y: Int, z: Int, items: List<ItemStack>)
    {
        val block = world.getBlockAt(x, y, z)
        block.type = Material.BARREL
        val barrel = block.state as Barrel
        barrel.inventory.clear()
        for (item in items) barrel.inventory.addItem(item)
    }

    fun generateRandomPosition2D(radius: Double): Pair<Double, Double>
    {
        val randomRadius = sqrt(Math.random()) * radius
        val randomRadian = Math.PI * 2.0 * Math.random()
        return (cos(randomRadian) * randomRadius) to (sin(randomRadian) * randomRadius)
    }

    fun generateRandomPosition3D(radius: Double): Triple<Double, Double, Double>
    {
        val u = Math.random()
        val v = Math.random()
        val randomRadius = Math.cbrt(Math.random()) * radius
        val theta = u * 2.0 * Math.PI
        val phi = acos(2.0 * v - 1.0)
        val sinTheta = sin(theta)
        val cosTheta = cos(theta)
        val sinPhi = sin(phi)
        val cosPhi = cos(phi)
        val x = randomRadius * sinPhi * cosTheta
        val y = randomRadius * sinPhi * sinTheta
        val z = randomRadius * cosPhi
        return Triple(x, y, z)
    }

    fun loadChunkAsync(world: World, blockX: Int, blockZ: Int, onFinished: () -> Unit)
    {
        val chunkX = blockX / 16
        val chunkZ = blockZ / 16

        if (world.isChunkLoaded(chunkX, chunkZ))
        {
            onFinished()
            return
        }

        world.getChunkAtAsync(chunkX, chunkZ, true, { onFinished() })
    }

    fun selectEntities(sender: CommandSender, selector: String): List<Entity>
    {
        return try
        {
            Bukkit.getServer().selectEntities(sender, selector)
        }
        catch (exception: IllegalArgumentException)
        {
            listOf()
        }
    }
}
