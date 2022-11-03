package terminbuchungssystem;

import java.sql.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;

/**
 * Terminfenster dienen als Container von Buchungen und ist immer exakt einem Arzt zugewiesen.
 * Terminfenster sind aktiv oder inaktiv beginnen zu einem Zeitpunkt und haben eine Länge von plaetze * 30 Minuten.
 *
 * @author Nicholas Kappauf
 */
public class Terminfenster {
    private final Arzt arzt;
    private final int idTerminfenster;
    private final int vonUhrzeit;
    private final int plaetze;
    private Boolean aktiv;

    private ArrayList<Buchung> eintraege = new ArrayList<Buchung>();

    /**
     * Konstruiert ein Terminfenster, welches sich automatisch im zugeordnetem Arzt einfügt.
     *
     * NOTE: Instanzen der Klasse Arzt prüfen selbst, ob ihnen ein identisches Terminfenster bereits bekannt ist.
     */
    public Terminfenster(int idArzt, int idTerminfenster, Time vonUhrzeit, int plaetze, Arzt arzt, Boolean aktiv) {
        this.arzt = arzt;
        this.idTerminfenster = idTerminfenster;
        this.aktiv = aktiv;
        LocalTime start = LocalTime.of(8, 0);

        int uhrzeit = -2;
        for (int i = -2; i < 18; i++) {
            if (vonUhrzeit.toLocalTime().isAfter(start.plusMinutes(i * 30))) {
                uhrzeit++;
            }
        }
        this.vonUhrzeit = uhrzeit;
        this.plaetze = plaetze;
        arzt.addTerminfenster(this);
    }


    /**
     * Lädt, je nach Datum, Buchungen aus der Datenbank.
     */
    public void loadSichtbareBuchungen(Login login, Date datum) throws SQLException {
        ResultSet buchungenSet = Db.query("CALL selectTermineNachDatum(" + this.idTerminfenster + ",'" + datum + "');", login);
        while (buchungenSet.next()) {
            boolean nichtBekannt = true;
            this.addBuchung(new Buchung(
                    buchungenSet.getInt("bearbeitetVon"),
                    buchungenSet.getInt("idBuchung"),
                    buchungenSet.getDate("Datum"),
                    buchungenSet.getTimestamp("einfuegeDatum"),
                    buchungenSet.getInt("HalbeStundeNummer"),
                    buchungenSet.getString("Patientenname"),
                    this
            ));
        }
        buchungenSet.getStatement().close();
        buchungenSet.close();
        login.getConnection().close();
    }

    /**
     * gleicht eine neue Buchung mit den bereits bekannten Buchungen ab
     * und fügt nur ein, wenn diese Buchung noch unbekannt ist.
     */
    public void addBuchung(Buchung neueBuchung) {
        boolean nichtBekannt = true;
        for (Buchung buchung : eintraege) {
            if (buchung.getIdBuchung() == neueBuchung.getIdBuchung()) {
                nichtBekannt = false;
                break;
            }
        }
        if (nichtBekannt) {
            eintraege.add(neueBuchung);
        }
    }
    /**
     * sucht nach einer Buchung und entfernt diese aus der Liste, wenn gefunden.
     */
    public void loescheBuchung(Buchung remBuchung) {
        for (Buchung buchung : eintraege) {
            if (buchung.getIdBuchung() == remBuchung.getIdBuchung()) {
                eintraege.remove(buchung);
                break;
            }
        }
    }

    public ArrayList<Buchung> getEintraege() {
        return eintraege;
    }

    public Arzt getArzt() {
        return arzt;
    }

    public int getIdTerminfenster() {
        return idTerminfenster;
    }

    public int getVonUhrzeit() {
        return vonUhrzeit;
    }

    public int getPlaetze() {
        return plaetze;
    }

    public Boolean getAktiv() {
        return aktiv;
    }

    public void setAktiv(Boolean aktiv) {
        this.aktiv = aktiv;
    }


}
