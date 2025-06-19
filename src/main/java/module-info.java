module org.example.powtorzenie2kolokwium {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens org.example.powtorzenie2kolokwium to javafx.fxml;
    exports org.example.powtorzenie2kolokwium;
}