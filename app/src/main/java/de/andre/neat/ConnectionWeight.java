package de.andre.neat;

import static de.andre.neat.Parameter.INIT_MAX_WEIGHT;
import static de.andre.neat.Parameter.MAX_PERTUBE_WEIGHT_DELTA;

import java.util.Random;
import lombok.Value;

@Value(staticConstructor = "of")
public class ConnectionWeight {

  float weight;

  public static ConnectionWeight random(Random r) {
    return ConnectionWeight.of(nextFloat(r) * INIT_MAX_WEIGHT);
  }

  /**
   * @return the next pseudorandom, uniformly distributed float value between -1.0 and +1.0 from
   * this random number generator's sequence
   */
  private static float nextFloat(Random r) {
    return (r.nextFloat() - 0.5f) * 2f;
  }

  public static ConnectionWeight one() {
    return ConnectionWeight.of(1f);
  }

  public ConnectionWeight pertube(Random r) {
    return ConnectionWeight.of(this.weight + nextFloat(r) * MAX_PERTUBE_WEIGHT_DELTA);
  }
}
