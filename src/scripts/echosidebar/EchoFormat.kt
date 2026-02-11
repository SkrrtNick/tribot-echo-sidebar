package scripts.echosidebar

object EchoFormat {

    fun formatTime(ms: Long): String {
        val totalSec = ms / 1000
        val h = totalSec / 3600
        val m = (totalSec % 3600) / 60
        val s = totalSec % 60
        return String.format("%d:%02d:%02d", h, m, s)
    }

    fun formatNumber(value: Long): String = when {
        value >= 1_000_000 -> String.format("%.1fM", value / 1_000_000.0)
        value >= 1_000 -> String.format("%.1fk", value / 1_000.0)
        else -> value.toString()
    }

    fun perHour(gained: Long, elapsedMs: Long): Long =
        if (elapsedMs > 0) (gained * 3_600_000L) / elapsedMs else 0

    fun formatSkillName(name: String): String =
        name.lowercase().replaceFirstChar { it.uppercase() }
}
