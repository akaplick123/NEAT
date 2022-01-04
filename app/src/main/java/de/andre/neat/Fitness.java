package de.andre.neat;

import lombok.Value;

@Value(staticConstructor = "of")
public class Fitness implements Comparable<Fitness> {

  public static Fitness NOT_AVAILABLE = Fitness.of(Float.MIN_VALUE);

  float value;

  @Override
  public int compareTo(Fitness o) {
    return Float.compare(this.value, o.value);
  }
}
