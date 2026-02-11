package scripts.echosidebar

import org.tribot.script.sdk.client.ClientSidebar
import scripts.echosidebar.component.SidebarComponent
import scripts.echosidebar.tracker.LootTracker
import java.awt.Color
import java.awt.Component
import java.awt.Dimension
import java.awt.Graphics
import java.awt.image.BufferedImage
import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.JPanel
import javax.swing.SwingUtilities
import javax.swing.border.EmptyBorder

class EchoSidebar internal constructor(
    private val title: String,
    private val theme: EchoTheme,
    private val components: List<SidebarComponent>,
    private val lootTracker: LootTracker?
) {
    private val startTime = System.currentTimeMillis()
    private val panel = JPanel().apply {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
        border = EmptyBorder(theme.padding, theme.padding + 2, theme.padding, theme.padding + 2)
        background = theme.background
    }

    init {
        for ((index, component) in components.withIndex()) {
            if (index > 0) {
                panel.add(Box.createVerticalStrut(theme.sectionSpacing))
                panel.add(createSeparator())
                panel.add(Box.createVerticalStrut(theme.sectionSpacing))
            }
            val sub = component.buildPanel(theme)
            sub.alignmentX = Component.LEFT_ALIGNMENT
            panel.add(sub)
        }
        panel.add(Box.createVerticalGlue())
    }

    fun open(icon: BufferedImage? = null) {
        val sidebarIcon = icon ?: createDefaultIcon()
        ClientSidebar.addSidebarTab(title, sidebarIcon, panel)
    }

    fun update() {
        SwingUtilities.invokeLater {
            val elapsed = System.currentTimeMillis() - startTime
            for (component in components) {
                component.refresh(elapsed)
            }
            panel.revalidate()
            panel.repaint()
        }
    }

    fun addLoot(itemId: Int, quantity: Int = 1) {
        lootTracker?.addLoot(itemId, quantity)
    }

    fun close() {
        ClientSidebar.removeSidebarTab(title)
    }

    private fun createSeparator(): JPanel {
        val color = theme.separatorColor
        return object : JPanel() {
            init {
                isOpaque = false
                alignmentX = Component.LEFT_ALIGNMENT
                maximumSize = Dimension(Int.MAX_VALUE, 1)
                preferredSize = Dimension(0, 1)
            }
            override fun paintComponent(g: Graphics) {
                super.paintComponent(g)
                g.color = color
                g.fillRect(0, 0, width, 1)
            }
        }
    }

    private fun createDefaultIcon(): BufferedImage =
        BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB).apply {
            val g = createGraphics()
            g.color = theme.accentColor
            g.fillOval(0, 0, 16, 16)
            g.dispose()
        }
}
