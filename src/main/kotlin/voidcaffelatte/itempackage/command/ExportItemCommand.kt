package voidcaffelatte.itempackage.command

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player
import voidcaffelatte.itempackage.item.Item
import voidcaffelatte.itempackage.item.ItemRepository

class ExportItemCommand(private val itemRepository: ItemRepository) : TabExecutor
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
            2 -> sequenceOf("<item-id>")
                .plus(itemRepository.getAllIds().filter { it.startsWith(currentText) })
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

        val itemInMainHand = sender.inventory.itemInMainHand
        if (itemInMainHand.type == Material.AIR)
        {
            sender.sendMessage(Component.text("Need to have an item in the main hand.", NamedTextColor.RED))
            return true
        }

        val item = Item(itemId, itemInMainHand)
        itemRepository.set(item)
        sender.sendMessage(Component.text("Exported the item as \"$itemId\".", NamedTextColor.GRAY))

        return true
    }
}