package ma.enset.tp2_sma.agents;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import ma.enset.tp2_sma.controllers.ServerController;
import ma.enset.tp2_sma.gui.ServerGUI;

import java.util.Iterator;

public class ServerAgent extends GuiAgent {

    private DFAgentDescription dfAgentDescription;
    private static ServerController serverController;
    private static ServerGUI serverGUI;

    @Override
    protected void setup() {
        // Create and show the GUI
        serverGUI = new ServerGUI(this);
        serverGUI.show();

        // Create the description of the agent
        dfAgentDescription = new DFAgentDescription();

        // Register the agent in the DF
        try {
            System.out.println("ServerAgent " + getAID().getName() + " is ready.");
            DFService.register(this, dfAgentDescription);
        } catch (FIPAException e) {
            throw new RuntimeException(e);
        }

        // Cyclic behaviour to receive messages
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                System.out.println("Waiting for messages...");
                ACLMessage msg = receive();
                if(msg != null){
                    // Check performative
                    if(msg.getPerformative() == ACLMessage.PROPOSE){
                        // Display proposal
                        serverController.displayProposal(msg);
                    } else if(msg.getPerformative() == ACLMessage.AGREE){
                        ACLMessage response = msg.createReply();
                        response.setPerformative(ACLMessage.INFORM);
                        response.setContent(msg.getContent().split(":")[0]);
                        send(response);
                    }
                }else{
                    block();
                }
            }
        });
    }

    @Override
    public void onGuiEvent(GuiEvent guiEvent) {
        // If event type = 1 : Get inserted data from the GUI and add it to the DF
        if (guiEvent.getType() == 1) {
            // Create new service
            String type = (String) guiEvent.getParameter(0);
            String descreption = (String) guiEvent.getParameter(1);
            ServiceDescription service = new ServiceDescription();
            service.setType(type);
            service.setName(descreption);

            // Add the service to the DF
            dfAgentDescription.addServices(service);
            Iterator iterator = dfAgentDescription.getAllServices();
            while (iterator.hasNext()) {
                ServiceDescription sd = (ServiceDescription) iterator.next();
                System.out.println(sd);
            }
            // Update the DF
            try {
                DFService.modify(this, dfAgentDescription);
            } catch (FIPAException e) {
                throw new RuntimeException(e);
            }
        }
        // If event type = 2 : Send proposal accept to the client
        if (guiEvent.getType() == 2) {
            ACLMessage msg = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
            msg.addReceiver((AID) guiEvent.getParameter(0));
            msg.setContent(guiEvent.getParameter(1).toString());
            send(msg);
        }
        // If event type = 3 : Send proposal reject to the client
        if(guiEvent.getType() == 3){
            ACLMessage msg = new ACLMessage(ACLMessage.REJECT_PROPOSAL);
            msg.addReceiver((AID) guiEvent.getParameter(0));
            msg.setContent("Refused");
            send(msg);
        }
    }

    @Override
    protected void takeDown() {
        System.out.println("ServerAgent " + getAID().getName() + " terminating.");
        try {
            DFService.deregister(this);
        } catch (FIPAException e) {
            e.printStackTrace();
        }
    }

    public static void setServerController(ServerController serverController) {
        ServerAgent.serverController = serverController;
    }
}
