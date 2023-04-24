package voidcaffelatte.itempackage.command

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import voidcaffelatte.itempackage.item.ItemRepository
import voidcaffelatte.itempackage.lot.LotRepository

class RemoveLotItem(private val lotRepository: LotRepository, private val itemRepository: ItemRepository) : TabExecutor
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

            3 -> sequenceOf("<item-id>")
                .plus(itemRepository.getAllIds()
                    .asSequence()
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
        val itemId = arguments.next()

        val lot = lotRepository.get(lotId)
        if (lot == null)
        {
            sender.sendMessage(Component.text("The lot \"$lotId\" is not found.", NamedTextColor.RED))
            return true
        }

        if (!lot.contains(itemId))
        {
            sender.sendMessage(
                Component.text("The item \"$itemId\" is not found in the lot \"$lotId\".", NamedTextColor.RED))
            return true
        }

        lot.remove(itemId)
        lotRepository.set(lot)

        sender.sendMessage(
            Component.text("Removed the item \"$itemId\" from the lot \"$lotId\".", NamedTextColor.GRAY))

        return true
    }
}