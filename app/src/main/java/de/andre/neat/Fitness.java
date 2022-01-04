package de.andre.neat;

import lombok.Value;

@Value(staticConstructor = "of")
public class Fitness implements Comparable<Fitness> {

  float value;

  public static Fitness zero() {
    return Fitness.of(0f);
  }

  @Override
  public int compareTo(Fitness o) {
    return Float.compare(this.value, o.value);
  }
}
