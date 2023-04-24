package voidcaffelatte.itempackage.command

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player
import voidcaffelatte.itempackage.item.ItemRepository

class GetItemCommand(private val itemRepository: ItemRepository) : TabExecutor
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
            2 -> sequenceOf("<item-id>")
                .plus(itemRepository.getAll()
                    .asSequence()
                    .map { it.id }
                    .filter { it.startsWith(currentText) })
                .toList()

            else -> listOf()
        }
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean
    {
        if (sender !is Player)
        {
            sender.sendMessage(Component.text("This command can only be called by players.", NamedTextColor.RED))
            return true
        }

        val arguments = args.iterator()

        if (!arguments.hasNext()) return false
        arguments.next()

        if (!arguments.hasNext()) return false
        val itemId = arguments.next()

        val item = itemRepository.get(itemId)
        if (item == null)
        {
            sender.sendMessage(Component.text("The item \"$itemId\" is not found.", NamedTextColor.RED))
            return true
        }

        sender.inventory.addItem(item.itemStack)
        sender.sendMessage(Component.text("Got the item \"$itemId\".", NamedTextColor.GRAY))

        return true
    }
}