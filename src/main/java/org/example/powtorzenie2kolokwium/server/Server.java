package org.example.powtorzenie2kolokwium.server; // Pakiet serwera

import javafx.scene.paint.Color; // Import klasy Color z JavaFX
import org.example.powtorzenie2kolokwium.Dot; // Import klasy Dot

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Server
{

    private ServerSocket serverSocket; // Główny socket nasłuchujący połączeń od klientów

    // Mapa przechowująca klientów i odpowiadające im strumienie do wysyłania wiadomości
    private final Map<Socket, PrintWriter> clientWriters = new ConcurrentHashMap<>();

    private Connection dbConnection; // Połączenie z bazą danych SQLite

    // Konstruktor serwera – uruchamia serwer na podanym porcie
    public Server(int port) throws IOException
    {
        // Nawiązanie połączenia z bazą danych SQLite
        try
        {
            dbConnection = DriverManager.getConnection("jdbc:sqlite:dot.db"); // Połączenie z plikiem bazy danych
            createTableIfNotExists(); // Tworzy tabelę, jeśli jeszcze nie istnieje
        }
        catch (SQLException e)
        {
            e.printStackTrace(); // Obsługa błędu SQL
        }

        serverSocket = new ServerSocket(port); // Tworzy serwer nasłuchujący na wskazanym porcie

        new Thread(this::acceptClients).start(); // Uruchamia w osobnym wątku metodę obsługi przychodzących klientów
    }

    // Tworzy tabelę dot w bazie danych, jeśli jeszcze nie istnieje
    private void createTableIfNotExists() throws SQLException
    {
        String sql = """
            CREATE TABLE IF NOT EXISTS dot (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                x REAL NOT NULL,
                y REAL NOT NULL,
                color TEXT NOT NULL,
                radius REAL NOT NULL
            );
            """;
        try (Statement stmt = dbConnection.createStatement())
        {
            stmt.execute(sql); // Wykonanie polecenia SQL
        }
    }

    // Metoda przyjmująca nowych klientów
    private void acceptClients()
    {
        System.out.println("Serwer nasłuchuje na porcie " + serverSocket.getLocalPort()); // Info o porcie

        while (!serverSocket.isClosed()) { // Działa dopóki socket nie zostanie zamknięty
            try
            {
                Socket clientSocket = serverSocket.accept(); // Oczekiwanie na nowego klienta
                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true); // Strumień do wysyłania danych do klienta
                clientWriters.put(clientSocket, writer); // Zapisujemy klienta i jego strumień

                // Wysyłanie wszystkich dotychczasowych okręgów do nowego klienta
                List<Dot> savedDots = getSavedDots();

                for (Dot dot : savedDots)
                {
                    writer.println(dot.toMessage()); // Każdy zapisany okrąg jako wiadomość
                }

                // Tworzenie i uruchomienie wątku obsługującego klienta
                new ClientThread(this, clientSocket).start();

                System.out.println("Nowy klient podłączony"); // Log informacyjny
            }
            catch (IOException e)
            {
                e.printStackTrace(); // Obsługa błędów sieciowych
            }
        }
    }

    // Rozsyła wiadomość do wszystkich klientów
    public synchronized void broadcast(String message)
    {
        saveDot(Dot.fromMessage(message)); // Zapisuje wiadomość (czyli dot) do bazy

        for (PrintWriter writer : clientWriters.values())
        {
            writer.println(message); // Wysyła wiadomość do każdego klienta
        }
    }

    // Zapisuje obiekt Dot do bazy danych
    public void saveDot(Dot dot)
    {
        String sql = "INSERT INTO dot(x, y, color, radius) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = dbConnection.prepareStatement(sql))
        {
            stmt.setDouble(1, dot.x()); // Współrzędna X
            stmt.setDouble(2, dot.y()); // Współrzędna Y
            stmt.setString(3, dot.color().toString()); // Kolor jako String
            stmt.setDouble(4, dot.radius()); // Promień
            stmt.executeUpdate(); // Wykonanie zapytania
        }
        catch (SQLException e)
        {
            e.printStackTrace(); // Obsługa błędu
        }
    }

    // Pobiera wszystkie zapisane okręgi z bazy danych
    public List<Dot> getSavedDots()
    {
        List<Dot> dots = new ArrayList<>(); // Lista wynikowa
        String sql = "SELECT x, y, color, radius FROM dot"; // Zapytanie SQL

        try (Statement stmt = dbConnection.createStatement();
             ResultSet rs = stmt.executeQuery(sql))
        {

            while (rs.next())
            { // Przechodzenie po wynikach
                double x = rs.getDouble("x");
                double y = rs.getDouble("y");
                String colorStr = rs.getString("color");
                double radius = rs.getDouble("radius");

                // Tworzy obiekt Dot i dodaje do listy
                dots.add(new Dot(x, y, Color.web(colorStr), radius));
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace(); // Obsługa błędów
        }
        return dots; // Zwraca listę obiektów Dot
    }

    // Usuwa klienta z mapy i zamyka jego socket
    public void removeClient(Socket socket)
    {
        clientWriters.remove(socket); // Usunięcie z mapy

        try
        {
            socket.close(); // Zamknięcie połączenia
        }
        catch (IOException ignored) {} // Błąd ignorowany
    }
}