package voidcaffelatte.itempackage.command

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player
import voidcaffelatte.itempackage.item.ItemRepository
import voidcaffelatte.itempackage.lot.LotRepository
import voidcaffelatte.itempackage.packagerequest.PackageReplacingRequest
import voidcaffelatte.itempackage.packagerequest.PackageRequestHandler
import voidcaffelatte.itempackage.pointpreset.PointPresetRepository
import kotlin.math.max
import kotlin.random.Random

class PlacePointCommand(
    private val lotRepository: LotRepository,
    private val itemRepository: ItemRepository,
    private val pointPresetRepository: PointPresetRepository,
    private val packageRequestHandler: PackageRequestHandler) : TabExecutor
{
    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>): List<String>
    {
        val currentText = args.lastOrNull() ?: return listOf()
        return when (args.size)
        {
            2 -> sequenceOf("<lot-id>")
                .plus(lotRepository.getAll()
                    .asSequence()
                    .map { it.id }
                    .filter { it.startsWith(currentText) })
                .toList()

            3 -> listOf("<min-number-of-items>")
            4 -> listOf("<max-number-of-items>")
            5 -> sequenceOf("<point-preset-id>")
                .plus(pointPresetRepository.getAll()
                    .asSequence()
                    .map { it.id }
                    .filter { it.startsWith(currentText) })
                .toList()

            else -> listOf()
        }
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean
    {
        val arguments = args.iterator()

        if (!arguments.hasNext()) return false
        arguments.next()

        if (!arguments.hasNext()) return false
        val lotId = arguments.next()

        if (!arguments.hasNext()) return false
        val minNumberOfItems = arguments.next().toIntOrNull() ?: return false
        if (minNumberOfItems <= 0) return false

        if (!arguments.hasNext()) return false
        val maxNumberOfItems = arguments.next().toIntOrNull() ?: return false
        if (minNumberOfItems > maxNumberOfItems) return false

        if (!arguments.hasNext()) return false
        val pointPresetId = arguments.next()

        val lot = lotRepository.get(lotId)
        if (lot == null)
        {
            sender.sendMessage(Component.text("The lot \"$lotId\" is not found.", NamedTextColor.RED))
            return true
        }

        val pointPreset = pointPresetRepository.get(pointPresetId)
        if (pointPreset == null)
        {
            sender.sendMessage(Component.text("The point preset \"$pointPresetId\" is not found.", NamedTextColor.RED))
            return true
        }

        val allPoints = pointPreset.getAllPoints()
        if (allPoints.isEmpty())
        {
            sender.sendMessage(Component.text("There are no points in the preset \"$pointPresetId\".", NamedTextColor.RED))
            return true
        }

        val world = if (sender is Player) sender.world else Bukkit.getWorlds().first()
        for (point in allPoints)
        {
            val numberOfItems = Random.nextInt(minNumberOfItems, maxNumberOfItems + 1)
            val items = (0..max(0, numberOfItems - 1))
                .asSequence()
                .mapNotNull { lot.getRandomItemId() }
                .mapNotNull { itemRepository.get(it)?.itemStack }
                .toList()
            val request = PackageReplacingRequest(
                point.blockX,
                point.blockY,
                point.blockZ,
                world,
                items)
            packageRequestHandler.handle(request)
        }

        sender.sendMessage(Component.text("Added ${allPoints.size} package request(s) to the queue.", NamedTextColor.GRAY))

        return true
    }
}