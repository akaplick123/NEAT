package de.andre.neat;

import java.util.Comparator;
import lombok.Value;

@Value(staticConstructor = "of")
public class NodeId {

  public static final Comparator<NodeId> COMPARATOR = (a, b) -> Integer.compare(a.value, b.value);

  private static int count = 0;

  int value;

  public static NodeId next() {
    return NodeId.of(++count);
  }
}
