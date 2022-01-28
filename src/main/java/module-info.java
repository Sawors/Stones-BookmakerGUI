module com.github.sawors.bookmaker {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires org.apache.pdfbox;
    
    
    opens com.github.sawors.bookmaker to javafx.fxml;
    exports com.github.sawors.bookmaker;
}