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
import java.util.Random;

public class AStar extends Application {

    public static String filename;
    public int length = 0, width;
    public ArrayList<Path> frontier = new ArrayList<>();
    public ArrayList<Node> path = new ArrayList<>();

    public Node[][] graph;
    public boolean[][] visited;

    public Node start, end;

    @Override
    public void start(Stage primaryStage) throws Exception{
        GridPane root = new GridPane();
        Random rand = new Random();
        int size = 30;

        readMap(filename);
        findPattern();

        Color[] colors = {Color.BLACK, Color.BLUE, Color.GREEN, Color.RED};


        for(int y = 0; y < length; y++){
            for (int x = 0; x < width; x++){
                Rectangle rect = new Rectangle();

                rect.setWidth(size);
                rect.setHeight(size);
                rect.setFill(Color.GREY);

                GridPane.setRowIndex(rect, y * size);
                GridPane.setColumnIndex(rect, x * size);

                if (graph[x][y] == null){
                    rect.setFill(Color.BLACK);
                }
                else{
                    for (int i = 0; i < path.size(); i++){
                        if (path.get(i).equals(graph[x][y]))
                            rect.setFill(Color.BLUE);
                    }

                    if (graph[x][y].equalsSame(start))
                        rect.setFill(Color.GREEN);

                    if (graph[x][y].equalsSame(end))
                        rect.setFill(Color.RED);
                }

                root.getChildren().addAll(rect);
            }
        }

        Scene scene = new Scene(root, (width * size), (length * size));
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java AStar.java <map_file.txt>");
            return;
        }

        filename = args[0];
        launch(args);
    }

    public void setupNodeValues(){
        for(int y = 0; y < length; y++){
            for (int x = 0; x < width; x++) {
                if (graph[x][y] != null){
                    Node curr = graph[x][y];
                    curr.setHScore(findHScore(curr,end));
                    if (curr.equals(start)){
                        frontier.clear();
                        frontier.add(new Path(curr));
                        //visited[x][y] = true;
                    }
                }
            }
        }
    }

    public double findHScore(Node start, Node end){
        double xOffset = start.getX() - end.getX();
        double yOffset = start.getY() - end.getY();
        return  Math.sqrt(Math.pow(xOffset, 2) + Math.pow(yOffset, 2));
    }

    public void findPattern(){
        Path p = new Path(start);

        //Expand frontier
        while (!frontier.isEmpty()){
            //Sort frontier
            Collections.sort(frontier);

            //Display frontier
            System.out.println();
            System.out.println("Frontier Size: " + frontier.size());

            Path expand = frontier.get(0);
            frontier.remove(0);

            if (expand.getH() == 0){
                System.out.println("FOUND MATCH: (" + expand.getCurrent().getX() + "," + expand.getCurrent().getY() + ") with an H Score: " + expand.getH());
                path = expand.reCreatePath();
                return;
            }

            //While the expand path has neighbor nodes, expand them.
            int x = expand.getCurrent().getX();
            int y = expand.getCurrent().getY();

            //System.out.println("This expanded nodes point is: (" + x + ", " + y + ")");

            checkNeighbor(expand,x + 1, y);
            checkNeighbor(expand,x - 1, y);
            checkNeighbor(expand,x, y + 1);
            checkNeighbor(expand,x, y - 1);
        }
    }

    public void addFrontier(Path p, int x, int y) {
        frontier.add(new Path(p, graph[x][y]));
        visited[x][y] = true;
    }

    public boolean checkNeighbor(Path p, int x, int y){
        if (graph[x][y] == null) {
            //System.out.println(x + " " + y +" IS NOT TRAVERSABLE");
            return false;
        }

        if (p.visited(graph[x][y])){
            //System.out.println(x + " " + y +" IS IN THE SAME PATH");
            return false;
        }

        if (visited[x][y]){
            Path newP = new Path(p, graph[x][y]);
            for (int i = 0; i < frontier.size(); i++){
                Path frontierComp = frontier.get(i);
                if (frontierComp.getCurrent().equals(graph[x][y])){
                    if (newP.getF() < frontierComp.getF()){
                        frontier.remove(i);
                        break;
                    }
                    else{
                        return false;
                    }
                }
            }
        }

        //System.out.println(x + " " + y +" HAS BEEN ADDED");
        addFrontier(p, x, y);
        return true;
    }

    public void readMap(String map){
        try{
            int y = 0;
            String line;
            BufferedReader bw = new BufferedReader(new FileReader(map));

            while ((line = bw.readLine()) != null) {
                for (int x = 0; x < line.length(); x++) {
                    width = line.length();
                }
                length++;
            }
            bw.close();
            bw = new BufferedReader(new FileReader(map));

            graph = new Node[width][length];
            visited = new boolean[width][length];

            while ((line = bw.readLine()) != null){
                for (int x = 0; x < line.length(); x++){
                    width = line.length();
                    char c = line.charAt(x);
                    //See what the character is
                    System.out.print(c);
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
                        end = node;
                    }
                    visited[x][y] = false;
                }
                System.out.println();
                y++;
            }
            bw.close();
            setupNodeValues();
        }
        catch (Exception ex){
            System.err.println("Error: " + ex);
        }
    }
}
