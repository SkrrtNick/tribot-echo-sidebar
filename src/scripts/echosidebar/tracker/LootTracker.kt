package scripts.echosidebar.tracker

import scripts.echosidebar.EchoFormat
import scripts.echosidebar.EchoTheme
import scripts.echosidebar.component.SidebarComponent
import scripts.echosidebar.ui.sectionHeader
import java.awt.Color
import java.awt.Component
import java.awt.Dimension
import java.awt.Font
import java.awt.GridLayout
import java.awt.image.BufferedImage
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicLong
import javax.swing.BorderFactory
import javax.swing.BoxLayout
import javax.swing.ImageIcon
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.SwingConstants

class LootTracker : SidebarComponent {

    private val counts = ConcurrentHashMap<Int, AtomicLong>()
    private val ordering = CopyOnWriteArrayList<Int>()
    private val cells = ConcurrentHashMap<Int, JLabel>()
    private val fillers = mutableListOf<JLabel>()

    private lateinit var theme: EchoTheme
    private lateinit var lootPanel: JPanel
    private lateinit var gridPanel: JPanel
    private lateinit var totalLabel: JLabel

    companion object {
        private const val COLUMNS = 6
        private const val ICON_W = 36
        private const val ICON_H = 32
        private val OVERLAY_FONT = Font("SansSerif", Font.BOLD, 10)
        private val COLOR_YELLOW = Color(0xFF, 0xFF, 0x00)
        private val COLOR_WHITE = Color.WHITE
        private val COLOR_GREEN = Color(0x00, 0xFF, 0x80)
        private val COLOR_SHADOW = Color.BLACK
    }

    fun addLoot(itemId: Int, quantity: Int) {
        counts.computeIfAbsent(itemId) { AtomicLong(0) }.addAndGet(quantity.toLong())
        if (!ordering.contains(itemId)) {
            ordering.addIfAbsent(itemId)
            ItemDataService.loadIconAsync(itemId)
        }
    }

    override fun buildPanel(theme: EchoTheme): JPanel {
        this.theme = theme

        totalLabel = JLabel("Total: 0 gp").apply {
            font = theme.valueFont
            foreground = Color(0xFF, 0xD7, 0x00) // gold
            alignmentX = Component.LEFT_ALIGNMENT
        }

        gridPanel = JPanel(GridLayout(0, COLUMNS, 1, 1)).apply {
            isOpaque = false
        }

        val gridWrapper = JPanel(java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0)).apply {
            isOpaque = false
            alignmentX = Component.LEFT_ALIGNMENT
            add(gridPanel)
        }

        lootPanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            isOpaque = false
            alignmentX = Component.LEFT_ALIGNMENT
            add(sectionHeader("Loot", theme))
            add(totalLabel)
            add(gridWrapper)
        }
        return lootPanel
    }

    override fun refresh(elapsedMs: Long) {
        var totalGp = 0L

        for (itemId in ordering) {
            val qty = counts[itemId]?.get() ?: continue
            val price = ItemDataService.getPrice(itemId)
            totalGp += qty * price

            val cell = cells.getOrPut(itemId) {
                createCell().also { gridPanel.add(it) }
            }

            val icon = ItemDataService.getIcon(itemId)
            if (icon != null) {
                cell.icon = ImageIcon(renderQuantityOverlay(icon, qty))
            }

            val name = ItemDataService.getName(itemId)
            val priceEach = if (price > 0) EchoFormat.formatNumber(price) else "?"
            cell.toolTipText = "$name x${EchoFormat.formatNumber(qty)} (${priceEach} ea)"
        }

        // Manage filler cells to pad last row to COLUMNS
        val itemCount = ordering.size
        val remainder = if (itemCount % COLUMNS == 0 && itemCount > 0) 0 else COLUMNS - (itemCount % COLUMNS)

        // Remove old fillers
        for (filler in fillers) {
            gridPanel.remove(filler)
        }
        fillers.clear()

        // Add new fillers
        if (remainder in 1 until COLUMNS) {
            repeat(remainder) {
                val fillerSize = Dimension(ICON_W, ICON_H)
                val filler = JLabel().apply {
                    isOpaque = false
                    preferredSize = fillerSize
                    minimumSize = fillerSize
                    maximumSize = fillerSize
                }
                fillers.add(filler)
                gridPanel.add(filler)
            }
        }

        totalLabel.text = "Total: ${EchoFormat.formatNumber(totalGp)} gp"

        gridPanel.revalidate()
        gridPanel.repaint()
    }

    private fun createCell(): JLabel {
        val cellBg = brighten(theme.background, 15)
        val size = Dimension(ICON_W, ICON_H)
        return JLabel().apply {
            horizontalAlignment = SwingConstants.CENTER
            verticalAlignment = SwingConstants.CENTER
            isOpaque = true
            background = cellBg
            preferredSize = size
            minimumSize = size
            maximumSize = size
            border = BorderFactory.createEmptyBorder(0, 0, 0, 0)
        }
    }

    private fun renderQuantityOverlay(icon: BufferedImage, qty: Long): BufferedImage {
        if (qty <= 1) return icon

        val copy = BufferedImage(icon.width, icon.height, BufferedImage.TYPE_INT_ARGB)
        val g = copy.createGraphics()
        g.drawImage(icon, 0, 0, null)
        g.font = OVERLAY_FONT

        val text = formatStackSize(qty)
        val color = when {
            qty >= 10_000_000 -> COLOR_GREEN
            qty >= 100_000 -> COLOR_WHITE
            else -> COLOR_YELLOW
        }

        // Shadow
        g.color = COLOR_SHADOW
        g.drawString(text, 2, 11)
        // Foreground
        g.color = color
        g.drawString(text, 1, 10)
        g.dispose()
        return copy
    }

    private fun formatStackSize(qty: Long): String = when {
        qty >= 10_000_000 -> "${qty / 1_000_000}M"
        qty >= 100_000 -> "${qty / 1_000}K"
        else -> qty.toString()
    }

    private fun brighten(c: Color, amount: Int): Color {
        return Color(
            (c.red + amount).coerceIn(0, 255),
            (c.green + amount).coerceIn(0, 255),
            (c.blue + amount).coerceIn(0, 255)
        )
    }
}
