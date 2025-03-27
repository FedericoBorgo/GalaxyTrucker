module it.polimi.softeng.is25am10 {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.json;
    requires jdk.jsobject;
    requires jdk.jdi;
    requires java.desktop;
    requires java.rmi;

    opens it.polimi.softeng.is25am10 to javafx.fxml;
    exports it.polimi.softeng.is25am10;
    exports it.polimi.softeng.is25am10.network;
    opens it.polimi.softeng.is25am10.network to javafx.fxml;
    exports it.polimi.softeng.is25am10.network.rmi;
    opens it.polimi.softeng.is25am10.network.rmi to javafx.fxml;
    exports it.polimi.softeng.is25am10.client;
    opens it.polimi.softeng.is25am10.client to javafx.fxml;
}