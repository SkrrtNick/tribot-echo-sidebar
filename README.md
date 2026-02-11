# EchoSidebar

A utility for [TRiBot](https://tribot.org/) using the Tribot echo sidebar in scripts.

## Features

- **Runtime Tracker** — live elapsed time display
- **Skill Tracker** — auto-detects XP gains across all skills, shows XP/hr
- **Loot Grid** — item icons in a compact grid with OSRS-style quantity overlays and GP total
- **Themes** — Dark, Light, and OSRS built-in themes
- **Kotlin DSL** — declarative sidebar builder

## Quick Start

```kotlin
val sidebar = echoSidebar("My Script") {
    theme = EchoTheme.DARK
    runtime()
    skillTracker()
    lootTracker()
    section("Status") {
        row("State") { currentState }
    }
}
sidebar.open()

// In your main loop:
sidebar.addLoot(itemId = 554, quantity = 50) // Fire rune
sidebar.update()
```

## Installation

Copy the `src/scripts/echosidebar/` directory into your TRiBot script project's source tree.

## Components

### Runtime Tracker
Displays elapsed script runtime in `H:MM:SS` format.

### Skill Tracker
Tracks XP gains across skills. Call `skillTracker()` with no arguments to auto-detect all skills, or pass specific skills:
```kotlin
skillTracker(Skill.MINING, Skill.SMITHING)
```

### Loot Tracker
Displays collected items as a grid of 36x32 icons fetched from RuneLite's cache. Prices come from the TRiBot SDK's `Pricing` API.

Quantity overlay text follows OSRS conventions:
- Yellow for < 100K
- White for 100K-9.99M
- Green for 10M+

### Custom Sections
Add arbitrary label/value rows:
```kotlin
section("Banking") {
    row("Trips") { tripCount.toString() }
    row("GP/hr") { formatNumber(gpPerHour) }
}
```

## Themes

| Theme | Description |
|-------|-------------|
| `EchoTheme.DARK` | Dark background, teal accents |
| `EchoTheme.LIGHT` | Light background, muted accents |
| `EchoTheme.OSRS` | Old School-styled brown/gold |

