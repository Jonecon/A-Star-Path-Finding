package sample;

public class Node {
    //Private variables.
    private int _x, _y, _cScore;
    private double _hScore;

    //Creates a node at the given x and y Coords.
    public Node (int x, int y){
        _x = x;
        _y = y;
        _cScore = 1;
    }

    //Returns the X coord of this Node.
    public int getX(){
        return _x;
    }

    //Returns teh Y coord of this Node.
    public int getY(){
        return _y;
    }

    //Returns the H Value of this node.
    public double getH(){
        return _hScore;
    }

    //Returns the C Value of this Node.
    public int getC(){return _cScore;}

    //Sets this variables H Score.
    public void setHScore(double score){
        _hScore = score;
    }
}
