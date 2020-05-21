package sample;

import java.util.ArrayList;

public class Path implements Comparable{
    //Private variables.
    private Node _current;
    private Path _parent;
    private double _fScore, _hScore;
    private int _cScore;

    //Creates a start of a Path linked list.
    public Path(Node start){
        //Sets up the start of this Path to have the same values as it's given Node.
        _current = start;
        _cScore = start.getC();
        _hScore = start.getH();
        _fScore = _cScore + _hScore;

        //Start of the path
        _parent = null;
    }

    //Adds to the linked path list, using the Path object that it came from, and the Node it wants to add.
    public Path(Path previous, Node current){
        //Sets up the values of this Path to be calculated based of where it has come from.
        _current = current;
        _cScore = previous.getC() + current.getC();
        _hScore = current.getH();
        _fScore = _cScore + _hScore;
        //Sets the parent of this object the the previous path.
        _parent = previous;
    }

    //Returns the current Node linked to this part of the path.
    public Node getCurrent(){
        return _current;
    }

    //Returns the C Value that it took to create this path.
    public int getC(){
        return _cScore;
    }

    //Returns this paths Node's H Score.
    public double getH(){
        return _hScore;
    }

    //Returns this paths F Score
    public double getF() {return _fScore;}

    //Returns the Path this node is linked to.
    public Path getParent(){return _parent;}

    /*
    Check if the given node is already in this path.
    This takes care of a path looping back on itself.
    */
    public boolean visited(Node n){
        //If it's this node, return that it's a part of the path/
        if (_current.equals(n))
            return true;

        //Get the next Path node to go to.
        Path next = _parent;
        //While this path node isn't null.
        while (next != null){
            //Get it's Current Node to check.
            Node curr = next.getCurrent();
            //If it's the node we're comparing return true.
            if (curr.equals(n))
                return true;
            //Otherwise check the next Parent Node
            next = next.getParent();
        }
        //Loop found nothing, return that the node is not in the list.
        return false;
    }

    //Follow the path's back to the start, while adding each current to the return list.
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

    //Make it so that we can order this list, in ascending order.
    @Override
    public int compareTo(Object o) {
        //Convert this object into a Path.
        Path p = (Path)o;
        //Get the value we want to order it based on (F Score)
        double comp =(p.getF());

        //If this object's score is larger
        if (this._fScore > comp)
            return 1;
        //If this objects score is less
        else if (this._fScore < comp)
            return -1;
        else
        //Otherwise they're the same.
            return 0;
    }
}
