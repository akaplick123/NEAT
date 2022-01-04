package de.andre.neat;

import de.andre.neat.ConnectionGene.ConnectionGeneBuilder;
import java.util.concurrent.atomic.AtomicInteger;

public interface ConnectionGeneTestData {
  AtomicInteger counter = new AtomicInteger(1);

  static void resetInnovationNumber() {
    counter.setPlain(1);
  }

  static ConnectionGeneBuilder connection(NodeGene inNode, NodeGene outNode) {
    return ConnectionGene.builder()
        .inNode(inNode)
        .outNode(outNode)
        .weight(ConnectionWeight.of(1))
        .expressed(ExpressedState.EXPRESSED)
        .innovation(InnovationNumber.of(counter.getAndIncrement()));
  }
}
