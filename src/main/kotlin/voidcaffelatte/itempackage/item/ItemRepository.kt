package voidcaffelatte.itempackage.item

import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

class ItemRepository(private val dataFolder: File)
{
    private val items: MutableMap<String, Item> = hashMapOf()

    fun load()
    {
        items.clear()

        val file = File(dataFolder, FILE_NAME)
        val yaml = YamlConfiguration.loadConfiguration(file)
        val rootSection = getRootSection(yaml)
        val itemIds = rootSection.getKeys(false)
        for (itemId in itemIds)
        {
            val itemStack = rootSection.getItemStack(itemId) ?: continue
            val item = Item(itemId, itemStack)
            items[itemId] = item
        }
    }

    fun get(id: String): Item?
    {
        return items[id]
    }

    fun getAll(): List<Item>
    {
        return items.values.toList()
    }

    fun set(item: Item)
    {
        val id = item.id
        items[id] = item
        save(item)
    }

    fun getAllIds(): List<String>
    {
        return items.keys.toList()
    }

    private fun save(item: Item)
    {
        val file = File(dataFolder, FILE_NAME)
        val yaml = YamlConfiguration.loadConfiguration(file)
        val rootSection = getRootSection(yaml)
        rootSection.set(item.id, item.itemStack)
        yaml.save(file)
    }

    private fun getRootSection(yaml: YamlConfiguration): ConfigurationSection
    {
        return yaml.getConfigurationSection(ROOT_SECTION_NAME) ?: yaml.createSection(ROOT_SECTION_NAME)
    }

    companion object
    {
        private const val FILE_NAME = "items.yml"
        private const val ROOT_SECTION_NAME = "items"
    }
}
