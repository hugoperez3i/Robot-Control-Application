package app;

import java.io.IOException;
import controllers.Client;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class MainGUI extends Application {

    public static void main(String[] args)  {
        
        try {
            Client.startUp("192.168.137.1",54817);
            Client.selectMCU("Maroon");
            Client.updateCurrentPositions();

            launch();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }



    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        System.out.println("IM here");
        FXMLLoader loader = new FXMLLoader(MainGUI.class.getResource("/gui/FancyClient.fxml"));
        Parent root = loader.load();

        Image windowIcon = new Image("/img/logo.png");
        primaryStage.getIcons().add(windowIcon);	

        primaryStage.setTitle("Robot Control Panel"); 
        primaryStage.setResizable(false);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        System.out.println("Should be up");
    }
}