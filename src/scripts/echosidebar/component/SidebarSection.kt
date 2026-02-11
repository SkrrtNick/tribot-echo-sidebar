package scripts.echosidebar.component

import scripts.echosidebar.EchoDsl
import scripts.echosidebar.EchoTheme
import scripts.echosidebar.ui.sectionHeader
import java.awt.Component
import java.awt.Dimension
import javax.swing.BoxLayout
import javax.swing.JPanel

class SidebarSection(
    private val title: String,
    private val rows: List<SidebarRow>
) : SidebarComponent {

    private lateinit var panel: JPanel

    override fun buildPanel(theme: EchoTheme): JPanel {
        panel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            isOpaque = false
            alignmentX = Component.LEFT_ALIGNMENT
            add(sectionHeader(title, theme))
            for (row in rows) {
                add(row.buildPanel(theme))
            }
            maximumSize = Dimension(Int.MAX_VALUE, preferredSize.height)
        }
        return panel
    }

    override fun refresh(elapsedMs: Long) {
        for (row in rows) {
            row.refresh(elapsedMs)
        }
    }
}

@EchoDsl
class SectionBuilder {
    private val rows = mutableListOf<SidebarRow>()

    fun row(label: String, valueSupplier: () -> String) {
        rows.add(SidebarRow(label, valueSupplier))
    }

    fun build(): List<SidebarRow> = rows.toList()
}
