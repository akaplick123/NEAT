package de.andre.neat;

import static de.andre.neat.Parameter.PARAM_ACTIVATION_FUNCTION;

import de.andre.neat.NodeGene.Type;
import java.util.List;
import java.util.Random;

public class XorEvaluator extends Evaluator {

  private NodeGene biasNode;
  private NodeGene input1Node;
  private NodeGene input2Node;
  private NodeGene outputNode;
  private InnovationNumber inno1;
  private InnovationNumber inno2;
  private InnovationNumber inno3;

  private static final List<List<Integer>> TESTDATA = List.of(
      List.of(1, 0, 0, 1),
      List.of(1, 1, 0, 0),
      List.of(1, 0, 1, 0),
      List.of(1, 1, 1, 1)
  );

  protected XorEvaluator(int populationSize) {
    super(populationSize);
  }

  @Override
  protected Genome initializeGenome(Random r) {
    if (biasNode == null) {
      biasNode = NodeGene.builder()
          .id(NodeId.next())
          .type(Type.INPUT)
          .build();
      input1Node = NodeGene.builder()
          .id(NodeId.next())
          .type(Type.INPUT)
          .build();
      input2Node = NodeGene.builder()
          .id(NodeId.next())
          .type(Type.INPUT)
          .build();
      outputNode = NodeGene.builder()
          .id(NodeId.next())
          .type(Type.OUTPUT)
          .build();
      inno1 = InnovationNumber.next();
      inno2 = InnovationNumber.next();
      inno3 = InnovationNumber.next();
    }

    ConnectionGene con1 = ConnectionGene.builder()
        .inNode(biasNode)
        .outNode(outputNode)
        .innovation(inno1)
        .expressed(ExpressedState.EXPRESSED)
        .weight(ConnectionWeight.random(r))
        .build();
    ConnectionGene con2 = ConnectionGene.builder()
        .inNode(input1Node)
        .outNode(outputNode)
        .innovation(inno2)
        .expressed(ExpressedState.EXPRESSED)
        .weight(ConnectionWeight.random(r))
        .build();
    ConnectionGene con3 = ConnectionGene.builder()
        .inNode(input2Node)
        .outNode(outputNode)
        .innovation(inno3)
        .expressed(ExpressedState.EXPRESSED)
        .weight(ConnectionWeight.random(r))
        .build();

    return Genome.init(
        List.of(biasNode, input1Node, input2Node, outputNode),
        List.of(con1, con2, con3));
  }

  @Override
  protected Fitness evaluateGenome(Genome genome) {

    for (List<Integer> testdate : TESTDATA) {
      float bias = testdate.get(0);
      float input1 = testdate.get(1);
      float input2 = testdate.get(2);
      float expectedOutput = testdate.get(3);

      NeuralNetwork neuralNetwork = NeuralNetwork.createFromGenome(genome);
      neuralNetwork.putValue(biasNode, bias);
      neuralNetwork.putValue(input1Node, input1);
      neuralNetwork.putValue(input2Node, input2);
      neuralNetwork.compute(PARAM_ACTIVATION_FUNCTION);
      float output = neuralNetwork.getValue(outputNode);
    }

    return null;
  }
}
