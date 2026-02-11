package scripts.echosidebar.tracker

import org.tribot.script.sdk.pricing.Pricing
import org.tribot.script.sdk.query.Query
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.net.HttpURLConnection
import java.net.URI
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import javax.imageio.ImageIO

object ItemDataService {

    private val icons = ConcurrentHashMap<Int, BufferedImage>()

    private val executor = Executors.newFixedThreadPool(4) { r ->
        Thread(r).apply { isDaemon = true }
    }

    fun loadIconAsync(itemId: Int) {
        if (icons.containsKey(itemId)) return
        executor.submit {
            try {
                val url = "https://static.runelite.net/cache/item/icon/$itemId.png"
                val bytes = fetchBytes(url)
                if (bytes != null) {
                    val img = ImageIO.read(ByteArrayInputStream(bytes))
                    if (img != null) icons[itemId] = img
                }
            } catch (_: Exception) {
                // Icon load failed silently — cell stays empty until retry
            }
        }
    }

    fun getIcon(itemId: Int): BufferedImage? = icons[itemId]

    fun getPrice(itemId: Int): Long = Pricing.lookupPrice(itemId).orElse(0).toLong()

    fun getName(itemId: Int): String {
        val result = Query.itemDefinitions().idEquals(itemId).findFirst()
        return if (result.isPresent) result.get().name else "Item #$itemId"
    }

    private fun fetchBytes(url: String): ByteArray? {
        return try {
            val conn = URI(url).toURL().openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.setRequestProperty("User-Agent", "LootTracker/1.0")
            conn.connectTimeout = 5_000
            conn.readTimeout = 10_000
            if (conn.responseCode == 200) {
                conn.inputStream.use { it.readBytes() }
            } else null
        } catch (_: Exception) {
            null
        }
    }
}
