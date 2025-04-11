module it.polimi.softeng.is25am10 {
    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.json;
    requires jdk.jsobject;
    requires jdk.jdi;
    requires java.rmi;
    requires com.googlecode.lanterna;
    requires java.desktop;

    opens it.polimi.softeng.is25am10 to javafx.fxml;
    exports it.polimi.softeng.is25am10;
    exports it.polimi.softeng.is25am10.network;
    opens it.polimi.softeng.is25am10.network to javafx.fxml;
    exports it.polimi.softeng.is25am10.network.rmi;
    opens it.polimi.softeng.is25am10.network.rmi to javafx.fxml;
    exports it.polimi.softeng.is25am10.tui;
    opens it.polimi.softeng.is25am10.tui to javafx.fxml;
    exports it.polimi.softeng.is25am10.tui.asciiui;
    opens it.polimi.softeng.is25am10.tui.asciiui to javafx.fxml;

    exports it.polimi.softeng.is25am10.gui;
    opens it.polimi.softeng.is25am10.gui to javafx.fxml;
}