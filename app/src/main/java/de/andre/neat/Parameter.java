package de.andre.neat;

import java.util.function.UnaryOperator;

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
   * number of genomes handled in parallel
   */
  int PARAM_POPULATION_SIZE = 150;
  /**
   * measure of compatibility for one specie
   */
  float PARAM_COMPATIBILITY_DISTANCE = 3.0f;
  /**
   * number of genomes within a species before the species gets shrunk at generation change
   */
  int PARAM_MIN_GENOMES_PER_SPECIES = 5;

  /**
   * percentage of genomes that make it to the next generation without a crossover
   */
  float PARAM_NO_CROSSOVER_RATE = 0.25f;

  /**
   * probability of a weight mutation
   */
  float PARAM_MUTATION_RATE = 0.8f;
  /**
   * probability of a connection to have it weight perturbed
   */
  float PARAM_MUTATION_RATE_WEIGHT_PERTUBE = 0.9f;
  /**
   * probability of a new node mutation
   */
  float PARAM_NEW_NODE_MUTATION_RATE = 0.03f;
  /**
   * probability of a new connection mutation
   */
  float PARAM_NEW_CONNECTION_MUTATION_RATE = 0.05f;

  /**
   * probability a couples without respect to their species
   */
  float PARAM_INTERSPECIES_MATING_RATE = 0.001f;

  /**
   * The activation function used
   */
  UnaryOperator<Float> PARAM_ACTIVATION_FUNCTION = x -> (float) (1d / (1d + Math.exp(-4.9d * x)));
}
