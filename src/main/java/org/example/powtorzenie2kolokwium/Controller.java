package org.example.powtorzenie2kolokwium; // Pakiet, do którego należy ta klasa

import javafx.event.ActionEvent; // Import klasy dla obsługi zdarzeń akcji (np. kliknięcia przycisku)
import javafx.fxml.FXML; // Adnotacja używana do wiązania elementów FXML z kodem Java
import javafx.scene.canvas.Canvas; // Komponent graficzny do rysowania 2D
import javafx.scene.canvas.GraphicsContext; // Kontekst graficzny do rysowania na Canvasie
import javafx.scene.control.ColorPicker; // Komponent umożliwiający wybór koloru
import javafx.scene.control.Slider; // Suwak (np. do ustawienia promienia)
import javafx.scene.control.TextField; // Pole tekstowe
import javafx.scene.input.MouseEvent; // Zdarzenie myszy
import javafx.scene.paint.Color; // Klasa reprezentująca kolor
import org.example.powtorzenie2kolokwium.server.Server; // Import klasy Server z pakietu server
import org.example.powtorzenie2kolokwium.client.ServerThread; // Import klasy ServerThread z pakietu client

import java.io.IOException; // Obsługa wyjątków wejścia/wyjścia

public class Controller
{ // Główna klasa kontrolera powiązana z plikiem FXML

    @FXML
    private TextField addressField; // Pole tekstowe na adres IP serwera

    @FXML
    private TextField portField; // Pole tekstowe na numer portu

    @FXML
    private Canvas canvas; // Obszar rysowania

    @FXML
    private ColorPicker colorPicker; // Komponent do wyboru koloru

    @FXML
    private Slider radiusSlider; // Suwak do wyboru promienia koła

    private Server server; // Obiekt serwera (jeśli ta instancja uruchamia serwer)
    private ServerThread client; // Wątek klienta, do komunikacji z serwerem

    public Controller() {} // Domyślny konstruktor wymagany przez FXMLLoader (FXML)

    public void setServer(Server server) { // Ustawienie serwera z zewnątrz
        this.server = server;
    }

    public void setClient(ServerThread client) { // Ustawienie klienta z zewnątrz
        this.client = client;
        if (this.client != null)
        {
            this.client.setDotConsumer(this::drawDot); // Ustawienie funkcji, która będzie rysować punkty otrzymane od serwera
        }
    }

    @FXML
    private void onStartServerClicked(ActionEvent event) // Obsługa kliknięcia przycisku "Start Server"
    {
        try
        {
            int port = Integer.parseInt(portField.getText()); // Pobranie portu z pola tekstowego

            server = new Server(port); // Utworzenie i uruchomienie serwera na danym porcie

            System.out.println("Serwer uruchomiony na porcie " + port); // Informacja w konsoli
        }
        catch (IOException e)
        {
            e.printStackTrace(); // Obsługa wyjątku jeśli serwer nie może się uruchomić
        }
    }

    @FXML
    private void onConnectClicked(ActionEvent event)// Obsługa kliknięcia przycisku "Connect"
    {
        try
        {
            String address = addressField.getText(); // Pobranie adresu IP z pola tekstowego
            int port = Integer.parseInt(portField.getText()); // Pobranie portu z pola tekstowego
            client = new ServerThread(address, port); // Utworzenie klienta i połączenie z serwerem

            client.setDotConsumer(this::drawDot); // Ustawienie odbiorcy, który narysuje doty
            client.start(); // Uruchomienie wątku klienta
            System.out.println("Połączono z serwerem: " + address + ":" + port); // Informacja w konsoli
        }
        catch (IOException e)
        {
            e.printStackTrace(); // Obsługa błędów przy połączeniu z serwerem
        }
    }

    @FXML
    public void onMouseClicked(MouseEvent event) // Obsługa kliknięcia myszą na obszarze canvas
    {
        double x = event.getX(); // Współrzędna X kliknięcia
        double y = event.getY(); // Współrzędna Y kliknięcia
        double radius = radiusSlider.getValue(); // Pobranie wartości promienia z suwaka
        Color color = colorPicker.getValue(); // Pobranie koloru z ColorPickera

        Dot dot = new Dot(x, y, color, radius); // Utworzenie obiektu Dot (punkt/okrąg)
        drawDot(dot); // Narysowanie okręgu lokalnie

        if (client != null) // Jeśli klient jest połączony
        {
            client.send(dot.toMessage()); // Wyślij dane okręgu do serwera jako wiadomość tekstową
        }
    }

    private void drawDot(Dot dot) // Metoda rysująca okrąg na canvasie
    {
        GraphicsContext gc = canvas.getGraphicsContext2D(); // Pobranie kontekstu graficznego do rysowania
        gc.setFill(dot.color()); // Ustawienie koloru
        double r = dot.radius(); // Pobranie promienia
        gc.fillOval(dot.x() - r, dot.y() - r, r * 2, r * 2); // Narysowanie wypełnionego okręgu o środku (x, y)
    }
}