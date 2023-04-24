package voidcaffelatte.itempackage.pointpreset

class PointPreset(val id: String)
{
    private val points: MutableSet<Point> = hashSetOf()

    fun add(point: Point)
    {
        points.add(point)
    }

    fun contains(point: Point): Boolean
    {
        return points.contains(point)
    }

    fun remove(point: Point)
    {
        points.remove(point)
    }

    fun getAllPoints(): List<Point>
    {
        return points.toList()
    }

    companion object
    {
        data class Point(val blockX: Int, val blockY: Int, val blockZ: Int)
    }
}