package sample;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import java.io.*;
import java.text.DateFormat;
import java.util.GregorianCalendar;
import java.util.Optional;

public class Main extends Application {

    VBox container;
    Label titel, armed, activation;
    Boolean is_armed;
    Button arm,test, abrufen;
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// Grundlegendes Gerüst erstellt
    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStage.setTitle("Arduino Alarm");
        BorderPane root=new BorderPane();
        //..................................................................    Normalerweise komm Boolean vom Arduino
        is_armed=false;
        //..................................................................
        HBox top=new HBox();
        top.getChildren().add(titel=new Label("Alarmanlage:"));
        root.setTop(top);
        titel.setStyle("-fx-font-size: 28; -fx-text-fill: lightgrey; -fx-font-family: 'Arial Black'");
        top.setStyle("-fx-alignment: center; -fx-background-color: #23353f");


        root.setCenter(container=new VBox());
        container.setStyle("-fx-background-color: #23353f");
        container.setSpacing(15);

        default_settings();                                        //Container wird beschriftet
        is_on();                                                   //Beschriftung wird an Zustand angepasst

        //....................................................................  Normalerweise Ultraschallsensor
        Button einbruch=new Button("einbruch");
        root.setBottom(einbruch);
        einbruch.setOnAction(e->{
            alarm(true);
        });
        //....................................................................

