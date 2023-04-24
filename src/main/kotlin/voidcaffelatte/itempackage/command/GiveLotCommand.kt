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
import voidcaffelatte.itempackage.lot.LotRepository

class GiveLotCommand(
    private val plugin: ItemPackage,
    private val lotRepository: LotRepository,
    private val itemRepository: ItemRepository) : TabExecutor
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
                .plus(lotRepository.getAllIds())
                .filter { it.startsWith(currentText) }
                .toList()

            3 -> sequenceOf("<player-id/selector>")
                .plus(plugin.server.onlinePlayers.map { it.name })
                .filter { it.startsWith(currentText) }
                .toList()

            else -> listOf()
        }
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean
    {
        val arguments = args.iterator()
        arguments.next()

        if (!arguments.hasNext()) return false
        val lotId = arguments.next()

        if (!arguments.hasNext()) return false
        val selector = arguments.next()

        val lot = lotRepository.get(lotId)
        if (lot == null)
        {
            sender.sendMessage(Component.text("The lot \"$lotId\" is not found.", NamedTextColor.RED))
            return true
        }

        val items = lot.getAllItemIds().asSequence()
            .mapNotNull { itemRepository.get(it) }
            .map { it.itemStack }
            .toList()

        val players = Utility.selectEntities(sender, selector).filterIsInstance<Player>()

        if (players.isEmpty())
        {
            sender.sendMessage(Component.text("There are no target players.", NamedTextColor.RED))
            return true
        }

        for (player in players)
        {
            for (item in items)
            {
                player.inventory.addItem(item)
            }
        }

        sender.sendMessage(
            Component.text("Gave the lot \"$lotId\" items to ${players.size} player(s).", NamedTextColor.GRAY))

        return true
    }
}