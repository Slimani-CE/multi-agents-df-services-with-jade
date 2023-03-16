package ma.enset.tp2_sma.entities;

import jade.core.AID;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class Service {
    private AID buyer;
    private DFAgentDescription dfAgentDescription;
    private ServiceDescription service;
    private boolean isPurchased = false;
    private boolean isBuyerAgreed = false;
    private boolean isProposed = false;
    private double price;
    private int serviceId;

    public Service(DFAgentDescription dfAgentDescription, ServiceDescription service, int serviceId) {
        this.buyer = dfAgentDescription.getName();
        this.dfAgentDescription = dfAgentDescription;
        this.service = service;
        this.serviceId = serviceId;
    }

    public AID getBuyer() {
        return buyer;
    }

    public void setBuyer(AID buyer) {
        this.buyer = buyer;
    }

    public DFAgentDescription getDfAgentDescription() {
        return dfAgentDescription;
    }

    public void setDfAgentDescription(DFAgentDescription dfAgentDescription) {
        this.dfAgentDescription = dfAgentDescription;
    }
    public ServiceDescription getService() {
        return service;
    }

    public void setService(ServiceDescription service) {
        this.service = service;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setBuyerAgreed(boolean buyerAgreed) {
        isBuyerAgreed = buyerAgreed;
    }

    public void setPurchased(boolean purchased) {
        isPurchased = purchased;
    }

    public boolean isPurchased() {
        return isPurchased;
    }

    public boolean isBuyerAgreed() {
        return isBuyerAgreed;
    }

    public boolean isProposed() {
        return isProposed;
    }

    public int getServiceId() {
        return serviceId;
    }

    public void setProposed(boolean proposed) {
        isProposed = proposed;
    }
}
