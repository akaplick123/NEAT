package de.andre.neat;

import de.andre.neat.NodeGene.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

public class Genome {

  private final List<NodeGene> nodes = new ArrayList<>();
  private final List<ConnectionGene> connections = new ArrayList<>();

  static Genome init(List<NodeGene> nodes, List<ConnectionGene> connections) {
    // ensure connections are valid
    for (ConnectionGene connection : connections) {
      // check inNode and outNode are present
      if (!nodes.contains(connection.getInNode())) {
        throw new IllegalArgumentException("InNode is not present");
      }
      if (!nodes.contains(connection.getOutNode())) {
        throw new IllegalArgumentException("OutNode is not present");
      }
    }

    Genome newGenome = new Genome();
    newGenome.nodes.addAll(nodes);
    newGenome.connections.addAll(connections);
    return newGenome;
  }

  /**
   * @param parent1 the more fit parent
   * @param parent2 the less fit parent
   * @return a new genome
   */
  public static Genome crossover(Genome parent1, Genome parent2, boolean sameFitness, Random r) {
    // build superset of all nodes present
    Collection<NodeGene> newNodes = new ArrayList<>();
    if (sameFitness) {
      TreeMap<NodeId, NodeGene> orderedNewNodes = new TreeMap<>(NodeId.COMPARATOR);
      for (NodeGene node : parent1.nodes) {
        orderedNewNodes.put(node.getId(), node);
      }
      for (NodeGene node : parent2.nodes) {
        orderedNewNodes.put(node.getId(), node);
      }
      newNodes.addAll(orderedNewNodes.values());
    } else {
      newNodes.addAll(parent1.nodes);
    }

    // find matching connection genes
    Map<InnovationNumber, ConnectionGene> parent1Innos = new HashMap<>();
    for (ConnectionGene connection : parent1.connections) {
      parent1Innos.put(connection.getInnovation(), connection);
    }
    Map<InnovationNumber, ConnectionGene> parent2Innos = new HashMap<>();
    for (ConnectionGene connection : parent2.connections) {
      parent2Innos.put(connection.getInnovation(), connection);
    }
    List<ConnectionGene> newConnectionsBases = new ArrayList<>();
    for (ConnectionGene p1Connection : parent1.connections) {
      ConnectionGene p2Connection = parent2Innos.get(p1Connection.getInnovation());
      if (p2Connection == null) {
        // add "disjoint" and "excess" connection from the fitter parent
        newConnectionsBases.add(p1Connection);
      } else {
        // choose matching connections randomly from p1 or p2
        newConnectionsBases.add(r.nextBoolean() ? p1Connection : p2Connection);
      }
    }

    if (sameFitness) {
      // add "disjoint" and "excess" connection from both parents when both parents have the same fitness
      for (ConnectionGene p2Connection : parent2.connections) {
        if (!parent1Innos.containsKey(p2Connection.getInnovation())) {
          newConnectionsBases.add(p2Connection);
        }
      }
    }

    // create new genome
    Genome offspring = new Genome();
    offspring.nodes.addAll(newNodes);
    for (ConnectionGene connectionBase : newConnectionsBases) {
      offspring.connections.add(connectionBase.createCopy());
    }
    return offspring;
  }

  public List<NodeGene> getNodes() {
    return Collections.unmodifiableList(nodes);
  }

  public List<ConnectionGene> getConnections() {
    return Collections.unmodifiableList(connections);
  }

  public void weightMutation(Random r) {
    for (ConnectionGene connection : connections) {
      if (r.nextFloat() < 0.8f) {
        // weight shall mutate
        if (r.nextFloat() < 0.9f) {
          // weight shall mutate uniformly perturbed
          connection.pertubeWeight(r);
        } else {
          // weight shall get a totally random new value
          connection.assignNewWeight(r);
        }
      }
    }

  }

  public void addConnectionMutation(Random r) {
    NodeGene inNode = pickRandomNode(r);
    NodeGene outNode = pickRandomNode(r);

    // check if connection exists
    if (!connectionExists(inNode, outNode)) {
      // create new connection
      ConnectionGene connection = ConnectionGene.builder()
          .inNode(inNode)
          .outNode(outNode)
          .weight(ConnectionWeight.random(r))
          .expressed(ExpressedState.EXPRESSED)
          .innovation(InnovationNumber.next())
          .build();
      connections.add(connection);
    }
  }

  public void addNodeMutation(Random r) {
    // pick a random connection
    ConnectionGene oldConnection = pickRandomConnection(r);
    oldConnection.disable();

    // create a new node
    NodeGene newNode = NodeGene.builder()
        .id(NodeId.next())
        .type(Type.HIDDEN)
        .build();
    nodes.add(newNode);

    // add connection
    ConnectionGene newConnection1 = ConnectionGene.builder()
        .inNode(oldConnection.getInNode())
        .outNode(newNode)
        .weight(ConnectionWeight.one())
        .expressed(ExpressedState.EXPRESSED)
        .innovation(InnovationNumber.next())
        .build();
    connections.add(newConnection1);

    ConnectionGene newConnection2 = ConnectionGene.builder()
        .inNode(newNode)
        .outNode(oldConnection.getOutNode())
        .weight(oldConnection.getWeight())
        .expressed(ExpressedState.EXPRESSED)
        .innovation(InnovationNumber.next())
        .build();
    connections.add(newConnection2);
  }

  private ConnectionGene pickRandomConnection(Random r) {
    return connections.get(r.nextInt(connections.size()));
  }

  private boolean connectionExists(NodeGene inNode, NodeGene outNode) {
    for (ConnectionGene connection : connections) {
      if (connection.getInNode().getId().equals(inNode.getId())
          && connection.getOutNode().getId().equals(outNode.getId())) {
        return true;
      }
    }
    return false;
  }

  private NodeGene pickRandomNode(Random r) {
    return nodes.get(r.nextInt(nodes.size()));
  }

  public int nodeCount() {
    return nodes.size();
  }

  public InnovationNumber maxInnovationNumber() {
    InnovationNumber max = null;
    for (ConnectionGene connection : this.connections) {
      if (max == null) {
        max = connection.getInnovation();
      } else if (connection.getInnovation().getValue() > max.getValue()) {
        max = connection.getInnovation();
      }
    }

    return max;
  }
}
