package org.example;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class MapTest extends Application {
    private WebView webView;
    private WebEngine webEngine;

    private String apiKey;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Load API key from application.properties
        loadApiKey();

        webView = new WebView();
        webEngine = webView.getEngine();

        TextField searchField = new TextField();
        Button searchButton = new Button("Search");

        searchButton.setOnAction(e -> {
            String location = searchField.getText();
            if (!location.isEmpty()) {
                searchLocation(location);
            }
        });

        VBox root = new VBox(searchField, searchButton, webView);
        Scene scene = new Scene(root, 800, 600);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Dynamic Map Example");
        primaryStage.show();

        // Load the initial map
        loadMap();
    }

    private void loadApiKey() {
        Properties properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                System.out.println("Sorry, unable to find application.properties");
                return;
            }
            properties.load(input);
            apiKey = properties.getProperty("google.maps.api.key");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void loadMap() {
        webEngine.setOnError(event -> {
            System.out.println("Error loading the map: " + event.getMessage());
        });

        String mapHtml = "<!DOCTYPE html><html><head><title>Dynamic Map Example</title></head><body><div id=\"map\" style=\"width: 100%; height: 100vh;\"></div><script src=\"https://maps.googleapis.com/maps/api/js?key="+apiKey+" \" " +
                "onerror=\"window.location='https://www.google.com/maps';\">" +
                "</script><script>function initMap() { const colombo = { lat: 6.9271, lng: 79.8612 }; const map = new google.maps.Map(document.getElementById(\"map\"), { center: colombo, zoom: 15, }); const marker = new google.maps.Marker({ position: colombo, map: map, title: \"Colombo\", }); } initMap();</script></body></html>";
        webEngine.loadContent(mapHtml);
    }

    private void searchLocation(String location) {
        // Use the Google Places API to search for the location and update the map
        webEngine.executeScript("var geocoder = new google.maps.Geocoder(); " +
                "geocoder.geocode({ 'address': '" + location + "' }, function(results, status) { " +
                "if (status === 'OK') { " +
                "var map = new google.maps.Map(document.getElementById('map'), { " +
                "center: results[0].geometry.location, " +
                "zoom: 15 " +
                "}); " +
                "var marker = new google.maps.Marker({ " +
                "map: map, " +
                "position: results[0].geometry.location, " +
                "title: '" + location + "' " +
                "}); " +
                "} " +
                "});");
    }
}
