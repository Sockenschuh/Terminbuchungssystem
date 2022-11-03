package terminbuchungssystem;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Objects;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import net.miginfocom.swing.MigLayout;

/**
 * Panel für das erstellen von Buchungen und das Festlegen von verfügbaren Terminfenstern.
 *
 * Je nach autorisiertem Nutzer stehen unterschiedliche Funktionen zur Verfügung
 *
 * @author Nicholas Kappauf
 */
public class JPanelBuchungsfenster extends JPanel {

	private ZellenEditierbarModel neuesModel;
	private JScrollPane scrollPane;
	private Arzt arzt;
	private Nutzer auth_nutzer;
	private int woche;
	JTable table;
	private ZellenRenderer meinRenderer;
	ArrayList<Object> terminfenster = new ArrayList<Object>();
	private JPanel panel;
	private JTextField jtfPatient;
	private JButton btnNeueBuchung;
	private JButton btnBuchungLoeschen;
	private JButton btnTerminfensterBearbeiten;
	private JLabel lblFehler;
	private Boolean terminfensterModus;
	private JButton btnVorherigeWoche;
	private JButton btnNaechsteWoche;
	private JButton btnArztWahl;
	private JTabbedPane tabbedPane;
	private int index;
	private JLabel lblDetailInformation;


