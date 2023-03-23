module zgames.zgames {
    requires javafx.controls;
    requires javafx.media;
    requires javafx.fxml;


    opens zgames.zgames to javafx.fxml;
    exports zgames.zgames;
}