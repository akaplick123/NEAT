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
  private final ConnectionWeight weight;
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

  public ConnectionGene pertubeWeight(Random r) {
    ConnectionWeight newWeight = this.weight.pertube(r);
    return new ConnectionGene(inNode, outNode, newWeight, expressed, innovation);
  }

  public ConnectionGene assignNewWeight(Random r) {
    ConnectionWeight newWeight = ConnectionWeight.random(r);
    return new ConnectionGene(inNode, outNode, newWeight, expressed, innovation);
  }

  public ConnectionGene createCopy() {
    return new ConnectionGene(inNode, outNode, weight, expressed, innovation);
  }
}
