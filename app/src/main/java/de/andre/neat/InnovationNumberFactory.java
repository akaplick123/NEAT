package de.andre.neat;

import java.util.HashMap;

public class InnovationNumberFactory {

  private final HashMap<NodeId, HashMap<NodeId, InnovationNumber>> allConnections = new HashMap<>();

  /**
   * @param inNode  a node
   * @param outNode a node
   * @return a innovation number for the resulting connection. When calling it twice with the same
   * input, then the same innovation number must be returned.
   */
  public InnovationNumber create(NodeGene inNode, NodeGene outNode) {
    HashMap<NodeId, InnovationNumber> sublist = allConnections.computeIfAbsent(inNode.getId(),
        k -> new HashMap<>());
    InnovationNumber innovation = sublist.get(outNode.getId());
    if (innovation == null) {
      innovation = InnovationNumber.next();
      sublist.put(outNode.getId(), innovation);
    }
    return innovation;
  }

}
