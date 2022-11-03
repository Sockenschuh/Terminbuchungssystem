package terminbuchungssystem;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
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

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

/**
 * Hauptfenster für Ärzte. Hauptfunktionen werden über Tabs aufgerufen.
 *
 * @author Nicholas Kappauf
 */

public class JFrameZugangArzt extends JFrame {

	private JPanel contentPane;

	/**
	 * Test main-Methode mit gültigem Nutzer.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Nutzer markus;
					markus = new Login("Markus","Markus").getNutzer();
					Arzt arztMarkus = (Arzt)markus;		
					JFrameZugangArzt frame = new JFrameZugangArzt(arztMarkus);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Erzeugt den Frame und mit einem TabbedPane. Dem TabbedPane wird ein Start-Panel, ein Monatsübersicht-panel
	 * und ein Buchungs-Panel hinzugefügt.
	 */
	public JFrameZugangArzt(Arzt arzt){
		setTitle("Zugang für Ärzte");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		contentPane.add(tabbedPane, BorderLayout.CENTER);		
		
		JPanel panelStart = new JPanelStart(arzt, this);
		tabbedPane.addTab("Start", null, panelStart, null);
		
		JPanel panelUebersicht = new JPanelMonatsuebersicht(arzt);
		tabbedPane.addTab("Übersichten", null, panelUebersicht, null);

		JPanel panelBuchungen = new JPanelBuchungsfenster(arzt, arzt, tabbedPane, tabbedPane.getTabCount());
		tabbedPane.addTab("Buchungen", null, panelBuchungen, null);
		
	}
}
