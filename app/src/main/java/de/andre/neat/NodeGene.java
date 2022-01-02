package de.andre.neat;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class NodeGene {

  private final NodeId id;
  private final Type type;

  @Builder
  public NodeGene(Type type, NodeId id) {
    this.type = type;
    this.id = id;
  }

  public enum Type {
    /**
     * also called SENSOR
     */
    INPUT,
    HIDDEN,
    OUTPUT
  }
}
