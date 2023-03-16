package ma.enset.tp2_sma.containers;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.ControllerException;
public class MainContainer {
    public static void main(String[] args) throws ControllerException {
        Runtime runtime = Runtime.instance();
        ProfileImpl profileImpl = new ProfileImpl();
        profileImpl.setParameter(Profile.GUI, "true");
        profileImpl.setParameter(Profile.MAIN_HOST, "localhost");
        AgentContainer mainContainer = runtime.createMainContainer(profileImpl);
        mainContainer.start();
    }
}