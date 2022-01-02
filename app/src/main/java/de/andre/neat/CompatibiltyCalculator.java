package de.andre.neat;

import java.util.HashMap;
import java.util.Map;

public class CompatibiltyCalculator {

  private static final float PARAM_C1 = 1.0f;
  private static final float PARAM_C2 = 1.0f;
  private static final float PARAM_C3 = 0.4f;

  public float calcCompatibility(Genome genome1, Genome genome2) {

    float nodeCount = Math.max(genome1.nodeCount(), genome2.nodeCount());

    InnovationNumber g1MaxInno = genome1.maxInnovationNumber();
    InnovationNumber g2MaxInno = genome2.maxInnovationNumber();

    int countDisjointConnectionGenes = 0;
    int countExcessConnectionGenes = 0;
    int countMatchingConnectionGenes = 0;
    Map<InnovationNumber, ConnectionGene> g1Connections = new HashMap<>();
    for (ConnectionGene connection : genome1.getConnections()) {
      g1Connections.put(connection.getInnovation(), connection);
    }
    Map<InnovationNumber, ConnectionGene> g2Connections = new HashMap<>();
    for (ConnectionGene connection : genome2.getConnections()) {
      g2Connections.put(connection.getInnovation(), connection);
    }

    for (ConnectionGene g1Connection : genome1.getConnections()) {
      ConnectionGene g2Connection = g2Connections.get(g1Connection.getInnovation());
      if (g2Connection != null) {
        // it's a matching connection
        countMatchingConnectionGenes++;
      } else if (g1Connection.getInnovation().getValue() < g2MaxInno.getValue()) {
        countDisjointConnectionGenes++;
      } else {
        countExcessConnectionGenes++;
      }
    }

    float sumWeightDifference = 0f;
    for (ConnectionGene g2Connection : genome2.getConnections()) {
      ConnectionGene g1Connection = g1Connections.get(g2Connection.getInnovation());
      if (g1Connection != null) {
        // it's a matching connection
        sumWeightDifference =
            Math.abs(g1Connection.getWeight().getWeight() - g2Connection.getWeight().getWeight())
                / countMatchingConnectionGenes;
      } else if (g2Connection.getInnovation().getValue() < g1MaxInno.getValue()) {
        countDisjointConnectionGenes++;
      } else {
        countExcessConnectionGenes++;
      }
    }

    return PARAM_C1 * countExcessConnectionGenes / nodeCount
        + PARAM_C2 * countDisjointConnectionGenes / nodeCount
        + PARAM_C3 * sumWeightDifference;
  }
}
