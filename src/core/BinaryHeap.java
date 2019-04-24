package core;

import java.nio.BufferUnderflowException;

public class BinaryHeap {
    private int capacity = 100;
    private int currentSize = 0;

    private Node[] items = new Node[capacity];

    private int parent(int i){
        return (i -1)/2;
    }

    private int left(int i){
        return 2*i+1;
    }

    private int right(int i){
        return 2*i + 2;
    }

    BinaryHeap(){}

    //create a BinaryHeap
    BinaryHeap(int capacity){
        currentSize = 0;
        items = new Node[capacity + 1];
    }

    //insert a element into the heap
    void insert(Node node){
        if (currentSize == capacity){
            enlarge(capacity*2);
            capacity *= 2;
        }

        items[currentSize++] = node;
        int index = currentSize - 1;
        while (index > 0 && items[parent(index)].getElement() > items[index].getElement()){
            Node temp = items[index];
            items[index] = items[parent(index)];
            items[parent(index)] = temp;
            index = parent(index);
        }
    }

    //return the minimum element
    private Node findMin(){
        if (isEmpty())
            throw new Error("heap underflow");
        return items[0];
    }

    //remove and return the minimum element
    Node deleteMin(){
        if (isEmpty())
            throw new Error("heap underflow");

        Node minItem = items[0];
        items[0] = items[currentSize-1];
        items[currentSize - 1] = null;
        currentSize--;
        if (currentSize != 1)
            minHeapify(0);
        return minItem;
    }

    //judge whether the heap is empty
    private boolean isEmpty(){
        return currentSize==0;
    }

    //remove all elements
    public void makeEmpty() throws BufferUnderflowException{
        currentSize = 0;
    }

    private void enlarge(int length){
        Node[] newItems = new Node[length];
        System.arraycopy(items, 0, newItems, 0, items.length);
        items = newItems;
    }

    private void minHeapify(int i){
        int left = left(i);
        int right = right(i);
        int min = i;

        if (left < currentSize && items[left].getElement() < items[min].getElement())
            min = left;
        if (right < currentSize && items[right].getElement() < items[min].getElement())
            min = right;
        if (min != i){
            Node temp = items[i];
            items[i] = items[min];
            items[min]= temp;
            minHeapify(min);
        }

    }

    private void buildHeap(){  //建堆
        for(int i = currentSize/2 + 1; i >= 0; i--)
            //从最后一个儿子开始倒着往前下溯
            minHeapify(i);
    }
}
