package scripts.echosidebar.ui

import scripts.echosidebar.EchoTheme
import java.awt.Component
import java.awt.Dimension
import java.awt.FlowLayout
import javax.swing.JLabel
import javax.swing.JPanel

/**
 * Creates a left-aligned row that constrains its max height to prevent
 * BoxLayout from stretching FlowLayout panels vertically.
 */
fun compactRow(vararg components: java.awt.Component): JPanel =
    JPanel(FlowLayout(FlowLayout.LEFT, 0, 0)).apply {
        isOpaque = false
        alignmentX = Component.LEFT_ALIGNMENT
        for (c in components) add(c)
        maximumSize = Dimension(Int.MAX_VALUE, preferredSize.height)
    }

/**
 * Creates a styled section header label.
 */
fun sectionHeader(text: String, theme: EchoTheme): JLabel =
    JLabel(text).apply {
        font = theme.headerFont
        foreground = theme.headerColor
        alignmentX = Component.LEFT_ALIGNMENT
    }
