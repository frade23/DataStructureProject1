package core;

import java.io.*;

public class HighInputStream {
    private FileInputStream fileInputStream;
    private FileOutputStream fileOutputStream;
    private ObjectInputStream objectInputStream;
    private File sourceFile;
    private HuffmanTree huffmanTree = new HuffmanTree();
    private long length = 0;//
    private final int SIZE = 4096;
    private int value;

    HighInputStream(File sourceFile) throws IOException{//构造方法
        this.sourceFile = sourceFile;
        fileInputStream = new FileInputStream(sourceFile);
    }

    private boolean readPre() throws IOException, ClassNotFoundException{//预读文件头,解压时构造目录结构
        try{
            objectInputStream = new ObjectInputStream(fileInputStream);
        }catch (EOFException ignored){


        }
        int myValue;
        Object object;
        try {
            myValue = objectInputStream.readByte();
            if (myValue != 2){
                if (myValue == 1){//如果是file
                    value = 1;
                    object = objectInputStream.readObject();
                    String nextEntry = (String) object;
                    object = objectInputStream.readObject();//读出字符集
                    huffmanTree.setHuffmanTreeNodes(object);//创建哈夫曼树
                    int leftNum = (Integer) objectInputStream.readObject();//读出对8取模后bit位数
                    huffmanTree.setLeftNumber(leftNum);
                    length = (Long) objectInputStream.readObject();//读出byte数
                    huffmanTree.setLengthCompress(length);
                    String fileName = uncompress2CurrentFilePath(nextEntry);
                    checkPath(fileName);//检查文件夹是否创建成功
                    File currentDir = new File(fileName);//解压到当前的目录下
                    fileOutputStream = new FileOutputStream(currentDir);
                    huffmanTree.emptyLeft();
                }else if (myValue == 0){//如果是空文件夹
                    value = 0;
                    object = objectInputStream.readObject();
                    String nextEntry = (String) object;
                    String  fileName = uncompress2CurrentFilePath(nextEntry);
                    checkPath(fileName + "\\");//检查文件夹是否创建成功
                }else {
                    value = 3;
                    object = objectInputStream.readObject();
                    String nextEntry = (String) object;
                    String  fileName = uncompress2CurrentFilePath(nextEntry);
                    checkPath(fileName);//检查文件夹是否创建成功
                    File emptyFile = new File(fileName);
                    if(emptyFile.exists()){
                        emptyFile.delete();
                    }
                    emptyFile.createNewFile();
                }
                return true;
            }else {
                return false;
            }
        }catch (EOFException exception){
            objectInputStream.close();
            fileInputStream.close();
            return false;
        }
    }

    void read() throws IOException{//读取文件
        try {
            while (readPre()){
                if (value == 0 || value ==3){
                    continue;//如果是文件夹或者空文件，跳过文件夹或空文件，读其他数据
                }
                while (length > 0){
                    byte[] bytes;
                    if (length > SIZE) {
                        bytes = new byte[SIZE];
                    }
                    else{
                        bytes = new byte[(int) length];
                    }
                    int byteLength = fileInputStream.read(bytes);
                    Object object[] = huffmanTree.decode(bytes, byteLength);
                    fileOutputStream.write((byte[])object[0] ,0,(int) object[1]);
                    length -= bytes.length;
                }
                fileOutputStream.close();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    public void close() throws IOException{
        fileInputStream.close();
        objectInputStream.close();
    }

    private String uncompress2CurrentFilePath(String prePath){//解压缩到现有文件路径
        return sourceFile.getAbsolutePath().replaceAll(sourceFile.getName(), "") + prePath;
    }


    private void checkPath(String path){//检查文件夹是否创建成功
        String basis = sourceFile.getAbsolutePath().replace(sourceFile.getName(), "");
        int basisLength = basis.length();
        for (int i = basisLength; i < path.length(); i++){
            if (path.charAt(i) == '\\'){
                File file = new File(path.substring(0, i));
                if (!file.exists()){
                    file.mkdir();
                }
            }
        }
    }
}


