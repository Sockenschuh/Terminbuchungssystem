package terminbuchungssystem;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

/**
 * Panel für die Monatsübersicht für Ärzte mit einer Tabelle und zwei Pfeiltasten.
 *
 * @author Nicholas Kappauf
 */
public class JPanelMonatsuebersicht extends JPanel {
	
	private JTable table;
	private int monat = 0;
	private DefaultTableModel model;
	private JScrollPane scrollPane;
	private Arzt arzt;
	JLabel lblMonat; 
	JPanel panel;
	
	/**
	 * erzeugt das Panel.
	 */
	public JPanelMonatsuebersicht(Arzt arzt) {
		this.arzt = arzt;
		setBounds(100, 100, 450, 300);
		this.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.setLayout(new BorderLayout(0, 0));
		
		
		
		panel = new JPanel();
		this.add(panel, BorderLayout.SOUTH);
		
		scrollPane = new JScrollPane();
		this.add(scrollPane, BorderLayout.CENTER);
		
		JButton btnVorherigerMonat = new JButton("<<");
		btnVorherigerMonat.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				monat = monat==0?0:monat-1;
				ladeMonat();
			}
		});
		panel.add(btnVorherigerMonat);
		
		JButton btnNächsterMonat = new JButton(">>");
		btnNächsterMonat.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				monat += 1;
				ladeMonat();
			}
		});
		panel.add(btnNächsterMonat);
		
		lblMonat = new JLabel("New label");
		this.add(lblMonat, BorderLayout.NORTH);
		ladeMonat();
	}
	/**
	 * fordert eine Neue Tabelle an und befüllt sie mit den Daten der Datenbankabfrage.
	 * Wenn ein Datum auf ein Wochenende fällt, wird zur Übersichtlichkeit in der Anzahl-Spalte "Wochenende" eingetragen.
	 */
	public void ladeMonat() {
		initTable();
		ArrayList<Object> statistik = new ArrayList<Object>();
		try {
			statistik = arzt.eigeneMonatsUebersicht(monat);
		} catch (SQLException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		for(int i=0;i<statistik.size(); i += 2) {
			model.addRow(new Object[] {statistik.get(i), (((((LocalDate)statistik.get(i)).getDayOfWeek()).toString().equals("SUNDAY"))||(( (LocalDate)statistik.get(i)).getDayOfWeek().toString().equals("SATURDAY")) )?"Wochenende":statistik.get(i+1)});
		}
	}
	/**
	 * entfernt die zuletzt erzeugte Tabelle und erzeugt eine Neue des aktuell eingestellten Monats.
	 * die Tabelle hat die Spalten "Datum" und "Anzahl Termine" und ist nicht editierbar.
	 */
	public void initTable() {
		if(table!=null) {
		scrollPane.remove(table);	}	
		table = new JTable();
		table.setColumnSelectionAllowed(true);
		this.model = new DefaultTableModel(
				new Object[][] {
				},
				new String[] {
					"Datum", "Anzahl Termine"
				}
			) {
				boolean[] columnEditables = new boolean[] {
					false, false
				};
				public boolean isCellEditable(int row, int column) {
					return columnEditables[column];
				}
			};			
		LocalDate heute = LocalDate.now().minusDays(LocalDate.now().getDayOfMonth()-1);	
		LocalDate zielDatum = heute.plusMonths(monat);
			
		lblMonat.setText("Monatübersicht: " + zielDatum.getYear() + " - " +zielDatum.getMonthValue());
			
		table.setModel(this.model);
		table.getColumnModel().getColumn(0).setPreferredWidth(98);
		table.getColumnModel().getColumn(1).setPreferredWidth(48);
		scrollPane.setViewportView(table);
	}



}
