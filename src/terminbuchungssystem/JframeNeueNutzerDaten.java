package terminbuchungssystem;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import net.miginfocom.swing.MigLayout;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.DefaultComboBoxModel;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.time.LocalTime;
import java.awt.event.ActionEvent;
import javax.swing.border.EtchedBorder;


/**
 * Fenster zum Festlegen neuer Nutzerdaten.
 *
 * @author Nicholas Kappauf
 */
public class JframeNeueNutzerDaten extends JFrame {

	private JPanel contentPane;
	private JTextField jtfVorname;
	private JTextField jtfNachname;
	private JTextField jtfPostleitzahl;	
	private JTextField jtfOrt;
	private JTextField jtfStrasse;
	private JTextField jtfHausnummer;
	private JTextField jtfEmail;
	private JTextField jtfTelefon;
	private JLabel lblPostleitzahl;
	private JLabel lblOrt;
	private JLabel lblStrasse;
	private JLabel lblHausnummer;
	private JLabel lblAdresse;
	private JPanel panel_1;
	private JLabel lblErreichbarkeit;
	private JLabel lblEmail;
	private JLabel lblTelefon;
	private JComboBox comboBoxVon;
	private JComboBox comboBoxBis;
	private JLabel lblVon;
	private JLabel lblBis;
	private JButton btnNewButton;
	private JLabel lblHinweis;
	
	Nutzer nutzer;
	private JLabel lblFehler;
	private JLabel lblPlatzhalter;

