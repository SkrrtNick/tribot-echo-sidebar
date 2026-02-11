package scripts.echosidebar.tracker

import scripts.echosidebar.EchoFormat
import scripts.echosidebar.EchoTheme
import scripts.echosidebar.component.SidebarComponent
import scripts.echosidebar.ui.compactRow
import javax.swing.JLabel
import javax.swing.JPanel

class RuntimeTracker : SidebarComponent {

    private lateinit var valueLabel: JLabel

    override fun buildPanel(theme: EchoTheme): JPanel {
        valueLabel = JLabel("0:00:00").apply {
            font = theme.valueFont
            foreground = theme.valueColor
        }
        return compactRow(
            JLabel("Runtime: ").apply {
                font = theme.labelFont
                foreground = theme.labelColor
            },
            valueLabel
        )
    }

    override fun refresh(elapsedMs: Long) {
        valueLabel.text = EchoFormat.formatTime(elapsedMs)
    }
}
