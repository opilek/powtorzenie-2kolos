package org.example.powtorzenie2kolokwium.server; // Pakiet serwera

import java.io.*;          // Import klas do obsługi wejścia/wyjścia
import java.net.Socket;    // Import klasy Socket do komunikacji sieciowej

// Klasa ClientThread rozszerza Thread i obsługuje pojedynczego klienta po stronie serwera
public class ClientThread extends Thread
{

    private final Server server;     // Referencja do głównego serwera (do broadcastowania i zarządzania klientami)
    private final Socket socket;     // Gniazdo połączenia z klientem
    private BufferedReader in;       // Strumień wejściowy do odbioru danych od klienta

    // Konstruktor klasy ClientThread – przyjmuje obiekt serwera i gniazdo klienta
    public ClientThread(Server server, Socket socket)
    {
        this.server = server;   // Przypisanie referencji do serwera
        this.socket = socket;   // Przypisanie gniazda klienta

        try
        {
            // Tworzy buforowany strumień wejściowy z gniazda klienta
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }
        catch (IOException e)
        {
            // Obsługuje ewentualny błąd przy tworzeniu strumienia wejściowego
            e.printStackTrace();
        }
    }

    @Override
    public void run()
    {
        String message; // Zmienna do przechowywania odebranej wiadomości

        try
        {
            // Pętla odczytująca wiadomości od klienta dopóki są dostępne
            while ((message = in.readLine()) != null)
            {
                server.broadcast(message); // Przekazuje wiadomość dalej do wszystkich klientów
            }
        }
        catch (IOException e)
        {
            // Obsługuje wyjątki związane z połączeniem/odczytem
            e.printStackTrace();
        }
        finally
        {
            // Po zakończeniu pracy usuwa klienta z listy w serwerze
            server.removeClient(socket);
            System.out.println("Klient rozłączony"); // Informacja diagnostyczna w konsoli
        }
    }
}