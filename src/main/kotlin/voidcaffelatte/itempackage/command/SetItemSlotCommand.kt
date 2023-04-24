package voidcaffelatte.itempackage.command

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import voidcaffelatte.itempackage.ItemPackage
import voidcaffelatte.itempackage.Utility
import voidcaffelatte.itempackage.item.ItemRepository

class SetItemSlotCommand(
    private val plugin: ItemPackage,
    private val itemRepository: ItemRepository)
    : TabExecutor
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

            4 -> sequenceOf("<inventory-slot>")
                .plus(EquipmentSlot.values().map { it.toString() })
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

        if (!arguments.hasNext()) return false
        val equipmentSlotName = arguments.next()
        val equipmentSlot = EquipmentSlot.values()
            .firstOrNull { equipmentSlotName.equals(it.toString(), true) }
            ?: return false

        val item = itemRepository.get(itemId)
        if (item == null)
        {
            sender.sendMessage(Component.text("The item \"$itemId\" is not found.", NamedTextColor.RED))
            return true
        }

        val players = Utility.selectEntities(sender, selector).filterIsInstance<Player>()

        if (players.isEmpty())
        {
            sender.sendMessage(Component.text("There are no target players.", NamedTextColor.RED))
            return true
        }

        for (player in players)
        {
            player.inventory.setItem(equipmentSlot, item.itemStack)
        }

        sender.sendMessage(
            Component.text("Set the item \"$itemId\" in the target slots of ${players.size} player(s).", NamedTextColor.GRAY))

        return true
    }
}