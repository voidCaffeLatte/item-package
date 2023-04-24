package voidcaffelatte.itempackage.command

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import voidcaffelatte.itempackage.item.ItemRepository
import voidcaffelatte.itempackage.lot.Lot
import voidcaffelatte.itempackage.lot.LotRepository

class SetLotItemCommand(private val lotRepository: LotRepository, private val itemRepository: ItemRepository) :
    TabExecutor
{
    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>): List<String>
    {
        val currentText = args.lastOrNull() ?: return mutableListOf()
        return when (args.size)
        {
            2 -> sequenceOf("<lot-id>")
                .plus(lotRepository.getAllIds()
                    .asSequence()
                    .filter { it.startsWith(currentText) })
                .toList()

            3 -> sequenceOf("<item-id>")
                .plus(itemRepository.getAllIds()
                    .asSequence()
                    .filter { it.startsWith(currentText) })
                .toList()

            4 -> listOf("<rate>")
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
        val itemId = arguments.next()

        if (!arguments.hasNext()) return false
        val rate = arguments.next().toDoubleOrNull() ?: return false
        if (rate < 0.0) return false

        val item = itemRepository.get(itemId)
        if (item == null)
        {
            sender.sendMessage(Component.text("The item \"$itemId\" is not found.", NamedTextColor.RED))
            return true
        }

        var lot = lotRepository.get(lotId)
        if (lot == null)
        {
            lot = Lot(lotId)
            sender.sendMessage(Component.text("The new lot \"$lotId\" is created.", NamedTextColor.YELLOW))
        }

        lot.set(itemId, rate)
        lotRepository.set(lot)

        sender.sendMessage(
            Component.text("Set the item \"$itemId\" with the rate \"$rate\" in the lot \"$lotId\".", NamedTextColor.GRAY))

        return true
    }
}