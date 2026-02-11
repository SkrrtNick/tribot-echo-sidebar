package scripts.echosidebar

import org.tribot.script.sdk.Skill
import scripts.echosidebar.component.SectionBuilder
import scripts.echosidebar.component.SidebarComponent
import scripts.echosidebar.component.SidebarSection
import scripts.echosidebar.tracker.LootTracker
import scripts.echosidebar.tracker.RuntimeTracker
import scripts.echosidebar.tracker.SkillTracker

@DslMarker
annotation class EchoDsl

@EchoDsl
class EchoSidebarBuilder(private val title: String) {
    var theme: EchoTheme = EchoTheme.DARK

    internal val components = mutableListOf<SidebarComponent>()
    internal var lootTracker: LootTracker? = null

    fun runtime() {
        components.add(RuntimeTracker())
    }

    /** Track specific skills. If no skills given, tracks all and only shows those with XP gained. */
    fun skillTracker(vararg skills: Skill) {
        components.add(SkillTracker(skills.toList()))
    }

    fun lootTracker() {
        val tracker = LootTracker()
        lootTracker = tracker
        components.add(tracker)
    }

    fun section(title: String, init: SectionBuilder.() -> Unit) {
        val rows = SectionBuilder().apply(init).build()
        components.add(SidebarSection(title, rows))
    }

    fun build(): EchoSidebar = EchoSidebar(title, theme, components.toList(), lootTracker)
}

fun echoSidebar(title: String, init: EchoSidebarBuilder.() -> Unit): EchoSidebar =
    EchoSidebarBuilder(title).apply(init).build()
