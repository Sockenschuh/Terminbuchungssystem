package terminbuchungssystem;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import net.miginfocom.swing.MigLayout;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.awt.event.ActionEvent;
/**
 * Erstes Fenster der Anwendung zum Anmelden des Nutzers.
 *
 * @author Nicholas Kappauf
 */
public class JFrameLoginFenster extends JFrame {

	private JPanel contentPane;
	private JTextField jtfBenutzer;
	private JTextField jtfPasswort;
	private JLabel lblNutzer;
	private JLabel lblPasswort;
	private JButton btnLogin;

	/**
	 * startet das Fenster.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					JFrameLoginFenster startFrame = new JFrameLoginFenster();
					startFrame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * erzeugt den Frame mit Textfeldern zum Eintragen der Nutzerdaten und einer Bestätigungstaste.
	 * Versucht bei Bestätigung eine Verbindung mit der Datenbank aufzubauen. Wenn erfolgreich, wird dieses Fenster
	 * geschlossen und der nächste Frame angefordert.
	 */
	public JFrameLoginFenster() {
		setTitle("Terminvergabesystem Anmeldung");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 431, 107);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new MigLayout("", "[][grow][][][]", "[][][]"));
		
		lblNutzer = new JLabel("Benutzer");
		lblNutzer.setVerticalAlignment(SwingConstants.BOTTOM);
		lblNutzer.setHorizontalAlignment(SwingConstants.TRAILING);
		contentPane.add(lblNutzer, "cell 0 0");
		
		jtfBenutzer = new JTextField();
		contentPane.add(jtfBenutzer, "cell 1 0,growx");
		jtfBenutzer.setColumns(10);
		
		lblPasswort = new JLabel("Passwort");
		contentPane.add(lblPasswort, "cell 0 1");
		
		jtfPasswort = new JTextField();
		contentPane.add(jtfPasswort, "cell 1 1,growx");
		jtfPasswort.setColumns(10);
		
		btnLogin = new JButton("Anmelden");		
		btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					if (jtfBenutzer.getText().equals("")){
						jtfBenutzer.requestFocus();
					}
					else if (jtfPasswort.getText().equals("")){
						jtfPasswort.requestFocus();
					}else{
					Login login = new Login(jtfPasswort.getText(), jtfBenutzer.getText());
					if(login.getNutzer() != null){
					dispose();
					FensterTerminal.naechsterJframe(login.getNutzer());}	}
				} catch (SQLException | IllegalArgumentException ignore) {
					//TODO Errorlabel
				}
			}
		});
		contentPane.add(btnLogin, "cell 3 1");
	}

}
