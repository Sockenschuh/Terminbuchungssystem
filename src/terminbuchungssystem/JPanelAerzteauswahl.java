package terminbuchungssystem;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;

import net.miginfocom.swing.MigLayout;

/**
 * Panel für Sprechstundenhilfen zur Wahl des Arztes, von welchem die Buchungen bearbeitet werden sollen.
 *
 * @author Nicholas Kappauf
 */
public class JPanelAerzteauswahl extends JPanel {

	private List<Arzt> bekannteAerzte = new ArrayList<Arzt>();
	private List<String> bekannteNamen = new ArrayList<String>();
	private Sprechstundenhilfe hilfe;
	private DefaultListModel<String> aerzteModel;
	private JList list;
	private JTabbedPane tabbedPane;
	private int index;
	
	/**
	 * erzeugt das Panel mit einer zu scrollenden JList und einer Bestätigungstaste.
	 *
	 * Beim Betätigen der Bestätigungstaste wird zum Buchungsfenster-Panel des in der JList ausgewählten Arztes
	 * gewechselt.
	 */
	public JPanelAerzteauswahl(Sprechstundenhilfe hilfe, JTabbedPane tabbedPane, int index) {
		this.hilfe = hilfe;
		this.tabbedPane = tabbedPane;
		this.index = index;
		
		setBounds(100, 100, 450, 300);
		this.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.setLayout(new BorderLayout(0, 0));
		
		
		this.setLayout(new MigLayout("", "[grow]", "[][grow][]"));
		
		JLabel lblTabellenTitel = new JLabel("Buchungen verwalten von ...");
		this.add(lblTabellenTitel, "cell 0 0");
		
		JScrollPane scrollPane = new JScrollPane();
		this.add(scrollPane, "cell 0 1,grow");
		
		list = new JList();
		scrollPane.setViewportView(list);
		
		DefaultListModel<String> aerzteModel = new DefaultListModel<String>();
		list.setModel(aerzteModel);
		
		JButton btnEnter = new JButton("Auswahl bestätigen");
		this.add(btnEnter, "cell 0 2,alignx right");
		
		loadBekannteAerzte();
		
		aerzteModel.addAll((Collection) bekannteNamen);
		
		btnEnter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					Arzt arzt = bekannteAerzte.get(list.getSelectedIndex());
					switchPanel(arzt, hilfe);
				}catch(IndexOutOfBoundsException ignore){}
			}
		});
	}

	/**
	 * Das aktuelle Ärzteauswahl-Panel begeht Soduko und erzeugt ein neues BuchungsFenster-Panel des ausgewählten Arztes
	 * welches dem TabbedPane, in welches dieses Ärzteauswahl-Panel eingebunden war, an der leeren Stelle hinzufügt
	 * und ausgewählt wird.
	 */
	private void switchPanel(Arzt arzt, Sprechstundenhilfe hilfe) {
		this.tabbedPane.remove(index);
		JPanel panelBuchungen = new JPanelBuchungsfenster(arzt, hilfe, tabbedPane, index);
		tabbedPane.addTab("Buchungen", null, panelBuchungen, null);
		tabbedPane.setSelectedIndex(index);
	}

	/**
	 * Lädt die bekannten Ärzte und zeigt sie mit Vor- und Nachnamen in der Auswahlliste an.
	 */
	private void loadBekannteAerzte() {
		try {
			hilfe.loadAerzte();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.bekannteAerzte = hilfe.getBekannteAerzte();
		for(Arzt arzt: hilfe.getBekannteAerzte()) {
			this.bekannteNamen.add(arzt.getVorname() + " " + arzt.getNachname());
		};
	}
}
