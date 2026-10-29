# Create: Realistic Lighting

A Minecraft Java Edition **1.21.1 NeoForge** mod that brings replaceable light bulbs, industrial lighting fixtures, and native **Create: Power Grid** electricity to the Create ecosystem.

> **Current release: [1.0.0-alpha2](https://github.com/Gunner76th/Create-Realistic-Lighting/releases/tag/v1.0.0-alpha2)** — Public alpha for testing. Native Create: Power Grid electrical integration is implemented; voltage-dependent bulb behavior is planned for Alpha 3.

## Overview

Create: Realistic Lighting is an independent project based on the original Realistic Lighting mod. It uses **Create: Power Grid's native electrical network**, not the original project's Forge Energy (FE/RF) storage system.

The original Realistic Lighting project remains separate and follows its own development roadmap.

## Current Features

### Native Create: Power Grid Electricity

- Fixtures participate in Create: Power Grid's electrical circuits.
- Each fixture provides **four connection terminals**: live-in, neutral-in, live-out, and neutral-out, allowing power to continue downstream.
- Installed bulb type determines the fixture's electrical load.
- The fixture GUI displays electrical readings derived from the Power Grid circuit.
- On / Off / Redstone operating modes control the fixture's load and light output.
- The current implementation uses a **120 V nominal** electrical model, with a **90 V minimum operating threshold**. More detailed voltage effects are planned for Alpha 3.

| Bulb | Nominal power | Nominal resistance at 120 V |
| --- | ---: | ---: |
| Incandescent | 60 W | 240 Ω |
| Fluorescent | 30 W | 480 Ω |
| LED | 15 W | 960 Ω |
| Grow Bulb | 45 W | 320 Ω |

These are the implemented nominal load values, not a claim that all planned undervoltage and overvoltage effects are complete.

### Lighting Fixtures

- **Industrial Cage Fixture** with a dark gray default appearance and support for all 16 vanilla dye colors on its metalwork
- **Square Flush-Mount Ceiling Light**
- **Wall-Mounted Sconce**
- Separate, replaceable bulb items
- Fixture-specific GUI titles and controls

### Light Bulbs

| Bulb | Active-use lifespan | Characteristics |
| --- | --- | --- |
| Incandescent | 30 Minecraft days | Traditional lighting; 60 W nominal |
| Fluorescent | 90 Minecraft days | Efficient lighting; 30 W nominal |
| LED | 300 Minecraft days | Extended, terrain-aware illumination; 15 W nominal |
| Grow Bulb | 90 Minecraft days | Accelerates nearby crop growth; 45 W nominal |

Bulb wear is tracked during operation and preserved when bulbs are removed and reinstalled.

### Fixture Controls

Each fixture GUI supports three modes:

- **On:** Operates when a usable bulb and sufficient electrical power are available.
- **Off:** Forces the fixture off.
- **Redstone:** Operates only while receiving a redstone signal, provided the bulb and power requirements are met.

### Alpha 2 Lighting and Growing Improvements

**LED bulbs**
- Extended illumination over a wider surrounding area
- Terrain-aware light placement and obstruction checks
- Smoother brightness falloff between illuminated areas
- Improved ground-level coverage for upward-facing fixtures

**Grow Bulbs**
- Directional crop-growth acceleration based on fixture orientation
- Centered **5×5** crop coverage beneath overhead fixtures
- Solid-block obstruction checks
- Multiple powered Grow Bulbs contribute with diminishing returns

Alpha 2 was tested in-game, including multiple simultaneously powered LED fixtures, crop-growth comparisons, overlapping Grow Bulbs, and obstruction checks. These tests do not constitute exhaustive multiplayer or long-duration performance validation.

## Photosensitivity and Accessibility

The project includes a first-launch photosensitivity warning. Players can acknowledge it and save a client preference for bulb flickering.

**Important:** The warning and preference persistence are implemented, but the planned voltage-related flicker effect and its integration with that preference are **not yet implemented**.

## Requirements

- Minecraft Java Edition **1.21.1**
- **NeoForge 21.1.264**
- **Java 21**
- **Create 6.0.10**
- **Create: Power Grid 0.6.2**
- **Architectury API 13.0.11**
- **Ponder 1.0.82**

Use compatible dependency versions as required by your modpack and loader.

## Installation

1. Install Minecraft 1.21.1 with the supported NeoForge version.
2. Install Create, Create: Power Grid, Architectury API, and the required Ponder dependency.
3. Download the current alpha JAR from [GitHub Releases](https://github.com/Gunner76th/Create-Realistic-Lighting/releases).
4. Place the JAR in your Minecraft instance's `mods` folder.
5. Start Minecraft and verify the mod and its dependencies load correctly.

**Alpha software:** Back up existing worlds before testing or updating.

## Building

From the project development directory on Windows:

```powershell
.\gradlew.bat clean build --offline
```

The resulting JAR is placed in `build/libs/`. The offline build requires previously cached dependencies; local third-party development dependency JARs may also be needed. The project has been tested using built JARs in Minecraft rather than Gradle's development game launch tasks.

## Configuration

Common and client settings use TOML configuration files. Configuration changes are intended to take effect after restarting Minecraft, not through live reloads.

Client settings include the photosensitivity warning acknowledgment and the saved flicker preference. Some inherited configuration entries may remain during the migration; they should not be interpreted as evidence that the fixtures still use FE/RF power.

## Development Roadmap

### Alpha 1 — Released

- Native Create: Power Grid integration and circuit-based electrical loads
- Three functional fixture families and four replaceable bulb types
- On / Off / Redstone controls and bulb lifespans
- Photosensitivity warning and saved client preferences

### Alpha 2 — Released

- Extended, surface-aware LED illumination and smoother falloff
- Directional Grow Bulb acceleration, overhead 5×5 coverage, obstruction handling, and diminishing returns

See [Alpha 2 release notes](RELEASE_NOTES_ALPHA2.md) on the Alpha 2 development branch or the [published release](https://github.com/Gunner76th/Create-Realistic-Lighting/releases/tag/v1.0.0-alpha2).

### Alpha 3 — Planned

- Incandescent undervoltage dimming
- Low-voltage flickering for fluorescent, LED, and Grow Bulbs
- Voltage-dependent wear, including LED undervoltage wear
- Overvoltage damage and burnout thresholds
- Integration of the client flicker preference with actual effects
- Further dedicated-server and multiplayer regression testing

## Project History

The original Realistic Lighting mod targeted Forge 1.20.1 and also has a separate NeoForge development line. **Create: Realistic Lighting** is an independent Minecraft 1.21.1 NeoForge project with native Create: Power Grid electricity. Changes to this repository do not modify the original mod.

## License

MIT — see [LICENSE](LICENSE).
