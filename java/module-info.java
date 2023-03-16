module com.sma.tp2_withjavafx_sma {
    requires javafx.controls;
    requires javafx.fxml;
    requires jade;
    opens ma.enset.tp2_sma.agents;
    exports ma.enset.tp2_sma.containers;
    exports ma.enset.tp2_sma.agents;
    exports ma.enset.tp2_sma.gui;
    exports ma.enset.tp2_sma.controllers;
    opens ma.enset.tp2_sma.gui;
    opens ma.enset.tp2_sma.controllers;
}