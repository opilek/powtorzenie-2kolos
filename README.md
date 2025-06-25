## ✅ Zadanie 1: Rysowanie kół na kanwie (Canvas + JavaFX)
# 🔎 Co trzeba wiedzieć:
Canvas w JavaFX to komponent do rysowania 2D.

Rysujemy przy użyciu GraphicsContext, który pobieramy przez canvas.getGraphicsContext2D().

Do nasłuchiwania kliknięcia myszką służy setOnMouseClicked.

# 📌 Przykład:
```java
canvas.setOnMouseClicked(event -> {
    double x = event.getX();
    double y = event.getY();
    double radius = slider.getValue(); // zakładamy że masz suwak
    Color color = colorPicker.getValue();

    GraphicsContext gc = canvas.getGraphicsContext2D();
    gc.setFill(color);
    gc.fillOval(x - radius, y - radius, radius * 2, radius * 2);
});
```
## ✅ Zadanie 2: Serwer i klient (podstawowa komunikacja TCP)
# 🔎 Co trzeba wiedzieć:
ServerSocket słucha na danym porcie.

Socket łączy klienta z serwerem.

Do obsługi wielu klientów używamy wątków (każdy klient -> osobny ClientThread).

broadcast() = wysyłanie wiadomości do wszystkich klientów.

# 📌 Struktura:
```java
// Wątek klienta
class ClientThread extends Thread {
    Socket socket;
    PrintWriter out;
    BufferedReader in;

    public void run() {
        while ((line = in.readLine()) != null) {
            server.broadcast(line); // przesyła do wszystkich
        }
    }
}

// Serwer
class Server {
    ServerSocket serverSocket;
    List<ClientThread> clients = new ArrayList<>();

    void broadcast(String msg) {
        for (ClientThread c : clients) {
            c.out.println(msg);
        }
    }
}
```
## ✅ Zadanie 3: Rekord Dot i serializacja danych
# 🔎 Co trzeba wiedzieć:
record w Javie to uproszczona struktura do trzymania danych (jak DTO).

Przesyłamy dane jako tekst (np. "100,200,#FF0000,30").

Serializacja: obiekt → string

Deserializacja: string → obiekt

# 📌 Przykład:
```java
public record Dot(int x, int y, String color, int radius) {
    public static String toMessage(int x, int y, String color, int radius) {
        return x + "," + y + "," + color + "," + radius;
    }

    public static Dot fromMessage(String msg) {
        String[] parts = msg.split(",");
        return new Dot(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]),
                       parts[2], Integer.parseInt(parts[3]));
    }
}
```
## ✅ Zadanie 4: Połączenie klienta i obsługa rysowania po sieci
# 🔎 Co trzeba wiedzieć:
Klient wysyła dane do serwera przy kliknięciu (send()).

Serwer przekazuje dane innym klientom (broadcast()).

Klient odbiera dane i wywołuje consumer.accept(dot).

# 📌 Przykład klienta:
```java
public class ServerThread extends Thread {
    Consumer<Dot> dotConsumer;

    public void setDotConsumer(Consumer<Dot> consumer) {
        this.dotConsumer = consumer;
    }

    public void run() {
        while ((line = in.readLine()) != null) {
            Dot dot = Dot.fromMessage(line);
            dotConsumer.accept(dot); // wywołuje rysowanie
        }
    }

    public void send(Dot dot) {
        out.println(Dot.toMessage(dot.x(), dot.y(), dot.color(), dot.radius()));
    }
}
```
## ✅ Zadanie 5: Zapis do bazy danych (SQLite)
# 🔎 Co trzeba wiedzieć:
SQLite to lekka baza danych.

JDBC służy do komunikacji z bazą w Javie.

Connection, PreparedStatement, ResultSet.

# 📌 Przykład zapisu i odczytu:
```java
public void saveDot(Dot dot) throws SQLException {
    String sql = "INSERT INTO dot(x, y, color, radius) VALUES (?, ?, ?, ?)";
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setInt(1, dot.x());
        stmt.setInt(2, dot.y());
        stmt.setString(3, dot.color());
        stmt.setInt(4, dot.radius());
        stmt.executeUpdate();
    }
}

public List<Dot> getSavedDots() throws SQLException {
    List<Dot> dots = new ArrayList<>();
    String sql = "SELECT * FROM dot";
    try (Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {
        while (rs.next()) {
            dots.add(new Dot(rs.getInt("x"), rs.getInt("y"),
                             rs.getString("color"), rs.getInt("radius")));
        }
    }
    return dots;
}
```
