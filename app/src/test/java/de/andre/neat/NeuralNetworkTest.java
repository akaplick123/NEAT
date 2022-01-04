package de.andre.neat;

import static de.andre.neat.ConnectionGeneTestData.connection;
import static de.andre.neat.NodeGeneTestData.hidden;
import static de.andre.neat.NodeGeneTestData.input;
import static de.andre.neat.NodeGeneTestData.output;
import static de.andre.neat.NodeGeneTestData.resetNodeCounter;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.function.UnaryOperator;
import org.assertj.core.data.Percentage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NeuralNetworkTest {

  /**
   * a very simple activation function
   */
  private static final UnaryOperator<Float> ACTIVATION = f -> f;

  @BeforeEach
  void setup() {
    resetNodeCounter();
  }

  @Test
  void shouldComputeNetwork_OneConnectionOnly() {
    // given: a genome
    NodeGene inputNode = input();
    NodeGene outputNode = output();
    ConnectionGene con1 = connection(inputNode, outputNode).build();
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
  void shouldComputeNetwork_TwoInputs() {
    // given: a genome
    NodeGene inputNode1 = input();
    NodeGene inputNode2 = input();
    NodeGene outputNode = output();
    ConnectionGene con1 = connection(inputNode1, outputNode).build();
    ConnectionGene con2 = connection(inputNode2, outputNode).build();
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
  void shouldComputeNetwork_DisabledConnection() {
    // given: a genome
    NodeGene inputNode1 = input();
    NodeGene inputNode2 = input();
    NodeGene outputNode = output();
    ConnectionGene con1 = connection(inputNode1, outputNode).build();
    ConnectionGene con2 = connection(inputNode2, outputNode)
        .expressed(ExpressedState.NOT_EXPRESSED)
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

  @Test
  void shouldComputeNetwork_OneHiddenNode() {
    // given: a genome
    NodeGene inputNode1 = input();
    NodeGene inputNode2 = input();
    NodeGene outputNode = output();
    NodeGene hiddenNode1 = hidden();
    ConnectionGene con1 = connection(inputNode1, outputNode).build();
    ConnectionGene con2 = connection(inputNode2, outputNode).build();
    ConnectionGene con3 = connection(inputNode1, hiddenNode1).build();
    ConnectionGene con4 = connection(hiddenNode1, outputNode).build();
    Genome genome = Genome.init(List.of(inputNode1, inputNode2, outputNode, hiddenNode1),
        List.of(con1, con2, con3, con4));

    // when: computing the network
    NeuralNetwork network = NeuralNetwork.createFromGenome(genome);
    network.putValue(inputNode1, 3f);
    network.putValue(inputNode2, 5f);
    network.compute(ACTIVATION);

    float result = network.getValue(outputNode);
    assertThat(result).isCloseTo(11f, Percentage.withPercentage(0.1d));
  }

  @Test
  void shouldComputeNetwork_OneHiddenNodeEvaluateTwice() {
    // given: a genome
    NodeGene inputNode1 = input();
    NodeGene inputNode2 = input();
    NodeGene outputNode = output();
    NodeGene hiddenNode1 = hidden();
    ConnectionGene con1 = connection(inputNode1, outputNode).build();
    ConnectionGene con2 = connection(inputNode2, outputNode).build();
    ConnectionGene con3 = connection(inputNode1, hiddenNode1).build();
    ConnectionGene con4 = connection(hiddenNode1, outputNode).build();
    Genome genome = Genome.init(List.of(inputNode1, inputNode2, outputNode, hiddenNode1),
        List.of(con1, con2, con3, con4));

    // when: computing the network once
    NeuralNetwork network = NeuralNetwork.createFromGenome(genome);
    network.putValue(inputNode1, 3f);
    network.putValue(inputNode2, 5f);
    network.compute(ACTIVATION);

    // when: computing the network twice
    network.resetValues();
    network.putValue(inputNode1, 5f);
    network.putValue(inputNode2, 7f);
    network.compute(ACTIVATION);

    float result2 = network.getValue(outputNode);
    assertThat(result2).isCloseTo(17f, Percentage.withPercentage(0.1d));
  }

  @Test
  void shouldComputeNetwork_WithSelfReference() {
    // given: a genome
    NodeGene inputNode1 = input();
    NodeGene inputNode2 = input();
    NodeGene outputNode = output();
    NodeGene hiddenNode1 = hidden();
    ConnectionGene con1 = connection(inputNode1, outputNode).build();
    ConnectionGene con2 = connection(inputNode2, outputNode).build();
    ConnectionGene con3 = connection(inputNode1, hiddenNode1).build();
    ConnectionGene con4 = connection(hiddenNode1, outputNode).build();
    ConnectionGene con5 = connection(hiddenNode1, hiddenNode1).build();
    Genome genome = Genome.init(List.of(inputNode1, inputNode2, outputNode, hiddenNode1),
        List.of(con1, con2, con3, con4, con5));

    // when: computing the network
    NeuralNetwork network = NeuralNetwork.createFromGenome(genome);
    network.putValue(inputNode1, 3f);
    network.putValue(inputNode2, 5f);
    network.compute(ACTIVATION);

    // then: out should not have been calculated
    assertThrows(ValueNotPresentException.class, () -> network.getValue(outputNode));
  }

  @Test
  void shouldComputeNetwork_WithCycle() {
    // given: a genome
    NodeGene inputNode1 = input();
    NodeGene inputNode2 = input();
    NodeGene outputNode = output();
    NodeGene hiddenNode1 = hidden();
    ConnectionGene con1 = connection(inputNode1, outputNode).build();
    ConnectionGene con2 = connection(inputNode2, outputNode).build();
    ConnectionGene con3 = connection(inputNode1, hiddenNode1).build();
    ConnectionGene con4 = connection(hiddenNode1, outputNode).build();
    ConnectionGene con5 = connection(outputNode, hiddenNode1).build();
    Genome genome = Genome.init(List.of(inputNode1, inputNode2, outputNode, hiddenNode1),
        List.of(con1, con2, con3, con4, con5));

    // when: computing the network
    NeuralNetwork network = NeuralNetwork.createFromGenome(genome);
    network.putValue(inputNode1, 3f);
    network.putValue(inputNode2, 5f);
    network.compute(ACTIVATION);

    // then: out should not have been calculated
    assertThrows(ValueNotPresentException.class, () -> network.getValue(outputNode));
  }
}