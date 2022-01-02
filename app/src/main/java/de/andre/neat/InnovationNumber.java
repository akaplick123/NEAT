package de.andre.neat;

import lombok.Value;

@Value(staticConstructor = "of")
public class InnovationNumber {

  private static int count = 0;

  int value;

  public static InnovationNumber next() {
    return InnovationNumber.of(++count);
  }
}
