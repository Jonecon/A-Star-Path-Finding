package sample;

public class Node {
    private int _x, _y, _cScore;
    private double _fScore, _hScore;
    private Node parent;

    public Node (int x, int y){
        _x = x;
        _y = y;
        _cScore = 1;
    }

    public int getX(){
        return _x;
    }

    public int getY(){
        return _y;
    }

    public double getF(){
        return _fScore;
    }

    public double getH(){
        return _hScore;
    }

    public int getC(){return _cScore;}

    public void setPosition(int x, int y){
        _x = x;
        _y = y;
    }

    public void setHScore(double score){
        _hScore = score;
        _fScore = _hScore + _cScore;
    }

    public void setCScore(int score){
        _cScore = score;
        _fScore = _hScore + _cScore;
    }

    //If the two nodes are in the same position, they're the same node.
    public boolean equalsSame(Node one){
        if (one.getX() == this.getX() && one.getY() == this.getY()){
            return true;
        }
        return false;
    }

    //Checks to see if the node is
    public boolean equalsSame(int x, int y){
        return false;
    }
}
