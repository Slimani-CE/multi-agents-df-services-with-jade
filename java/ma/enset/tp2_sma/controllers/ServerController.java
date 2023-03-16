package ma.enset.tp2_sma.controllers;

import jade.core.AID;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import ma.enset.tp2_sma.agents.ServerAgent;

import java.net.URL;
import java.util.ResourceBundle;

public class ServerController implements Initializable {

    // -> This block is reserved for the FXML components
    @FXML
    private Button addBtn;
    @FXML
    private TextField typeField;
    @FXML
    private TextField descreptionField;
    @FXML
    private ListView listView;
    // <- End of the FXML components block

    // -> This block is reserved for other variables
    private ObservableList<String> observableList = FXCollections.observableArrayList();
    private static ServerAgent serverAgent;
    // <- End of the other variables block
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize the ServerController instance in the agent class
        serverAgent.setServerController(this);

        // Set the list view
        listView.setItems(observableList);

        // Add event listener to the add button
        addBtn.setOnAction(event -> {
            String type = typeField.getText();
            String descreption = descreptionField.getText();
            if (type.isEmpty() || descreption.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Error");
                alert.setContentText("Please fill all fields");
                alert.showAndWait();
            } else {
                // Show the message in the list view
                showMessage("Type: " + type + " Name: " + descreption);
                // Send inputs to server agent
                GuiEvent guiEvent = new GuiEvent(this, 1);
                guiEvent.addParameter(type);
                guiEvent.addParameter(descreption);
                ServerController.serverAgent.onGuiEvent(guiEvent);
                typeField.setText("");
                descreptionField.setText("");
            }
        });
    }
    public void showMessage(String message) {
        Platform.runLater(() -> {
            observableList.add(message);
        });
    }

    public void displayProposal(ACLMessage msg){
        // Display and wait for confirmation
        // If the user accept the price, send confirmation to the client agent
        Platform.runLater(() -> {
            double priceProposed = Double.parseDouble(msg.getContent().split(":")[1]);
            AID client = msg.getSender();
            int serviceId = Integer.parseInt(msg.getContent().split(":")[0]);
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setHeaderText("Confirmation");
            alert.setContentText("Do you accept the price proposed: " + priceProposed);
            alert.showAndWait();
            if (alert.getResult().getText().equals("OK")) {
                GuiEvent guiEvent = new GuiEvent(this, 2);
                guiEvent.addParameter(client);
                guiEvent.addParameter(serviceId);
                ServerController.serverAgent.onGuiEvent(guiEvent);
            }else{
                GuiEvent guiEvent = new GuiEvent(this, 3);
                guiEvent.addParameter(client);
                ServerController.serverAgent.onGuiEvent(guiEvent);
            }
        });
    }

    public void setServerAgent(ServerAgent serverAgent) {
        ServerController.serverAgent = serverAgent;
    }
}
