package voidcaffelatte.itempackage.command

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player
import voidcaffelatte.itempackage.ItemPackage
import voidcaffelatte.itempackage.Utility
import voidcaffelatte.itempackage.item.ItemRepository

class GiveItemCommand(private val plugin: ItemPackage, private val itemRepository: ItemRepository) : TabExecutor
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
                .plus(itemRepository.getAllIds())
                .filter { it.startsWith(currentText) }
                .toList()

            3 -> sequenceOf("<player-id/selector>")
                .plus(plugin.server.onlinePlayers.map { it.name })
                .filter { it.startsWith(currentText) }
                .toList()

            4 -> sequenceOf("[amount]")
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

        if (!arguments.hasNext()) return false
        val itemId = arguments.next()

        if (!arguments.hasNext()) return false
        val selector = arguments.next()

        val amount = if (arguments.hasNext()) arguments.next().toIntOrNull() ?: return false else 1

        val item = itemRepository.get(itemId)
        if (item == null)
        {
            sender.sendMessage(Component.text("The item \"$itemId\" is not found.", NamedTextColor.RED))
            return true
        }

        val itemStack = item.itemStack.clone().also { it.amount *= amount }

        val players = Utility.selectEntities(sender, selector).filterIsInstance<Player>()

        if (players.isEmpty())
        {
            sender.sendMessage(Component.text("There are no target players.", NamedTextColor.RED))
            return true
        }

        for (player in players)
        {
            player.inventory.addItem(itemStack)
        }

        sender.sendMessage(
            Component.text("Gave $amount item(s) \"$itemId\" to ${players.size} player(s).", NamedTextColor.GRAY))

        return true
    }
}