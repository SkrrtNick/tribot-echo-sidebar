package scripts.echosidebar

import java.awt.Color
import java.awt.Font

data class EchoTheme(
    val background: Color,
    val headerColor: Color,
    val labelColor: Color,
    val valueColor: Color,
    val accentColor: Color,
    val separatorColor: Color,
    val headerFont: Font,
    val labelFont: Font,
    val valueFont: Font,
    val padding: Int = 8,
    val sectionSpacing: Int = 8
) {
    companion object {
        val DARK = EchoTheme(
            background = Color(30, 30, 30),
            headerColor = Color(200, 200, 200),
            labelColor = Color(150, 150, 150),
            valueColor = Color(100, 220, 180),
            accentColor = Color(0, 150, 136),
            separatorColor = Color(60, 60, 60),
            headerFont = Font("SansSerif", Font.BOLD, 12),
            labelFont = Font("SansSerif", Font.PLAIN, 11),
            valueFont = Font("SansSerif", Font.BOLD, 11)
        )

        val LIGHT = EchoTheme(
            background = Color(240, 240, 240),
            headerColor = Color(40, 40, 40),
            labelColor = Color(80, 80, 80),
            valueColor = Color(0, 120, 100),
            accentColor = Color(0, 150, 136),
            separatorColor = Color(210, 210, 210),
            headerFont = Font("SansSerif", Font.BOLD, 12),
            labelFont = Font("SansSerif", Font.PLAIN, 11),
            valueFont = Font("SansSerif", Font.BOLD, 11)
        )

        val OSRS = EchoTheme(
            background = Color(60, 50, 35),
            headerColor = Color(255, 176, 0),
            labelColor = Color(200, 180, 140),
            valueColor = Color(255, 255, 100),
            accentColor = Color(255, 176, 0),
            separatorColor = Color(80, 70, 50),
            headerFont = Font("SansSerif", Font.BOLD, 12),
            labelFont = Font("SansSerif", Font.PLAIN, 11),
            valueFont = Font("SansSerif", Font.BOLD, 11)
        )
    }
}
