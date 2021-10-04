module com.chess.agchess {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;

    opens com.chess.agchess to javafx.fxml;
    exports com.chess.agchess;
}