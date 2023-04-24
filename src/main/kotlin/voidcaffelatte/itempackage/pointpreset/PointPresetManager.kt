package voidcaffelatte.itempackage.pointpreset

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import voidcaffelatte.itempackage.ItemPackage

class PointPresetManager(
    plugin: ItemPackage,
    private val pointPresetRepository: PointPresetRepository) : Listener
{
    private val editors: HashMap<String, PointPreset> = hashMapOf()

    init
    {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    fun startEditing(editorId: String, presetId: String)
    {
        if (getEditingPresetId(editorId) != null || getEditorId(presetId) != null) return
        editors[editorId] = pointPresetRepository.get(presetId) ?: PointPreset(presetId)
    }

    fun endEditing(editorId: String): PointPreset?
    {
        val preset = editors[editorId] ?: return null
        pointPresetRepository.set(preset)
        editors.remove(editorId)
        return preset
    }

    fun getEditorId(presetId: String): String?
    {
        return editors.entries.firstOrNull { it.value.id == presetId }?.key
    }

    fun getEditingPresetId(editorId: String): String?
    {
        return editors[editorId]?.id
    }

    @EventHandler
    private fun onPlayerInteract(event: PlayerInteractEvent)
    {
        if (event.action != Action.RIGHT_CLICK_BLOCK) return
        if (event.hand != EquipmentSlot.HAND) return

        val uuid = event.player.uniqueId.toString()
        val preset = editors[uuid] ?: return

        val clickedPosition = event.clickedBlock?.location?.toVector() ?: return
        val point = PointPreset.Companion.Point(clickedPosition.blockX, clickedPosition.blockY, clickedPosition.blockZ)

        if (preset.contains(point))
        {
            preset.remove(point)
            event.player.sendMessage(
                Component.text("Unregistered the point (${point.blockX}, ${point.blockY}, ${point.blockZ}) to the preset \"${preset.id}\".", NamedTextColor.GRAY))
        }
        else
        {
            preset.add(point)
            event.player.sendMessage(
                Component.text("Registered the point (${point.blockX}, ${point.blockY}, ${point.blockZ}) to the preset \"${preset.id}\".", NamedTextColor.GRAY))
        }

        event.isCancelled = true
    }
}