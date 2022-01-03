package de.andre.neat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class NeuralNetwork {

  private final Map<NodeGene, NeuralNetworkNode> nodeGeneMap = new HashMap<>();
  private final List<NeuralNetworkNode> nodes = new ArrayList<>();

  public static NeuralNetwork createFromGenome(Genome genome) {
    NeuralNetwork network = new NeuralNetwork();
    for (NodeGene node : genome.getNodes()) {
      NeuralNetworkNode neuralNetworkNode = new NeuralNetworkNode();
      network.nodes.add(neuralNetworkNode);
      network.nodeGeneMap.put(node, neuralNetworkNode);
    }

    for (ConnectionGene connection : genome.getConnections()) {
      if (connection.getExpressed() == ExpressedState.EXPRESSED) {
        NeuralNetworkNode inNode = network.nodeGeneMap.get(connection.getInNode());
        NeuralNetworkNode outNode = network.nodeGeneMap.get(connection.getOutNode());
        Connection connection1 = new Connection(connection.getWeight(), inNode);
        outNode.addConnection(connection1);
      }
    }

    return network;
  }

  public void putValue(NodeGene node, float value) {
    nodeGeneMap.get(node).setValue(value);
  }

  public float getValue(NodeGene node) {
    NeuralNetworkNode networkNode = nodeGeneMap.get(node);
    if (networkNode.isCalculated()) {
      return networkNode.getValue();
    }
    throw new IllegalArgumentException("Node " + node.getId() + " has not been calculated.");
  }

  public void compute(UnaryOperator<Float> activationFunction) {
    boolean foundNodeForCalculation;
    List<NeuralNetworkNode> uncomputedNodes = new ArrayList<>(nodes);
    do {
      foundNodeForCalculation = false;
      Iterator<NeuralNetworkNode> iterator = uncomputedNodes.iterator();
      while (iterator.hasNext()) {
        NeuralNetworkNode node = iterator.next();
        if (node.canValueBeCalculated()) {
          foundNodeForCalculation = true;
          node.calculateValue(activationFunction);
          iterator.remove();
        }
      }
    } while (foundNodeForCalculation);
  }

  private static class NeuralNetworkNode {

    private final List<Connection> connections = new ArrayList<>();
    @Getter
    private float value;
    @Getter
    private boolean calculated;

    public void addConnection(Connection connection) {
      this.connections.add(connection);
    }

    public void setValue(float value) {
      this.value = value;
      this.calculated = true;
    }

    public boolean canValueBeCalculated() {
      if (calculated) {
        return true;
      }
      if (connections.isEmpty()) {
        return false;
      }

      for (Connection connection : connections) {
        if (!connection.canCalculate()) {
          return false;
        }
      }

      // all connections can be calculated
      return true;
    }

    public void calculateValue(UnaryOperator<Float> activationFunction) {
      if (calculated) {
        return;
      }

      float sumValue = 0f;
      for (Connection connection : connections) {
        sumValue += connection.weightedValue();
      }

      this.value = activationFunction.apply(sumValue);
      this.calculated = true;
    }
  }

  @AllArgsConstructor
  private static class Connection {

    private final ConnectionWeight weight;
    private final NeuralNetworkNode sourceNode;

    public boolean canCalculate() {
      return sourceNode.isCalculated();
    }

    public float weightedValue() {
      return sourceNode.getValue() * weight.getWeight();
    }
  }
}
