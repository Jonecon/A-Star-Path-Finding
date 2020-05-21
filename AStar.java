package sample;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.stage.Stage;

import java.io.BufferedReader;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;

public class AStar extends Application {
    //Page setup
    private int length, width;

    //Parallel Grids
    private Node[][] graph;
    private boolean[][] visited;

    //Frontier and Final Path lists.
    private ArrayList<Path> frontier = new ArrayList<>();
    private ArrayList<Node> path = new ArrayList<>();

    //Start and End pointers.
    private Node start, goal;


    //Used so that we can display the Map to the user, and also the final Path.
    @Override
    public void start(Stage primaryStage) throws Exception{
        //Setup a GridPane so that we can draw all the squares aligned to the Grid we have made to store our Nodes.
        GridPane root = new GridPane();
        //Since out grid is based on the index give it a size to scale it by for displaying.
        int size = 30;

        //Parse the map into our program.
        readMap(this.getParameters().getRaw().get(0));

        //Find the shortest path in this map.
        findShortestPath();

        /*
            Go through every position on the Grid and draw on it based on it's value.
            These values could be:
                Null: Means that this position wasn't either a space, start Node or End node.
                Has a Node: Means that this is a normal Node that the user can use to find a path.
                Start Node: A normal Node that has the Start pointer linked to it. Where the pattern search will start.
                End Node: A normal Node that has the End pointer linked to it. The end goal of the pattern search.
                Path Node: A normal Node that is also linked to a node in the Path list. This the nodes that make up the shortest path.
         */
        for(int y = 0; y < length; y++){
            for (int x = 0; x < width; x++){
                //Rectangle size, colour, and position setup.
                Rectangle rect = new Rectangle();
                rect.setWidth(size);
                rect.setHeight(size);
                rect.setFill(Color.LIGHTBLUE);
                GridPane.setRowIndex(rect, y * size);
                GridPane.setColumnIndex(rect, x * size);

                //If this is a NULL Node, set it's colour to a dark blue.
                if (graph[x][y] == null){
                    rect.setFill(Color.valueOf("010114"));
                }
                //Otherwise do the rest of the checks on the Node.
                else{
                    //Go through the Path's list of Nodes.
                    for (int i = 0; i < path.size(); i++){
                        //If one of them match Make the rectangle light blue.
                        if (path.get(i).equals(graph[x][y])) {
                            rect.setFill(Color.valueOf("0f52ba"));
                            break;
                        }
                    }

                    //Check to see if the Node is a start Node, if so set it's colour to Light Green.
                    if (graph[x][y].equals(start))
                        rect.setFill(Color.LIGHTGREEN);

                    //Check to see if the Node is an end Node, if so set it's colour to a Light Red.
                    if (graph[x][y].equals(goal))
                        rect.setFill(Color.valueOf("ffc6c4"));
                }
                //Add the rectangle to the grid.
                root.getChildren().addAll(rect);
            }
        }
        //Setup the scene/display to the width and length of our array, scaled by size.
        Scene scene = new Scene(root, (width * size), (length * size));
        primaryStage.setScene(scene);
        //Display to the user.
        primaryStage.show();
    }


    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java AStar.java <map_file.txt>");
            return;
        }
        launch(args);
    }


    //Goes through the list of Nodes in our Matrix to setup their H Score.
    public void setupNodeValues(){
        //Go through every index in the Grid.
        for(int y = 0; y < length; y++){
            for (int x = 0; x < width; x++) {
                //Make sure we aren't working with a NULL Node.
                if (graph[x][y] != null){
                    //Get the Node we want to work with
                    Node curr = graph[x][y];
                    //Find it's H Score with this Node and the End node using the Euclidean Method.
                    curr.setHScore(findHScore(curr,goal));
                    //If the Node is the start Node, setup the frontier for it.
                    if (curr.equals(start)){
                        frontier.clear();
                        frontier.add(new Path(curr));
                        visited[x][y] = true;
                    }
                }
            }
        }
    }

    //Does the Euclidean Equation on the two Nodes to find what it's H Score is.
    public double findHScore(Node start, Node end){
        double xOffset = start.getX() - end.getX();
        double yOffset = start.getY() - end.getY();
        return  Math.sqrt(Math.pow(xOffset, 2) + Math.pow(yOffset, 2));
    }

    public void findShortestPath(){
        //Expand frontier
        while (!frontier.isEmpty()){
            //Sort frontier
            Collections.sort(frontier);

            //Display frontier
            System.out.println();
            System.out.println("Frontier Size: " + frontier.size());

            //Since we sorted our frontier by F Score we just pop the first Frontier Node.
            Path expand = frontier.get(0);
            frontier.remove(0);

            //Check to see if we have found the goal.
            if (expand.getH() == 0){
                System.out.println("FOUND MATCH: (" + expand.getCurrent().getX() + "," + expand.getCurrent().getY() + ") with an H Score: " + expand.getH());
                path = expand.reCreatePath();
                return;
            }

            //Find the x and y of this's paths Head Node so that we can check it's neighbors.
            int x = expand.getCurrent().getX();
            int y = expand.getCurrent().getY();

            //Checking the possible moves (up, down, right and left)
            checkNeighbor(expand,x + 1, y);
            checkNeighbor(expand,x - 1, y);
            checkNeighbor(expand,x, y + 1);
            checkNeighbor(expand,x, y - 1);
            //Will potentially add diagonals.
        }
    }

    //Creates a new Path with a Node at the given Coords, and then adds this to the frontier.
    public void addFrontier(Path p, int x, int y) {
        frontier.add(new Path(p, graph[x][y]));
        visited[x][y] = true;
    }


    /*
        Finds whether the node with the given Coords would be suitable
        to be added to the frontier by passing these checks:
            1.) Whether the Node is traversable or not.
            2.) Whether the Node is already a part of the current Path.
            3.) Whether the Node is more efficient when crossing into another Path.
     */
    public void checkNeighbor(Path p, int x, int y){
        //This position is not traversable
        if (graph[x][y] == null)
            return;

        //If this Node is in the path that it's being added to, stop it from looping back.
        if (p.visited(graph[x][y]))
            return;

        //Path pruning.
        if (visited[x][y]){
            //Create a temp Path so that we can compare the F score of the two paths.
            Path newP = new Path(p, graph[x][y]);
            //For all the Paths in the frontier.
            for (int i = 0; i < frontier.size(); i++){
                //Get each path.
                Path frontierComp = frontier.get(i);
                //If this Path's Current Node, is the same node that we created the Temp node with.
                if (frontierComp.getCurrent().equals(graph[x][y])){
                    //Find the Node with the larger F Score and discard it
                    if (newP.getF() < frontierComp.getF()){
                        //It was the node in the list, so remove that node from the list and add the new one.
                        frontier.remove(i);
                        break;
                    }
                    else{
                        //It was the new Path so stop creating this Path.
                        return;
                    }
                }
            }
        }

        //We have made it through all the checks, therefore this node should be added?
        addFrontier(p, x, y);
    }

    //Take the map from the console Argument.
    public void readMap(String map){
        try{
            //Variables init.
            int y = 0;
            length = 0;
            String line;
            BufferedReader bw = new BufferedReader(new FileReader(map));

            //Get the dimensions of this map.
            while ((line = bw.readLine()) != null) {
                width = line.length();
                length++;
            }
            //Cleanup
            bw.close();

            //Re open the file, so that we can actually read its values now.
            bw = new BufferedReader(new FileReader(map));

            //Setup the Grids's with the values we read from the when we read the map earlier.
            graph = new Node[width][length];
            visited = new boolean[width][length];

            //While there is information in this file to read.
            while ((line = bw.readLine()) != null){
                //For every character on this line.
                for (int x = 0; x < line.length(); x++){
                    //Get the individual character.
                    char c = line.charAt(x);

                    /*
                        See what character it is, we will only process three characters:
                            1.) ' ' - the space character, this is somewhere the is accessible as a path.
                            2.) 'S' - Where the Path has to start. We will set a start pointer to this Node.
                            3.) 'G' - Where the Path has to end. We will set a goal pointer to this Node.
                     */
                    if (c == ' '){
                        Node node = new Node(x, y);
                        graph[x][y] = node;
                    }else if (c == 'S'){
                        Node node = new Node(x, y);
                        graph[x][y] = node;
                        start = node;
                    }else if (c == 'G'){
                        Node node = new Node(x, y);
                        graph[x][y] = node;
                        goal = node;
                    }
                    visited[x][y] = false;
                }
                y++;
            }
            //Cleanup
            bw.close();
            //Now that we have every Node, go through and set up their H Scores.
            setupNodeValues();
        }
        catch (Exception ex){
            System.err.println("Error: " + ex);
        }
    }
}
