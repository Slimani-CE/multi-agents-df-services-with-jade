package ma.enset.tp2_sma.gui;

import jade.gui.GuiEvent;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import ma.enset.tp2_sma.agents.ServerAgent;
import ma.enset.tp2_sma.controllers.ServerController;

import java.io.IOException;

public class ServerGUI extends Application {

    private static ServerAgent serverAgent;
    private static ServerController serverController;

    public ServerGUI() {
    }
    public ServerGUI(ServerAgent serverAgent) {
        ServerGUI.serverAgent = serverAgent;
    }

    public void show(){
        new Thread(() -> {
            launch();
        }).start();
    }

    @Override
    public void start(Stage stage) {
        BorderPane borderPane = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ma/enset/tp2_sma/views/serverView.fxml"));
            borderPane = loader.load();
            ServerGUI.serverController = loader.getController();
            ServerGUI.serverController.setServerAgent(ServerGUI.serverAgent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Scene scene = new Scene(borderPane);
        stage.setScene(scene);
        stage.setTitle("Server");
        stage.setResizable(false);
        stage.show();

        // Handle the close of gui
        stage.setOnCloseRequest(e -> {
            // Stop agent
            ServerGUI.serverAgent.doDelete();
            Platform.exit();
            System.exit(0);
        });
    }
}