        arm.setOnAction(e->{                                        //Arm aktiviert oder deaktiviert die Alarmanlage
            File f=new File("List.txt");

            FileWriter fw= null;                                    //Zustand wird in File gespeichert
            try {
                fw = new FileWriter(f,true);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            BufferedWriter bw=new BufferedWriter(fw);

            DateFormat df,dg;                                       //Datum der Zustandsänderung wird festgehalten
            GregorianCalendar now = new GregorianCalendar();
            df = DateFormat.getTimeInstance(DateFormat.SHORT);
            dg=DateFormat.getDateInstance(DateFormat.SHORT);

            if(is_armed==true){
                try {
                    if (password_check()==true){                //Sicherheitsabfrage um Alarmanlage zu deaktivieren
                        //+ an Arduino senden
                        is_armed=!is_armed;
                        is_on();                                //Beschriftung wird an den Zustand angepasst

                        try {                                                                                               //Änderung wird gespeichert
                            bw.write(dg.format(now.getTime())+"     "+df.format(now.getTime())+" Disarmed "+"\n");
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                        try {
                            bw.close();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                        Alert a=new Alert(Alert.AlertType.WARNING);         //Ausgabefenster
                        a.setContentText("Alarm ist deaktiviert");
                        a.setHeaderText("Disarmed");
                        a.showAndWait();
                    }
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                }
            }
            else{                                   //Aktivierung erfolgt ohne Sicherheitsabfrage
                is_armed=!is_armed;
                //+ an Arduino senden
                is_on();

                try {
                    bw.write(dg.format(now.getTime())+"     "+df.format(now.getTime())+" Armed "+"\n");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                try {
                    bw.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                Alert a=new Alert(Alert.AlertType.WARNING);
                a.setContentText("Alarm ist aktiviert");
                a.setHeaderText("Armed");
                a.showAndWait();
            }
        });

        test.setOnAction(e->{       //Der Test ist nur zum Alarmauslösen
            alarm(false);
        });

        abrufen.setOnAction(e->{        //Gespeicherte Daten abrufen
            try {
                if (password_check()==true){        //Sicherheitsabfrage
                    Stage secondary=new Stage();        //Neues Fenster
                    List list=new List();
                    secondary.setScene(new Scene(list, 300, 275));
                    secondary.show();
                }
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
    }
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// Container wird am Anfang gefüllt
    void default_settings(){
        container.setAlignment(Pos.CENTER);
        container.getChildren().add(armed=new Label(""));
        container.getChildren().add(activation=new Label(""));
        container.getChildren().add(arm=new Button(""));
        container.getChildren().add(test=new Button(""));
        HBox bb =new HBox();
        bb.getChildren().add(abrufen=new Button("Daten"));
        bb.setAlignment(Pos.CENTER_RIGHT);
        bb.getChildren().add(new Label("    "));
        container.getChildren().add(bb);
    }
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// Farbe der Beschriftung und Text wird an Zustand angepasst
    void is_on(){
        if (is_armed==false) {                                         //Ausgeschalten
            armed.setText("NOT Armed");
            armed.setStyle("-fx-font-size: 20; -fx-text-fill: red; -fx-font-family: 'Britannic Bold'");

            activation.setText("Time of Activation: --:--");
            activation.setStyle("-fx-font-size: 20; -fx-text-fill: red; -fx-font-family: 'Britannic Bold'");

            arm.setText("ARM");
            arm.setStyle("-fx-text-fill: green; -fx-font-size: 14");

            test.setText("Alarm testen");
            test.setStyle("-fx-text-fill: red;-fx-font-size: 14");
        } else{                                                         //Eingeschalten
            armed.setText("Armed");
            armed.setStyle("-fx-font-size: 20; -fx-text-fill: green; -fx-font-family: 'Britannic Bold'");

            DateFormat df;
            GregorianCalendar now = new GregorianCalendar();
            df = DateFormat.getTimeInstance(DateFormat.SHORT);
            activation.setText("Time of Activation: "+df.format(now.getTime()));
            activation.setStyle("-fx-font-size: 20; -fx-text-fill: green; -fx-font-family: 'Britannic Bold'");

            arm.setText("Disarm");
            arm.setStyle("-fx-text-fill: red;-fx-font-size: 14; ");


            test.setText("Alarm testen");
            test.setStyle("-fx-text-fill: green;-fx-font-size: 14");
            }
    }
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// Tonausgabe, normalerweise Nachricht an Arduino, welcher Alarm auslöst
    void alarm(Boolean real)  {
            if(is_armed!=false){
                //..........................................................................
                String musicFile = "Alarm.mp3";     // For example
                Media sound = new Media(new File(musicFile).toURI().toString());
                MediaPlayer mediaPlayer = new MediaPlayer(sound);
                mediaPlayer.play();
                //...........................................................................

                File f=new File("List.txt");
                FileWriter fw= null;
                try {
                    fw = new FileWriter(f,true);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                BufferedWriter bw=new BufferedWriter(fw);

                DateFormat df,dg;
                GregorianCalendar now = new GregorianCalendar();
                df = DateFormat.getTimeInstance(DateFormat.SHORT);
                dg=DateFormat.getDateInstance(DateFormat.SHORT);

                try {
                    if(real==true)
                        bw.write(dg.format(now.getTime())+"     "+df.format(now.getTime())+" Alarm"+"\n");
                    else
                        bw.write(dg.format(now.getTime())+"     "+df.format(now.getTime())+" Testalarm"+"\n");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                try {
                    bw.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
    }
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// Passwort wird verschlüsselt und mit einer beriets verschlüsselten Vorlage
    Boolean password_check() throws FileNotFoundException {                                                           // verglichen
        TextInputDialog dialog=new TextInputDialog();
        dialog.setTitle("Sicherheitsabfrage");
        dialog.setHeaderText("Passwort wird benötigt");
        dialog.setContentText("Passwort:");
        Optional<String> result = dialog.showAndWait();
        String s="";
        if (result.isPresent()){
            s=result.get();
        }
        s=encrypt(s);                              //eingabe wird verschlüsselt
        File f=new File("yeet.txt");     //richtiges Passwort wird aus File gelesen
        FileReader fr=new FileReader(f);
        BufferedReader br=new BufferedReader(fr);

        try {

            if(s.equals(br.readLine())==true) {    //Passwort vergleichen
                return true;
            }
            else
                return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// Die Eingabe wird verschlüsselt
    String encrypt(String s){
        String s2="";
        char c[]=s.toCharArray();           //Aus String wird ein Char Array
        for (int i=0; i<s.length();i++){
           c[i]*=(i*i+3);                    //Die Buchstaben werden basierend af der Caesarverschlüsselung
           s2+=c[i];                         //verschlüsselt und zu String zusammengeführt
        }

        //How the password looks:
        //System.out.println(s2);
        return s2;
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static void main(String[] args) {
        launch(args);
    }
}
