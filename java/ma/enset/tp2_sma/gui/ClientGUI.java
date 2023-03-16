package ma.enset.tp2_sma.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import ma.enset.tp2_sma.agents.ClientAgent;
import ma.enset.tp2_sma.controllers.ClientController;
import java.io.IOException;

public class ClientGUI extends Application {
    private static ClientAgent clientAgent;
    private static ClientController clientController;
    public ClientGUI() {
    }
    public ClientGUI(ClientAgent clientAgent) {
        ClientGUI.clientAgent = clientAgent;
    }

    public void show(){
        new Thread(() -> {
            launch();
        }).start();
    }
    @Override
    public void start(Stage stage)  {
        BorderPane borderPane = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ma/enset/tp2_sma/views/clientView.fxml"));
            borderPane = loader.load();
            ClientGUI.clientController = loader.getController();
            ClientGUI.clientController.setClientAgent(ClientGUI.clientAgent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Scene scene = new Scene(borderPane);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("Client");
        stage.show();

        // Handle the close of gui
        stage.setOnCloseRequest(e -> {
            // Stop agent
            ClientGUI.clientAgent.doDelete();
            Platform.exit();
            System.exit(0);
        });
    }
}
