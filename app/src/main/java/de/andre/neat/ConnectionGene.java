package de.andre.neat;

import java.util.Random;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ConnectionGene {

  private final NodeGene inNode;
  private final NodeGene outNode;
  private final InnovationNumber innovation;
  private ConnectionWeight weight;
  private ExpressedState expressed;

  @Builder
  public ConnectionGene(NodeGene inNode, NodeGene outNode, ConnectionWeight weight,
      ExpressedState expressed, InnovationNumber innovation) {
    this.inNode = inNode;
    this.outNode = outNode;
    this.weight = weight;
    this.expressed = expressed;
    this.innovation = innovation;
  }

  public void disable() {
    this.expressed = ExpressedState.NOT_EXPRESSED;
  }

  public void pertubeWeight(Random r) {
    this.weight = this.weight.pertube(r);
  }

  public void assignNewWeight(Random r) {
    this.weight = ConnectionWeight.random(r);
  }

  public ConnectionGene createCopy() {
    return new ConnectionGene(inNode, outNode, weight, expressed, innovation);
  }
}
