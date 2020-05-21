package sample;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Path implements Comparable{
    private Node _current;
    private Path _parent;
    private double _fScore, _hScore;
    private int _cScore;


    public Path(Node start){
        _current = start;
        _fScore = start.getF();
        _cScore = start.getC();
        _hScore = start.getH();

        //Start of the path
        _parent = null;
    }

    public Path(Path previous, Node current){
        _current = current;
        _cScore = previous.getC() + current.getC();
        _hScore = current.getH();
        _fScore = _cScore + _hScore;
        _parent = previous;
    }

    public Node getCurrent(){
        return _current;
    }

    public int getC(){
        return _cScore;
    }

    public double getH(){
        return _hScore;
    }

    public double getF() {return _fScore;}

    public Path getParent(){return _parent;}

    public boolean visited(Node n){
        if (_current.equals(n))
            return true;

        Path next = _parent;
        while (next != null){
            Node curr = next.getCurrent();
            if (curr.equals(n))
                return true;
            next = next.getParent();
        }
        return false;
    }

    public ArrayList<Node> reCreatePath(){
        ArrayList<Node> result = new ArrayList<>();
        result.add(_current);
        Path next = _parent;
        while(next != null){
            result.add(next.getCurrent());
            next = next.getParent();
        }
        return result;
    }


    @Override
    public int compareTo(Object o) {
        Path p = (Path)o;
        double comp =(p.getF());
        /* For Ascending order*/

        if (this._fScore > comp)
            return 1;
        else if (this._fScore < comp)
            return -1;
        else
            return 0;
    }
}
