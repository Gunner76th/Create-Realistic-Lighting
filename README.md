
# Create: Realistic Lighting

A Minecraft Java Edition 1.21.1 NeoForge mod bringing replaceable light bulbs, industrial lighting fixtures, and realistic electrical behavior to the Create ecosystem.

> **Development status:** Work in progress. The native Create: Power Grid electrical integration is currently being researched and has not yet replaced the existing FE-based lighting system.

## Overview

Create: Realistic Lighting is an independent successor project based on Realistic Lighting. Its goal is to integrate realistic lighting fixtures and electrical consumption into the Create ecosystem using Create: Power Grid's native electrical network.

This repository is separate from the original Realistic Lighting project and follows its own development roadmap.

## Current Features

### Lighting Fixtures

- Industrial Cage Fixture with a dark gray default appearance
- Square Flush-Mount Ceiling Light
- Wall-Mounted Sconce
- Sixteen vanilla dye colors for fixture metalwork
- Separate, replaceable bulbs
- Fixture-specific GUI titles and controls

### Light Bulbs

| Bulb | Active-use lifespan | Characteristics |
| --- | --- | --- |
| Incandescent | 30 Minecraft days | Traditional lighting |
| Fluorescent | 90 Minecraft days | More efficient lighting |
| LED | 300 Minecraft days | Extended directional lighting |
| Grow Bulb | 90 Minecraft days | Accelerates nearby crop growth |

Bulb wear is tracked during operation and preserved when bulbs are removed and reinstalled.

### Fixture Controls

The fixture GUI supports three operating modes:

- **On:** Operates when a usable bulb and sufficient power are available.
- **Off:** Forces the fixture off.
- **Redstone:** Operates only while receiving a redstone signal, provided the bulb and power requirements are met.

### Additional Lighting Features

- Extended directional lighting from LED bulbs
- Crop growth acceleration from Grow Bulbs
- Persistent bulb age and fixture settings
- Configurable electrical consumption and energy storage in the current legacy implementation

## Photosensitivity and Accessibility

The project includes a first-launch photosensitivity warning.

Players can acknowledge the warning and choose whether to enable or disable bulb flickering. Their selection is saved in the client configuration file.

The warning screen and preference persistence have been tested in the Minecraft client.

**Important:** The flickering effect and its connection to the saved preference are not yet implemented. Disabling flickering currently saves the preference for future use.

## Planned Electrical Integration

The project is migrating from its inherited Forge Energy (FE) system to native Create: Power Grid electricity.

The planned electrical model includes:

- Native Power Grid wire connections
- Two electrical terminals per fixture
- Voltage-dependent lighting behavior
- Electrical resistance and power consumption based on bulb type
- Low-voltage dimming or flickering, depending on bulb technology
- Overvoltage damage and accelerated bulb wear
- Electrical switching controlled by the fixture GUI and redstone

The intended nominal operating voltage is 120 V.

| Bulb | Nominal power | Planned resistance |
| --- | ---: | ---: |
| Incandescent | 60 W | 240 ohms |
| Fluorescent | 30 W | 480 ohms |
| LED | 15 W | 960 ohms |
| Grow Bulb | 45 W | 320 ohms |

These electrical values describe the planned integration, not functionality currently available in the compiled mod.

The eventual goal is to remove the inherited FE/RF implementation and use Power Grid electricity exclusively.

## Requirements

Development currently targets:

- Minecraft Java Edition 1.21.1
- NeoForge 21.1.264
- Java 21
- Create 6.0.10
- Create: Power Grid 0.6.2
- Architectury API 13.0.11

Dependency versions may change during development.

## Building

On Windows, from the development directory:

```powershell
.\gradlew.bat build --offline
```

The compiled mod JAR is generated in `build/libs/`.

This project is currently tested using compiled JARs installed in an actual Minecraft client or dedicated server environment, rather than Gradle's development game launch tasks.

Local third-party dependency JARs may be required in the development environment. They are not included in this repository.

## Installation

This project is under active development and is not yet intended as a stable public release.

For development testing:

1. Install Minecraft 1.21.1 with the supported NeoForge version.
2. Install Create, Create: Power Grid, and Architectury API.
3. Build Create: Realistic Lighting.
4. Place the compiled mod JAR in the Minecraft instance's `mods` folder.
5. Start Minecraft and verify the installed mod and its dependencies.

Always back up existing worlds before testing development builds.

## Configuration

The current implementation uses TOML configuration files for common and client settings.

Client settings include:

- First-launch photosensitivity warning acknowledgment
- Bulb flickering preference

Common settings currently include inherited FE consumption and fixture energy storage values. These will be revised as the native Power Grid integration progresses.

Configuration changes are intended to take effect after restarting Minecraft, rather than being applied live.

The internal mod ID and configuration filenames have not yet been migrated to the final Create: Realistic Lighting naming scheme.

## Development Status

### Implemented and Tested

- Existing replaceable-bulb fixture system
- Four bulb types with finite operating lifespans
- On / Off / Redstone fixture controls
- Dyeable industrial-style fixtures
- Extended LED lighting
- Grow Bulb crop acceleration
- First-launch photosensitivity warning
- Persistent client photosensitivity and flickering preferences

### In Progress

- Native Create: Power Grid electrical architecture
- Electrical terminal placement and wire compatibility
- Migration of fixture block entities to Power Grid's circuit system

### Planned

- Complete removal of legacy FE/RF power handling
- Voltage-sensitive bulb operation
- Bulb-specific dimming and flickering
- Overvoltage damage and electrical failure behavior
- Integration of the client flickering preference with actual lighting effects
- Final mod ID and configuration filename migration
- Dedicated multiplayer and regression testing of the completed electrical system

## Project History

Create: Realistic Lighting is being developed as a separate project based on the original Realistic Lighting mod.

The original project targeted Minecraft 1.20.1 and Forge, with FE-based electrical systems and optional technology-mod integrations.

This project targets Minecraft 1.21.1 and NeoForge, with a new electrical architecture built around Create: Power Grid.

## License

MIT — see [LICENSE](LICENSE).
