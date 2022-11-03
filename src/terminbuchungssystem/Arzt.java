package terminbuchungssystem;

import java.sql.*;
import java.time.LocalTime;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Spezialisierter Nutzer mit Zugriff auf seine eigenen Terminfenster und Buchungen, welche in der Datenbank hinterlegt
 * sind. Bildet Container für Terminfenster, welche nur diese Instanz selbst anlegen/bearbeiten kann.
 *
 * @author Nicholas Kappauf
 */

public class Arzt extends Nutzer{

    ArrayList<Terminfenster> eigeneTerminfenster = new ArrayList<Terminfenster>();

    ArrayList<Sprechstundenhilfe> bekannteSprechstundenhilfen = new ArrayList<Sprechstundenhilfe>();

    public Arzt(Login login, int id, boolean valideDaten, String vorname, String nachname, String postleitzahl, String ort, String strasse, String hausnummer, LocalTime erreichbarVon, LocalTime erreichbarBis, String email, String telefon) {
        super(login, id, valideDaten, vorname, nachname, postleitzahl, ort, strasse, hausnummer, erreichbarVon, erreichbarBis, email, telefon);
    }

    /**
     * Minimierter Konstruktor zum Erzeugen einer Instanz dieser Klasse für Sprechstundenhilfen.
     * Die Instanz muss durch den Hauptnutzer und dessen zugriffsrechte gesteuert werden.
     */

    public Arzt(int id, String vorname, String nachname) {
        super(id, vorname, nachname);
    }

    /**
     * erzeugt neue Zeitfenster bestimmter Länge, wenn nicht bereits vorhanden. Wenn ein identisches Fenster vorhanden
     * ist, wird das gefundene Fenster übernommen und aktiviert.
     *
     * Terminfenster werden automatisch der ausführenden Instanz zugeordnet.
     *
     * Zur Reduktion der möglichen Fenster werden regulär Terminfenster mit mehr als 1 Platz in mehrere Terminfenster
     * mit jeweils nur 1 Platz aufgespalten.
     */
    public void insertEigeneTerminFenster(int vonUhrzeit, int plaetze) throws SQLException {
        if(plaetze != 1) {
        	loadEigeneTerminFenster();
        	for (int i=0;i<plaetze;i++) {        		
        		boolean bekannt = false;
        		Terminfenster bekanntesFenster = null;
        		for(Terminfenster fenster: eigeneTerminfenster) {
        			if (fenster.getVonUhrzeit() == vonUhrzeit && fenster.getPlaetze() == 1) {
        				bekannt = true;
        				bekanntesFenster = fenster;
        				break;
        			}
        		}
        		if (bekannt) {
        			bekanntesFenster.setAktiv(true);
        			Db.exec("CALL aktiviereEigeneTerminfenster("+bekanntesFenster.getIdTerminfenster()+");", this.getLogin());
        		}else {        		
        		insertEigeneTerminFenster(vonUhrzeit+i, 1);
        		}
        	}
        }    	
    	Db.exec("CALL insertEigeneTerminfenster('"+Time.valueOf(LocalTime.parse("08:00").plusMinutes(vonUhrzeit*30)) +"',"+plaetze+", "+1+");", this.getLogin());
        loadEigeneTerminFenster();
    }


    /**
     * Deaktiviert ein Zeitfenster.
     */
    public void loescheEigeneTerminfenster(int idFenster) throws SQLException {
        Db.exec("CALL deaktiviereEigeneTerminfenster("+idFenster+");", this.getLogin());
        for (Terminfenster fenster: eigeneTerminfenster){
            if (fenster.getIdTerminfenster() == idFenster){
                fenster.setAktiv(false);
                break;
            }
        }
    }

    /**
     * Fragt alle Termine eines Monats ab.
     * Für jede erhaltene Buchung wird die Zählung des entsprechenden Tages des Monats um 1 erhöht.
     *
     * Gibt einen Array mit abwechselnd dem Tag des Monats und danach die Anzahl der Buchungen dieses Tages zurück.
     *
     * @return ArrayList {LocaleDate, Integer, LocaleDate, Integer, ... LocaleDate, Integer}
     */
    public ArrayList<Object> eigeneMonatsUebersicht(int zukunftMonat) throws SQLException {
        ResultSet buchungenSet = Db.query("CALL selectEigeneTermineNachMonat("+zukunftMonat+");", this.getLogin());
        LocalDate startdatum = LocalDate.now().minusDays(LocalDate.now().getDayOfMonth()-1).plusMonths(zukunftMonat);
        LocalDate enddatum = LocalDate.now().minusDays(LocalDate.now().getDayOfMonth()-1).plusMonths(zukunftMonat+1);
        ArrayList<LocalDate> daten = new ArrayList<>();
        ArrayList<Integer> zaehlung = new ArrayList<>();

        LocalDate datum = startdatum;
        int index = 0;
        while(!datum.equals(enddatum)){
            daten.add(datum);
            zaehlung.add(0);
            datum = datum.plusDays(1);
           }       
            
        while(buchungenSet.next()){
            datum = startdatum;
            index = 0;
            while(!datum.equals(enddatum)){
            	if (buchungenSet.getDate("Datum").toString().equals(Date.valueOf(datum).toString())){
                    zaehlung.set(index, zaehlung.get(index)+1);
                    break;
                }
                index += 1;
                datum = datum.plusDays(1);
            }             
        }
        
        buchungenSet.getStatement().close();
        buchungenSet.close();
        this.getLogin().getConnection().close();

        ArrayList<Object> result = new ArrayList<Object>();
        for (int i = 0; i < daten.size(); i++) {
            result.add(daten.get(i));
            result.add(zaehlung.get(i));
        }
        return result;
    }


    /**
     * gleicht der Instanz zugeordneten Terminfenster mit der Datenbank ab.
     */
    public void loadEigeneTerminFenster() throws SQLException {
        ResultSet fensterSet = Db.query("CALL selectEigeneTerminfenster();", this.getLogin());
        while(fensterSet.next()){
            boolean nichtBekannt = true;
            for (Terminfenster fenster: eigeneTerminfenster){
                if (fenster.getIdTerminfenster() == fensterSet.getInt("idTerminfenster")){
                    nichtBekannt = false;
                    break;
                }
            }
            if(nichtBekannt){
                new Terminfenster(
                        this.getId(),
                        fensterSet.getInt("idTerminfenster"),
                        fensterSet.getTime("Von_Uhrzeit"),
                        fensterSet.getInt("plaetze"),
                        this,
                        fensterSet.getBoolean("aktiv"));
            }
        }
        fensterSet.getStatement().close();
        fensterSet.close();
        this.getLogin().getConnection().close();
    }

    /**
     * füllt die Lokale ArrayList mit einem neuen Eintrag wenn unbekannt.
     * Datenbank-Abgleich sollte vorher stattfinden und bestätigt sein
     */
    public void addTerminfenster(Terminfenster neuesFenster){
        boolean nichtBekannt = true;
        for (Terminfenster fenster: eigeneTerminfenster){
            if (fenster.getIdTerminfenster() == neuesFenster.getIdTerminfenster()){
                nichtBekannt = false;
                break;
            }
        }
        if(nichtBekannt) {
            eigeneTerminfenster.add(neuesFenster);
        }
    }

    public ArrayList<Terminfenster> getEigeneTerminfenster() {
        return eigeneTerminfenster;
    }

    public ArrayList<Sprechstundenhilfe> getBekannteSprechstundenhilfen() {
        return bekannteSprechstundenhilfen;
    }
}
