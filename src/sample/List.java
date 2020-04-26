package sample;

import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class List extends ScrollPane {
    List() throws IOException {
        VBox container=new VBox();
        File f=new File("List.txt");
        FileReader fr=new FileReader(f);
        BufferedReader br=new BufferedReader(fr);
        this.setContent(container);
        String c;
        while((c=br.readLine())!=null){
            HBox box=new HBox();
            box.getChildren().add(new Label(""+c));
            container.getChildren().add(box);
        }
    }
}
