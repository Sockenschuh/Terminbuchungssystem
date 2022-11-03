package terminbuchungssystem;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.Objects;

import static terminbuchungssystem.Db.exec;

/**
 * Speichert die Login-Daten eines Benutzers nach Anmeldung und realisiert den wiederholten Verbindungs-
 * aufbau zur Datenbank. Das Passwort wird hierbei vor dem Abspeichern verschlüsselt.
 * Prüft neu vergebene Passwörter auf Mindestanforderungen, und gibt diese verschlüsselt an die Datenbank weiter.
 * Speichert außerdem, ob jemals ein eigenes Passwort erfolgreich erstellt wurde.
 *
 * Ist immer einem Nutzer zugeordnet und speichert die vorgesehene Rolle zur Erstellung des passenden Benutzerkontos.
 *
 * @author Nicholas Kappauf
 */

public class Login {
    private String encryptedPsw;
    private final String benutzer;
    private final int rolle;
    private boolean neukonto;
    private Connection connection;
    private Nutzer nutzer;


    /**
     * HauptanwenderKonstruktor
     *
     * Im Konstruktor werden über den erfolgreichen Login die nötigen Daten zum Konstruieren der Instanz
     * gelesen. Es wird ein Nutzer mit passender Rolle erstellt und mit den in der Datenbank hinterlegten
     * Daten befüllt.
     */

    public Login(String Psw, String benutzer) throws SQLException, IllegalArgumentException {
        this.encryptedPsw = encryptText(Psw);
        this.benutzer = benutzer;
        try {
            connect();
        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        }
        ResultSet daten = Db.query("CALL eigeneDaten();", this);

        if (daten.next()){
        this.rolle = daten.getInt("Rolle");
        this.neukonto = daten.getBoolean("neukonto");

        if (this.rolle == 1) {
        	this.nutzer = new Admin(this,
                    daten.getInt("idNutzer"),
                    daten.getBoolean("valideDaten"),
                    daten.getString("Vorname"),
                    daten.getString("Nachname"),
                    daten.getString("Postleitzahl"),
                    daten.getString("Ort"),
                    daten.getString("Strasse"),
                    daten.getString("Hausnummer"),
                    daten.getTime("Von_Uhrzeit")==null?null:daten.getTime("Von_Uhrzeit").toLocalTime(),
                    daten.getTime("Bis_Uhrzeit")==null?null:daten.getTime("Bis_Uhrzeit").toLocalTime(),
                    daten.getString("Email"),
                    daten.getString("Telefon"));
        }
        if(rolle == 2) {
            this.nutzer = new Arzt(this,
                    daten.getInt("idNutzer"),
                    daten.getBoolean("valideDaten"),
                    daten.getString("Vorname"),
                    daten.getString("Nachname"),
                    daten.getString("Postleitzahl"),
                    daten.getString("Ort"),
                    daten.getString("Strasse"),
                    daten.getString("Hausnummer"),
                    daten.getTime("Von_Uhrzeit")==null?null:daten.getTime("Von_Uhrzeit").toLocalTime(),
                    daten.getTime("Bis_Uhrzeit")==null?null:daten.getTime("Bis_Uhrzeit").toLocalTime(),
                    daten.getString("Email"),
                    daten.getString("Telefon"));
        }
        if(rolle == 3){
            this.nutzer = new Sprechstundenhilfe(this,
                    daten.getInt("idNutzer"),
                    daten.getBoolean("valideDaten"),
                    daten.getString("Vorname"),
                    daten.getString("Nachname"),
                    daten.getString("Postleitzahl"),
                    daten.getString("Ort"),
                    daten.getString("Strasse"),
                    daten.getString("Hausnummer"),
                    daten.getTime("Von_Uhrzeit")==null?null:daten.getTime("Von_Uhrzeit").toLocalTime(),
                    daten.getTime("Bis_Uhrzeit")==null?null:daten.getTime("Bis_Uhrzeit").toLocalTime(),
                    daten.getString("Email"),
                    daten.getString("Telefon"));
        }

        daten.getStatement().close();
        daten.close();
        this.connection.close();
    }else {this.rolle = 0;}
    }

    /**
     * Hilfskonstruktor
     *
     * Konstruiert eine Instanz, welche lediglich die rudimentär notwendigen Daten enthält, um einen fremden Nutzer
     * zu identifizieren. Für diese Instanz ist kein Verbindungsaufbau möglich.
     */

    public Login(String benutzer, int rolle, boolean neukonto, int idNutzer, String vorname, String nachname, boolean valideDaten) {
    	this.benutzer = benutzer;
    	this.rolle = rolle;
    	this.neukonto = neukonto;
    	this.nutzer = new Nutzer(idNutzer, vorname, nachname, valideDaten);
    }       
    

    public void connect() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/terminvergabesystem";
       // System.out.println("logging in as " + this.benutzer + " with password " + this.encryptedPsw);
        this.connection = DriverManager.getConnection(url, this.benutzer, this.encryptedPsw);
    }

    /**
     * Prüfung der Passwortanforderungen für das gelieferte Passwort. Wenn erfolgreich, Weitergabe an die Datenbank und
     * dauerhaftes setzen der neukonto-variable auf false.
     */
    public void setEncryptedPsw(String klarPasswort) throws SQLException {
        if (klarPasswort.matches(".{12,}") &&
                klarPasswort.matches(".*[A-ZÄÖÜ]+.*") &&
                klarPasswort.matches(".*[a-zäöü]+.*") &&
                klarPasswort.matches(".*[1-9]+.*") &&
                klarPasswort.matches(".*(?=\\.*?[#?!@$%^&*-\\+])+.*")){
            exec("CALL updateEigenesPasswort('"+klarPasswort+"', '"+ encryptText(klarPasswort) +"');", this);
            this.encryptedPsw = encryptText(klarPasswort);
            this.neukonto = false;
        }else throw new IllegalArgumentException();
    }

    /**
     * Verschlüsselt Klartext in SHA512 verschlüsselten Text mittels Fremdsoftware:
     *      terminbuchungssystem.EncryptPassword.SHA512()
     */
    public static String encryptText(String plainText){
        try {
            return EncryptPassword.SHA512(plainText);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            System.out.println("Text konnte nicht verschlüsselt werden! " + e.toString());
            return "";
        }
    }

    public boolean isNeukonto() {
        return neukonto;
    }

    public int getRolle() {
        return rolle;
    }

    public Connection getConnection() {
        return connection;
    }


    public String getEncryptedPsw() {
        return encryptedPsw;
    }

    public String getBenutzer() {
        return benutzer;
    }

    public Nutzer getNutzer() {
        return nutzer;
    }

}
