package terminbuchungssystem;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.sql.Date;

/**
 * Kerninformation der Applikation. Die Buchung speichert, an welchem Datum, in welchem Terminfenster, an welchem
 * Platz in diesem Terminfenster sie sich befindet und welcher Patienten diesen Platz beansprucht.
 * Über das Terminfenster ist die Buchung eindeutig einem Arzt zugewiesen.
 *
 * 'einfuegeDatum' und 'bearbeitetVon' bilden das Protokoll der Erzeugung in der Datenbank und
 *  könnten ausgelesen werden, ihre Nutzung ist jedoch noch unbestimmt.
 *
 * @author Nicholas Kappauf
 */

public class Buchung {
    private final Terminfenster terminfenster;
	private final int bearbeitetVon;
    private final int idBuchung;
    private final LocalDate datum;
    private final Timestamp einfuegeDatum;
    private final int HalbeStundeNummer;
    private final String Patientenname;    

    public Buchung(int bearbeitetVon, int idBuchung, Date datum, Timestamp einfuegeDatum, int halbeStundeNummer, String patientenname, Terminfenster fenster) {
        this.terminfenster = fenster;
    	this.bearbeitetVon = bearbeitetVon;
        this.idBuchung = idBuchung;
        this.datum = datum.toLocalDate();
        this.einfuegeDatum = einfuegeDatum;
        HalbeStundeNummer = halbeStundeNummer;
        Patientenname = patientenname;        
        fenster.addBuchung(this);
    }

    public int getBearbeitetVon() {
        return bearbeitetVon;
    }

    public int getIdBuchung() {
        return idBuchung;
    }
    
    public Terminfenster getTerminfenster() {
        return terminfenster;
    }

    public LocalDate getDatum() {
        return datum;
    }

    public Timestamp getEinfuegeDatum() {
        return einfuegeDatum;
    }

    public int getHalbeStundeNummer() {
        return HalbeStundeNummer;
    }

    public String getPatientenname() {
        return Patientenname;
    }
}
