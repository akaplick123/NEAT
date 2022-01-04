package de.andre.neat;

import de.andre.neat.NodeGene.Type;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Random;
import javax.imageio.ImageIO;

public class GenomePrinter {

  public static void printGenome(Genome genome, String path) {
    Random r = new Random();
    HashMap<NodeGene, Point> nodeGenePositions = new HashMap<>();
    int nodeSize = 20;
    int connectionSizeBulb = 6;
    int imageSize = 512;

    int halfNodeSize = nodeSize / 2;
    BufferedImage image = new BufferedImage(imageSize, imageSize, BufferedImage.TYPE_INT_ARGB);

    Graphics g = image.getGraphics();
    g.setColor(Color.WHITE);
    g.fillRect(0, 0, imageSize, imageSize);

    // draw nodes
    g.setColor(Color.BLUE);
    int numInputNodes = countInputNodes(genome);
    int seenInputNodes = 0;
    for (NodeGene gene : genome.getNodes()) {
      if (gene.getType() == Type.INPUT) {
//        float x = ((float) gene.getId() / ((float) countNodesByType(genome, Type.INPUT) + 1f))
//            * imageSize;
        ++seenInputNodes;
        float x = (seenInputNodes / (numInputNodes + 1f)) * imageSize;
        float y = (float) imageSize - halfNodeSize;
        g.fillOval((int) (x - halfNodeSize), (int) (y - halfNodeSize), nodeSize, nodeSize);
        nodeGenePositions.put(gene, new Point((int) x, (int) y));
      } else if (gene.getType() == Type.HIDDEN) {
        int x = r.nextInt(imageSize - nodeSize * 2) + nodeSize;
        int y = r.nextInt(imageSize - nodeSize * 3) + (int) (nodeSize * 1.5f);
        g.fillOval(x - halfNodeSize, y - halfNodeSize, nodeSize, nodeSize);
        nodeGenePositions.put(gene, new Point(x, y));
      } else if (gene.getType() == Type.OUTPUT) {
        int x = r.nextInt(imageSize - nodeSize * 2) + nodeSize;
        int y = halfNodeSize + 1;
        g.fillOval(x - halfNodeSize, y - halfNodeSize, nodeSize, nodeSize);
        nodeGenePositions.put(gene, new Point(x, y));
      }
    }

    // draw connections
    g.setColor(Color.BLACK);
    for (ConnectionGene gene : genome.getConnections()) {
      if (gene.getExpressed() != ExpressedState.EXPRESSED) {
        continue;
      }
      Point inNode = nodeGenePositions.get(gene.getInNode());
      Point outNode = nodeGenePositions.get(gene.getOutNode());

      Point lineVector = new Point((int) ((outNode.x - inNode.x) * 0.95f),
          (int) ((outNode.y - inNode.y) * 0.95f));
      Point lineEnd = new Point(inNode.x + lineVector.x, inNode.y + lineVector.y);

      g.drawLine(inNode.x, inNode.y, lineEnd.x, lineEnd.y);
      // draw "Bulb" at the outNode of the connection
      g.fillRect(lineEnd.x - connectionSizeBulb / 2, lineEnd.y - connectionSizeBulb / 2,
          connectionSizeBulb, connectionSizeBulb);
      // draw weight on the connection
      g.drawString("" + gene.getWeight().getWeight(), (int) (inNode.x + lineVector.x * 0.25f + 5),
          (int) (inNode.y + lineVector.y * 0.25f));
    }

    // draw NodeIds on the nodes
    g.setColor(Color.WHITE);
    FontMetrics fm = g.getFontMetrics();
    for (NodeGene nodeGene : genome.getNodes()) {
      Point p = nodeGenePositions.get(nodeGene);
      String txt = "" + nodeGene.getId().getValue();
      g.drawString(txt, p.x - fm.stringWidth(txt) / 2, p.y + 2);
    }

    try {
      File outputPath = new File(path);
      if (!outputPath.getParentFile().exists() && outputPath.getParentFile().mkdirs()) {
        System.out.println(
            "Directory " + outputPath.getParentFile().getAbsolutePath() + " created...");
      }
      ImageIO.write(image, "PNG", outputPath);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static int countInputNodes(Genome genome) {
    int count = 0;
    for (NodeGene node : genome.getNodes()) {
      if (node.getType() == Type.INPUT) {
        count++;
      }
    }
    return count;
  }
}
