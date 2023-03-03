module com.goat.app {
    requires javafx.controls;
    requires javafx.fxml;
    requires kotlin.stdlib;

    requires kotlin.stdlib.jdk7;
    requires mockito.kotlin;
    requires org.controlsfx.controls;
    requires java.sql;
    requires markdown.jvm;
    requires javafx.web;
    requires sqlite.jdbc;
    requires com.calendarfx.view;
    requires java.net.http;
    requires kotlinx.serialization.core;
    requires kotlinx.serialization.json;
    requires kotlinx.coroutines.core.jvm;

    opens com.goat.app to javafx.fxml;
    exports com.goat.app;
    exports com.goat.app.business.sync; // enables serialization
}