package core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class CompressAndUncompress {
    private String compressFileName;
    private String sourceFileName;//
    private int SIZE = 4096;

    public CompressAndUncompress(String compressFileName, String sourceFileName){
        this.compressFileName = compressFileName;
        this.sourceFileName = sourceFileName;
    }

    public CompressAndUncompress(String compressFileName){
        this.compressFileName = compressFileName;
    }

    public void compress() throws Exception {//压缩文件
        File sourceFile = new File(sourceFileName);//创建文件句柄
        HighOutputStream highOutputStream = new HighOutputStream(new File(compressFileName));
        long startTime1 = System.currentTimeMillis();
        compress(highOutputStream, sourceFile, sourceFile.getName());//压缩文件
        highOutputStream.close();
        long endTime = System.currentTimeMillis();
        System.out.println("压缩时间" + (endTime - startTime1) + " ms");
    }

    private void compress(HighOutputStream highOutputStream, File sourceFile, String str) throws Exception{
        if (sourceFile.isDirectory()){//如果path是文件夹
            //取出文件夹中的文件
            File[] files = sourceFile.listFiles();

            assert files != null;
            if (files.length == 0){//如果文件夹为空，则需在目的地zip文件中写入一个目录进入点
                highOutputStream.putNextEntry(str + "\\", 0);//0表示空文件夹
            }
            else {//如果文件夹不为空，则递归调用compress，文件夹中的每一个文件（或文件夹）进行压缩
                for (File file : files){
                    compress(highOutputStream, file, str + "\\" + file.getName());
                }
            }
        }
        else {//如果不是目录（文件夹），而是文件，则先写入目录进入点，之后将文件写入zip文件中
            //目录进入点的名字是文件在压缩文件中的路径
            if (sourceFile.length() == 0){
                highOutputStream.putNextEntry(str, 3);
                return;
            }
            FileInputStream fileInputStream = new FileInputStream(sourceFile);

            int length;
            byte[] bytes = new byte[SIZE];
            while ((length = fileInputStream.read(bytes)) != -1){
                highOutputStream.scan(bytes, length);
            }
            highOutputStream.putNextEntry(str, 1);//建立一个目录进入点，1表示文件
            fileInputStream.close();
            FileInputStream fileInputStream1 = new FileInputStream(sourceFile);
            while ((length = fileInputStream1.read(bytes)) != -1){
                highOutputStream.write(bytes, length);//写入文件
            }
            highOutputStream.proFile2File();//写入文件头，临时文件到zip里面
            fileInputStream1.close();
        }
    }

    public void uncompress() throws IOException {//解压文件
        File compressFile = new File(compressFileName);//创建文件句柄
        HighInputStream highInputStream = new HighInputStream(compressFile);
        long startTime = System.currentTimeMillis();
        uncompress(highInputStream);//解压compress文件
        long endTime = System.currentTimeMillis();
        System.out.println("解压时间" + (endTime - startTime) + " ms");
    }

    private void uncompress(HighInputStream highInputStream) throws IOException{
        highInputStream.read();
    }

}
