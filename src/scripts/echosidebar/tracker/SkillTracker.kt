package scripts.echosidebar.tracker

import org.tribot.script.sdk.Skill
import scripts.echosidebar.EchoFormat
import scripts.echosidebar.EchoTheme
import scripts.echosidebar.component.SidebarComponent
import scripts.echosidebar.ui.compactRow
import scripts.echosidebar.ui.sectionHeader
import java.awt.Component
import javax.swing.BoxLayout
import javax.swing.JLabel
import javax.swing.JPanel

/**
 * Tracks XP gained for skills. If [skills] is empty, tracks ALL skills
 * and only displays those with XP gained > 0.
 */
class SkillTracker(private val skills: List<Skill>) : SidebarComponent {

    private val trackAll = skills.isEmpty()
    private val trackedSkills: List<Skill> = skills.ifEmpty { Skill.values().toList() }

    private val baselines = mutableMapOf<Skill, Long>()
    private val skillLabels = mutableMapOf<Skill, JLabel>()
    private val skillRows = mutableMapOf<Skill, JPanel>()
    private lateinit var totalLabel: JLabel
    private lateinit var skillPanel: JPanel
    private lateinit var theme: EchoTheme
    private var baselinesInitialized = false

    private fun captureBaselines() {
        if (baselinesInitialized) return
        for (skill in trackedSkills) {
            if (skill !in baselines) {
                val xp = skill.xp.toLong()
                if (xp > 0) {
                    baselines[skill] = xp
                }
            }
        }
        if (baselines.size == trackedSkills.size) {
            baselinesInitialized = true
        }
    }

    override fun buildPanel(theme: EchoTheme): JPanel {
        this.theme = theme
        captureBaselines()

        totalLabel = JLabel("0 xp").apply {
            font = theme.valueFont
            foreground = theme.valueColor
            alignmentX = Component.LEFT_ALIGNMENT
        }

        skillPanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            isOpaque = false
            alignmentX = Component.LEFT_ALIGNMENT
        }

        // Pre-build rows for explicitly listed skills
        if (!trackAll) {
            for (skill in trackedSkills) {
                addSkillRow(skill)
            }
        }

        return JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            isOpaque = false
            alignmentX = Component.LEFT_ALIGNMENT
            add(sectionHeader("XP Gained", theme))
            add(totalLabel)
            add(skillPanel)
        }
    }

    override fun refresh(elapsedMs: Long) {
        captureBaselines()

        var totalXp = 0L
        for (skill in trackedSkills) {
            val baseline = baselines[skill] ?: continue
            val gained = (skill.xp.toLong() - baseline).coerceAtLeast(0)
            totalXp += gained

            if (gained > 0 || !trackAll) {
                val label = skillLabels.getOrPut(skill) { addSkillRow(skill) }
                val perHour = EchoFormat.perHour(gained, elapsedMs)
                label.text = "${EchoFormat.formatNumber(gained)} xp (${EchoFormat.formatNumber(perHour)}/hr)"
            }
        }
        totalLabel.text = "${EchoFormat.formatNumber(totalXp)} xp"
    }

    private fun addSkillRow(skill: Skill): JLabel {
        val label = JLabel("0 xp").apply {
            font = theme.valueFont
            foreground = theme.valueColor
        }
        skillLabels[skill] = label
        val row = compactRow(
            JLabel("  ${EchoFormat.formatSkillName(skill.name)}: ").apply {
                font = theme.labelFont
                foreground = theme.labelColor
            },
            label
        )
        skillRows[skill] = row
        skillPanel.add(row)
        skillPanel.revalidate()
        return label
    }
}
