package de.andre.neat;

import static de.andre.neat.Parameter.PARAM_COMPATIBILITY_DISTANCE;
import static de.andre.neat.Parameter.PARAM_INTERSPECIES_MATING_RATE;
import static de.andre.neat.Parameter.PARAM_MIN_GENOMES_PER_SPECIES;
import static de.andre.neat.Parameter.PARAM_MUTATION_RATE;
import static de.andre.neat.Parameter.PARAM_NEW_CONNECTION_MUTATION_RATE;
import static de.andre.neat.Parameter.PARAM_NEW_NODE_MUTATION_RATE;
import static de.andre.neat.Parameter.PARAM_NO_CROSSOVER_RATE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import lombok.Getter;

public abstract class Evaluator {

  private final int populationSize;
  private final List<Genome> genomes = new ArrayList<>();
  private final List<Species> species = new ArrayList<>();
  private final NodeFactory nodeFactory = new NodeFactory();
  private final InnovationNumberFactory innovationNumberFactory = new InnovationNumberFactory();
  private final Random r = new Random();

  @Getter
  private Fitness highestFitness;
  @Getter
  private Genome fittestGenome;

  protected Evaluator(int populationSize) {
    this.populationSize = populationSize;
    while (genomes.size() < populationSize) {
      genomes.add(initializeGenome(r));
    }
  }

  protected abstract Genome initializeGenome(Random r);

  protected abstract Fitness evaluateGenome(Genome genome);

  public void evaluateNextGeneration() {
    Map<Genome, Species> speciesMap = new HashMap<>(populationSize);
    Map<Genome, Fitness> fitnessMap = new HashMap<>(populationSize);

    // place genomes into species
    for (Genome genome : genomes) {
      Species specie = assignSpecie(genome);
      speciesMap.put(genome, specie);
    }

    // evaluate genomes and assign fitness
    for (Genome genome : genomes) {
      Fitness fitness = evaluateGenome(genome);
      fitnessMap.put(genome, fitness);

      Species specie = speciesMap.get(genome);
      specie.updateGenomeFitness(genome, fitness);

      // remember the fittest genome
      if (highestFitness == null || fitness.getValue() > highestFitness.getValue()) {
        this.highestFitness = fitness;
        this.fittestGenome = genome;
      }
    }

    // put the best genome of each species into the next generation
    List<Genome> nextGenGenomes = new ArrayList<>();
    for (Species specie : species) {
      if (specie.size() <= PARAM_MIN_GENOMES_PER_SPECIES) {
        // add all genomes when specie is too small
        nextGenGenomes.addAll(specie.getMembers());
      } else {
        // add the champion of the specie to next generation
        nextGenGenomes.add(specie.getBestGenome());
      }
    }

    // breed the rest of the genomes via mutation
    List<Genome> availableGenomes = new ArrayList<>(genomes);
    availableGenomes.removeAll(nextGenGenomes);
    int genomesWithoutCrossoverMissing = (int) (populationSize * PARAM_NO_CROSSOVER_RATE);
    while (genomesWithoutCrossoverMissing > 0 && !availableGenomes.isEmpty()) {
      // add genomes to next generation without crossover
      Genome genome = selectRandomly(r, availableGenomes);
      availableGenomes.remove(genome);
      applyMutations(genome);
      nextGenGenomes.add(genome);
      genomesWithoutCrossoverMissing--;
    }

    // breed the rest of the genomes via crossover
    while (nextGenGenomes.size() < populationSize) {
      Genome parent1 = selectRandomly(r, genomes);
      Genome parent2;
      if (r.nextFloat() <= PARAM_INTERSPECIES_MATING_RATE) {
        // select couple from any species
        parent2 = selectRandomly(r, genomes);
      } else {
        // select couple from same species
        parent2 = selectRandomly(r, speciesMap.get(parent1).getMembers());
      }

      // check which parent has the better fitness
      Fitness fitness1 = fitnessMap.get(parent1);
      Fitness fitness2 = fitnessMap.get(parent2);
      Genome offspring;
      if (fitness1.getValue() == fitness2.getValue()) {
        // both parents have the same fitness
        offspring = Genome.crossover(parent1, parent2, true, r);
      } else if (fitness1.getValue() > fitness2.getValue()) {
        // parent1 is the fitter parent
        offspring = Genome.crossover(parent1, parent2, false, r);
      } else {
        // parent2 is the fitter parent
        offspring = Genome.crossover(parent2, parent1, false, r);
      }
      applyMutations(offspring);
      nextGenGenomes.add(offspring);
    }

    // make the generation change
    genomes.clear();
    genomes.addAll(nextGenGenomes);
    for (Species specie : species) {
      if (specie.size() > PARAM_MIN_GENOMES_PER_SPECIES) {
        specie.reset();
      }
    }
    speciesMap.clear();
    fitnessMap.clear();
  }

  private void applyMutations(Genome genome) {
    if (r.nextFloat() <= PARAM_MUTATION_RATE) {
      genome.weightMutation(r);
    }
    if (r.nextFloat() <= PARAM_NEW_NODE_MUTATION_RATE) {
      genome.addNodeMutation(r, nodeFactory, innovationNumberFactory);
    }
    if (r.nextFloat() <= PARAM_NEW_CONNECTION_MUTATION_RATE) {
      genome.addConnectionMutation(r, innovationNumberFactory);
    }
  }

  private Genome selectRandomly(Random r, List<Genome> genomes) {
    return genomes.get(r.nextInt(genomes.size()));
  }

  private Species assignSpecie(Genome genome) {
    for (Species specie : this.species) {
      if (specie.isCompatible(genome)) {
        specie.add(genome);
        return specie;
      }
    }

    // create a new species
    Species specie = new Species(genome);
    this.species.add(specie);
    return specie;
  }

  public int getNumberOfSpecies() {
    return species.size();
  }

  private static class Species {

    @Getter
    private final List<Genome> members = new ArrayList<>();
    private Genome mascot;
    @Getter
    private Genome bestGenome;
    private Fitness maxFitness = Fitness.zero();

    private Species(Genome mascot) {
      this.mascot = mascot;
      this.bestGenome = mascot;
      this.members.add(mascot);
    }

    private void add(Genome genome) {
      if (genome != mascot) {
        this.members.add(genome);
      }
    }

    public void reset() {
      mascot = bestGenome;
      members.clear();
      members.add(bestGenome);
    }

    public boolean isCompatible(Genome genome) {
      return new CompatibiltyCalculator()
          .calcCompatibility(mascot, genome) < PARAM_COMPATIBILITY_DISTANCE;
    }

    public void updateGenomeFitness(Genome genome, Fitness fitness) {
      if (fitness.getValue() > this.maxFitness.getValue()) {
        this.maxFitness = fitness;
        this.bestGenome = genome;
      }
    }

    public int size() {
      return members.size();
    }
  }
}
