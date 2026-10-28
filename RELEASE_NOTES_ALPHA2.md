# Create: Realistic Lighting — Alpha 2

**Version:** 1.0.0-alpha2  
**Minecraft:** Java Edition 1.21.1  
**Mod Loader:** NeoForge

## What's New

### Improved LED Lighting
- Extended LED illumination across a larger surrounding area.
- Added terrain-aware light placement for more natural surface illumination.
- Improved handling of solid-block obstructions.
- Smoothed brightness falloff to create more realistic transitions between illuminated areas.
- Improved ground-level illumination for upward-facing fixtures.

### Improved Grow Bulbs
- Added directional crop-growth acceleration based on fixture orientation.
- Improved overhead Grow Bulb coverage with a centered 5×5 growing area.
- Supported multiple Grow Bulbs working together with diminishing returns.
- Added obstruction checks so solid blocks prevent growth acceleration through barriers.
- Retained the existing electrical power requirements.

## Testing

- Verified successful clean Gradle build.
- Tested extended LED illumination with four powered fixtures operating simultaneously.
- Tested Grow Bulb crop-growth acceleration against a control plot.
- Tested overlapping Grow Bulbs and solid-block obstruction behavior.

## Compatibility

- Minecraft 1.21.1
- NeoForge
- Create 6.0.10
- Create: Power Grid 0.6.2

**Note:** This is an alpha release intended for testing. Back up existing worlds before updating.