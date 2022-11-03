package terminbuchungssystem;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import net.miginfocom.swing.MigLayout;
/**
 * Panel für die abfrage von Terminübersichten von Patienten für Sprechstundenhilfen.
 *
 * @author Nicholas Kappauf
 */
public class JPanelPatientenUebersicht extends JPanel {
	private final Sprechstundenhilfe hilfe;
	private DefaultTableModel model;
	private JTextField jtfPatient;
	private ArrayList<Buchung> buchungen = new ArrayList<Buchung>();
	private JTable table;
	private JScrollPane scrollPane;

	/**
	 * Erzeugt Panel für die Patientenübersicht mit einer Tabelle und einem textfeld.
	 *
	 * die Tabelle hat die Spalten "Arzt", "Datum" und "Uhrzeit" und ist nicht editierbar.
	 */
	public JPanelPatientenUebersicht(Sprechstundenhilfe hilfe) {
		this.hilfe = hilfe;

		model = new DefaultTableModel(
				new Object[][] {
				},
				new String[] {
						"Arzt", "Datum", "Uhrzeit"
				}
		){
			boolean[] columnEditables = new boolean[] {
					false, false
			};
			public boolean isCellEditable(int row, int column) {
				return columnEditables[column];
			}
		};

		setBounds(100, 100, 450, 300);
		this.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.setLayout(new MigLayout("", "[grow][]", "[][grow][]"));
		
		JLabel lblTabellenTitel = new JLabel("Übersicht der Patiententermine");
		this.add(lblTabellenTitel, "cell 0 0");
		
		scrollPane = new JScrollPane();
		this.add(scrollPane, "cell 0 1 2 1,grow");
		
		table = new JTable();
		table.setModel(model);
		scrollPane.setViewportView(table);
		
		jtfPatient = new JTextField();
		this.add(jtfPatient, "cell 0 2,growx");
		jtfPatient.setColumns(10);
		
		JButton btnEnter = new JButton("Übersicht abfragen");
		btnEnter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				EnterAktion();
			}
		});
		this.add(btnEnter, "cell 1 2");
	}

	/**
	 * Wenn aufgelöst wird der im Textfeld eingegebene Patienten-Name als Parameter an die Datenbank weitergegeben.
	 * Die Tabelle wird im Anschluss mit den erhaltenen Einträgen befüllt.
	 */
	 private void EnterAktion() {
		String patient = jtfPatient.getText();
		try {
			this.buchungen = hilfe.buchungenNachPatient(patient);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.model = new DefaultTableModel(
				new Object[][] {
				},
				new String[] {
					"Arzt", "Datum", "Uhrzeit"
				}
			);
		for (Buchung buchung: this.buchungen) {
			model.addRow(new Object[] {buchung.getTerminfenster().getArzt().getNachname() ,buchung.getDatum().toString(), LocalTime.parse("08:00").plusMinutes((buchung.getTerminfenster().getVonUhrzeit()*30) + buchung.getHalbeStundeNummer()*30).toString()});
		}
		
		table.setModel(model);
		scrollPane.setViewportView(table);		
	}
}
