package com.gunner76th.createrealisticlighting.client.render;

/**
 * Purely client-side, deterministic bulb brightness modulation.
 * Does not change block states, electrical loads, or emitted world light.
 */
public final class BulbFlickerPattern {
  private BulbFlickerPattern() {}

  /** Returns a brightness factor from 0 (dark) to 1 (fully illuminated). */
  public static float brightness(double voltage, double gameTimeTicks, long positionSeed) {
    if (!Double.isFinite(voltage) || voltage < 100.0) return 0.0f;
    if (voltage >= 115.0) return 1.0f;

    // Use a stable per-fixture phase to prevent nearby bulbs from flickering in lockstep.
    double phase = (positionSeed & 0xFFFFL) * 0.0000958738;
    double fast = Math.sin(gameTimeTicks * 2.17 + phase * 17.0);
    double slow = Math.sin(gameTimeTicks * 0.53 + phase * 3.0);
    double pulse = (fast * 0.65 + slow * 0.35 + 1.0) * 0.5;

    if (voltage >= 111.0) {
      return (float) (0.85 + 0.15 * pulse); // mild: 85–100%
    }
    if (voltage >= 106.0) {
      return (float) (0.50 + 0.50 * pulse); // moderate: 50–100%
    }
    // Severe: irregular brief dark intervals, without changing the world light.
    double dropout = Math.sin(gameTimeTicks * 1.31 + phase * 11.0)
        * Math.sin(gameTimeTicks * 0.73 + phase * 7.0);
    if (dropout > 0.30) return 0.0f;
    return (float) (0.15 + 0.85 * pulse);
  }
}
