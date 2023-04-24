package voidcaffelatte.itempackage.lot

import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

class LotRepository(
    private val dataFolder: File)
{
    private val lots: MutableMap<String, Lot> = hashMapOf()

    fun load()
    {
        lots.clear()

        val file = File(dataFolder, FILE_NAME)
        val yaml = YamlConfiguration.loadConfiguration(file)
        val rootSection = getRootSection(yaml)
        val lotIds = rootSection.getKeys(false)
        for (lotId in lotIds)
        {
            val lotSection = rootSection.getConfigurationSection(lotId)!!
            val itemIds = lotSection.getKeys(false)
            val lot = Lot(lotId)
            for (itemId in itemIds)
            {
                val itemSection = lotSection.getConfigurationSection(itemId)!!
                val rate = itemSection.getDouble("rate")
                lot.set(itemId, rate)
            }
            lots[lotId] = lot
        }
    }

    fun getAllIds(): List<String>
    {
        return lots.keys.toList()
    }

    fun get(id: String): Lot?
    {
        return lots[id]
    }

    fun getAll(): List<Lot>
    {
        return lots.values.toList()
    }

    fun set(lot: Lot)
    {
        val lotId = lot.id

        lots[lotId] = lot

        val file = File(dataFolder, FILE_NAME)
        val yaml = YamlConfiguration.loadConfiguration(file)
        val rootSection = getRootSection(yaml)
        val lotSection = rootSection.createSection(lotId)
        for (itemId in lot.getAllItemIds())
        {
            val itemSection = lotSection.createSection(itemId)
            val rate = lot.getRate(itemId)
            itemSection.set("rate", rate)
        }
        yaml.save(file)
    }

    private fun getRootSection(yaml: YamlConfiguration): ConfigurationSection
    {
        return yaml.getConfigurationSection(ROOT_SECTION_NAME) ?: yaml.createSection(ROOT_SECTION_NAME)
    }

    companion object
    {
        private const val FILE_NAME = "lots.yml"
        private const val ROOT_SECTION_NAME = "lots"
    }
}