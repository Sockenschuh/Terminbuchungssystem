package terminbuchungssystem;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.ArrayList;

/**
 * Spezialisierter Nutzer mit Zugriff auf Terminfenster und Buchungen, welche in der Datenbank hinterlegt
 * sind. Dieser Klasse werden keine eigenen Buchungen oder Terminfenster zugeordnet.
 *
 * @author Nicholas Kappauf
 */

public class Sprechstundenhilfe extends Nutzer{

    private ArrayList<Arzt> bekannteAerzte = new ArrayList<Arzt>();

    public Sprechstundenhilfe(Login login, int id, boolean valideDaten, String vorname, String nachname, String postleitzahl, String ort, String strasse, String hausnummer, LocalTime erreichbarVon, LocalTime erreichbarBis, String email, String telefon) {
        super(login, id, valideDaten, vorname, nachname, postleitzahl, ort, strasse, hausnummer, erreichbarVon, erreichbarBis, email, telefon);
    }

    public Sprechstundenhilfe(int id, String vorname, String nachname) {
        super(id, vorname, nachname);
    }
    /**
     * Fragt eine Zusammenfassung aller Buchungen von der Datenbank ab, die in der Zukunft liegen und mit
     * dem aufgeführten Patienten übereinstimmen.
     *
     * Zum Abgleich wird zu jedem von der Datenbank erfasstem Eintrag das passende vorhandene Objekt gesucht.
     */
    public ArrayList<Buchung> buchungenNachPatient(String patient) throws SQLException {
        ResultSet buchungenSet = Db.query("CALL selectTermineNachPatient('"+patient+"');", this.getLogin());
        ArrayList<Buchung> result = new ArrayList<Buchung>();

        Terminfenster zugeordnetesTerminfenster;
        loadAerzte();

        for(Arzt arzt: bekannteAerzte){
            loadArztTerminfenster(arzt);
        }


        while(buchungenSet.next()){        	
            zugeordnetesTerminfenster = null;
            hauptloop:
            for(Arzt arzt: bekannteAerzte){
                for(Terminfenster terminfenster: arzt.getEigeneTerminfenster()){
                	terminfenster.loadSichtbareBuchungen(this.getLogin(), buchungenSet.getDate("Datum"));
                    if(buchungenSet.getInt("idTerminfenster") == terminfenster.getIdTerminfenster()){
                        for(Buchung buchung: terminfenster.getEintraege()) {
                        	if(buchung.getIdBuchung() == buchungenSet.getInt("idBuchung")) {
                        		result.add(buchung);
                        		break hauptloop;
                        	}
                        }
                    }
                }
            }
        }
        buchungenSet.getStatement().close();
        buchungenSet.close();
        this.getLogin().getConnection().close();
        return result;
    }

    /**
     * Lädt alle Ärzte aus der Datenbank, die valide Daten hinterlegt haben. Von jedem Eintrag wird eine
     * minimierte Instanz der Klasse Arzt erzeugt und in der ArrayList bekannteAerzte gespeichert.
     */
    public void loadAerzte() throws SQLException {
        ResultSet arztSet = Db.query("CALL selectAerzte();", this.getLogin());
        ArrayList<Arzt> result = new ArrayList<Arzt>();
        while(arztSet.next()) {
            result.add(new Arzt(
                    arztSet.getInt("idNutzer"),
                    arztSet.getString("Vorname"),
                    arztSet.getString("Nachname")
            ));
        }
        arztSet.getStatement().close();
        arztSet.close();
        this.getLogin().getConnection().close();
        this.bekannteAerzte = result;
    }

    public Nutzer findeMAR(int id){
        try {
            loadHilfen();
            loadAerzte();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        for(Sprechstundenhilfe hilfe: this.getBekannteHilfen()){
            if (hilfe.getId() == id){
                return hilfe;
            }
        }
        for(Arzt arzt: bekannteAerzte){
            if (arzt.getId() == id){
                return arzt;
            }
        }
        return null;
    }

    /**
     * Lädt alle Terminfenster aus der Datenbank, die dem angegebenem Arzt zugeordnet sind. Von jedem Eintrag wird eine
     * Instanz der Klasse Terminfenster erzeugt.
     *
     * NOTE: Instanzen der Klasse Arzt prüfen selbst, ob ihnen ein identisches Terminfenster bereits bekannt ist.
     */
    public void loadArztTerminfenster(Arzt arzt) throws SQLException{
        ResultSet fensterSet = Db.query("CALL selectTerminfenster("+ arzt.getId() +");", this.getLogin());
        while(fensterSet.next()) {
            new Terminfenster(    
            		arzt.getId(),
                    fensterSet.getInt("idTerminfenster"),
                    fensterSet.getTime("Von_Uhrzeit"),
                    fensterSet.getInt("plaetze"),
                    arzt,
                    fensterSet.getBoolean("aktiv"));
        }
        fensterSet.getStatement().close();
        fensterSet.close();
        this.getLogin().getConnection().close();
    }

    public ArrayList<Arzt> getBekannteAerzte() {
        return bekannteAerzte;
    }
}
