package de.andre.neat;

import static de.andre.neat.Parameter.PARAM_COMPATIBILITY_DISTANCE;
import static de.andre.neat.Parameter.PARAM_INTERSPECIES_MATING_RATE;
import static de.andre.neat.Parameter.PARAM_MAX_GENERATIONS_WITHOUT_IMPROVEMENT;
import static de.andre.neat.Parameter.PARAM_MIN_GENOMES_PER_SPECIES;
import static de.andre.neat.Parameter.PARAM_MUTATION_RATE;
import static de.andre.neat.Parameter.PARAM_NEW_CONNECTION_MUTATION_RATE;
import static de.andre.neat.Parameter.PARAM_NEW_NODE_MUTATION_RATE;
import static de.andre.neat.Parameter.PARAM_NO_CROSSOVER_RATE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import lombok.Getter;
import lombok.Value;

public abstract class Evaluator {

  private final int populationSize;
  private final List<Genome> genomes = new ArrayList<>();
  private final List<Species> species = new ArrayList<>();
  private final NodeFactory nodeFactory = new NodeFactory();
  private final InnovationNumberFactory innovationNumberFactory = new InnovationNumberFactory();
  private final Random r = new Random();

  private Generation generation = Generation.FIRST;

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
      specie.updateGenomeFitness(genome, fitness, generation);

