package com.example.afnd1;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    private BorderPane bdpPanel;

    @Override
    public void start(Stage stage) throws IOException {
        stage.setTitle("AFND Simulador");

        SimuladorAFN simuladorAFN = new SimuladorAFN();
        simuladorAFN.CrearUI(stage);

        // Crear un autómata finito no determinista de ejemplo y probar una cadena
        //SimuladorAFN simuladorAFN = new SimuladorAFN();
        //simuladorAFN.agregarTransicion(0, 'a', 1);
        //simuladorAFN.agregarTransicion(1, 'b', 2);
        //simuladorAFN.agregarTransicion(2, 'a', 3);
        //simuladorAFN.agregarTransicion(3, 'b', 0);
        //simuladorAFN.agregarEstadoFinal(0); // Por ejemplo, el estado 0 es un estado final
        //String entrada = "abab";

        //System.out.println("Entrada aceptada: " + simuladorAFN.acepta(entrada));
    }

    public static void main(String[] args) {
        launch();
    }
}

