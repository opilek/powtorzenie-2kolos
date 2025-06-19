package org.example.powtorzenie2kolokwium;
// Definicja pakietu, w którym znajduje się klasa Main

import javafx.application.Application;
// Import klasy bazowej dla aplikacji JavaFX

import javafx.fxml.FXMLLoader;
// Import klasy do ładowania plików FXML

import javafx.scene.Parent;
// Import klasy Parent, która reprezentuje główny kontener GUI

import javafx.scene.Scene;
// Import klasy Scene – kontenera dla całego widoku

import javafx.stage.Stage;
// Import klasy Stage – głównego okna aplikacji

import org.example.powtorzenie2kolokwium.server.Server;
// Import klasy Server (można jej użyć do uruchomienia serwera)

import org.example.powtorzenie2kolokwium.client.ServerThread;
// Import klasy ServerThread (klient do połączenia z serwerem)

public class Main extends Application {
    // Główna klasa aplikacji rozszerzająca klasę JavaFX Application

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        // Metoda uruchamiana po starcie aplikacji – ustawia i pokazuje GUI

        FXMLLoader loader = new FXMLLoader(getClass().getResource("app-view.fxml"));
        // Tworzy loader FXML i ładuje plik "app-view.fxml", który zawiera definicję GUI

        Parent root = loader.load();
        // Ładuje cały układ GUI z pliku FXML do obiektu root typu Parent

        Controller controller = loader.getController();
        // Pobiera kontroler powiązany z FXML – umożliwia manipulację logiką GUI

        controller.setServer(null);
        // Ustawia serwer na null – nie uruchamiamy serwera od razu (można zmienić)

        controller.setClient(null);
        // Ustawia klienta na null – nie łączymy się z serwerem od razu

        primaryStage.setTitle("Powtórzenie Kolokwium");
        // Ustawia tytuł okna głównego

        primaryStage.setScene(new Scene(root));
        // Tworzy nową scenę na podstawie root (czyli interfejsu z FXML)

        primaryStage.show();
        // Wyświetla główne okno aplikacji
    }

    public static void main(String[] args)
    {
        launch(args);
        // Uruchamia aplikację JavaFX – wywoła metodę start()
    }
}