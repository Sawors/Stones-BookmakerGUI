module com.github.sawors.bookmaker {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires org.apache.pdfbox;
    
    exports com.github.sawors.bookmaker;
    exports com.github.sawors.bookmaker.controllers;
    opens com.github.sawors.bookmaker.controllers to javafx.fxml;
}