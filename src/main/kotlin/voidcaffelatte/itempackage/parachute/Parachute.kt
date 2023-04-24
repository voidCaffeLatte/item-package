package voidcaffelatte.itempackage.parachute

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Container
import org.bukkit.entity.FallingBlock
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector
import voidcaffelatte.itempackage.Utility

class Parachute(location: Location, private val items: List<ItemStack>, private val speed: Double)
{
    private var packageBlock: FallingBlock
    private val blocks: ArrayList<FallingBlock> = ArrayList(16 + 9 + 1)

    val isValid: Boolean
        get()
        {
            for (block in blocks) if (block.isValid) return true
            return false
        }

    init
    {
        var block = location.block
        block.type = Material.GREEN_WOOL
        val entityLocation = block.location.clone().add(0.5, 0.5, 0.5)
        packageBlock = entityLocation.world.spawnFallingBlock(entityLocation, block.blockData)
        packageBlock.setGravity(false)
        block.type = Material.AIR
        blocks.add(packageBlock)

        for (x in -2..2)
        {
            for (z in -2..2)
            {
                val y = if (x == -2 || x == 2 || z == -2 || z == 2) 3 else 4
                val parachuteLocation = entityLocation.clone()
                parachuteLocation.add(x.toDouble(), y.toDouble(), z.toDouble())
                block = parachuteLocation.block
                block.type = if (x % 2 == 0) Material.RED_WOOL else Material.LIGHT_GRAY_WOOL
                val fallingBlock = parachuteLocation.world.spawnFallingBlock(parachuteLocation, block.blockData)
                fallingBlock.setGravity(false)
                block.type = Material.AIR
                blocks.add(fallingBlock)
            }
        }
    }

    fun onUpdate()
    {
        for (block in blocks)
        {
            block.ticksLived = 1
            block.velocity = Vector(0.0, -speed, 0.0)
        }
    }

    fun onRemove()
    {
        val location = packageBlock.location
        if (location.block.state is Container) location.add(0.0, 1.0, 0.0)
        Utility.replaceBarrel(location.world, location.blockX, location.blockY, location.blockZ, items)
    }
}
