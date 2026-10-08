//Oliver Perez
//Class for binary search tree
import java.util.*;
import java.util.function.BinaryOperator;

public class BinTree<T extends Comparable<T>>
{
    private Node<T> root;

    //combiner to merge equal keys
    private final BinaryOperator<T> combiner;

    //default constructor
    public BinTree() 
    {
        this(null);
    }

    //constructor with combiner
    public BinTree(BinaryOperator<T> combiner) 
    {
        this.combiner = combiner;
    }

    public void insert(T data)
    {
        root = insertHelper(root, new Node<T>(data));
    }

    private Node<T> insertHelper(Node<T> root, Node<T> node)
    {
        T data = node.data;
        if(root == null)
        {
            return node;
        }
        int cmp = data.compareTo(root.data);
        if(cmp < 0)
        {
            root.left = insertHelper(root.left, node);
        }
        else if (cmp > 0)
        {
            root.right = insertHelper(root.right, node);
        }
        else
        {
            if (combiner != null) 
            {
                root.data = combiner.apply(root.data, data);
            } else 
            {
                root.data = data;
            }
        }
        return root;
    }

    public boolean search(T data)
    {
        return searchHelper(root, data);
    }

    private boolean searchHelper(Node<T> root, T data)
    {
        if(root == null)
        {
            return false;
        }
        int cmp = root.data.compareTo(data);
        if(cmp == 0)
        {
            return true;
        }
        else if(cmp > 0)
        {
            return searchHelper(root.left, data);
        }
        else
        {
            return searchHelper(root.right, data);
        }
    }

    //Function to return binary tree as an array list in descending order
    public ArrayList<T> toListDescending()
    {
        ArrayList<T> res = new ArrayList<>();
        toListDescendingHelper(root, res);
        return res;
    }

    //Recursive helper for toListDescending
    private void toListDescendingHelper(Node<T> node, ArrayList<T> out)
    {
        if(node == null)
            return;
        toListDescendingHelper(node.right, out);
        out.add(node.data);
        toListDescendingHelper(node.left, out);
    }
}
