package voidcaffelatte.itempackage.pointpreset

import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

class PointPresetRepository(private val dataFolder: File)
{
    private val pointPresets: MutableMap<String, PointPreset> = hashMapOf()

    fun load()
    {
        pointPresets.clear()

        val file = File(dataFolder, FILE_NAME)
        val yaml = YamlConfiguration.loadConfiguration(file)
        val rootSection = yaml.getConfigurationSection(ROOT_SECTION_NAME) ?: yaml.createSection(ROOT_SECTION_NAME)
        for (id in rootSection.getKeys(false))
        {
            val pointPreset = PointPreset(id)

            val presetSection = rootSection.getConfigurationSection(id) ?: rootSection.createSection(id)
            for (index in presetSection.getKeys(false))
            {
                val pointSection = presetSection.getConfigurationSection(index.toString()) ?: presetSection.createSection(index.toString())
                val x = pointSection.getInt("x")
                val y = pointSection.getInt("y")
                val z = pointSection.getInt("z")
                val point = PointPreset.Companion.Point(x, y, z)
                pointPreset.add(point)
            }

            pointPresets[id] = pointPreset
        }
    }

    fun get(id: String): PointPreset?
    {
        return pointPresets[id]
    }

    fun getAll(): List<PointPreset>
    {
        return pointPresets.values.toList()
    }

    fun set(pointPreset: PointPreset)
    {
        val id = pointPreset.id
        pointPresets[id] = pointPreset

        val file = File(dataFolder, FILE_NAME)
        val yaml = YamlConfiguration.loadConfiguration(file)
        val rootSection = yaml.getConfigurationSection(ROOT_SECTION_NAME) ?: yaml.createSection(ROOT_SECTION_NAME)
        val presetSection = rootSection.getConfigurationSection(id) ?: rootSection.createSection(id)
        for ((index, point) in pointPreset.getAllPoints().withIndex())
        {
            val pointSection = presetSection.getConfigurationSection(index.toString()) ?: presetSection.createSection(index.toString())
            pointSection.set("x", point.blockX)
            pointSection.set("y", point.blockY)
            pointSection.set("z", point.blockZ)
        }
        yaml.save(file)
    }

    companion object
    {
        private const val FILE_NAME = "point_presets.yml"
        private const val ROOT_SECTION_NAME = "point-presets"
    }
}