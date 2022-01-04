package de.andre.neat;

import static de.andre.neat.Parameter.PARAM_POPULATION_SIZE;

public class App {

  public static void main(String[] args) {
    XorEvaluator evaluator = new XorEvaluator(PARAM_POPULATION_SIZE);

    for (int i = 0; i < 1_000; i++) {
      evaluator.evaluateNextGeneration();
      System.out.print("Generation: " + i);
      System.out.print("\tHighest fitness: " + evaluator.getHighestFitness().getValue());
      System.out.print("\tAmount of species: " + evaluator.getNumberOfSpecies());
      System.out.print(
          "\tConnections in best performer: " + evaluator.getFittestGenome().getConnections()
              .size());
      int deadGenes = 0;
      for (ConnectionGene cg : evaluator.getFittestGenome().getConnections()) {
        if (cg.getExpressed() == ExpressedState.NOT_EXPRESSED) {
          deadGenes++;
        }
      }
      System.out.print("\tDead genes in best performer: " + deadGenes);
      System.out.print("\n");
      if (i % 10 == 0) {
//        GenomePrinter.printGenome(eval.getFittestGenome(), "output/connection_amount_2/"+i+".png");
      }
    }
  }
}
