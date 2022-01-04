package de.andre.neat;

import de.andre.neat.NodeGene.Type;
import java.util.concurrent.atomic.AtomicInteger;

public interface NodeGeneTestData {

  AtomicInteger counter = new AtomicInteger(1);

  static void resetNodeCounter() {
    counter.setPlain(1);
  }

  static NodeGene input() {
    return NodeGene.builder()
        .type(Type.INPUT)
        .id(NodeId.of(counter.getAndIncrement()))
        .build();
  }

  static NodeGene output() {
    return NodeGene.builder()
        .type(Type.OUTPUT)
        .id(NodeId.of(counter.getAndIncrement()))
        .build();
  }

  static NodeGene hidden() {
    return NodeGene.builder()
        .type(Type.HIDDEN)
        .id(NodeId.of(counter.getAndIncrement()))
        .build();
  }
}
