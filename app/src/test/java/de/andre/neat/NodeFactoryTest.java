package de.andre.neat;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class NodeFactoryTest {

  @Test
  void shouldCreateNodeIds() {
    NodeFactory factory = new NodeFactory();

    InnovationNumber inno1 = InnovationNumber.of(1);
    InnovationNumber inno2 = InnovationNumber.of(2);

    // when: calling the first time
    NodeGene node1 = factory.create(inno1);
    assertThat(node1).isNotNull();

    // when: calling with a different innovation number
    NodeGene node2 = factory.create(inno2);
    assertThat(node2).isNotNull();
    assertThat(node2).usingRecursiveComparison().isNotEqualTo(node1);

    // when: calling with a same innovation number
    NodeGene node3 = factory.create(inno1);
    assertThat(node3).isNotNull();
    assertThat(node3).usingRecursiveComparison().isEqualTo(node1);
  }
}