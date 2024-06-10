package com.example.afnd1;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.scene.control.Alert.AlertType;

import java.util.*;

public class SimuladorAFN {
    private Set<Integer> estadosActuales; // Declarar como campo de clase
    private final Map<Integer, Map<Character, Set<Integer>>> tablaTransiciones;
    private final Map<String, Integer> estadoToNumero;
    private final Map<Integer, String> numeroToEstado;
    private final Set<Integer> estadosFinales;
    private int estadoActualNumero;
    private int estadoInicial;
    private int estadoFinal;
    private String alfabeto;

    public SimuladorAFN() {
        this.tablaTransiciones = new HashMap<>();
        this.estadoToNumero = new HashMap<>();
        this.numeroToEstado = new HashMap<>();
        this.estadoInicial = 0;
        this.estadosFinales = new HashSet<>();
        this.estadosActuales = new HashSet<>();
        this.estadosActuales.add(estadoInicial);
        this.estadosActuales.addAll(cierreEpsilon(estadoInicial));
        this.estadoActualNumero = 0;
    }

    public void agregarEstado(String estado) {
        estadoToNumero.putIfAbsent(estado, estadoActualNumero);
        numeroToEstado.putIfAbsent(estadoActualNumero, estado);
        estadoActualNumero++;
    }

    // Obtener el número asociado a un estado
    public int getNumeroEstado(String estado) {
        return estadoToNumero.getOrDefault(estado, -1); // Retorna -1 si no se encuentra el estado
    }

    // Obtener el nombre asociado a un número de estado
    public String getNombreEstado(int numero) {
        return numeroToEstado.getOrDefault(numero, null); // Retorna null si no se encuentra el número
    }

    public void agregarTransicion(String desdeEstado, char simbolo, String aEstado) {
        int desde = getNumeroEstado(desdeEstado);
        int hacia = getNumeroEstado(aEstado);
        if (desde != -1 && hacia != -1) {
            tablaTransiciones.putIfAbsent(desde, new HashMap<>());
            tablaTransiciones.get(desde).putIfAbsent(simbolo, new HashSet<>());
            tablaTransiciones.get(desde).get(simbolo).add(hacia);
        }
    }

    public void setEstadoInicial(String estadoInicial) {
        this.estadoInicial = getNumeroEstado(estadoInicial);
    }

    public void setEstadoFinal(String estadoFinal) {
        this.estadoFinal = getNumeroEstado(estadoFinal);
    }

    public void setAlfabeto(String alfabeto) {
        this.alfabeto = alfabeto;
    }

    public boolean acepta(String entrada) {
        estadosActuales = new HashSet<>();
        estadosActuales.add(estadoInicial);

        for (char simbolo : entrada.toCharArray()) {
            Set<Integer> siguientesEstados = new HashSet<>();
            for (int estado : estadosActuales) {
                Map<Character, Set<Integer>> transiciones = tablaTransiciones.get(estado);
                if (transiciones != null && transiciones.containsKey(simbolo)) {
                    siguientesEstados.addAll(transiciones.get(simbolo));
                }
            }
            estadosActuales = siguientesEstados;
        }

        return estadosActuales.contains(estadoFinal);
    }

    public void imprimirTraza(String cadena) {
        StringBuilder traza = new StringBuilder();
        traza.append("Resultado de la simulación\n");
        traza.append("Traza de la simulación:\n");
        estadosActuales = new HashSet<>();
        estadosActuales.add(estadoInicial);
        traza.append("\t").append(cadena).append(" - q").append(estadoInicial).append("\n");

        for (char simbolo : cadena.toCharArray()) {
            Set<Integer> siguientesEstados = new HashSet<>();
            for (int estado : estadosActuales) {
                Map<Character, Set<Integer>> transiciones = tablaTransiciones.get(estado);
                if (transiciones != null && transiciones.containsKey(simbolo)) {
                    siguientesEstados.addAll(transiciones.get(simbolo));
                }
            }
            estadosActuales = siguientesEstados;
            traza.append(simbolo).append(" - ").append(estadosActuales).append("\n");
        }

        traza.append("Estado final: q").append(estadoFinal).append("\n");
        if (estadosActuales.contains(estadoFinal)) {
            traza.append("Aceptación");
        } else {
            traza.append("Rechazo");
        }

        // Mostrar la traza en una alerta
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Resultado de la simulación");
        alert.setHeaderText(null);
        alert.setContentText(traza.toString());
        alert.showAndWait();
    }

    private Set<Integer> cierreEpsilon(int estado) {
        Set<Integer> cierre = new HashSet<>();
        cierre.add(estado);
        boolean agregado;
        do {
            agregado = false;
            Set<Integer> nuevosEstados = new HashSet<>();
            for (int s : cierre) {
                Map<Character, Set<Integer>> transiciones = tablaTransiciones.get(s);
                if (transiciones != null && transiciones.containsKey((char)0)) {
                    for (int siguienteEstado : transiciones.get((char)0)) {
                        if (!cierre.contains(siguienteEstado)) {
                            nuevosEstados.add(siguienteEstado);
                            agregado = true;
                        }
                    }
                }
            }
            cierre.addAll(nuevosEstados);
        } while (agregado);
        return cierre;
    }

    public void CrearUI(Stage primaryStage) {
        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(10);

        Label alfabetoLabel = new Label("Alfabeto:");
        TextField alfabetoField = new TextField();
        grid.add(alfabetoLabel, 0, 0);
        grid.add(alfabetoField, 1, 0);

        Label estadoInicialLabel = new Label("Estado inicial:");
        TextField estadoInicialField = new TextField();
        grid.add(estadoInicialLabel, 0, 1);
        grid.add(estadoInicialField, 1, 1);

        Label estadoFinalLabel = new Label("Estado final:");
        TextField estadoFinalField = new TextField();
        grid.add(estadoFinalLabel, 0, 2);
        grid.add(estadoFinalField, 1, 2);

        Label transicionesLabel = new Label("Transiciones:");
        TextArea transicionesArea = new TextArea();
        grid.add(transicionesLabel, 0, 3);
        grid.add(transicionesArea, 1, 3);

        Label cadenaLabel = new Label("Cadena:");
        TextField cadenaField = new TextField();
        grid.add(cadenaLabel, 0, 4);
        grid.add(cadenaField, 1, 4);

        Button btnAceptar = new Button("Aceptar");
        btnAceptar.setOnAction(event -> {
            setAlfabeto(alfabetoField.getText().trim());
            setEstadoInicial(estadoInicialField.getText().trim());
            setEstadoFinal(estadoFinalField.getText().trim());
            String[] transiciones = transicionesArea.getText().trim().split("\n");
            for (String transicion : transiciones) {
                String[] partes = transicion.trim().split(",");
                if (partes.length == 3) {
                    String desdeEstado = partes[0].trim();
                    char simbolo = partes[1].trim().charAt(0);
                    String aEstado = partes[2].trim();
                    agregarEstado(desdeEstado);
                    agregarEstado(aEstado);
                    agregarTransicion(desdeEstado, simbolo, aEstado);
                }
            }
            String cadena = cadenaField.getText().trim();
            imprimirTraza(cadena);
        });

        grid.add(btnAceptar, 0, 5);

        Scene scene = new Scene(grid);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