      // remember the fittest genome
      if (highestFitness == null || fitness.getValue() > highestFitness.getValue()) {
        this.highestFitness = fitness;
        this.fittestGenome = genome;
      }
    }

    // put the best genome of each species with more than 5 members into the next generation
    Set<Genome> nextGenGenomes = new HashSet<>();
    for (Species specie : species) {
      if (specie.size() >= PARAM_MIN_GENOMES_PER_SPECIES) {
        // add the champion of the specie to next generation
        nextGenGenomes.add(specie.getBestGenome());
      }
    }

    // assign the relative fitness to each genome
    RelativeFitnessMap overallRelativeFitnessMap = new RelativeFitnessMap();
    HashMap<Species, RelativeFitnessMap> speciesRelativeFitnessMap = new HashMap<>();
    for (Species specie : species) {
      RelativeFitnessMap specieRelativeFitnessMap = new RelativeFitnessMap();
      for (Genome genome : specie.getMembers()) {
        RelativeFitness relativeFitness = specie.relativeFitness(genome);
        if (relativeFitness == RelativeFitness.NOT_AVAILABLE) {
          // do not add networks with recursion
          continue;
        }

        specieRelativeFitnessMap.add(genome, relativeFitness);
        if (nextGenGenomes.contains(genome)) {
          overallRelativeFitnessMap.add(genome, relativeFitness);
        } else if (specie.getLastImprovedGeneration().getValue()
            + PARAM_MAX_GENERATIONS_WITHOUT_IMPROVEMENT >= generation.getValue()) {
          // do not let genomes reproduce, if species does not evolve
          overallRelativeFitnessMap.add(genome, relativeFitness);
        }
      }
      speciesRelativeFitnessMap.put(specie, specieRelativeFitnessMap);
    }

    // breed the rest of the genomes via mutation (prefer fitter genomes)
    int genomesWithoutCrossoverMissing = (int) (populationSize * PARAM_NO_CROSSOVER_RATE);
    while (genomesWithoutCrossoverMissing > 0) {
      // add genomes to next generation without crossover
      Genome genome = overallRelativeFitnessMap.pickRandomly(r);
      Genome mutatedGenome = applyMutations(genome);
      if (genome != mutatedGenome) {
        nextGenGenomes.add(genome);
        genomesWithoutCrossoverMissing--;
      }
    }

    // breed the rest of the genomes via crossover (prefer fitter genomes)
    while (nextGenGenomes.size() < populationSize) {
      Genome parent1 = overallRelativeFitnessMap.pickRandomly(r);
      Genome parent2;
      if (r.nextFloat() <= PARAM_INTERSPECIES_MATING_RATE) {
        // select couple from any species
        parent2 = overallRelativeFitnessMap.pickRandomly(r);
      } else {
        // select couple from same species
        Species specie = speciesMap.get(parent1);
        parent2 = speciesRelativeFitnessMap.get(specie).pickRandomly(r);
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
      offspring = applyMutations(offspring);
      nextGenGenomes.add(offspring);
    }

    // make the generation change
    generation = generation.next();
    genomes.clear();
    genomes.addAll(nextGenGenomes);
    species.removeIf(Species::hasNoMember);
    for (Species specie : species) {
      specie.reset();
    }
    speciesMap.clear();
    fitnessMap.clear();
  }

  private Genome applyMutations(Genome genome) {
    Genome mutatedGenome = genome;
    if (r.nextFloat() <= PARAM_MUTATION_RATE) {
      mutatedGenome = mutatedGenome.weightMutation(r);
    }
    if (r.nextFloat() <= PARAM_NEW_NODE_MUTATION_RATE) {
      mutatedGenome = mutatedGenome.addNodeMutation(r, nodeFactory, innovationNumberFactory);
    }
    if (r.nextFloat() <= PARAM_NEW_CONNECTION_MUTATION_RATE) {
      mutatedGenome = mutatedGenome.addConnectionMutation(r, innovationNumberFactory);
    }
    return mutatedGenome;
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
    private final Genome mascot;
    private final Map<Genome, Fitness> fitnessMap = new HashMap<>();
    @Getter
    private Genome bestGenome;
    private Fitness maxFitnessCurrentGeneration = Fitness.NOT_AVAILABLE;
    private Fitness maxFitnessOverallGeneration = Fitness.NOT_AVAILABLE;
    @Getter
    private Generation lastImprovedGeneration = Generation.FIRST;

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
      members.clear();
      fitnessMap.clear();
      maxFitnessCurrentGeneration = Fitness.NOT_AVAILABLE;
    }

    public boolean isCompatible(Genome genome) {
      return new CompatibiltyCalculator()
          .calcCompatibility(mascot, genome) < PARAM_COMPATIBILITY_DISTANCE;
    }

    public void updateGenomeFitness(Genome genome, Fitness fitness, Generation generation) {
      if (fitness.getValue() > this.maxFitnessCurrentGeneration.getValue()) {
        this.maxFitnessCurrentGeneration = fitness;
        this.bestGenome = genome;
      }
      if (fitness.getValue() > this.maxFitnessOverallGeneration.getValue()) {
        this.maxFitnessOverallGeneration = fitness;
        this.lastImprovedGeneration = generation;
      }
      this.fitnessMap.put(genome, fitness);
    }

    public int size() {
      return members.size();
    }

    public boolean hasNoMember() {
      return members.isEmpty();
    }

    public RelativeFitness relativeFitness(Genome genome) {
      Fitness fitness = this.fitnessMap.get(genome);
      if (fitness == Fitness.NOT_AVAILABLE) {
        return RelativeFitness.NOT_AVAILABLE;
      }

      return RelativeFitness.of(fitness.getValue() / size());
    }
  }

  @Value(staticConstructor = "of")
  private static class RelativeFitness {

    public static RelativeFitness NOT_AVAILABLE = RelativeFitness.of(Float.MIN_VALUE);

    float value;
  }

  private static class RelativeFitnessMap {

    private final List<Map.Entry<Genome, RelativeFitness>> fitnessMap = new ArrayList<>();
    private RelativeFitness sumFitness = null;
    private RelativeFitness minFitness = null;

    public void add(Genome genome, RelativeFitness relativeFitness) {
      sumFitness = null;
      fitnessMap.add(Map.entry(genome, relativeFitness));

      if (minFitness == null || relativeFitness.getValue() < minFitness.getValue()) {
        minFitness = relativeFitness;
      }
    }

    /*
    public Genome pickRandomly(Random r) {
      // calc sumFitness, if needed
      if (sumFitness == null) {
        float sumRelativeFitness = 0f;
        for (Entry<Genome, RelativeFitness> entry : fitnessMap) {
          sumRelativeFitness += Math.abs(minFitness.getValue() - entry.getValue().getValue());
        }
        sumFitness = RelativeFitness.of(sumRelativeFitness);
      }

      float threshold = r.nextFloat() * sumFitness.getValue();
      // find first genome that is above the threshold
      float sumRelativeFitness = 0f;
      for (Entry<Genome, RelativeFitness> entry : fitnessMap) {
        sumRelativeFitness += Math.abs(minFitness.getValue() - entry.getValue().getValue());
        if (sumRelativeFitness >= threshold) {
          return entry.getKey();
        }
      }

      throw new IllegalArgumentException("Cannot find a random genome");
    }
    */

    public Genome pickRandomly(Random r) {
      return fitnessMap.get(r.nextInt(fitnessMap.size())).getKey();
    }
  }


  @Value
  private static class Generation {

    public static final Generation FIRST = new Generation(1);

    int value;

    public Generation next() {
      return new Generation(value + 1);
    }
  }
}
