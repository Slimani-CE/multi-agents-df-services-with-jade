package ma.enset.tp2_sma.agents;


import jade.core.AID;
import jade.core.behaviours.*;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.Search;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import javafx.application.Platform;
import javafx.stage.Stage;
import ma.enset.tp2_sma.controllers.ClientController;
import ma.enset.tp2_sma.entities.Service;
import ma.enset.tp2_sma.gui.ClientGUI;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class ClientAgent extends GuiAgent {
    private static ClientGUI clientGUI;
    private static ClientController clientController;
    // List of all services
    private ArrayList<Service> allServices = new ArrayList<>();
    // List of filtered services. This list is used to update the GUI
    private ArrayList<Service> filteredServices = new ArrayList<>();
    private static int idCounter = 0;
    // Current value of the search field in the GUI
    private String typeCurrentValue = "";
    @Override
    protected void setup() {
        // Create and show the GUI
        clientGUI = new ClientGUI(this);
        clientGUI.show();
        ParallelBehaviour parallelBehaviour = new ParallelBehaviour();
        // Behaviour to wait for the GUI to be ready
        // Then get all services and update the GUI
        parallelBehaviour.addSubBehaviour(new Behaviour() {
            @Override
            public void action() {
                if(clientController != null){
                    // update the GUI
                    Platform.runLater(() -> {
                        getAllServices();
                        clientController.updateServices(allServices);
                    });
                }
            }
            @Override
            public boolean done() {
                if(clientController!=null)
                    return true;
                return false;
            }
        });

        // Cyclic behaviour to receive messages
        parallelBehaviour.addSubBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage msg = receive();
                if(msg != null){
                    // If message performative is AcceptProposal
                    if(msg.getPerformative() == ACLMessage.ACCEPT_PROPOSAL){
                        // Display the message
                        clientController.displayAcceptedService(msg);
                    } else if(msg.getPerformative() == ACLMessage.INFORM){
                        // Display the message
                        clientController.displayServiceProvided(msg);
                    }
                }else{
                    block();
                }
            }
        });
        addBehaviour(parallelBehaviour);
    }

    // Get all services from the DF
    public void getAllServices(){
        try {
            // Search for all services
            SearchConstraints sc = new SearchConstraints();
            sc.setMaxResults(1L);
            DFAgentDescription[] result = DFService.search(this, new DFAgentDescription());
            allServices.clear();
            System.out.println(result.length);
            for (DFAgentDescription tmpDF : result) {
                // Iterate over the services
                Iterator iterator = tmpDF.getAllServices();
                while (iterator.hasNext()) {
                    ServiceDescription sd = (ServiceDescription) iterator.next();
                    System.out.println(sd.getType());
                    allServices.add(new Service(tmpDF, sd, idCounter++));
                }
            }
            filteredServices.clear();
            filteredServices.addAll(allServices);
        } catch (FIPAException e) {
            throw new RuntimeException(e);
        }
    }

    public void filterByType(String typeRequest){
        // Filter the filteredServices list by type
        // Remove all services that don't match the type
        filteredServices.removeIf(service -> !service.getService().getType().toLowerCase().contains(typeRequest.toLowerCase()));
    }

    @Override
    public void onGuiEvent(GuiEvent guiEvent) {
        // If event value == 1, then the user has changed the type value
        if(guiEvent.getType() == 1){
            typeCurrentValue = (String) guiEvent.getParameter(0);
            filteredServices.clear();
            filteredServices.addAll(allServices);
            filterByType(typeCurrentValue);
            System.out.println(typeCurrentValue);
            // Update the GUI
            Platform.runLater(() -> {
                clientController.updateServices(filteredServices);
            });
        }
        // If event value == 2, then the user has clicked on the button "Refresh"
        if(guiEvent.getType() == 2){
            // Get all services
            getAllServices();
            // Filter services by type
            filterByType(typeCurrentValue);
            // Update the GUI
            Platform.runLater(() -> {
                clientController.updateServices(filteredServices);
            });
        }
        // If event value == 3, then the user want to send proposal
        if(guiEvent.getType() == 3){
            System.out.println("Sending proposal");
            // Get the service
            Service service = (Service) guiEvent.getParameter(0);
            // Get the price
            double price = service.getPrice();
            // Create message
            ACLMessage request = new ACLMessage(ACLMessage.PROPOSE);
            request.addReceiver(service.getBuyer());
            request.setContent("" + service.getServiceId() + ":" + price);
            System.out.println("Sending proposal to " + service.getBuyer().getName() + " with price " + price);
            // Send message
            send(request);
        }
        // If event value == 4, then the user want to send agreement message
        if(guiEvent.getType() == 4){
            System.out.println("Sending agreement");
            // Get the service
            Service service = (Service) guiEvent.getParameter(0);
            // Get the price
            double price = service.getPrice();
            // Create message
            ACLMessage request = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
            request.addReceiver(service.getBuyer());
            request.setContent("" + service.getServiceId() + ":" + price);
            System.out.println("Sending agreement to " + service.getBuyer().getName() + " with price " + price);
            // Send message
            send(request);
        }
    }

    @Override
    protected void takeDown() {
        System.out.println("Agent is shutting down");
    }

    public static void setClientController(ClientController clientController) {
        ClientAgent.clientController = clientController;
    }
}
