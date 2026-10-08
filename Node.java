//Oliver Perez
//
//Class for a node in a binary tree
class Node<T>
{
    T data;
    Node<T> left;
    Node<T> right;

    Node(T data)
    {
        this.data = data;
        left = null;
        right = null;
    }
}
