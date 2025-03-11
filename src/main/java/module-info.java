module it.polimi.softeng.is25am10 {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires java.desktop;
    requires org.json;
    requires jdk.jsobject;

    opens it.polimi.softeng.is25am10 to javafx.fxml;
    exports it.polimi.softeng.is25am10;
}