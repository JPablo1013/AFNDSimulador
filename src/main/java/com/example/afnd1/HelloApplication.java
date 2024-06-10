package com.example.afnd1;

import javafx.application.Application;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        stage.setTitle("AFND Simulador");

        SimuladorAFN simuladorAFN = new SimuladorAFN();
        simuladorAFN.CrearUI(stage);

    }

    public static void main(String[] args) {
        launch();
    }
}

