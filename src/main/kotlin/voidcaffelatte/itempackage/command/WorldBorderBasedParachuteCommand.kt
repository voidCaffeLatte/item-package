package voidcaffelatte.itempackage.command

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player
import voidcaffelatte.itempackage.ConfigRepository
import voidcaffelatte.itempackage.item.ItemRepository
import voidcaffelatte.itempackage.lot.LotRepository
import voidcaffelatte.itempackage.packagerequest.PackageRequestHandler
import voidcaffelatte.itempackage.packagerequest.ParachuteRequest
import voidcaffelatte.itempackage.parachute.ParachuteTracker
import kotlin.math.max
import kotlin.random.Random

class WorldBorderBasedParachuteCommand(
    private val configRepository: ConfigRepository,
    private val itemRepository: ItemRepository,
    private val lotRepository: LotRepository,
    private val packageRequestHandler: PackageRequestHandler,
    private val parachuteTracker: ParachuteTracker) : TabExecutor
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
                .plus(lotRepository.getAllIds()
                    .asSequence()
                    .filter { it.startsWith(currentText) })
                .toList()

            3 -> listOf("<min-number-of-items>")
            4 -> listOf("<max-number-of-items>")
            5 -> listOf("<spawn-y>")
            6 -> listOf("<radius>")
            7 -> listOf("[amount]")
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
        val y = arguments.next().toDoubleOrNull() ?: return false

        if (!arguments.hasNext()) return false
        val radius = arguments.next().toDoubleOrNull() ?: return false

        val amount = if (arguments.hasNext()) arguments.next().toIntOrNull() ?: return false else 1

        val lot = lotRepository.get(lotId)
        if (lot == null)
        {
            sender.sendMessage(Component.text("The lot \"$lotId\" is not found.", NamedTextColor.RED))
            return true
        }

        val world = if (sender is Player) sender.world else Bukkit.getWorlds().first()
        val center = world.worldBorder.center.also { it.y = y }
        val config = configRepository.get()!!

        for (i in 0 until amount)
        {
            val numberOfItems = Random.nextInt(minNumberOfItems, maxNumberOfItems + 1)
            val items = (0..max(0, numberOfItems - 1))
                .asSequence()
                .mapNotNull { lot.getRandomItemId() }
                .mapNotNull { itemRepository.get(it)?.itemStack }
                .toList()
            val request = ParachuteRequest(
                parachuteTracker,
                center.blockX, center.blockY, center.blockZ, radius,
                config.parachuteSpeed, world, items)
            packageRequestHandler.handle(request)
        }

        sender.sendMessage(Component.text("Added $amount parachute request(s) to the queue.", NamedTextColor.GRAY))

        return true
    }

}