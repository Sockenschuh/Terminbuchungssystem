package terminbuchungssystem;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.sql.Date;

/**
 * Speichert sämtliche personenbezogenen Nutzer-Daten und prüft diese auf Plausibilität. Wenn alle Daten vollständig
 * und plausibel sind, wird valideDaten auf true gesetzt.
 *
 * Ist immer einem Login zugeordnet, welches die Rolle des Nutzers festlegt.
 *
 * @author Nicholas Kappauf
 */

public class Nutzer {
    private Login login;
    private final int id;
    private boolean valideDaten;
    private String vorname;
    private String nachname;
    private String postleitzahl;
    private String ort;
    private String strasse;
    private String hausnummer;
    private LocalTime erreichbarVon;
    private LocalTime erreichbarBis;
    private String email;
    private String telefon;
    private ArrayList<Sprechstundenhilfe> bekannteHilfen;



    /**
     * Konstruktor für die Vollfunktionale Instanzierung des aktuellen Hauptnutzers.
     */
    public Nutzer(Login login, int id, boolean valideDaten, String vorname, String nachname, String postleitzahl, String ort, String strasse, String hausnummer, LocalTime erreichbarVon, LocalTime erreichbarBis, String email, String telefon) {
        this.login = login;
        this.id = id;
        this.valideDaten = valideDaten;
        this.vorname = vorname;
        this.nachname = nachname;
        this.postleitzahl = postleitzahl;
        this.ort = ort;
        this.strasse = strasse;
        this.hausnummer = hausnummer;
        this.erreichbarVon = erreichbarVon;
        this.erreichbarBis = erreichbarBis;
        this.email = email;
        this.telefon = telefon;
    }

    /**
     * Minimierter Konstruktor um Fremdnutzer für den Administrator zu identifizieren,
     * angepasst an reduzierte SQL-Anweisungen, um Benutzerdaten zu schützen.
     */
    public Nutzer(int id, String vorname, String nachname, boolean valideDaten) {
        this.id = id;
        this.vorname = vorname;
        this.nachname = nachname;
        this.valideDaten = valideDaten;
    }

    /**
     * Minimierter Konstruktor um Fremdnutzer zu identifizieren, angepasst an reduzierte SQL-Anweisungen,
     * um Benutzerdaten zu schützen.
     */
    public Nutzer(int id, String vorname, String nachname) {
        this.id = id;
        this.vorname = vorname;
        this.nachname = nachname;
    }

    public void loadHilfen() throws SQLException {
        ResultSet hilfenSet = Db.query("CALL selectSprechstundenhilfen();", this.getLogin());
        ArrayList<Sprechstundenhilfe> result = new ArrayList<Sprechstundenhilfe>();
        while(hilfenSet.next()) {
            result.add(new Sprechstundenhilfe(
                    hilfenSet.getInt("idNutzer"),
                    hilfenSet.getString("Vorname"),
                    hilfenSet.getString("Nachname")
            ));
        }
        hilfenSet.getStatement().close();
        hilfenSet.close();
        this.getLogin().getConnection().close();
        this.bekannteHilfen = result;
    }

