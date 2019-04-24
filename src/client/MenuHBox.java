package client;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.io.IOException;


class MenuHBox extends HBox {
    MenuHBox(Main menu){
        super();
        Button btCompress = new Button("压缩");
        btCompress.setGraphic(new ImageView(new Image("file:src/resource/compress.png")));
        btCompress.setOnMouseClicked(event -> {
            menu.showChooseStage();
        });
        Button btUncompress = new Button("解压");
        btUncompress.setGraphic(new ImageView(new Image("file:src/resource/uncompress.png")));
        btUncompress.setOnMouseClicked(event -> {
            try {
                menu.uncompress();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        });
        Button btDelete = new Button("删除");
        btDelete.setGraphic(new ImageView(new Image("file:src/resource/delete.png")));
        Button btPassword = new Button("密码");
        btPassword.setGraphic(new ImageView(new Image("file:src/resource/pwd.png")));
        Button btAutoUncompress = new Button("自解压");
        btAutoUncompress.setGraphic(new ImageView(new Image("file:src/resource/autocompress.png")));
        Button btKit = new Button("工具箱");
        btKit.setGraphic(new ImageView(new Image("file:src/resource/kit.png")));
        this.getChildren().addAll(btCompress,btUncompress, btDelete, btPassword, btAutoUncompress, btKit);
    }

}
