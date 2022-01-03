package de.andre.neat;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.in;

import de.andre.neat.NodeGene.Type;
import java.util.List;
import java.util.function.UnaryOperator;
import org.assertj.core.data.Percentage;
import org.junit.jupiter.api.Test;

class NeuralNetworkTest {

  /**
   * a very simple activation function
   */
  private static final UnaryOperator<Float> ACTIVATION = f -> f;

  @Test
  void shouldComputeNetwork1() {
    // given: a genome
    NodeGene inputNode = NodeGene.builder()
        .type(Type.INPUT)
        .id(NodeId.of(1))
        .build();
    NodeGene outputNode = NodeGene.builder()
        .type(Type.OUTPUT)
        .id(NodeId.of(2))
        .build();
    ConnectionGene con1 = ConnectionGene.builder()
        .inNode(inputNode)
        .outNode(outputNode)
        .weight(ConnectionWeight.of(1))
        .expressed(ExpressedState.EXPRESSED)
        .innovation(InnovationNumber.next())
        .build();
    Genome genome = Genome.init(List.of(inputNode, outputNode),
        List.of(con1));

    // when: computing the network
    NeuralNetwork network = NeuralNetwork.createFromGenome(genome);
    network.putValue(inputNode, 3f);
    network.compute(ACTIVATION);

    float result = network.getValue(outputNode);
    assertThat(result).isCloseTo(3f, Percentage.withPercentage(0.1d));
  }

  @Test
  void shouldComputeNetwork2() {
    // given: a genome
    NodeGene inputNode1 = NodeGene.builder()
        .type(Type.INPUT)
        .id(NodeId.of(1))
        .build();
    NodeGene inputNode2 = NodeGene.builder()
        .type(Type.INPUT)
        .id(NodeId.of(2))
        .build();
    NodeGene outputNode = NodeGene.builder()
        .type(Type.OUTPUT)
        .id(NodeId.of(3))
        .build();
    ConnectionGene con1 = ConnectionGene.builder()
        .inNode(inputNode1)
        .outNode(outputNode)
        .weight(ConnectionWeight.of(1))
        .expressed(ExpressedState.EXPRESSED)
        .innovation(InnovationNumber.next())
        .build();
    ConnectionGene con2 = ConnectionGene.builder()
        .inNode(inputNode2)
        .outNode(outputNode)
        .weight(ConnectionWeight.of(1))
        .expressed(ExpressedState.EXPRESSED)
        .innovation(InnovationNumber.next())
        .build();
    Genome genome = Genome.init(List.of(inputNode1, inputNode2, outputNode),
        List.of(con1, con2));

    // when: computing the network
    NeuralNetwork network = NeuralNetwork.createFromGenome(genome);
    network.putValue(inputNode1, 3f);
    network.putValue(inputNode2, 5f);
    network.compute(ACTIVATION);

    float result = network.getValue(outputNode);
    assertThat(result).isCloseTo(8f, Percentage.withPercentage(0.1d));
  }

  @Test
  void shouldComputeNetwork3() {
    // given: a genome
    NodeGene inputNode1 = NodeGene.builder()
        .type(Type.INPUT)
        .id(NodeId.of(1))
        .build();
    NodeGene inputNode2 = NodeGene.builder()
        .type(Type.INPUT)
        .id(NodeId.of(2))
        .build();
    NodeGene outputNode = NodeGene.builder()
        .type(Type.OUTPUT)
        .id(NodeId.of(3))
        .build();
    ConnectionGene con1 = ConnectionGene.builder()
        .inNode(inputNode1)
        .outNode(outputNode)
        .weight(ConnectionWeight.of(1))
        .expressed(ExpressedState.EXPRESSED)
        .innovation(InnovationNumber.next())
        .build();
    ConnectionGene con2 = ConnectionGene.builder()
        .inNode(inputNode2)
        .outNode(outputNode)
        .weight(ConnectionWeight.of(1))
        .expressed(ExpressedState.NOT_EXPRESSED)
        .innovation(InnovationNumber.next())
        .build();
    Genome genome = Genome.init(List.of(inputNode1, inputNode2, outputNode),
        List.of(con1, con2));

    // when: computing the network
    NeuralNetwork network = NeuralNetwork.createFromGenome(genome);
    network.putValue(inputNode1, 3f);
    network.putValue(inputNode2, 5f);
    network.compute(ACTIVATION);

    float result = network.getValue(outputNode);
    assertThat(result).isCloseTo(3f, Percentage.withPercentage(0.1d));
  }
}