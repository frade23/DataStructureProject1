package client;

import core.CompressAndUncompress;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.IOException;

public class Main extends Application {
    private Stage stage;
    private File chosenFile;
    private String compressFilePath;
    private String sourceFilePath;
    private CompressAndUncompress compressAndUncompress;

    @Override
    public void start(Stage primaryStage) {
        stage = new Stage();
        stage.setTitle("高速压缩");
        initFrame();
        stage.show();
    }

    private void initFrame(){//初始化界面
        stage.getIcons().add(new Image("file:src/resource/logo.jpg"));
        MenuHBox menuHBox = new MenuHBox(this);
        stage.setMinWidth(1200);
        stage.setHeight(700);
        HBox hBox = new HBox(stage.getWidth());
        hBox.getChildren().add(menuHBox);
        hBox.setId("myHBox");
        hBox.setPadding(new Insets(20, 10, 10, 10));
        BorderPane borderPane = new BorderPane();
        borderPane.setTop(hBox);
        borderPane.setCenter(new Button("高速压缩"));
        Scene scene = new Scene(borderPane, stage.getWidth(), stage.getHeight());
        scene.getStylesheets().add("file:src/css/main.css");
        stage.setScene(scene);
    }

    public static void main(String[] args){
        launch(args);
    }

    void showChooseStage(){//压缩界面，用于选择压缩文件还是压缩文件夹
        Stage stage = new Stage();
        stage.setTitle("选择压缩类型");
        stage.getIcons().add(new Image("file:src/resource/logo.jpg"));
        stage.setResizable(false);
        stage.setHeight(500);
        stage.setWidth(500);
        Button btChooseFile = new Button("选择文件");
        Button btChooseDir = new Button("选择文件夹");
        btChooseFile.setOnMouseClicked(event -> {
            stage.close();
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("高速压缩-选择文件");
            File file = new File(System.getProperty("user.dir"));
            fileChooser.setInitialDirectory(file);
            chosenFile = fileChooser.showOpenDialog(null);
            if (chosenFile != null)
                showCompressStage(chosenFile.getAbsolutePath());
        });
        btChooseDir.setOnMouseClicked(event -> {
            stage.close();
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("高速压缩-选择文件夹");
            File file = new File(System.getProperty("user.dir"));
            directoryChooser.setInitialDirectory(file);
            chosenFile = directoryChooser.showDialog(null);
            if (chosenFile != null){
                showCompressStage(chosenFile.getAbsolutePath());
            }
        });
        Pane pane = new Pane();
        pane.getChildren().addAll(btChooseFile, btChooseDir);
        Scene scene = new Scene(pane);
        btChooseFile.setLayoutX(150);
        btChooseFile.setLayoutY(50);
        btChooseDir.setLayoutY(150);
        btChooseDir.setLayoutX(140);
        scene.getStylesheets().add("file:src/css/chooseStage.css");
        stage.setScene(scene);
        stage.show();
    }

    private void showCompressStage(String path){//展示压缩界面
        sourceFilePath = path;
        int length = 0, first = 0;
        for (int i = 0; i < path.length(); i++){
            if (path.charAt(i) == '.'){
                length = i;
            }
            else if (path.charAt(i) == '\\'){
                first = i;
            }
        }
        compressFilePath = path.substring(0, length) + ".hzip";
        if (length == 0){
            compressFilePath = path + ".hzip";
            path = path.substring(first + 1, path.length()) + ".hzip";//文件夹
        }else {
            path = path.substring(first + 1, length) + ".hzip";
        }
        Stage stage = new Stage();
        stage.setResizable(false);
        stage.setWidth(680);
        stage.setHeight(420);
        stage.setTitle("高速压缩");
        stage.getIcons().add(new Image("file:src/resource/logo.jpg"));
        HBox hBox1 = new HBox();
        HBox hBox2 = new HBox();
        HBox hBox3 = new HBox();
        hBox1.setId("myHBox");
        hBox1.setPrefHeight(110);
        hBox1.setPrefWidth(680);
        GridPane gridPane = new GridPane();
        gridPane.setVgap(30);
        gridPane.add(hBox1, 0, 0);
        gridPane.add(hBox2, 0, 1);
        gridPane.add(hBox3, 0, 2);
        Scene scene = new Scene(gridPane);
        Label label = new Label("压缩到：");
        label.setPadding(new Insets(5, 0, 5, 0));
        TextField textFieldPath = new TextField(path);
        Button btChooseDir = new Button("更换目录");
        btChooseDir.setOnMouseClicked(event -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("高速压缩-更换目录");
            chosenFile = directoryChooser.showDialog(null);
            if (chosenFile != null){
                compressFilePath = chosenFile.getAbsolutePath() + "\\" + textFieldPath.getText();//确定压缩后的路径
            }
        });
        hBox2.getChildren().addAll(label, textFieldPath, btChooseDir);
        Button btOK = new Button("确认压缩");
        btOK.setOnMouseClicked(event -> {
            compressAndUncompress = new CompressAndUncompress(compressFilePath, sourceFilePath);
            try {
                compressAndUncompress.compress();
            } catch (Exception e) {
                e.printStackTrace();
            }
            stage.close();
        });
        hBox2.setPadding(new Insets(0, 20, 0, 120));
        hBox3.setPadding(new Insets(0, 20, 0, 300));
        hBox3.getChildren().add(btOK);
        scene.getStylesheets().add("file:src/css/main.css");
        stage.setScene(scene);
        stage.show();
    }

    void uncompress() throws IOException, ClassNotFoundException{
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("高速压缩-选择解压文件");
        File file = new File(System.getProperty("user.dir"));
        fileChooser.setInitialDirectory(file);
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Zip Files", "*.hzip"));
        chosenFile = fileChooser.showOpenDialog(null);
        if (chosenFile != null){
            compressAndUncompress = new CompressAndUncompress(chosenFile.getAbsolutePath());
            compressAndUncompress.uncompress();
        }
    }
}

