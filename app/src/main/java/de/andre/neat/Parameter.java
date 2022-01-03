package de.andre.neat;

public interface Parameter {

  /**
   * Weight of excess genes for compatibility
   */
  float PARAM_C1 = 1.0f;
  /**
   * Weight of disjoint genes for compatibility
   */
  float PARAM_C2 = 1.0f;
  /**
   * Weight of average weight differences for compatibility
   */
  float PARAM_C3 = 0.4f;

  /**
   * maximum initial weight for new connections and connection weight mutations
   */
  float INIT_MAX_WEIGHT = 1f;
  /**
   * maximum delta value for perturbed weights
   */
  float MAX_PERTUBE_WEIGHT_DELTA = 1f;

  /**
   * probability of a connection to have a weight mutation of any sort
   */
  float MUTATION_RATE_WEIGHT = 0.8f;
  /**
   * probability of a connection to have it weight perturbed
   */
  float MUTATION_RATE_WEIGHT_PERTUBE = 0.9f;
}
