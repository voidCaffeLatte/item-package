package voidcaffelatte.itempackage.lot

class Lot(val id: String)
{
    private val items: MutableMap<String, Item> = hashMapOf()

    fun set(itemId: String, rate: Double)
    {
        items.getOrPut(itemId) { Item(itemId) }.setRate(rate)
    }

    fun remove(itemId: String)
    {
        items.remove(itemId)
    }

    fun contains(itemId: String): Boolean
    {
        return items.containsKey(itemId)
    }

    fun getRandomItemId(): String?
    {
        if (items.isEmpty()) return null
        val total = items.values.sumOf { it.rate }
        var randomValue = Math.random() * total
        for (item in items.values)
        {
            val rate = item.rate
            if (rate >= randomValue) return item.id
            randomValue -= rate
        }
        return items.values.last().id
    }

    fun getAllItemIds(): List<String>
    {
        return items.values.map { it.id }
    }

    fun getRate(itemId: String): Double?
    {
        val item = items[itemId] ?: return null
        return item.rate
    }

    fun getAbsoluteRate(itemId: String): Double?
    {
        val item = items[itemId] ?: return null
        val total = items.values.sumOf { it.rate }
        return item.rate / total;
    }

    companion object
    {
        private class Item(val id: String)
        {
            var rate: Double = 0.0
                private set

            fun setRate(rate: Double)
            {
                this.rate = rate
            }
        }
    }
}