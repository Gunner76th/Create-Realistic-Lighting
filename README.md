# Create: Realistic Lighting

**Stable release: 1.0.1** · **Minecraft Java Edition 1.21.1** · **NeoForge**

Create: Realistic Lighting adds replaceable light bulbs, practical industrial fixtures, and voltage-sensitive lighting to the Create ecosystem. Fixtures operate on **Create: Power Grid's native electrical network**—not Forge Energy (FE/RF).

> **Latest release:** [v1.0.1 on GitHub](https://github.com/Gunner76th/Create-Realistic-Lighting/releases/tag/v1.0.1). Downloads are also available through the [CurseForge project page](https://www.curseforge.com/minecraft/mc-mods/create-realistic-lighting).

## Features

### Lighting fixtures

- **Square flush-mount ceiling light**
- **Wall-mounted sconce**
- **Industrial cage fixture**, dark gray by default, with 16 vanilla dye options for the metal cage and mounting plate (the bulb itself is not tinted)
- Replaceable bulbs and fixture-specific controls
- Persistent fixture settings and bulb wear

### Replaceable bulbs

| Bulb | Default nominal power at 120 V | Base active-use lifespan | Lighting behavior |
| --- | ---: | --- | --- |
| Incandescent | 60 W | 30 Minecraft days | Familiar localized light; dims as voltage drops |
| Fluorescent | 30 W | 90 Minecraft days | Efficient light; low-voltage flicker behavior |
| LED | 15 W | 300 Minecraft days | Extended, omnidirectional illumination; low-voltage flicker behavior |
| Grow | 45 W | 90 Minecraft days | Lighting plus nearby crop-growth acceleration; fluorescent-style low-voltage behavior |

The wattages shown above are **defaults**, not fixed values. Since v1.0.1, each bulb type's nominal electrical consumption can be configured independently (1–1,000 W). This changes its electrical load, not its lighting behavior or base lifespan.

Lifespans are baseline values, not guarantees: electrical conditions can accelerate or reduce wear. Wear is retained when a bulb is removed and reinstalled.

### Native Power Grid electricity

- **Create: Power Grid is required.** Fixtures connect to its electrical wiring and consume electricity through its native system.
- Default nominal operating voltage is **120 V**.
- Different bulbs have independently configurable nominal electrical loads (1–1,000 W per bulb type).
- No FE/RF energy buffers or FE/RF power compatibility.
- Lamp operation responds to available power, the selected control mode, and bulb condition.

### Voltage effects and bulb lifetime

Voltage is part of the lighting simulation rather than just an on/off power requirement.

- **Incandescent:** progressively dims at lower voltage, with a hard operating cutoff at 60 V.
- **Fluorescent, LED, and Grow:** can visibly flicker at reduced voltage and stop operating below their respective minimum operating conditions.
- **Overvoltage:** accelerates bulb wear; sufficiently high voltage can burn a bulb out immediately.
- **Undervoltage:** affects bulb behavior and wear according to bulb type.
- **Wear tracking:** accumulated bulb wear is persistent and shown in the fixture interface.

Voltage transition handling includes hysteresis to reduce rapid on/off switching near operating thresholds. Exact behavior varies by bulb and configuration.

### Extended LED lighting

LED bulbs provide extended, **omnidirectional** illumination with a finalized **24-block radius**, smooth falloff, and obstruction-aware placement of supplemental lighting. The system is designed to keep illumination centered on the fixture rather than offset in its facing direction.

### Fixture controls and lifespan display

Each fixture supports three modes:

- **On:** operate when a usable bulb and sufficient electrical power are available.
- **Off:** remain off regardless of power or redstone.
- **Redstone:** operate only while receiving a redstone signal, provided the bulb and electrical conditions allow it.

The fixture GUI includes electrical readouts and a bulb lifespan bar:

- **Green:** more than 50% life remaining
- **Yellow:** more than 20% through 50%
- **Red:** above 0% through 20%
- **Empty:** exhausted bulb; an empty slot indicates no bulb installed

Bulb inventory tooltips display remaining lifespan information.

## Requirements

| Component | Version |
| --- | --- |
| Minecraft Java Edition | 1.21.1 |
| NeoForge | 21.1.x (built against 21.1.264) |
| Create | 6.0.10 or newer compatible 1.21.1 build |
| Create: Power Grid | 0.6.2 or newer compatible 1.21.1 build |
| Java | 21 |

**Create and Create: Power Grid are required dependencies.** Architectury API is not listed as a direct dependency of Create: Realistic Lighting in its v1.0.0 mod metadata; install any additional dependencies required by your selected Create or Power Grid builds.

## Installation

1. Set up a **Minecraft Java Edition 1.21.1** instance with a compatible **NeoForge 21.1.x** installation.
2. Install compatible versions of **Create** and **Create: Power Grid**, along with any dependencies those mods require.
3. Place `create_realistic_lighting-1.0.1.jar` in the instance's `mods` folder.
4. Start Minecraft and confirm the mod loads successfully.

For multiplayer, install the required mods on the server and connecting clients. Back up existing worlds before adding or upgrading mods.

## Configuration and accessibility

The mod uses TOML configuration files for gameplay and client-side options. Configuration edits are intended to take effect **after restarting Minecraft**; live reloading is not supported.

- Nominal voltage and bulb electrical consumption are configured through the mod's electrical TOML settings.
- In v1.0.1, each bulb's nominal wattage can be set independently between **1.0 and 1000.0 W**. Default values remain unchanged from v1.0.0.
- Client-side flicker effects can be disabled through the client configuration.
- Flicker is a **visual effect**; it does not repeatedly change world light levels to simulate flashing.
- The earlier first-launch photosensitivity warning screen was removed and is **not** part of the current release.

### Configurable bulb wattage (v1.0.1)

Edit the mod's TOML configuration file while Minecraft or the dedicated server is stopped:

```toml
[electrical.bulbWattage]
    incandescentWatts = 60.0
    fluorescentWatts = 30.0
    ledWatts = 15.0
    growWatts = 45.0
```

Each value accepts **1.0–1000.0 watts**. The default nominal voltage remains **120 V** (`nominalVoltage` in the `[electrical]` section). Restart Minecraft or the dedicated server to apply changes; edits are not reloaded live.

The configured wattage determines the electrical load presented to **Create: Power Grid**. Actual measured consumption depends on supplied voltage. Changing wattage does **not** directly change bulb brightness, illumination radius, flicker thresholds, or base lifespan.

> **Accessibility note:** Lighting effects may include visible flicker when bulbs are operated outside their normal voltage range. Players sensitive to flashing effects should review the client flicker setting before play.

## Building from source

The project uses the included Gradle wrapper and targets Java 21. On Windows, from the repository root:

```powershell
.\gradlew.bat clean build
```

Build output is placed in `build/libs/`. Development builds may require locally supplied third-party mod dependencies; those JARs are not distributed in this repository.

The release was validated using compiled JARs in an actual Minecraft client. The project also uses runtime testing with ATLauncher rather than relying solely on Gradle's development game launcher.

## Release validation

Version 1.0.1 was built and tested in Minecraft with all four bulbs at their original default wattages and at custom values (100 W incandescent, 40 W fluorescent, 25 W LED, and 60 W Grow). Create: Power Grid electrical readings matched the configured loads at approximately 120 V. Normal lamp operation was observed.

Version 1.0.0 underwent local testing of fixture operation, all four bulb types, Power Grid connections, On/Off/Redstone modes, voltage-dependent behavior, burnout and wear persistence, world reloads, and multi-fixture lighting performance. This testing does not guarantee compatibility with every modpack or server environment.

Please report reproducible issues through the [GitHub issue tracker](https://github.com/Gunner76th/Create-Realistic-Lighting/issues), including the Minecraft/NeoForge versions, mod versions, and relevant logs.

## Project history

Create: Realistic Lighting is an independent project derived from the original Realistic Lighting concept. The original project used FE-based electrical systems; this Minecraft 1.21.1 NeoForge project uses **Create: Power Grid exclusively** and maintains its own release history and roadmap.

## License

MIT — see [LICENSE](LICENSE).
