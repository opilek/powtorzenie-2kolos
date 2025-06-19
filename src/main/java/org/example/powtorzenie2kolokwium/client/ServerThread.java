package org.example.powtorzenie2kolokwium.client; // Pakiet klienta

import org.example.powtorzenie2kolokwium.Dot; // Import klasy Dot (model danych kółka)

import java.io.*; // Import do obsługi strumieni wejścia/wyjścia
import java.net.Socket; // Import klasy Socket (połączenie TCP)
import java.util.function.Consumer; // Import funkcjonalnego interfejsu Consumer

public class ServerThread extends Thread
{ // Klasa dziedziczy po Thread, aby działać jako osobny wątek

    private final Socket socket; // Gniazdo TCP do komunikacji z serwerem
    private final BufferedReader in; // Buforowany czytnik do odbierania danych z serwera
    private final PrintWriter out; // Strumień do wysyłania danych na serwer

    private Consumer<Dot> dotConsumer; // Konsument obiektu Dot – służy do przekazania działania np. rysowania

    // Konstruktor – tworzy połączenie z serwerem
    public ServerThread(String address, int port) throws IOException
    {
        this.socket = new Socket(address, port); // Tworzy nowe połączenie z adresem i portem
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream())); // Odbiór danych z serwera
        this.out = new PrintWriter(socket.getOutputStream(), true); // Wysyłanie danych do serwera (auto-flush = true)
    }

    @Override
    public void run()
    {
        try
        {
            String line;
            // Odczyt danych z serwera w pętli – dopóki są dostępne
            while ((line = in.readLine()) != null)
            {
                Dot dot = Dot.fromMessage(line); // Parsuje wiadomość do obiektu Dot
                if (dotConsumer != null)
                {
                    dotConsumer.accept(dot); // Przekazuje dot do ustalonej metody (np. rysowania)
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace(); // Obsługa błędów połączenia
        }
        finally
        {
            try { socket.close(); } catch (IOException ignored) {} // Zamknięcie połączenia przy zakończeniu
        }
    }

    // Wysyła wiadomość tekstową do serwera
    public void send(String message)
    {
        out.println(message); // Wysyła wiadomość przez PrintWriter
    }

    // Tworzy wiadomość z danych kółka i wysyła ją
    public void send(double x, double y, double radius, String colorStr)

    {
        String msg = Dot.toMessage(x, y, radius, colorStr); // Tworzy wiadomość w formacie tekstowym
        send(msg); // Wysyła ją
    }

    // Ustawia funkcję, która zostanie wywołana po odebraniu nowego kółka
    public void setDotConsumer(Consumer<Dot> dotConsumer)
    {
        this.dotConsumer = dotConsumer;
    }
}