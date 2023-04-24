package voidcaffelatte.itempackage.command

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import voidcaffelatte.itempackage.item.ItemRepository

class ItemsCommand(private val itemRepository: ItemRepository) : TabExecutor
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
            2 -> sequenceOf("[item-id]")
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
        val arguments = args.iterator()

        if (!arguments.hasNext()) return false
        arguments.next()

        if (!arguments.hasNext())
        {
            val textComponent = Component.text()
                .append(Component.text("=== All Items ==="))
            for (item in itemRepository.getAll()) textComponent.appendNewline().append(Component.text("- ${item.id}"))
            sender.sendMessage(textComponent.color(NamedTextColor.GRAY))
            return true
        }

        val itemId = arguments.next()
        val item = itemRepository.get(itemId)
        if (item == null)
        {
            sender.sendMessage(Component.text("${ChatColor.RED}The item \"$itemId\" is not found.", NamedTextColor.RED))
            return true
        }

        val textComponent = Component.text()
            .append(Component.text("=== Item Details (\"$itemId\") ==="))
            .appendNewline()
            .append(Component.text("- item-name: ").append(item.itemStack.displayName()))
            .appendNewline()
            .append(Component.text("- amount: ${item.itemStack.amount}"))
            .color(NamedTextColor.GRAY)
        sender.sendMessage(textComponent)

        return true
    }
}