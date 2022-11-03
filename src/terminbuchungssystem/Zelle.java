package terminbuchungssystem;

import java.awt.Color;
import java.sql.SQLException;

/**
 * Objekte zur Verwendung in Termin-Tabellen.
 *
 * @author Nicholas Kappauf
 */
class Zelle {
	private final String text;
	private final Color farbe;
	private final Boolean fokussierbar;
	private final Boolean auswaehlbar;
	private final int textAusrichtung;
	private final Boolean terminfensterModus;
	private final Arzt arzt;
	private Boolean editierbar = false;
	private Buchung buchung;
	private Terminfenster terminfenster;

	/**
	 * Zelle konstruiert aus Eigenschaften des Verhaltens in der Tabelle, sowie den zugeordneten Kerndaten des Termin-
	 * fensters und der Buchung.
	 * */
 public Zelle(Terminfenster terminfenster, Buchung buchung, Color farbe, int textAusrichtung,Boolean auswaehlbar, Boolean fokussierbar, Boolean editierbar, Boolean terminfensterModus, Arzt arzt) {
		this.text = (buchung==null&&terminfenster.getAktiv())?" ":buchung==null?"":buchung.getPatientenname();
		this.farbe = farbe;
		this.fokussierbar = fokussierbar;
		this.auswaehlbar = auswaehlbar;
		this.editierbar = editierbar;
		this.textAusrichtung = textAusrichtung;
		this.buchung = buchung;
		this.terminfenster = terminfenster;
		this.terminfensterModus = terminfensterModus;
		this.arzt = arzt;
	}

	/**
	 * Zelle Konstruiert aus Eigenschaften des Verhaltens in der Tabelle.
	 * */
	public Zelle(String text, Color farbe,int textAusrichtung,Boolean auswaehlbar, Boolean fokussierbar, Boolean terminfensterModus, Arzt arzt) {
		this.text = text;
		this.farbe = farbe;
		this.fokussierbar = fokussierbar;
		this.auswaehlbar = auswaehlbar;
		this.textAusrichtung = textAusrichtung;		
		this.terminfensterModus = terminfensterModus;
		this.arzt = arzt;
	}

	/**
	 * Realisierung eines Schalter-Verhaltens für die Zelle und dessen Terminfenster.
	 * Für den Terminfenstermodus der Tabelle gedacht.
	 * */
	public void switchActive(int vonUhrzeit) {
		if (this.terminfenster != null && this.terminfenster.getAktiv()) {			
				try {
					arzt.loescheEigeneTerminfenster(this.terminfenster.getIdTerminfenster());
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}			
		}else {
		try {
					arzt.insertEigeneTerminFenster(vonUhrzeit, 1);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}		
		}

	public String toString(){
		return "";
	}

	public String getText() {
		return text;
	}

	public Color getFarbe() {
		return farbe;
	}

	public Boolean getFokussierbar() {
		return fokussierbar;
	}

	public int getTextAusrichtung() {
		return textAusrichtung;
	}

	public Boolean getAuswaehlbar() {
		return auswaehlbar;
	}

	public Buchung getBuchung() {
		return buchung;
	}

	public Terminfenster getTerminfenster() {
		return terminfenster;
	}

	public Boolean isEditierbar() {
		return editierbar;
	}

	public Boolean getTerminfensterModus() {
		return terminfensterModus;
	}

	
	
}
