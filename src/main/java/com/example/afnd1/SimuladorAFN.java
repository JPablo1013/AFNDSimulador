package com.example.afnd1;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

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
        this.estadosActuales = new HashSet<>(); // Inicializar en el constructor
        this.estadosActuales.add(estadoInicial);
        this.estadosActuales.addAll(cierreEpsilon(estadoInicial));
        this.estadoActualNumero = 0;
    }

    // Agregar un nuevo estado
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
        System.out.println("Resultado de la simulación");
        System.out.println("Traza de la simulación:");
        estadosActuales = new HashSet<>();
        estadosActuales.add(estadoInicial);
        System.out.println("\t" + cadena + " - q" + estadoInicial);

        for (char simbolo : cadena.toCharArray()) {
            Set<Integer> siguientesEstados = new HashSet<>();
            for (int estado : estadosActuales) {
                Map<Character, Set<Integer>> transiciones = tablaTransiciones.get(estado);
                if (transiciones != null && transiciones.containsKey(simbolo)) {
                    siguientesEstados.addAll(transiciones.get(simbolo));
                }
            }
            estadosActuales = siguientesEstados;
            System.out.println(simbolo + " - " + estadosActuales);
        }

        System.out.println("Estado final: q" + estadoFinal);
        if (estadosActuales.contains(estadoFinal)) {
            System.out.println("Aceptación");
        } else {
            System.out.println("Rechazo");
        }
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


















/*public class SimuladorAFN {
    private Set<Integer> estadosActuales;
    private final Map<Integer, Map<Character, Set<Integer>>> tablaTransiciones;
    private int estadoInicial;
    private int estadoFinal;
    //private final Set<Integer> estadosFinales;
    //private TextField estadoInicialField;
    //private TextField alfabetoField;
    //private TextField estadoFinalField;
    //private TextField cadenaField;
    private String alfabeto;

    public SimuladorAFN() {
        this.tablaTransiciones = new HashMap<>();
        //this.estadoInicial = 0;
        //this.estadosFinales = new HashSet<>();
        this.estadosActuales = new HashSet<>(); // Inicializar en el constructor
        //this.estadosActuales.add(estadoInicial);
        //this.estadosActuales.addAll(cierreEpsilon(estadoInicial));
    }

    public void agregarTransicion(int desdeEstado, char simbolo, int aEstado) {
        tablaTransiciones.putIfAbsent(desdeEstado, new HashMap<>());
        tablaTransiciones.get(desdeEstado).putIfAbsent(simbolo, new HashSet<>());
        tablaTransiciones.get(desdeEstado).get(simbolo).add(aEstado);
    }

    /*public void agregarEstadoFinal(int estadoFinal) {
        estadosFinales.add(estadoFinal);
    }

    public void setEstadoInicial(int estadoInicial) {
        this.estadoInicial = estadoInicial;
    }

    public void setEstadoFinal(int estadoFinal) {
        this.estadoFinal = estadoFinal;
    }

    public void setAlfabeto(String alfabeto) {
        this.alfabeto = alfabeto;
    }

    /*public boolean acepta(String entrada) {
        Set<Integer> estadosActuales = new HashSet<>();
        estadosActuales.add(estadoInicial);
        estadosActuales.addAll(cierreEpsilon(estadoInicial));

        for (char simbolo : entrada.toCharArray()) {
            Set<Integer> siguientesEstados = new HashSet<>();
            for (int estado : estadosActuales) {
                Map<Character, Set<Integer>> transiciones = tablaTransiciones.get(estado);
                if (transiciones != null && transiciones.containsKey(simbolo)) {
                    for (int siguienteEstado : transiciones.get(simbolo)) {
                        siguientesEstados.addAll(cierreEpsilon(siguienteEstado));
                    }
                }
            }
            estadosActuales = siguientesEstados;
            // Verificar si alguno de los estados actuales es final
            for (int estado : estadosActuales) {
                if (esEstadoFinal(estado)) {
                    return true;
                }
            }
        }
        // Al final de procesar la cadena, verificamos si alguno de los estados actuales es final
        for (int estado : estadosActuales) {
            if (esEstadoFinal(estado)) {
                return true;
            }
        }
        System.out.println("Estados actuales: " + estadosActuales);

        return false;
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
        System.out.println("Resultado de la simulación");
        System.out.println("Traza de la simulación:");
        estadosActuales = new HashSet<>();
        estadosActuales.add(estadoInicial);
        System.out.println("\t" + cadena + " - q" + estadoInicial);

        for (char simbolo : cadena.toCharArray()) {
            Set<Integer> siguientesEstados = new HashSet<>();
            for (int estado : estadosActuales) {
                Map<Character, Set<Integer>> transiciones = tablaTransiciones.get(estado);
                if (transiciones != null && transiciones.containsKey(simbolo)) {
                    siguientesEstados.addAll(transiciones.get(simbolo));
                }
            }
            estadosActuales = siguientesEstados;
            System.out.println(simbolo + " - " + estadosActuales);
        }

        System.out.println("Estado final: q" + estadoFinal);
        if (estadosActuales.contains(estadoFinal)) {
            System.out.println("Aceptación");
        } else {
            System.out.println("Rechazo");
        }
    }

    //public boolean esEstadoFinal(int estado) {
    //    return estadosFinales.contains(estado);
    //}

    //public void imprimirEstadosActuales() {
    //    System.out.println("Estados actuales: " + estadosActuales);
    //}

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

    public boolean esEstadoFinalAlcanzado() {
        for (int estado : estadosActuales) {
            if (esEstadoFinal(estado)) {
                return true;
            }
        }
        return false;
    }

    private void agregarTransicion(String texto) {
        String[] partes = texto.split(",");
        if (partes.length == 3) {
            int desdeEstado = Integer.parseInt(partes[0].trim().substring(1)); // Eliminar 'q' del estado inicial
            char simbolo = partes[1].trim().charAt(0);
            int aEstado = Integer.parseInt(partes[2].trim().substring(1)); // Eliminar 'q' del nuevo estado
            agregarTransicion(desdeEstado, simbolo, aEstado);
        } else {
            System.out.println("Formato de transición incorrecto: " + texto);
        }
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
            setEstadoInicial(Integer.parseInt(estadoInicialField.getText().trim()));
            setEstadoFinal(Integer.parseInt(estadoFinalField.getText().trim()));
            String[] transiciones = transicionesArea.getText().trim().split("\n");
            for (String transicion : transiciones) {
                String[] partes = transicion.trim().split(",");
                if (partes.length == 3) {
                    int desdeEstado = Integer.parseInt(partes[0].trim().substring(1));
                    char simbolo = partes[1].trim().charAt(0);
                    int aEstado = Integer.parseInt(partes[2].trim().substring(1));
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

}*/
