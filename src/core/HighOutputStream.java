package core;

import java.io.*;

class HighOutputStream{
    private FileOutputStream fileOutputStream;
    private FileOutputStream proFileOutputStream;//临时文件
    private ObjectOutputStream objectOutputStream;
    private File sourceFile, proFile;
    private HuffmanTree huffmanTree = new HuffmanTree();

    HighOutputStream(File sourceFile) throws FileNotFoundException{
        this.sourceFile = sourceFile;
        fileOutputStream = new FileOutputStream(sourceFile);
    }
    void write(byte[] bytes, int length) throws IOException {
        byte[] byteArray = huffmanTree.code(bytes, length);
        proFileOutputStream.write(byteArray);
    }

    void putNextEntry(String nextEntry, int value) throws IOException{
        if (value == 1) {
            //创建临时文件
            proFile = new File(sourceFile.getAbsolutePath().replace(sourceFile.getName(), "#" + sourceFile.getName()));
            proFileOutputStream = new FileOutputStream(proFile);
            huffmanTree.init();//初始化
            //写入了文件的内容，文件应该含有文件名（含文件夹内部相对路径）+ Huffman树+文件内容
            objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeByte(1);//写入值
            objectOutputStream.writeObject(nextEntry);//写入加密后的文件名不含绝对路径，若为文件夹，含内部绝对路径
            objectOutputStream.writeObject(huffmanTree.getCharacters());//写入字符集
        }else if (value == 0 || value == 3){
            objectOutputStream = new ObjectOutputStream(fileOutputStream);//空文件夹只需要写入类型0 文件名
            objectOutputStream.writeByte(value);
            objectOutputStream.writeObject(nextEntry);
        }
    }

    void scan(byte[] bytes, int length){
        huffmanTree.createElement(bytes, length);
    }

    void close() throws IOException{
        objectOutputStream.writeByte(2);//作为value值
        fileOutputStream.close();
    }

    void proFile2File() throws IOException{
        proFileOutputStream.close();
        objectOutputStream.writeObject(huffmanTree.getLeftNumber());//写入模8后的bit位
        objectOutputStream.writeObject(huffmanTree.getLengthCompress());//写入字节数
        byte[] bytes = new byte[2048];
        FileInputStream fileInputStream = new FileInputStream(new File(sourceFile.getAbsolutePath().replace(sourceFile.getName(), "#" + sourceFile.getName())));
        int length;
        while ((length = fileInputStream.read(bytes)) != -1)
            fileOutputStream.write(bytes, 0, length);
        fileInputStream.close();
        proFile.delete();
    }

}
