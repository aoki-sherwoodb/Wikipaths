import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Deque;
import java.util.ArrayDeque;

/**
* A class that represents an object that finds the shortest path 
* between two wiki articles, with or without specification of 
* an intermediate article.
*/
public class PathFinder {
  private Map<String, Integer> vertexMap = new HashMap<String, Integer>();
  private Map<Integer, String> vertexNames = new HashMap<Integer, String>();
  private MysteryUnweightedGraphImplementation articles = new MysteryUnweightedGraphImplementation();
  /**
  * Constructs a PathFinder that represents the graph with nodes (vertices) specified as in
  * nodeFile and edges specified as in edgeFile.
  * @param nodeFile name of the file with the node names
  * @param edgeFile name of the file with the edge names
  */
  public PathFinder(String nodeFile, String edgeFile) {
    load(nodeFile, edgeFile);
  }

  public void load(String nodeFile, String edgeFile) {
  
    try {
      File nodes = new File(nodeFile);
      File edges = new File(edgeFile);
      Scanner scanner;
      String input;
      String[] edge;
      int vertexID;

      for (int i = 0; i < 2; i++) {
        if (i == 0) {
          scanner = new Scanner(nodes);
        } else {
          scanner = new Scanner(edges);
        }
        while (scanner.hasNextLine()) {
          input = scanner.nextLine();

          if (input.length() > 0 && 
          !input.substring(0, 1).equals("#")) {
            if (i == 0) {
              //First, add all vertices
              vertexID = articles.addVertex();
              vertexMap.put(input, vertexID);
              vertexNames.put(vertexID, input);
            } else {
              //Next, fill in the edges
              edge = input.split("\\t");
              articles.addEdge(vertexMap.get(edge[0]), vertexMap.get(edge[1]));
            }
          }
        }
      }
    } catch (FileNotFoundException e) {
      System.out.println("Sorry, the file name you entered was invalid.");
      System.exit(0);
    }
  }
  
  /**
  * Returns the length of the shortest path from node1 to node2. If no path exists,
  * returns -1. If the two nodes are the same, the path length is 0.
  * @param node1 name of the starting article node
  * @param node2 name of the ending article node
  * @return length of shortest path
  */
  public int getShortestPathLength(String node1, String node2) {
    List<String> shortestPath = getShortestPath(node1, node2);
    if (shortestPath.size() > 0) {
      return shortestPath.size() - 1;
    } else {
      return -1;
    }
  }
  
  /**
  * Returns a shortest path from node1 to node2, represented as list that has node1 at
  * position 0, node2 in the final position, and the names of each node on the path
  * (in order) in between. If the two nodes are the same, then the "path" is just a
  * single node. If no path exists, returns an empty list.
  * @param node1 name of the starting article node
  * @param node2 name of the ending article node
  * @return list of the names of nodes on the shortest path
  */
  public List<String> getShortestPath(String node1, String node2) {
    
    List<Integer> visitedList = new ArrayList<Integer>();
    List<String> pathList = new ArrayList<String>();
    Deque<Integer> vertexQueue = new ArrayDeque<Integer>();
    //Stores previous vertex at index = current vertex
    Integer[] backwardsLinks = new Integer[articles.numVerts()]; 
    int startVertex = 0;
    int endVertex = 0;
    int frontVertex = -1;
    boolean foundTarget = false;
    
    try {
      startVertex = vertexMap.get(node1);
      endVertex = vertexMap.get(node2);
    } catch (NullPointerException e) {
      System.out.println("One or more article names could not be found. Please try again.");
      System.exit(0);
    } 

    visitedList.add(startVertex);
    vertexQueue.add(startVertex);
    backwardsLinks[startVertex] = -1;
    //BFS
    while (!foundTarget && !vertexQueue.isEmpty()) {
      frontVertex = vertexQueue.poll();
      for (int neighbor : articles.getNeighbors(frontVertex)) {
        if (!visitedList.contains(neighbor)) {
          visitedList.add(neighbor);
          vertexQueue.add(neighbor);
          backwardsLinks[neighbor] = frontVertex;
          if (neighbor == endVertex) {
            foundTarget = true;
          }
        }
      }
    }
    if (!visitedList.contains(endVertex)) {
      return pathList;
    }
    //Traversing backwards along the path from the target node
    int prevVertex = endVertex;
    while (prevVertex != -1) {
      pathList.add(0, vertexNames.get(prevVertex));
      prevVertex = backwardsLinks[prevVertex];
    }

    return pathList;
  }
  
  /**
  * Returns a shortest path from node1 to node2 that includes the node intermediateNode.
  * This may not be the absolute shortest path between node1 and node2, but should be 
  * a shortest path given the constraint that intermediateNodeAppears in the path. If all
  * three nodes are the same, the "path" is just a single node.  If no path exists, returns
  * an empty list.
  * @param node1 name of the starting article node
  * @param node2 name of the ending article node
  * @return list that has node1 at position 0, node2 in the final position, and the names of each node 
  *      on the path (in order) in between. 
  */
  public List<String> getShortestPath(String node1, String intermediateNode, String node2) {
    List<String> segment1 = getShortestPath(node1, intermediateNode);
    List<String> segment2 = getShortestPath(intermediateNode, node2);
    //If part of the path is missing, there is no path
    if (segment1.size() == 0) {
      return segment1;
    } else if (segment2.size() == 0) {
      return segment2;
    }
    //Combining the paths
    for (int i = 1; i < segment2.size(); i++) {
      
      segment1.add(segment2.get(i));
    }
    return segment1;
  }
  
  /**
  * Prints the @param path.
  */
  public void printPath(List<String> path) {
    for (int i = 0; i < path.size(); i++) {
      if (i < path.size() - 1) {
        System.out.print(path.get(i) + " --> ");
      } else {
        System.out.println(path.get(i));
      }
      
    }
  }

  public static void main(String[] args) {
    if (args.length != 4 && args.length != 5) {
      System.out.println("USAGE: the commandline arguments should be as follows: ");
      System.out.println("1. The file containing the articles");
      System.out.println("2. The file containing the links between articles");
      System.out.println("3. The name of the starting article");
      System.out.println("4. Either the end article, or if desired, an intermediate article which the path must also go through");
      System.out.println("5. Optional, if an intermediate article was entered as 4, then this should be the end article");
    } else {
    
      PathFinder pathFinder = new PathFinder(args[0], args[1]);
      List<String> shortestPath = new ArrayList<String>();
      int pathLength;

      if (args.length == 4) {
        
        pathLength = 
        pathFinder.getShortestPathLength(args[2], args[3]);

        if (pathLength != -1) {
          System.out.println("Shortest path from " + args[2] + " to " + args[3] + ", length = " + pathLength);
          pathFinder.printPath(pathFinder.getShortestPath(args[2], args[3]));
        } else {
          System.out.println("No path found between " + args[2] + " and " + args[3]);
        }
        
      } else {
        
        int path1 = 
        pathFinder.getShortestPathLength(args[2], args[3]);
        int path2 = 
        pathFinder.getShortestPathLength(args[3], args[4]);
        pathLength = path1 + path2;

        if (path1 != -1 && path2 != -1) {
          System.out.println("Shortest path from " + args[2] + " to " + args[4] + " through " + args[3] + ", length = " + pathLength);
          pathFinder.printPath(pathFinder.getShortestPath(args[2], args[3], args[4]));
        } else {
          System.out.println("No path found between " + args[2] + " and " + args[3] + " that passes through " + args[4]);
        } 
      }
    }
    System.exit(0);
  }
}
