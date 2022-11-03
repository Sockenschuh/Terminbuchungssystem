package terminbuchungssystem;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;

/**
 * Hauptfenster für Sprechstundenhilfen. Hauptfunktionen werden über Tabs aufgerufen.
 *
 * @author Nicholas Kappauf
 */

public class JFrameZugangSprechstundenhilfe extends JFrame {

	private JPanel contentPane;

	/**
	 * Test main-Methode mit gültigem Nutzer.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Nutzer mia =  new Login("Mia","Mia").getNutzer();
					Sprechstundenhilfe hilfeMia = (Sprechstundenhilfe)mia;
					JFrameZugangSprechstundenhilfe frame = new JFrameZugangSprechstundenhilfe(hilfeMia);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Erzeugt den Frame und mit einem TabbedPane. Dem TabbedPane wird ein Start-Panel, ein Patientenübersicht-panel
	 * und ein Ärzteauswahl (für den Zugang zum Buchungs-Panel) hinzugefügt.
	 */
	public JFrameZugangSprechstundenhilfe(Sprechstundenhilfe hilfe) {
		setTitle("Zugang für Sprechstundenhilfen");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		contentPane.add(tabbedPane, BorderLayout.CENTER);	
		
		JPanel panelStart = new JPanelStart(hilfe, this);
		tabbedPane.addTab("Start", null, panelStart, null);
		
		JPanel panelUebersicht = new JPanelPatientenUebersicht(hilfe);
		tabbedPane.addTab("Patienten Termine", null, panelUebersicht, null);
		
		JPanel panelBuchungen = new JPanelAerzteauswahl(hilfe, tabbedPane, tabbedPane.getTabCount());
		tabbedPane.addTab("Buchungen verwalten", null, panelBuchungen, null);		
	}

}