    public Nutzer findeMAR(int id) {
        try {
            loadHilfen();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        for(Sprechstundenhilfe hilfe: bekannteHilfen){
            if (hilfe.getId() == id){
                return hilfe;
            }
        }
        return this;
    }

    /**
     * Erzeugt neue Buchung in der Datenbank und gleicht im Anschluss das entsprechende Terminfenster ab.
     * Nutzer der Klasse Arzt haben nur für eigene Terminfenster Zugang auf DB-Ebene.
     */
    public void neueBuchung(Terminfenster fenster, Date datum, int platzNummer, String patient) throws SQLException, IllegalAccessException {
        Db.exec("CALL insertTermin("+fenster.getIdTerminfenster()+",'"+datum+"',"+platzNummer+",'"+patient+"');", this.login);
        fenster.loadSichtbareBuchungen(this.login , datum);
    }

    /**
     * löscht eine Buchung aus der Datenbank und gleicht im Anschluss das entsprechende Terminfenster ab.
     * Nutzer mit Rolle 2 (Arzt) haben nur für eigene Terminfenster Zugang auf DB-Ebene.
     */
    public void loescheBuchung(Terminfenster fenster, Buchung buchung) throws SQLException, IllegalAccessException {
        Db.exec("CALL deleteTermin("+buchung.getIdBuchung()+");", this.login);
        fenster.loescheBuchung(buchung);
    }

    /**
     * Realisierung der Plausibilitätsprüfungen (siehe Dokumentation)
     */
    public String valdiereEingabe(String input, String typ){
        if (input == null){
            throw new IllegalArgumentException();
        }
        switch(typ) {
            case ("telefon"):
                if (input.replaceAll(" ", "").matches("^([0-9|+]?[0-9]+([/]{0}|[/]{2})?[0-9]+)|([(]{1}[0-9]+[)]{1}[0-9]+)$"))
                    return input.replaceAll(" ", "");
                break;
            case ("name"):
                if (input.matches("([A-ZÄÖÜ]?[a-zäöüß]+){1}(([ \\-']){1}[A-ZÄÖÜ]?[a-zäöüß]+)*"))
                    return input;
                break;
            case ("email"): // General Email Regex (RFC 5322 Official Standard) Quelle: http://emailregex.com/
                if (input.toLowerCase().matches("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)])"))
                    return input;
                break;
            case ("plz"):
                if (input.matches("[0-9]{5}"))
                    return input;
                break;
            case ("ort"):
                if (input.matches("([A-ZÄÖÜ]?[a-zäöüß]+){1}(([ \\-]){1}[A-ZÄÖÜ]?[a-zäöüß]+)*"))
                    return input;
                break;
            case ("strasse"):
                if (input.matches("([A-ZÄÖÜ]?[a-zäöüß]+[\\.]?){1}(([ \\-]){1}[A-ZÄÖÜ]?[a-zäöüß]+[\\.]?)*"))
                    return input;
                break;
            case ("hausnummer"):
                if (input.matches("[1-9]{1}[0-9]*[ \\-]?[a-zA-Z]?"))
                    return input;
                break;
            default:
                System.out.println("FALSCHER TYP-STRING");
                throw new IllegalArgumentException();
        }
        throw new IllegalArgumentException();
    }

    /**
     * erzeugen einer SQL UPDATE-Anweisung mit allen lokal gespeicherten Daten.
     * Diese wird ausgeführt und im Anschluss die Vollständigkeit des Datensatzes auf DB-Ebene geprüft.
     */
    public void updateDatenpunkte() throws SQLException {
    	
    	String vorname = this.vorname==null?"null,":"\""+this.vorname+"\",";
    	String nachname = this.nachname==null?"null,":"\""+this.nachname+"\",";
    	String postleitzahl = this.postleitzahl==null?"null,":"'"+this.postleitzahl+"',";
    	String ort = this.ort==null?"null,":"'"+this.ort+"',";
    	String strasse = this.strasse==null?"null,":"'"+this.strasse+"',";
    	String hausnummer = this.hausnummer==null?"null,":"'"+this.hausnummer+"',";
    	String email = this.email==null?"null,":"'"+this.email+"',";
    	String telefon = this.telefon==null?"null,":"'"+this.telefon+"',";
    	String von = this.erreichbarVon==null?"null,":"'"+this.erreichbarVon.toString()+"',";
    	String bis = this.erreichbarBis==null?"null":"'"+this.erreichbarBis.toString()+"'";
        //System.out.println("CALL updateEigeneKontoDaten("+vorname+nachname+postleitzahl+ort+strasse+hausnummer+email+telefon+von+bis+");");
        Db.exec("CALL updateEigeneKontoDaten("+vorname+nachname+postleitzahl+ort+strasse+hausnummer+email+telefon+von+bis+");", this.login);
        Db.exec("CALL eigeneVollstaendigkeitPruefen();", this.login);
        ResultSet daten = Db.query("CALL eigeneDaten();", this.login);
        if (daten.next()){
        	this.valideDaten = daten.getBoolean(daten.findColumn("valideDaten"));
        }
        daten.getStatement().close();
        daten.close();
        this.login.getConnection().close();
    }

    public String getPostleitzahl() {
        return postleitzahl;
    }

    public void setPostleitzahl(String postleitzahl) throws SQLException {
        this.postleitzahl = valdiereEingabe(postleitzahl, "plz");
        updateDatenpunkte();
    }

    public String getOrt() {
        return ort;
    }

    public void setOrt(String ort) throws SQLException {
        this.ort = valdiereEingabe(ort, "ort");
        updateDatenpunkte();
    }

    public String getHausnummer() {
        return hausnummer;
    }

    public void setHausnummer(String hausnummer) throws SQLException {
        this.hausnummer = valdiereEingabe(hausnummer, "hausnummer");
        updateDatenpunkte();
    }

    public Login getLogin() {
        return login;
    }

    public boolean isValideDaten() {
        return valideDaten;
    }

    public String getVorname() {
        return vorname;
    }

    public void setVorname(String vorname) throws SQLException {
        this.vorname = valdiereEingabe(vorname, "name");
        updateDatenpunkte();
    }

    public String getNachname() {
        return nachname;
    }

    public void setNachname(String nachname) throws SQLException {
        this.nachname = valdiereEingabe(nachname, "name");
        updateDatenpunkte();
    }

    public LocalTime getErreichbarVon() {
        return erreichbarVon;
    }

    public void setErreichbarVon(LocalTime erreichbarVon) throws SQLException { //throws DateTimeParseException
        this.erreichbarVon = erreichbarVon;
        updateDatenpunkte();
    }

    public LocalTime getErreichbarBis() {
        return erreichbarBis;
    }

    public void setErreichbarBis(LocalTime erreichbarBis) throws SQLException { //throws DateTimeParseException
        this.erreichbarBis = erreichbarBis;
        updateDatenpunkte();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) throws SQLException {
        this.email = valdiereEingabe(email, "email");
        updateDatenpunkte();
    }

    public String getTelefon() {
        return telefon;
    }

    public void setTelefon(String telefon) throws SQLException {
        this.telefon = valdiereEingabe(telefon, "telefon");
        updateDatenpunkte();
    }

    public int getId() {
        return id;
    }

    public String getStrasse() {
        return strasse;
    }

    public void setStrasse(String strasse) throws SQLException {
        this.strasse = valdiereEingabe(strasse, "strasse");
        updateDatenpunkte();
    }

    public ArrayList<Sprechstundenhilfe> getBekannteHilfen() {
        return bekannteHilfen;
    }
}
