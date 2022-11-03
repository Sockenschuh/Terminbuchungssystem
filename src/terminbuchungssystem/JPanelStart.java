package terminbuchungssystem;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import net.miginfocom.swing.MigLayout;
/**
 * Panel für das Begrüßungsfenster mit Wahl zur Passwort- oder Datenaktualisierung.
 * Beide Optionen schließen das Hauptfenster, da Kerndaten verändert werden.
 *
 * @author Nicholas Kappauf
 */
public class JPanelStart extends JPanel {

	/**
	 * Erzeugt das Panel. Benötigt den Jframe, in welches es eingebunden wird, um dieses schließen zu können.
	 */
	public JPanelStart(Nutzer nutzer, JFrame jframe) {
		
		setBounds(100, 100, 260, 300);
		this.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.setLayout(new MigLayout("", "[][][][][]", "[][][][][][][][][]"));
		
		JLabel lblWillkommen = new JLabel("<html><center>Willkommen <br>Herr/Frau</center>");
		lblWillkommen.setText("<html><center>Willkommen <br> Herr/Frau "+ nutzer.getNachname() + "</center>");
		this.add(lblWillkommen, "cell 0 0,alignx center,growy");
						
		JButton btnPasswort = new JButton("Neues Passwort festlegen");
		btnPasswort.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFrameNeuesPasswort framePsw = new JFrameNeuesPasswort(nutzer.getLogin());
				framePsw.setVisible(true);	
				jframe.dispose();
			}
		});
		this.add(btnPasswort, "cell 0 7,growx");
		
		JButton btnDaten = new JButton("Persönliche Daten aktualisieren");
		btnDaten.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JframeNeueNutzerDaten frameDaten = new JframeNeueNutzerDaten(nutzer);
				frameDaten.setVisible(true);
				jframe.dispose();
			}
		});
		this.add(btnDaten, "cell 0 8");
	}

}
