package de.andre.neat;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.andre.neat.NodeGene.Type;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GenomeTest {

  @Test
  void testAddConnectionMutation() {
    // given: a genome
    NodeGene node1 = NodeGene.builder().id(NodeId.of(1)).type(Type.INPUT).build();
    NodeGene node2 = NodeGene.builder().id(NodeId.of(2)).type(Type.INPUT).build();
    NodeGene node3 = NodeGene.builder().id(NodeId.of(3)).type(Type.INPUT).build();
    NodeGene node4 = NodeGene.builder().id(NodeId.of(4)).type(Type.OUTPUT).build();
    NodeGene node5 = NodeGene.builder().id(NodeId.of(5)).type(Type.HIDDEN).build();

    ConnectionGene con1 = ConnectionGene.builder().inNode(node1).outNode(node4)
        .weight(ConnectionWeight.of(0.7f)).expressed(ExpressedState.EXPRESSED)
        .innovation(InnovationNumber.of(1))
        .build();
    ConnectionGene con2 = ConnectionGene.builder().inNode(node2).outNode(node4)
        .weight(ConnectionWeight.of(-0.5f)).expressed(ExpressedState.NOT_EXPRESSED)
        .innovation(InnovationNumber.of(2))
        .build();
    ConnectionGene con3 = ConnectionGene.builder().inNode(node3).outNode(node4)
        .weight(ConnectionWeight.of(0.5f)).expressed(ExpressedState.EXPRESSED)
        .innovation(InnovationNumber.of(3))
        .build();
    ConnectionGene con4 = ConnectionGene.builder().inNode(node2).outNode(node5)
        .weight(ConnectionWeight.of(0.2f)).expressed(ExpressedState.EXPRESSED)
        .innovation(InnovationNumber.of(4))
        .build();
    ConnectionGene con5 = ConnectionGene.builder().inNode(node5).outNode(node4)
        .weight(ConnectionWeight.of(0.4f)).expressed(ExpressedState.EXPRESSED)
        .innovation(InnovationNumber.of(5))
        .build();
    ConnectionGene con6 = ConnectionGene.builder().inNode(node1).outNode(node5)
        .weight(ConnectionWeight.of(0.6f)).expressed(ExpressedState.EXPRESSED)
        .innovation(InnovationNumber.of(6))
        .build();

    Genome genome = Genome.init(
        List.of(node1, node2, node3, node4, node5),
        List.of(con1, con2, con3, con4, con5, con6)
    );
    assertThat(genome.getConnections()).hasSize(6);

    Random r = mock(Random.class);
    when(r.nextInt(anyInt())).thenReturn(2, 4);
    when(r.nextFloat()).thenReturn(0.8f);

    // when: add connection is called
    genome.addConnectionMutation(r);

    // then: a new connection should have been created
    assertThat(genome.getConnections()).hasSize(7);
    assertThat(genome.getConnections().get(6).getInNode()).isSameAs(node3);
    assertThat(genome.getConnections().get(6).getOutNode()).isSameAs(node5);
  }

  @Test
  void testAddNodeMutation() {
    // given: a genome
    NodeGene node1 = NodeGene.builder().id(NodeId.of(1)).type(Type.INPUT).build();
    NodeGene node2 = NodeGene.builder().id(NodeId.of(2)).type(Type.INPUT).build();
    NodeGene node3 = NodeGene.builder().id(NodeId.of(3)).type(Type.INPUT).build();
    NodeGene node4 = NodeGene.builder().id(NodeId.of(4)).type(Type.OUTPUT).build();
    NodeGene node5 = NodeGene.builder().id(NodeId.of(5)).type(Type.HIDDEN).build();

    ConnectionGene con1 = ConnectionGene.builder().inNode(node1).outNode(node4)
        .weight(ConnectionWeight.of(0.7f)).expressed(ExpressedState.EXPRESSED)
        .innovation(InnovationNumber.of(1))
        .build();
    ConnectionGene con2 = ConnectionGene.builder().inNode(node2).outNode(node4)
        .weight(ConnectionWeight.of(-0.5f)).expressed(ExpressedState.NOT_EXPRESSED)
        .innovation(InnovationNumber.of(2))
        .build();
    ConnectionGene con3 = ConnectionGene.builder().inNode(node3).outNode(node4)
        .weight(ConnectionWeight.of(0.5f)).expressed(ExpressedState.EXPRESSED)
        .innovation(InnovationNumber.of(3))
        .build();
    ConnectionGene con4 = ConnectionGene.builder().inNode(node2).outNode(node5)
        .weight(ConnectionWeight.of(0.2f)).expressed(ExpressedState.EXPRESSED)
        .innovation(InnovationNumber.of(4))
        .build();
    ConnectionGene con5 = ConnectionGene.builder().inNode(node5).outNode(node4)
        .weight(ConnectionWeight.of(0.4f)).expressed(ExpressedState.EXPRESSED)
        .innovation(InnovationNumber.of(5))
        .build();
    ConnectionGene con6 = ConnectionGene.builder().inNode(node1).outNode(node5)
        .weight(ConnectionWeight.of(0.6f)).expressed(ExpressedState.EXPRESSED)
        .innovation(InnovationNumber.of(6))
        .build();

    Genome genome = Genome.init(
        List.of(node1, node2, node3, node4, node5),
        List.of(con1, con2, con3, con4, con5, con6)
    );
    assertThat(genome.getNodes()).hasSize(5);
    assertThat(genome.getConnections()).hasSize(6);

    Random r = mock(Random.class);
    when(r.nextInt(anyInt())).thenReturn(2);

    // when: add connection is called
    genome.addNodeMutation(r);

    // then: a new node should have been created
    assertThat(genome.getNodes()).hasSize(6);
    assertThat(genome.getNodes().get(5).getType()).isEqualTo(Type.HIDDEN);
    NodeGene node6 = genome.getNodes().get(5);

    // then: two new connection should have been created
    assertThat(genome.getConnections()).hasSize(8);
    assertThat(genome.getConnections().get(6).getInNode()).isSameAs(node3);
    assertThat(genome.getConnections().get(6).getOutNode()).isSameAs(node6);
    assertThat(genome.getConnections().get(6).getWeight()).isEqualTo(ConnectionWeight.of(1f));
    assertThat(genome.getConnections().get(7).getInNode()).isSameAs(node6);
    assertThat(genome.getConnections().get(7).getOutNode()).isSameAs(node4);
    assertThat(genome.getConnections().get(7).getWeight()).isEqualTo(ConnectionWeight.of(0.5f));
    assertThat(genome.getConnections().get(2).getExpressed()).isEqualTo(
        ExpressedState.NOT_EXPRESSED);
  }

  @Test
  void testOffspring() {
    // given: two genomes
    NodeGene node1 = NodeGene.builder().id(NodeId.of(1)).type(Type.INPUT).build();
    NodeGene node2 = NodeGene.builder().id(NodeId.of(2)).type(Type.INPUT).build();
    NodeGene node3 = NodeGene.builder().id(NodeId.of(3)).type(Type.INPUT).build();
    NodeGene node4 = NodeGene.builder().id(NodeId.of(4)).type(Type.OUTPUT).build();
    NodeGene node5 = NodeGene.builder().id(NodeId.of(5)).type(Type.HIDDEN).build();
    NodeGene node6 = NodeGene.builder().id(NodeId.of(6)).type(Type.HIDDEN).build();

    ConnectionGene con11 = ConnectionGene.builder().inNode(node1).outNode(node4)
        .weight(ConnectionWeight.of(1f)).expressed(ExpressedState.EXPRESSED)
        .innovation(InnovationNumber.of(1))
        .build();
    ConnectionGene con12 = ConnectionGene.builder().inNode(node2).outNode(node4)
        .weight(ConnectionWeight.of(-1f)).expressed(ExpressedState.NOT_EXPRESSED)
        .innovation(InnovationNumber.of(2))
        .build();
    ConnectionGene con13 = ConnectionGene.builder().inNode(node3).outNode(node4)
        .weight(ConnectionWeight.of(1f)).expressed(ExpressedState.EXPRESSED)
        .innovation(InnovationNumber.of(3))
        .build();
    ConnectionGene con14 = ConnectionGene.builder().inNode(node2).outNode(node5)
        .weight(ConnectionWeight.of(1f)).expressed(ExpressedState.EXPRESSED)
        .innovation(InnovationNumber.of(4))
        .build();
    ConnectionGene con15 = ConnectionGene.builder().inNode(node5).outNode(node4)
        .weight(ConnectionWeight.of(1f)).expressed(ExpressedState.EXPRESSED)
        .innovation(InnovationNumber.of(5))
        .build();
    ConnectionGene con18 = ConnectionGene.builder().inNode(node1).outNode(node5)
        .weight(ConnectionWeight.of(1f)).expressed(ExpressedState.EXPRESSED)
        .innovation(InnovationNumber.of(8))
        .build();

    Genome genome1 = Genome.init(
        List.of(node1, node2, node3, node4, node5),
        List.of(con11, con12, con13, con14, con15, con18)
    );

    ConnectionGene con21 = ConnectionGene.builder().inNode(node1).outNode(node4)
        .weight(ConnectionWeight.of(3f)).expressed(ExpressedState.EXPRESSED)
        .innovation(InnovationNumber.of(1))
        .build();
    ConnectionGene con22 = ConnectionGene.builder().inNode(node2).outNode(node4)
        .weight(ConnectionWeight.of(3f)).expressed(ExpressedState.NOT_EXPRESSED)
        .innovation(InnovationNumber.of(2))
        .build();
    ConnectionGene con23 = ConnectionGene.builder().inNode(node3).outNode(node4)
        .weight(ConnectionWeight.of(3f)).expressed(ExpressedState.EXPRESSED)
        .innovation(InnovationNumber.of(3))
        .build();
    ConnectionGene con24 = ConnectionGene.builder().inNode(node2).outNode(node5)
        .weight(ConnectionWeight.of(3f)).expressed(ExpressedState.EXPRESSED)
        .innovation(InnovationNumber.of(4))
        .build();
    ConnectionGene con25 = ConnectionGene.builder().inNode(node5).outNode(node4)
        .weight(ConnectionWeight.of(3f)).expressed(ExpressedState.NOT_EXPRESSED)
        .innovation(InnovationNumber.of(5))
        .build();
    ConnectionGene con26 = ConnectionGene.builder().inNode(node1).outNode(node5)
        .weight(ConnectionWeight.of(3f)).expressed(ExpressedState.EXPRESSED)
        .innovation(InnovationNumber.of(6))
        .build();
    ConnectionGene con27 = ConnectionGene.builder().inNode(node1).outNode(node5)
        .weight(ConnectionWeight.of(3f)).expressed(ExpressedState.EXPRESSED)
        .innovation(InnovationNumber.of(7))
        .build();
    ConnectionGene con29 = ConnectionGene.builder().inNode(node1).outNode(node5)
        .weight(ConnectionWeight.of(3f)).expressed(ExpressedState.EXPRESSED)
        .innovation(InnovationNumber.of(9))
        .build();
    ConnectionGene con2A = ConnectionGene.builder().inNode(node1).outNode(node5)
        .weight(ConnectionWeight.of(3f)).expressed(ExpressedState.EXPRESSED)
        .innovation(InnovationNumber.of(10))
        .build();

    Genome genome2 = Genome.init(
        List.of(node1, node2, node3, node4, node5, node6),
        List.of(con21, con22, con23, con24, con25, con26, con27, con29, con2A)
    );

    Random r = mock(Random.class);
    when(r.nextBoolean()).thenReturn(true, true, false, true, false);

    // when: offspring is called
    Genome child = Genome.crossover(genome1, genome2, true, r);

    // then: should have 6 nodes
    assertThat(child.getNodes())
        .usingRecursiveFieldByFieldElementComparator()
        .containsExactly(node1, node2, node3, node4, node5, node6);

    // then: should have 10 connections
    assertThat(child.getConnections())
        .usingRecursiveFieldByFieldElementComparator()
        .containsExactlyInAnyOrder(con11, con12, con23, con14, con25, con26, con27, con18, con29, con2A);
  }
}