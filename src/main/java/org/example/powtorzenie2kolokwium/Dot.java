package org.example.powtorzenie2kolokwium; // Deklaracja pakietu, do którego należy ta klasa

import javafx.scene.paint.Color; // Import klasy reprezentującej kolor (z JavaFX)

// Definicja rekordu Dot (rekordy są niezmiennymi strukturami danych w Javie od wersji 16)
// Zawiera współrzędne środka (x, y), kolor oraz promień okręgu
public record Dot(double x, double y, Color color, double radius)
{

    // Statyczna metoda tworząca wiadomość tekstową do przesłania przez sieć
    // Łączy dane okręgu w postaci jednego Stringa oddzielonego średnikami
    public static String toMessage(double x, double y, double radius, String colorString)
    {
        return x + ";" + y + ";" + colorString + ";" + radius;
    }

    // Statyczna metoda odczytująca dane okręgu z przesłanej wiadomości tekstowej
    public static Dot fromMessage(String message)
    {
        // Podział wiadomości po średnikach, aby wydobyć dane
        String[] parts = message.split(";");

        // Sprawdzenie poprawności danych – musi być dokładnie 4 elementy
        if (parts.length != 4)
        {
            throw new IllegalArgumentException("Nieprawidłowy format wiadomości Dot"); // Rzucenie wyjątku jeśli format niepoprawny
        }

        // Parsowanie poszczególnych danych z tekstu do odpowiednich typów
        double x = Double.parseDouble(parts[0]); // Współrzędna X
        double y = Double.parseDouble(parts[1]); // Współrzędna Y
        String colorStr = parts[2];              // Kolor w postaci tekstu (np. 0xff0000ff)
        double radius = Double.parseDouble(parts[3]); // Promień

        // Konwersja tekstowego koloru na obiekt Color
        Color color = Color.web(colorStr);

        // Zwrócenie nowego obiektu Dot z odczytanymi danymi
        return new Dot(x, y, color, radius);
    }

    // Metoda instancyjna – konwertuje bieżący obiekt Dot do formatu wiadomości (tekstowego)
    public String toMessage()
    {
        // Używa statycznej wersji toMessage i przekazuje dane z tego obiektu
        return toMessage(x, y, radius, color.toString());
    }
}