package de.andre.neat;

import de.andre.neat.NodeGene.Type;
import java.util.HashMap;

public class NodeFactory {

  private final HashMap<InnovationNumber, NodeGene> allNodes = new HashMap<>();

  /**
   * @param innovationNumber a innovation number (connection id)
   * @return the NodeId for the node when splitting the connection with the given innovation number
   */
  public NodeGene create(InnovationNumber innovationNumber) {
    NodeGene node = allNodes.get(innovationNumber);
    if (node == null) {
      // it's a new node
      node = NodeGene.builder()
          .type(Type.HIDDEN)
          .id(NodeId.next())
          .build();
      allNodes.put(innovationNumber, node);
    }
    return node;
  }
}
