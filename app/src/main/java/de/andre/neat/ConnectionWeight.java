package de.andre.neat;

import java.util.Random;
import lombok.Value;

@Value(staticConstructor = "of")
public class ConnectionWeight {
  float weight;

  public static ConnectionWeight random(Random r) {
    return ConnectionWeight.of(r.nextFloat() * 2f - 1f);
  }

  public ConnectionWeight pertube(Random r) {
    return ConnectionWeight.of(this.weight + r.nextFloat() - 0.5f);
  }

  public static ConnectionWeight one() {
    return ConnectionWeight.of(1f);
  }
}
