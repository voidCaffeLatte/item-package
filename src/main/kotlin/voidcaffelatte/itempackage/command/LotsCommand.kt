package voidcaffelatte.itempackage.command

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import voidcaffelatte.itempackage.lot.LotRepository

class LotsCommand(private val lotRepository: LotRepository) : TabExecutor
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
            2 -> sequenceOf("[lot-id]")
                .plus(lotRepository.getAllIds())
                .filter { it.startsWith(currentText) }
                .toList()

            else -> listOf()
        }
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean
    {
        val arguments = args.iterator()

        if (!arguments.hasNext()) return false
        arguments.next()

        if (!arguments.hasNext())
        {
            val textComponent = Component.text()
                .append(Component.text("=== All Lots ==="))
            for (id in lotRepository.getAllIds()) textComponent.appendNewline().append(Component.text("- $id"))
            sender.sendMessage(textComponent.color(NamedTextColor.GRAY))
            return true
        }

        val lotId = arguments.next()
        val lot = lotRepository.get(lotId)
        if (lot == null)
        {
            sender.sendMessage(Component.text("The lot \"$lotId\" is not found.", NamedTextColor.RED))
            return true
        }

        val textComponent = Component.text()
            .append(Component.text("=== Lot Details ($lotId) ==="))
        for (itemId in lot.getAllItemIds())
        {
            val absolutePercentage = lot.getAbsoluteRate(itemId)!! * 100.0
            textComponent
                .appendNewline()
                .append(Component.text("- $itemId"))
                .appendNewline()
                .append(Component.text("    - rate: ${lot.getRate(itemId)}"))
                .appendNewline()
                .append(Component.text("    - absolute-percentage: %.2f%%".format(absolutePercentage)))
        }
        sender.sendMessage(textComponent.color(NamedTextColor.GRAY))

        return true
    }
}