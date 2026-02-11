package scripts.echosidebar.component

import scripts.echosidebar.EchoTheme
import scripts.echosidebar.ui.compactRow
import javax.swing.JLabel
import javax.swing.JPanel

class SidebarRow(
    private val label: String,
    private val valueSupplier: () -> String
) : SidebarComponent {

    private lateinit var valueLabel: JLabel

    override fun buildPanel(theme: EchoTheme): JPanel {
        valueLabel = JLabel(valueSupplier()).apply {
            font = theme.valueFont
            foreground = theme.valueColor
        }
        return compactRow(
            JLabel("  $label: ").apply {
                font = theme.labelFont
                foreground = theme.labelColor
            },
            valueLabel
        )
    }

    override fun refresh(elapsedMs: Long) {
        valueLabel.text = valueSupplier()
    }
}
