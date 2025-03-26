module it.polimi.softeng.is25am10 {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.json;
    requires jdk.jsobject;
    requires jdk.jdi;
    requires java.desktop;

    opens it.polimi.softeng.is25am10 to javafx.fxml;
    exports it.polimi.softeng.is25am10;
    exports it.polimi.softeng.is25am10.network;
    opens it.polimi.softeng.is25am10.network to javafx.fxml;
}