package scripts.echosidebar.component

import scripts.echosidebar.EchoTheme
import javax.swing.JPanel

interface SidebarComponent {
    fun buildPanel(theme: EchoTheme): JPanel
    fun refresh(elapsedMs: Long)
}
