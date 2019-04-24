package core;

import java.util.HashMap;

class HuffmanTree {
    private final int SIZE = 4096;
    private BinaryHeap binaryHeap = new BinaryHeap();
    private int n = 0;//字符种类数
    private long[] characters = new long[256];
    private HashMap<Integer, String> map = new HashMap<>();//利用散列表储存字符及对应编码
    private Node root;
    private long lengthCompress = 0;//压缩之后的字节数
    private int leftNumber = 0;//0101... 除8取字节数后的余数
    private Node left;
    private String leftString = "";

    private void dequeue(){
        n = 0;
        binaryHeap = new BinaryHeap();
        for (int i =0; i < characters.length; i++){
            if (characters[i] != 0){
                Node node = new Node( i, characters[i]);
                binaryHeap.insert(node);
                n++;
            }
        }
    }

    void createElement(byte[] bytes, int length){//获得leaf节点，用计数的方法得其权值
        for (int i = 0; i < length; i++)
            characters[bytes[i] + 128]++;//计算每个字符的权，加上128确保不为负数
    }

    private void createHuffman(){//创造huffman树，获得root节点
        dequeue();
        for (int i = 0; i < n -1; i++){
            Node temp = new Node();
            Node leftNode = binaryHeap.deleteMin();
            temp.setLeft(leftNode);
            Node rightNode = binaryHeap.deleteMin();
            temp.setRight(rightNode);
            temp.setElement(leftNode.getElement() + rightNode.getElement());
            binaryHeap.insert(temp);
        }
        root = binaryHeap.deleteMin();
    }

    byte[] code(byte[] bytes, int length){//编码字节数组
        StringBuilder value = new StringBuilder();
        for (int i = 0; i < length; i++)
            value.append(map.get(bytes[i] + 128));
        return string2Bytes(value.toString(), length);
    }

    private byte[] string2Bytes(String string, int length){//将二进制字符串转化为byte数组
        string += leftString;
        int strLength = string.length();
        int i = strLength/8;
        int j = strLength%8;
        int k = 0;
        if (j > 0 && length < SIZE)
            k = 1;
        byte[] number = new byte[i + k];
        for (int m = 0; m < i; m++)
            number[m] = (byte)Integer.parseInt(string.substring(m*8, m*8 + 8), 2);
        leftString = string.substring(i*8, strLength);
        if (j > 0 && length < SIZE){
            leftNumber = strLength - i*8;
            number[i] = (byte) Integer.parseInt(string.substring(i*8, strLength), 2);
        }
        lengthCompress += i+ k ;
        return number;
    }

    void setHuffmanTreeNodes(Object binaryTree){//解压
        characters = (long[]) binaryTree;
        dequeue();
        createHuffman();
    }

    //解码
    Object[] decode(byte[] bytes, long length){
        lengthCompress -= length;
        Node temp = root;
        if (left != null){
            temp = left;
        }
        byte[] bytes1 = new byte[SIZE];
        int k = 0;
        for (int i = 0; i < length; i++){
            byte oneByte = bytes[i];
            if (lengthCompress == 0 && i == length-1 && leftNumber != 0){
                //lengthCompress等于0表示读到最后一个字节
                for (int m = 9 - leftNumber; m <= 8; m++){
                    assert temp != null;
                    if (getBit(m, oneByte) == 0 && temp.getLeft() != null){//是0左遍历
                        temp = temp.getLeft();
                    }else if (getBit(m, oneByte) == 1 && temp.getRight() != null){//是1右遍历
                        temp = temp.getRight();
                    }else {
                        if (k == bytes1.length){
                            bytes1 = enlarge(bytes1.length * 2, bytes1);
                        }
                        bytes1[k++] = (byte)(temp.getKey() - 128);
                        left = null;
                    }
                }
            }else {
                for (int m = 1; m <= 8;m++){
                    assert temp != null;
                    if (getBit(m, oneByte) == 0 && temp.getLeft() != null){//是0左遍历
                        temp = temp.getLeft();
                    }else if (getBit(m, oneByte) == 1 && temp.getRight() != null){//是1右遍历
                        temp = temp.getRight();
                    }else {
                        if (k == bytes1.length){
                            bytes1 = enlarge(bytes1.length * 2, bytes1);
                        }
                        bytes1[k++] = (byte)(temp.getKey() - 128);
                        temp = getBit(m, oneByte) == 0? root.getLeft() : root.getRight();
                    }
                    if (i == length - 1 && temp.getKey() == 256 && m == 8){
                        left = temp;
                    }else if (i == length -1 && m == 8){
                        if (k == bytes1.length){
                            bytes1 = enlarge(bytes1.length * 2, bytes1);
                        }
                        bytes1[k++] = (byte)(temp.getKey() - 128);
                        left = null;
                    }
                }
            }
        }
        return new Object[]{bytes1, k};
    }

    private void creativeHuffmanNode(Node node, String string){
        if (node.getKey() != 256){
            map.put(node.getKey(), string);
            return;
        }
        if (node.getLeft() != null){
            creativeHuffmanNode(node.getLeft(), string + "0");
        }
        if (node.getRight() != null){
            creativeHuffmanNode(node.getRight(), string + "1");
        }
    }

    private int getBit(int index, byte b){
        return (b & (1<<8-index)) >> (8-index);
    }

    private byte[] enlarge(int length, byte[] bytes){
        byte[] newItems = new byte[length];
        System.arraycopy(bytes, 0, newItems, 0, bytes.length);
        return newItems;
    }

    long[] getCharacters() {
        return characters;
    }


    long getLengthCompress() {
        return lengthCompress;
    }

    void setLengthCompress(long lengthCompress) {
        this.lengthCompress = lengthCompress;
    }

    int getLeftNumber() {
        return leftNumber;
    }

    void setLeftNumber(int leftNumber) {
        this.leftNumber = leftNumber;
    }

    void init(){
        lengthCompress = 0;
        leftNumber = 0;
        createHuffman();
        creativeHuffmanNode(root, "");
    }

    void emptyLeft(){
        left = null;
    }
}
