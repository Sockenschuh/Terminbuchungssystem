package terminbuchungssystem;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Spezialisierter Nutzer zur Administrierung der Nutzer in der Datenbank.
 * Dieser Nutzer ist von den eigentlichen Grundfunktionen der Anwendung ausgeschlossen.
 *
 * @author Nicholas Kappauf
 */
public class Admin extends Nutzer{

    private ArrayList<Login> bekannteLogins = new ArrayList<Login>();

    public Admin(Login login, int id, boolean valideDaten, String vorname, String nachname, String postleitzahl, String ort, String strasse, String hausnummer, LocalTime erreichbarVon, LocalTime erreichbarBis, String email, String telefon) {
        super(login, id, valideDaten, vorname, nachname, postleitzahl, ort, strasse, hausnummer, erreichbarVon, erreichbarBis, email, telefon);
    }

    /**
     * Legt einen neuen Nutzer in der Datenbank an und legt das Standardpasswort fest.
     * (In der DB-Prozedur werden automatisch je nach Rolle Zugriff zu entsprechenden Prozeduren gewährt.)
     */
    public void neuerBenutzer(String klarPasswort, String benutzer, int rolle) throws SQLException {
    	if (klarPasswort.equals("") || benutzer.equals("")) {
    		throw new IllegalArgumentException();
    	}    	
        String sha512Passwort = Login.encryptText(klarPasswort);
        Db.exec("CALL neuerBenutzer('"+ Login.encryptText(klarPasswort) +"', '" + benutzer + "',"+ rolle +");", this.getLogin());
        loadNutzer();
    }

    /**
     * Löscht Nutzerzugang aus der Datenbank und macht zugewiesene Terminfenster/Buchungen effektiv unzugänglich.
     * Buchungen bleiben auf DB-Ebene gespeichert und nachvollziehbar.
     */
    public void loescheNutzer(String loginName) throws SQLException {
    	Db.exec("CALL loescheNutzer('"+ loginName +"');", this.getLogin());
    	loadNutzer();
    }


    /**
     * Lädt zu verwaltende Nutzerkonten. Zur Fehleranalyse/Nutzereinweisung werden erweiterte Nutzer-Informationen
     * von der Datenbank geladen. (valideDaten, neukonto)
     */
    public void loadNutzer() throws SQLException {
        ResultSet nutzerSet = Db.query("CALL selectNutzer();", this.getLogin());
        ArrayList<Login> result = new ArrayList<Login>();
        while(nutzerSet.next()) {
            result.add(new Login(
            		nutzerSet.getString("LoginName"),
            		nutzerSet.getInt("Rolle"),
            		nutzerSet.getBoolean("neuKonto"),
                    nutzerSet.getInt("idNutzer"),
                    nutzerSet.getString("Vorname"),
                    nutzerSet.getString("Nachname"),
                    nutzerSet.getBoolean("valideDaten")
            ));
        }
        nutzerSet.getStatement().close();
        nutzerSet.close();
        this.getLogin().getConnection().close();
        this.bekannteLogins = result;
    }

    /**
     * Für Nutzer der Klasse Admin ist diese Methode inaktiv.
     */
    public void loescheBuchung(Terminfenster fenster, Buchung buchung) throws IllegalAccessException {
        throw new IllegalAccessException("Buchung löschen: Admin hat keinen Zugang zu dieser Funktion.");
    }

    /**
     * Für Nutzer der Klasse Admin ist diese Methode inaktiv.
     */
    public void neueBuchung(Terminfenster fenster, Date datum, int platzNummer, String patient) throws IllegalAccessException {
        throw new IllegalAccessException("Buchung erzeugen: Admin hat keinen Zugang zu dieser Funktion.");
    }

    public ArrayList<Login> getBekannteLogins() {
        return bekannteLogins;
    }

}
