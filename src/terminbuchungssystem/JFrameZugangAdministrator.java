package terminbuchungssystem;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
/**
 * Hauptfenster für den Admin. Biete alle Funktionen, um Nutzer der Datenbank zu verwalten.
 *
 * @author Nicholas Kappauf
 */
public class JFrameZugangAdministrator extends JFrame {

	private JPanel contentPane;
	private JTextField jtfBenutzerName;
	private JTextField jtfPasswort;
	DefaultTableModel model;

	/**
	 * Test main-Methode mit gültigem Nutzer.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					JFrameZugangAdministrator frame = new JFrameZugangAdministrator(
							(Admin) new Login("Admin","praxisAdministrator").getNutzer());
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Erzeugt den Frame zur Nutzerverwaltung. Der Frame enthält eine Tabelle der registrierten Nutzer, ein Formular
	 * zum erstellen neuer Nutzer mit zwei textfelder, einer Combobox und einer bestätigungstaste,
	 * sowie eine Taste zum Entfernen von in der Tabelle ausgewählten Nutzern.
	 */
	public JFrameZugangAdministrator(Admin admin){
		setTitle("Nutzerverwaltung");
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 562, 651);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 11, 526, 406);
		contentPane.add(scrollPane);

		this.model = new DefaultTableModel();
	    model.addColumn("Benutzername");
	    model.addColumn("Rolle");
	    model.addColumn("Neukonto");
	    model.addColumn("Daten vollständig");
	    model.addColumn("Vorname");
	    model.addColumn("Nachname");	    
	    JTable table = new JTable(model);	
	    
	    try {
			admin.loadNutzer();
		} catch (SQLException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
	    
	    ArrayList<Login> bekannteLogins = admin.getBekannteLogins();
	    	    
	    
	    for (Login bekanntesLogin: bekannteLogins){
	    	model = (DefaultTableModel) table.getModel();
	        model.addRow(new Object[]{
	        		bekanntesLogin.getBenutzer(),
	        		bekanntesLogin.getRolle()==2?"Arzt":"Sprechstundenhilfe",
	        		bekanntesLogin.isNeukonto()?"Ja":"Nein",
	        		bekanntesLogin.getNutzer().isValideDaten()?"Ja":"Nein",
	        		bekanntesLogin.getNutzer().getVorname()==null?"unbekannt":bekanntesLogin.getNutzer().getVorname(),		
	        		bekanntesLogin.getNutzer().getNachname()==null?"unbekannt":bekanntesLogin.getNutzer().getNachname()	
	        		});		    
	    }	    
	    
	    table.setColumnSelectionAllowed(false);
		scrollPane.setViewportView(table);
		
		JButton btnLoeschen = new JButton("ausgewählte löschen");
		btnLoeschen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				model = (DefaultTableModel) table.getModel();
				String doomedNutzer = (String) model.getValueAt(table.getSelectedRow(), 0);
				model.removeRow(table.getSelectedRow());
				try {
					admin.loescheNutzer(doomedNutzer);
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}				
			}
		});
		btnLoeschen.setBounds(346, 550, 169, 23);
		contentPane.add(btnLoeschen);
		
		JLabel lblNewLabel = new JLabel("Neuen Benutzer erstellen");
		lblNewLabel.setBounds(20, 428, 152, 14);
		contentPane.add(lblNewLabel);
		
		jtfBenutzerName = new JTextField();
		jtfBenutzerName.setBounds(20, 453, 152, 20);
		contentPane.add(jtfBenutzerName);
		jtfBenutzerName.setColumns(10);
		
		jtfPasswort = new JTextField();
		jtfPasswort.setBounds(20, 484, 152, 20);
		contentPane.add(jtfPasswort);
		jtfPasswort.setColumns(10);
		
		String[] options = {"Arzt", "Sprechstundenhilfe"};
		
		JComboBox<String> comboBoxRolle = new JComboBox(options);
		comboBoxRolle.setBounds(20, 517, 152, 22);
		contentPane.add(comboBoxRolle);
		
		JLabel lblNeuerBenutzerName = new JLabel("Benutzername");
		lblNeuerBenutzerName.setBounds(182, 456, 97, 14);
		contentPane.add(lblNeuerBenutzerName);
		
		JLabel lblNeuerBenutzerPasswort = new JLabel("Passwort");
		lblNeuerBenutzerPasswort.setBounds(182, 487, 97, 14);
		contentPane.add(lblNeuerBenutzerPasswort);
		
		JLabel lblMitarbeiterrolle = new JLabel("Mitarbeiterrolle");
		lblMitarbeiterrolle.setBounds(182, 521, 97, 14);
		contentPane.add(lblMitarbeiterrolle);
		

		JLabel lblError = new JLabel("Fehlschlag:");
		lblError.setVisible(false);
		lblError.setBounds(20, 584, 495, 14);
		contentPane.add(lblError);
		
		JButton btnNeuerBenutzer = new JButton("Neuen Benutzer erstellen");
		btnNeuerBenutzer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					admin.neuerBenutzer(jtfPasswort.getText(), jtfBenutzerName.getText(), comboBoxRolle.getSelectedItem().equals("Arzt")?2:3);
				} catch (Exception e1) {
					if (e1.getClass() == IllegalArgumentException.class) {
						lblError.setText("Fehlschlag: Benutzer/Passwort-felder dürfen nicht leer sein!");
						lblError.setVisible(true);
					}else {
						lblError.setText("Fehlschlag: Benutzername schon vorhanden!");
						lblError.setVisible(true);
					}
					e1.printStackTrace();
				}
				Login neuesLogin = admin.getBekannteLogins().get(admin.getBekannteLogins().size()-1);
				model.addRow(new Object[]{
						neuesLogin.getBenutzer(),
						neuesLogin.getRolle()==2?"Arzt":"Sprechstundenhilfe",
						neuesLogin.isNeukonto()?"Ja":"Nein",
						neuesLogin.getNutzer().isValideDaten()?"Ja":"Nein",
						neuesLogin.getNutzer().getVorname()==null?"unbekannt":neuesLogin.getNutzer().getVorname(),		
						neuesLogin.getNutzer().getNachname()==null?"unbekannt":neuesLogin.getNutzer().getNachname()	
		        		});
			}
		});
		btnNeuerBenutzer.setBounds(20, 550, 259, 23);
		contentPane.add(btnNeuerBenutzer);
		
	}
}
