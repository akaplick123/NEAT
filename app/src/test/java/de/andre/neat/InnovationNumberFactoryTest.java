package de.andre.neat;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class InnovationNumberFactoryTest {

  @Test
  void shouldCreateNewInnovationNumber() {
    InnovationNumberFactory factory = new InnovationNumberFactory();
    NodeGene node1 = NodeGene.builder()
        .id(NodeId.of(1))
        .build();
    NodeGene node2 = NodeGene.builder()
        .id(NodeId.of(2))
        .build();
    NodeGene node3 = NodeGene.builder()
        .id(NodeId.of(3))
        .build();

    // when: called the first time
    InnovationNumber inno1 = factory.create(node1, node2);
    assertThat(inno1).isNotNull();

    // when: called with different parameters
    InnovationNumber inno2 = factory.create(node1, node3);
    assertThat(inno2).isNotNull();
    assertThat(inno2).usingRecursiveComparison().isNotEqualTo(inno1);

    // when: called the second time with the same input
    InnovationNumber inno3 = factory.create(node1, node2);
    assertThat(inno3).isNotNull();
    assertThat(inno3).usingRecursiveComparison().isEqualTo(inno1);
  }
}