	/**
	 * erzeugt das Panel mit einem editierbarem Kalender, einem Patienten-Textfeld, einer bestätigungstaste,
	 * einer Taste zum freigeben von ausgewählten Terminen, einer Taste zum wechseln zum Kalender eines anderen
	 * Arztes (nur für Sprechstundenhilfen aktiv) und einer Taste zum Bearbeiten der eigenen verfügbaren
	 * Terminfenster (nur für Ärzte verfügbar).
	 *
	 * Buchungen-Bearbeitungsmodus (Standardmäßig ausgewählt):
	 * -Der Kalender wird (wieder) vom Renderer vollständig von Montag bis Freitag mit Buchungen und
	 * farblich gekennzeichneter verfügbarkeit angezeigt.
	 *
	 * Terminfenstermodus:
	 * -Der Kalender wird vom Renderer auf eine aktive Spalte reduziert und keine Buchungen angezeigt.
	 * Diese verbliebene Spalte wird als "zum Buchen freigegeben" beschriftet und die verfügbarkeit farblich
	 * gekennzeichnet. Bei Klicken auf Zellen wird die Verfügbarkeit des Terminfensters im Objekt Zelle umgeschaltet.
	 *
	 * */
	public JPanelBuchungsfenster(Arzt arzt, Nutzer auth_nutzer, JTabbedPane tabbedPane, int index) {
		this.arzt = arzt;
		this.auth_nutzer = auth_nutzer;
		this.woche = 0;
		this.terminfensterModus = false;
		this.meinRenderer = new ZellenRenderer();
		this.tabbedPane = tabbedPane;
		this.index = index;

		setBounds(100, 100, 1104, 801);
		this.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.setLayout(new MigLayout("", "[][grow][]", "[grow][grow][][grow]"));

		btnVorherigeWoche = new JButton("<");
		btnVorherigeWoche.setMaximumSize(new Dimension(89, 899));
		btnVorherigeWoche.setMinimumSize(new Dimension(45, 1));
		btnVorherigeWoche.setPreferredSize(new Dimension(15, 10000));
		btnVorherigeWoche.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				woche = woche<=1?0:woche-1;
				ladeTabelleBuchungen(auth_nutzer, woche, terminfensterModus);
			}
		});

		this.add(btnVorherigeWoche, "cell 0 0");

		this.scrollPane = new JScrollPane();
		this.add(scrollPane, "cell 1 0,grow");

		panel = new JPanel();
		this.add(panel, "cell 0 3 3 1,grow");
		panel.setLayout(new MigLayout("", "[][grow][]", "[][][][]"));

		btnNeueBuchung = new JButton("ausgewähltes Fenster buchen");
		panel.add(btnNeueBuchung, "cell 0 0,growx");

		jtfPatient = new JTextField();
		panel.add(jtfPatient, "cell 1 0,growx");
		jtfPatient.setColumns(10);

		btnBuchungLoeschen = new JButton("entfernen");
		btnBuchungLoeschen.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode()==KeyEvent.VK_ENTER) {
					loeschaktion();
				}
			}
		});
		panel.add(btnBuchungLoeschen, "cell 2 0,growx");

		btnTerminfensterBearbeiten = new JButton("Eigene Verfügbarkeit bearbeiten");
		btnTerminfensterBearbeiten.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				switchModus();
				ladeTabelleBuchungen(auth_nutzer, woche, terminfensterModus);
			}
		});
		btnTerminfensterBearbeiten.setEnabled(false);
		panel.add(btnTerminfensterBearbeiten, "cell 0 1,growx");

		lblFehler = new JLabel("Fehler:");
		lblFehler.setVisible(false);
		panel.add(lblFehler, "cell 1 1");

		btnArztWahl = new JButton("Arzt wählen");
		btnArztWahl.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				switchPanel(auth_nutzer);
			}
		});
		btnArztWahl.setEnabled(false);
		panel.add(btnArztWahl, "cell 2 1,growx");
		
		lblDetailInformation = new JLabel("Erstellt von:");
		panel.add(lblDetailInformation, "cell 1 2,alignx right");


		if(arzt == auth_nutzer) {
			btnTerminfensterBearbeiten.setEnabled(true);
		}else {
			btnArztWahl.setEnabled(true);
		}


		LocalDate heute = LocalDate.now();
		int tageSeitMontag = heute.getDayOfWeek().getValue()-1;
		LocalDate montag = heute.minusDays(tageSeitMontag);

		if(auth_nutzer.getLogin().getRolle()==2) {
			Arzt auth_arzt = (Arzt) auth_nutzer;
			try {
				auth_arzt.loadEigeneTerminFenster();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			ladeTabelleBuchungen(auth_arzt,0,terminfensterModus);
		}else {
			Sprechstundenhilfe auth_sprechstundenhilfe = (Sprechstundenhilfe) auth_nutzer;
			try {
				auth_sprechstundenhilfe.loadArztTerminfenster(this.arzt);
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			ladeTabelleBuchungen(auth_sprechstundenhilfe ,0, terminfensterModus);
		}


		btnNaechsteWoche = new JButton(">");
		btnNaechsteWoche.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				woche += 1;
				ladeTabelleBuchungen(auth_nutzer, woche, terminfensterModus);
			}
		});
		btnNaechsteWoche.setPreferredSize(new Dimension(15, 10000));
		btnNaechsteWoche.setMinimumSize(new Dimension(45, 1));
		btnNaechsteWoche.setMaximumSize(new Dimension(89, 899));
		this.add(btnNaechsteWoche, "cell 2 0");
		btnBuchungLoeschen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loeschaktion();
			}
		});
		btnNeueBuchung.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				LocalDate heute = LocalDate.now();
				int tageSeitMontag = heute.getDayOfWeek().getValue()-1;

				int platz = meinRenderer.getZuletztGewaehltZeile();
				int spalte = meinRenderer.getZuletztGewaehltSpalte();
				int wochentag = spalte-1;

				LocalDate tag = heute.minusDays(tageSeitMontag).plusWeeks(woche).plusDays(wochentag);

				String patient = jtfPatient.getText();
				if (patient.isEmpty()) {
					lblFehler.setText("Fehler: Bitte Patientennamen eingeben!");
					lblFehler.setVisible(true);
				}else {
					Zelle zelle = (Zelle) neuesModel.getValueAt(platz, spalte);
					if (zelle.getBuchung()!= null) {
						lblFehler.setText("Fehler: Platz bereits vergeben!");
						lblFehler.setVisible(true);
					}else {
						try {
							auth_nutzer.neueBuchung(zelle.getTerminfenster(), java.sql.Date.valueOf(tag), platz-zelle.getTerminfenster().getVonUhrzeit(), patient);
							ladeTabelleBuchungen(auth_nutzer, woche, terminfensterModus);
						} catch (SQLException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch	(NullPointerException e2) {
							lblFehler.setText("Fehler: kein gültiges Feld ausgewählt!");
							lblFehler.setVisible(true);
						} catch (IllegalAccessException ex) {
							System.out.println("Zugang verweigert!");
							lblFehler.setText("Zugang verweigert!");
							lblFehler.setVisible(true);
						}
					}
				}
			}
		});
	}

	/**
	 * löscht dieses Panel vom TabbedPane, in welches es eingebunden ist, und fügt anstatt dessen
	 * das Panel zu Ärzteauswahl ein und selektiert diesen Tab.
	 */
	private void switchPanel(Nutzer auth_nutzer) {
		this.tabbedPane.remove(index);
		JPanel panelAuswahl = new JPanelAerzteauswahl((Sprechstundenhilfe) auth_nutzer, tabbedPane, index);
		tabbedPane.addTab("Buchungen", null, panelAuswahl, null);
		tabbedPane.setSelectedIndex(index);
	}

	/**
	 * Gibt die ausgewählte Buchung frei. aus der zuletzt ausgewählten Spalte und Zeile, welche im Renderer registriert wurden,
	 * wird die zugeordnete Instanz der Klasse Zelle ausgewählt und die in ihr gespeicherte Buchung als Parameter
	 * in die loescheBuchung Methode weitergegeben.
	 * Sollte die identifizierte Zelle keine Buchung enthalten wird der Nutzer auf den nonsense hingewiesen.
	 */
	private void loeschaktion() {
		int platz = meinRenderer.getZuletztGewaehltZeile();
		int spalte = meinRenderer.getZuletztGewaehltSpalte();

		try {
			this.auth_nutzer.loescheBuchung((Terminfenster)terminfenster.get(platz),((Zelle) table.getValueAt(platz, spalte)).getBuchung());
			ladeTabelleBuchungen(this.auth_nutzer, woche, terminfensterModus);
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}catch(NullPointerException e2) {
			lblFehler.setText("Fehler: ungültige Auswahl!");
			lblFehler.setVisible(true);
		} catch (IllegalAccessException e) {
			System.out.println("Zugang verweigert!");
			lblFehler.setText("Zugang verweigert!");
			lblFehler.setVisible(true);
		}
	}

	/**
	 * Modusschalter zum Wechsel zwischen der Funktionalität der Buchungen-Bearbeitung im Kalender und der Funktion,
	 * in welchem Ärzte ihre Tages-Terminfenster bearbeiten können. (nur für Ärzte verfügbar)
	 *
	 * Buchungen-Bearbeitungsmodus:
	 * -Sämtliche Buchungbezogenen Panel-Elemente sind aktiviert.
	 * -Der Modusschalter wird beschriftet als "Eigene Verfügbarkeit bearbeiten".
	 *
	 * Terminfenstermodus:
	 * -Sämtliche Buchungbezogenen Panel-Elemente bleiben sichtbar werden allerdings deaktiviert.
	 * -Der Modusschalter wird beschriftet als "bearbeiten beenden".
	 *
	 * Note: Schalter zur Ärztewahl bleibt für Ärzte generell inaktiv.
	 */
	public void switchModus() {
		this.terminfensterModus = !terminfensterModus;
		if (terminfensterModus) {
			btnTerminfensterBearbeiten.setText("bearbeiten beenden");
			btnBuchungLoeschen.setEnabled(false);
			btnNeueBuchung.setEnabled(false);
			btnNaechsteWoche.setEnabled(false);
			btnVorherigeWoche.setEnabled(false);
			jtfPatient.setEnabled(false);
		}else {
			btnTerminfensterBearbeiten.setText("Eigene Verfügbarkeit bearbeiten");
			btnBuchungLoeschen.setEnabled(true);
			btnNeueBuchung.setEnabled(true);
			btnNaechsteWoche.setEnabled(true);
			btnVorherigeWoche.setEnabled(true);
			jtfPatient.setEnabled(true);
		}
	}

	/**
	 * Befüllt eine neue Tabelle je nach Modus für die ausgewählte Woche.
	 *
	 * Übernimmt die Scroll-Position der vorherigen Tabelle, wenn vorhanden.
	 */
	private void ladeTabelleBuchungen(Nutzer nutzer, int plusWoche, Boolean terminfenstermodus) {
		int scrollposition = scrollPane.getVerticalScrollBar().getValue();
		neueLeereTabelle(nutzer, plusWoche, terminfenstermodus);
		LocalTime start = LocalTime.of(9, 0);
		LocalTime ende = LocalTime.of(17, 0);
		LocalTime mittagStart = LocalTime.of(12,0);
		LocalTime mittagEnde = LocalTime.of(13,30);

		Color farbeUhrzeiten = new Color(200,200,200);
		Color farbegeschlossen = new Color(210,210,210);
		Color farbeBelegt = new Color(230,150, 162);
		Color farbeOffenesfenster = new Color(240,230,160);

		lblDetailInformation.setVisible(false);
		lblFehler.setVisible(false);

		neuesModel = (ZellenEditierbarModel) table.getModel();
		for(int i=-2;i<19;i++) {
			this.terminfenster.add("");
			int ausrichtungUhrzeit = 0;

			Object[] zeile = new Object[] {
					new Zelle(start.plusMinutes(i*30).toString(), farbeUhrzeiten, ausrichtungUhrzeit, true, false, terminfenstermodus, arzt),
					new Zelle("",null,0, false,terminfenstermodus, terminfenstermodus, arzt),
					new Zelle("",terminfenstermodus?farbegeschlossen:null,0, false,false, terminfenstermodus, arzt),
					new Zelle("",terminfenstermodus?farbegeschlossen:null,0, false,false, terminfenstermodus, arzt),
					new Zelle("",terminfenstermodus?farbegeschlossen:null,0, false,false, terminfenstermodus, arzt),
					new Zelle("",terminfenstermodus?farbegeschlossen:null,0, false,false, terminfenstermodus, arzt)};
			if(i<0 || (i>5 && i<9) || i>16) {
				zeile = new Object[] {
						new Zelle(start.plusMinutes(i*30).toString(), farbeUhrzeiten, ausrichtungUhrzeit, true, false, terminfenstermodus, arzt),
						new Zelle("",farbegeschlossen,0, false,false, terminfenstermodus, arzt),
						new Zelle("",farbegeschlossen,0, false,false, terminfenstermodus, arzt),
						new Zelle("",farbegeschlossen,0, false,false, terminfenstermodus, arzt),
						new Zelle("",farbegeschlossen,0, false,false, terminfenstermodus, arzt),
						new Zelle("",farbegeschlossen,0, false,false, terminfenstermodus, arzt)};
			}
			neuesModel.addRow(zeile);
		}

		this.scrollPane.setViewportView(table);

		LocalDate heute = LocalDate.now();
		int tageSeitMontag = heute.getDayOfWeek().getValue()-1;
		LocalDate montag = heute.minusDays(tageSeitMontag).plusWeeks(plusWoche);

		for(Terminfenster fenster: this.arzt.getEigeneTerminfenster()) {
			for(int j=fenster.getVonUhrzeit();j<fenster.getVonUhrzeit()+fenster.getPlaetze();j++) {
				terminfenster.set(j, fenster);
			}
			int aktiveSpalten = terminfenstermodus?1:5;
			for(int i =0; i<aktiveSpalten; i++) {

				for(int j=fenster.getVonUhrzeit();j<fenster.getVonUhrzeit()+fenster.getPlaetze();j++) {
					neuesModel.setValueAt(new Zelle(fenster, null, fenster.getAktiv()?farbeOffenesfenster:null, 0, false, terminfenstermodus || fenster.getAktiv(), fenster.getAktiv(), terminfenstermodus, arzt), j,i+1);
				}
				try {
					fenster.loadSichtbareBuchungen(nutzer.getLogin(), java.sql.Date.valueOf(montag.plusDays(i)));
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			if(!terminfenstermodus) {
				for(Buchung buchung:fenster.getEintraege()) {
					if(buchung.getDatum().isAfter(montag.plusDays(-1)) && buchung.getDatum().isBefore(montag.plusDays(5))) {
						neuesModel.setValueAt(new Zelle(fenster, buchung, farbeBelegt,0, false, true, false, terminfenstermodus, arzt), fenster.getVonUhrzeit()+buchung.getHalbeStundeNummer(), buchung.getDatum().getDayOfWeek().getValue());

					}
				}
			}
		}

		scrollPane.getVerticalScrollBar().setValue(scrollposition);
	}

	/**
	 * stellt eine neue leere Tabelle bereit mit Listenern für die entsprechenden Modi.
	 *
	 *
	 * Listener:
	 * -Wird eine Editierung registriert, wird dieser eingetragene String in eine Instanz der Klasse Zelle überführt.
	 *
	 * - Wird im Terminfenstermodus durch einmaliges Klicken die Verfügbarkeit eines Terminfensters umgeschaltet,
	 * folgt ein neu-laden der Tabelle zum Abgleich mit der Datenbank.
	 *
	 */
	private void neueLeereTabelle(Nutzer nutzer, int plusWoche, Boolean terminfenstermodus){
		if(table != null) {
			scrollPane.remove(table);
		}
		this.terminfenster = new ArrayList<Object>();

		table = new JTable(neuesLeeresModel(plusWoche, terminfenstermodus));
		this.meinRenderer = new ZellenRenderer();
		table.setDefaultRenderer( Object.class, meinRenderer);
		table.getTableHeader().setPreferredSize(new Dimension(0,35));
		table.setRowHeight(55);
		table.setAutoscrolls(false);
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent event) {
				if(terminfenstermodus && event.getClickCount()==1) {
					ladeTabelleBuchungen(nutzer, plusWoche, terminfenstermodus);
					table.tableChanged(null);
				}else{
				try {
					Zelle zelle = (Zelle) neuesModel.getValueAt(meinRenderer.getZuletztGewaehltZeile(),meinRenderer.getZuletztGewaehltSpalte());
					if(zelle.getTerminfenster()!=null) {
						jtfPatient.requestFocus();
						jtfPatient.selectAll();
						lblDetailInformation.setVisible(false);
					}

					if(zelle.getBuchung()!=null) {
						jtfPatient.requestFocus();
						jtfPatient.selectAll();
						btnBuchungLoeschen.requestFocus();
						lblDetailInformation.setText("Angelegt von: Fr/Hr " + nutzer.findeMAR(zelle.getBuchung().getBearbeitetVon()).getNachname() + ",    am: " + zelle.getBuchung().getEinfuegeDatum());
						lblDetailInformation.setVisible(true);
					}

				}catch(ClassCastException ignore) {}

			}}
		});

		table.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				if(Objects.equals(evt.getPropertyName(), "tableCellEditor") && evt.getOldValue()!=null) {
					LocalDate heute = LocalDate.now();
					int tageSeitMontag = heute.getDayOfWeek().getValue()-1;

					int platz = meinRenderer.getZuletztGewaehltZeile();
					int spalte = meinRenderer.getZuletztGewaehltSpalte();
					int wochentag = spalte-1;

					LocalDate tag = heute.minusDays(tageSeitMontag).plusWeeks(woche).plusDays(wochentag);

					Terminfenster fenster = (Terminfenster) terminfenster.get(table.getSelectedRow());

					String patient = (String) table.getValueAt(meinRenderer.getZuletztGewaehltZeile(), meinRenderer.getZuletztGewaehltSpalte());
					if(patient.equals("")){
						lblFehler.setText("Fehler: Bitte Patientennamen eingeben!");
						lblFehler.setVisible(true);
					}else {
						try {
							nutzer.neueBuchung(fenster, java.sql.Date.valueOf(tag), platz-fenster.getVonUhrzeit(), patient);
						}catch(SQLException e) {
							//TODO EXCEPTION
						} catch (IllegalAccessException e) {
							System.out.println("Zugang verweigert!");
							lblFehler.setText("Zugang verweigert!");
							lblFehler.setVisible(true);
						}
					}
					ladeTabelleBuchungen(nutzer, plusWoche, terminfensterModus);
				}
			}
		});
	}

	/**
	 * erzeugt ein Leeres TableModel der Klasse ZellenEditierbarModel mit passenden Beschriftungen, je nach gewähltem
	 * Modus und gewählter Woche.
	 *
	 * Spaltentitel Terminfenstermodus: 	erste Spalte "Uhrzeit", zweite Spalte "Zum Buchen freigeben", Rest leer
	 * Spaltentitel Kalendermodus:			erste Spalte "Uhrzeit", danach jeweils Werktage mit dynamischem Datum
	 */
	private ZellenEditierbarModel neuesLeeresModel(int plusWoche, Boolean terminfenstermodus){
		ZellenEditierbarModel result = new ZellenEditierbarModel();
		LocalDate heute = LocalDate.now();
		int tageSeitMontag = heute.getDayOfWeek().getValue()-1;
		LocalDate montag = heute.minusDays(tageSeitMontag).plusWeeks(plusWoche);

		result.addColumn("Termin Uhrzeit");
		if(terminfenstermodus) {
			result.addColumn("<html> zum Buchen <br>freigegeben");
			result.addColumn("<html>   <br> ");
			result.addColumn("<html>   <br> ");
			result.addColumn("<html>   <br> ");
			result.addColumn("<html>   <br> ");
		}else {
			result.addColumn("<html><center>Montag<br>" +  montag.plusDays(0).toString());
			result.addColumn("<html><center>Dienstag<br>" + montag.plusDays(1).toString());
			result.addColumn("<html><center>Mittwoch<br>" + montag.plusDays(2).toString());
			result.addColumn("<html><center>Donnerstag<br>" + montag.plusDays(3).toString());
			result.addColumn("<html><center>Freitag<br>" + montag.plusDays(4).toString());  }

		return result;
	}



}





