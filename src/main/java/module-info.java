module com.example.diabetes {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires javafx.swing;

    requires java.sql; // ✅ needed for SQLite JDBC

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    
    // ✅ Chart and Report generation
    requires org.jfree.jfreechart;
    requires itextpdf;

    opens com.example.diabetes to javafx.fxml;
    exports com.example.diabetes;
}
