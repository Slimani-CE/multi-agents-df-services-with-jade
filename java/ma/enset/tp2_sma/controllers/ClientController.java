package ma.enset.tp2_sma.controllers;

import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import ma.enset.tp2_sma.agents.ClientAgent;
import ma.enset.tp2_sma.entities.Service;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ClientController implements Initializable {

    // -> This block is reserved for the FXML components
    @FXML
    private TextField searchField;
    @FXML
    private ListView listView;
    @FXML
    private Button refreshBtn;
    @FXML
    private Label nameLabel;
    @FXML
    private Label typeLabel;
    @FXML
    private Label buyerLabel;
    @FXML
    private HBox footer;
    @FXML
    private Label serviceStatus;
    @FXML
    private TextField proposeField;
    @FXML
    private Button sendProposalBtn;
    @FXML
    private Button closeFooterBtn;
    @FXML
    private Button acceptBtn;
    // <- End of the FXML components block

    // -> This block is reserved for other variables
    ObservableList<String> observableList = FXCollections.observableArrayList();
    private ArrayList<Service> services = new ArrayList<>();
    private static ClientAgent clientAgent;
    // <- End of the other variables block
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //  Initialize the ClientController instance in the agent class
        clientAgent.setClientController(this);

        //  Set the list view
        listView.setItems(observableList);

        //  Add event listener to the search field
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            GuiEvent guiEvent = new GuiEvent(this, 1);
            guiEvent.addParameter(newValue);
            clientAgent.onGuiEvent(guiEvent);
        });

        // Add event listener to the refresh button
        refreshBtn.setOnAction(event -> {
            GuiEvent guiEvent = new GuiEvent(this, 2);
            clientAgent.onGuiEvent(guiEvent);
        });

        // Add event listener to the list view
        listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            int index = observableList.indexOf(newValue);
            System.out.println(index);
            Service service = services.get(index);
            nameLabel.setText(service.getService().getName());
            typeLabel.setText(service.getService().getType());
            buyerLabel.setText(service.getBuyer().getLocalName());
            footer.setVisible(true);
            if(service.isPurchased()){
                serviceStatus.setText("Purchased");
                proposeField.setVisible(false);
                sendProposalBtn.setVisible(false);
                acceptBtn.setVisible(false);
            } else if(service.isBuyerAgreed()){
                serviceStatus.setText("Buyer agreed");
                acceptBtn.setVisible(true);
            } else if(service.isProposed()){
                serviceStatus.setText("Waiting for buyer");
                proposeField.setVisible(true);
                sendProposalBtn.setVisible(true);
                acceptBtn.setVisible(false);
            }
            else{
                serviceStatus.setText("Send a proposal !!!");
                proposeField.setVisible(true);
                sendProposalBtn.setVisible(true);
                acceptBtn.setVisible(false);
            }
        });

        // Add event listener to the send proposal button
        sendProposalBtn.setOnAction(event -> {
            // Check if the proposal field is empty or not a number
            if(proposeField.getText().isEmpty() || !proposeField.getText().matches("[0-9]+")){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Invalid input");
                alert.setContentText("The propose field must be a number");
                alert.showAndWait();
            }
            else{
                int index = listView.getSelectionModel().getSelectedIndex();
                Service service = services.get(index);
                GuiEvent guiEvent = new GuiEvent(this, 3);
                service.setPrice(Double.parseDouble(proposeField.getText()));
                service.setProposed(true);
                guiEvent.addParameter(service);
                guiEvent.addParameter(proposeField.getText());
                clientAgent.onGuiEvent(guiEvent);
                serviceStatus.setText("Waiting for buyer");
            }
        });

        // Add event listener to the accept button
        acceptBtn.setOnAction(event -> {
            int index = listView.getSelectionModel().getSelectedIndex();
            Service service = services.get(index);
            GuiEvent guiEvent = new GuiEvent(this, 4);
            guiEvent.addParameter(service);
            clientAgent.onGuiEvent(guiEvent);
            serviceStatus.setText("Purchased");
            proposeField.setVisible(false);
            sendProposalBtn.setVisible(false);
            acceptBtn.setVisible(false);
        });

        // Add event listener to the close footer button
        closeFooterBtn.setOnAction(event -> {
            footer.setVisible(false);
        });
    }

    public void setClientAgent(ClientAgent clientAgent) {
        ClientController.clientAgent = clientAgent;
    }

    public void updateServices(ArrayList<Service> services) {
        this.services = services;
        Platform.runLater(() -> {
            observableList.clear();
            for (Service service : services) {
                String type = service.getService().getType();
                String name = service.getService().getName();
                observableList.add("Type: " + type + " | Name: " + name);
            }
        });
    }

    public void displayAcceptedService(ACLMessage msg) {
        // Get the accepted service
        int serviceId = Integer.parseInt(msg.getContent());
        Service service = services.stream().filter(s -> s.getServiceId() == serviceId).findFirst().get();
        // Update the service status
        service.setBuyerAgreed(true);
        // Update the list view
        int index = services.indexOf(service);
        Platform.runLater(() -> {
            observableList.set(index, "Type: " + service.getService().getType() + " | Name: " + service.getService().getName() + " | Status: Buyer agreed");
        });
        // Update the footer
        proposeField.setVisible(true);
        sendProposalBtn.setVisible(true);
        acceptBtn.setVisible(true);
    }

    public void displayServiceProvided(ACLMessage msg) {
        // Get the provided service
        int serviceId = Integer.parseInt(msg.getContent());
        Service service = services.stream().filter(s -> s.getServiceId() == serviceId).findFirst().get();
        // Update the service status
        service.setPurchased(true);
        // Update the list view
        int index = services.indexOf(service);
        Platform.runLater(() -> {
            observableList.set(index, "Type: " + service.getService().getType() + " | Name: " + service.getService().getName() + " | Status: Purchased");
        });
        // Update the footer
        proposeField.setVisible(false);
        sendProposalBtn.setVisible(false);
        acceptBtn.setVisible(false);
    }
}