	/**
	 * Erzeugt das Fenster mit Textfeldern zum Eintragen der Daten und Comboboxen zum Angeben der Erreichbarkeiten,
	 * sowie einer Taste zum Bestätigen.
	 * Die Prüfung der gültigkeit der Textfelder erfolgt in der Klasse Nutzer und in der Datenbank.
	 *
	 * Das Formular wird mit den bereits bekannten Daten befüllt.
	 */
	public JframeNeueNutzerDaten(Nutzer nutzer) {	
		setTitle("Nutzerdaten aktualisieren");
		this.nutzer = nutzer;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 409);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new MigLayout("", "[grow][]", "[][][][grow][grow][][][]"));
		
		JLabel lblNutzer = new JLabel("Nutzer");
		contentPane.add(lblNutzer, "cell 0 0");		
		
		
		JLabel lblVorname = new JLabel("Vorname");
		contentPane.add(lblVorname, "cell 1 1");		

		lblAdresse = new JLabel("Adresse");
		contentPane.add(lblAdresse, "cell 1 3");
		
		JLabel lblNewLabel = new JLabel("Nachname");
		contentPane.add(lblNewLabel, "cell 1 2");
		
		JPanel panel = new JPanel();
		panel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		contentPane.add(panel, "cell 0 3,grow");
		panel.setLayout(new MigLayout("", "[grow][grow]", "[][][][]"));
		
		lblPostleitzahl = new JLabel("Postleitzahl");
		panel.add(lblPostleitzahl, "cell 0 0");
		
		lblOrt = new JLabel("Ort");
		panel.add(lblOrt, "cell 1 0");
		
		lblStrasse = new JLabel("Straße");
		panel.add(lblStrasse, "cell 0 2");
		
		lblHausnummer = new JLabel("Hausnummer");
		panel.add(lblHausnummer, "cell 1 2");		
				
		panel_1 = new JPanel();
		panel_1.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		contentPane.add(panel_1, "cell 0 4,grow");
		panel_1.setLayout(new MigLayout("", "[grow][]", "[][][][]"));
				
		lblEmail = new JLabel("Email-Adresse");
		panel_1.add(lblEmail, "cell 1 0");		
	
		lblTelefon = new JLabel("Telefonnummer");
		panel_1.add(lblTelefon, "cell 1 1");
		
		
		
		jtfVorname = new JTextField();
		contentPane.add(jtfVorname, "cell 0 1,growx");		
		jtfVorname.setColumns(10);
		jtfVorname.setText(nutzer.getVorname()!=null?nutzer.getVorname():"");
		
		jtfNachname = new JTextField();
		contentPane.add(jtfNachname, "cell 0 2,growx");
		jtfNachname.setColumns(10);
		jtfNachname.setText(nutzer.getNachname()!=null?nutzer.getNachname():"");
		
		jtfPostleitzahl = new JTextField();
		panel.add(jtfPostleitzahl, "cell 0 1,growx");
		jtfPostleitzahl.setColumns(10);
		jtfPostleitzahl.setText(nutzer.getPostleitzahl()!=null?nutzer.getPostleitzahl():"");
		
		jtfOrt = new JTextField();
		panel.add(jtfOrt, "cell 1 1,growx");
		jtfOrt.setColumns(10);	
		jtfOrt.setText(nutzer.getOrt()!=null?nutzer.getOrt():"");	
		
		jtfStrasse = new JTextField();
		panel.add(jtfStrasse, "cell 0 3,growx");
		jtfStrasse.setColumns(10);
		jtfStrasse.setText(nutzer.getStrasse()!=null?nutzer.getStrasse():"");	
		
		jtfHausnummer = new JTextField();
		panel.add(jtfHausnummer, "cell 1 3,growx");
		jtfHausnummer.setColumns(10);
		jtfHausnummer.setText(nutzer.getHausnummer()!=null?nutzer.getHausnummer():"");	
		
		jtfTelefon = new JTextField();
		panel_1.add(jtfTelefon, "cell 0 1,growx");
		jtfTelefon.setColumns(10);
		jtfTelefon.setText(nutzer.getTelefon()!=null?nutzer.getTelefon():"");	
		
		jtfEmail = new JTextField();
		panel_1.add(jtfEmail, "cell 0 0,growx");
		jtfEmail.setColumns(10);
		jtfEmail.setText(nutzer.getEmail()!=null?nutzer.getEmail():"");	
		
		comboBoxVon = new JComboBox();
		comboBoxVon.setModel(new DefaultComboBoxModel(new String[] {" ", "00:00", "01:00", "02:00", "03:00", "04:00", "05:00", "06:00", "07:00", "08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00", "22:00", "23:00"}));
		panel_1.add(comboBoxVon, "cell 0 2,growx");
		if(nutzer.getErreichbarVon()!= null) {
			for (int i =0;i< comboBoxVon.getModel().getSize();i++) {
				if (nutzer.getErreichbarVon().toString().equals((String)comboBoxVon.getModel().getElementAt(i))) {
					Object selectVon = comboBoxVon.getModel().getElementAt(i);
					comboBoxVon.getModel().setSelectedItem(selectVon);
				}
			}
		}
		
		comboBoxBis = new JComboBox();
		comboBoxBis.setModel(new DefaultComboBoxModel(new String[] {" ", "00:00", "01:00", "02:00", "03:00", "04:00", "05:00", "06:00", "07:00", "08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00", "22:00", "23:00"}));
		panel_1.add(comboBoxBis, "cell 0 3,growx");
		if(nutzer.getErreichbarBis()!= null) {
			for (int i =0;i< comboBoxBis.getModel().getSize();i++) {
				if (nutzer.getErreichbarBis().toString().equals((String)comboBoxBis.getModel().getElementAt(i))) {
					Object selectBis = comboBoxBis.getModel().getElementAt(i);
					comboBoxBis.getModel().setSelectedItem(selectBis);
				}
			}
		}
		
		lblVon = new JLabel("Erreichbar von");
		panel_1.add(lblVon, "cell 1 2");
		
		
		lblBis = new JLabel("Erreichbar bis");
		panel_1.add(lblBis, "cell 1 3");
		
		lblErreichbarkeit = new JLabel("Erreichbarkeit");
		contentPane.add(lblErreichbarkeit, "cell 1 4");
		
		lblHinweis = new JLabel("Hinweis: Daten müssen vollständig angegeben werden!");
		lblHinweis.setVerticalAlignment(SwingConstants.TOP);
		contentPane.add(lblHinweis, "cell 0 5,aligny top");
		
		lblFehler = new JLabel("Fehler:");
		lblFehler.setVisible(false);
		
		btnNewButton = new JButton("<html>Änderungen<br> Speichern");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				enterAktion();
			}
		});
		btnNewButton.setVerticalAlignment(SwingConstants.BOTTOM);
		contentPane.add(btnNewButton, "cell 1 5");
		contentPane.add(lblFehler, "cell 0 6");
		
		lblPlatzhalter = new JLabel(".");
		lblPlatzhalter.setDoubleBuffered(true);
		contentPane.add(lblPlatzhalter, "cell 0 7");
	}

	/**
	 * bewirkt die Plausibilitätsprüfung der eingetragenen Daten und markiert das erste gescheiterte Feld.
	 * Bei Erfolg wird das Fenster geschlossen, die Daten übernommen und der nächste Frame angefordert.
	 */
	public void enterAktion() {
		try {
			jtfVorname.requestFocus();
			jtfVorname.selectAll();
			nutzer.setVorname(jtfVorname.getText());
			
			jtfNachname.requestFocus();
			jtfNachname.selectAll();
			nutzer.setNachname(jtfNachname.getText());	
						
			jtfPostleitzahl.requestFocus();
			jtfPostleitzahl.selectAll();
			nutzer.setPostleitzahl(jtfPostleitzahl.getText());
			
			jtfOrt.requestFocus();
			jtfOrt.selectAll();
			nutzer.setOrt(jtfOrt.getText());
			
			jtfStrasse.requestFocus();
			jtfStrasse.selectAll();
			nutzer.setStrasse(jtfStrasse.getText());
			
			jtfHausnummer.requestFocus();
			jtfHausnummer.selectAll();
			nutzer.setHausnummer(jtfHausnummer.getText());
			
			jtfEmail.requestFocus();
			jtfEmail.selectAll();
			nutzer.setEmail(jtfEmail.getText());
			
			jtfTelefon.requestFocus();
			jtfTelefon.selectAll();
			nutzer.setTelefon(jtfTelefon.getText());
			
			comboBoxVon.requestFocus();
			if(!comboBoxVon.getModel().getSelectedItem().equals(" ")) {				
				nutzer.setErreichbarVon(LocalTime.parse((String) comboBoxVon.getModel().getSelectedItem()));
				comboBoxBis.requestFocus();}
			
			if(!comboBoxBis.getModel().getSelectedItem().equals(" ")) {
			nutzer.setErreichbarBis(LocalTime.parse((String) comboBoxBis.getModel().getSelectedItem()));}
			
			if(this.nutzer.isValideDaten()) {
				lblFehler.setText("Erfolgreich!");
				lblFehler.setVisible(true);	
				dispose();
				FensterTerminal.naechsterJframe(nutzer);
				
			}
			
		}catch(IllegalArgumentException e) {
			lblFehler.setText("Fehler: Das markierte Feld ist ungültig!");
			lblFehler.setVisible(true);
		}catch(SQLException e2) {
			System.out.println(e2.toString());
			lblFehler.setText("Fehler: Datenbankfehler!");
			lblFehler.setVisible(true);
		}
	}


}
