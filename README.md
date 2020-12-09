# Wikipaths
A project for Data Structures with Anna Rafferty at Carleton College.  
Uses a graph to simulate the links in a wiki and uses queues and the breadth-first search algorithm to find the shortest path between two "articles" in the wiki, or vertices in the graph.

To see output, compile PathFinder.java, then run PathFinder.java with the following arguments:

   java PathFinder [file with vertices] [file with edges] [start vertex] [OPTIONAL intermediate vertex] [end vertex]
   
If only two vertices/article names are included as arguments, then PathFinder finds the shortest direct path between them. If the optional intermediate vertex/article
is included, PathFinder finds the shortest path between the start and end vertices that includes the intermediate vertex. 
Sample wiki articles and links are included in the files "articles.tsv" and "links.tsv".
For example, if I wanted to find the shortest path between the articles for "Helsinki" and "Orca", I would enter the command

  java PathFinder articles.tsv links.tsv Helsinki Orca

