module zgames.zgames {
    requires javafx.controls;
    requires javafx.fxml;


    opens zgames.zgames to javafx.fxml;
    exports zgames.zgames;
}