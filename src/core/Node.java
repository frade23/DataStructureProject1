package core;

public class Node {
    private long element;
    private Node left, right;
    private int key = 256;//标记

    Node(){}
    Node( int key, long element){
        this.element = element;
        this.key = key;
    }

    int getKey() {
        return key;
    }

    public void setKey(char key) {
        this.key = key;
    }

    void setElement(long element) {
        this.element = element;
    }

    long getElement() {
        return element;
    }

    public void setElement(int element) {
        this.element = element;
    }

    Node getLeft() {
        return left;
    }

    void setLeft(Node left) {
        this.left = left;
    }

    Node getRight() {
        return right;
    }

    void setRight(Node right) {
        this.right = right;
    }
}
