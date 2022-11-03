package terminbuchungssystem;

import javax.swing.table.DefaultTableModel;

/**
 * Zellen-spezialisierte abgeleitete Klasse von swing.table.DefaultTableModel
 *
 * @author Nicholas Kappauf
 */

class ZellenEditierbarModel extends DefaultTableModel{

	public ZellenEditierbarModel() {
		super();
		// TODO Auto-generated constructor stub
	}

/**
 * Überschriebene Methode um Festlegen der Editierbarkeit über das Objekt "Zelle" zu realisieren.
 * Terminfenstermodus blockiert jede Editierbarkeit.
 */

	public boolean isCellEditable(int row, int column) {
		if (this.getValueAt(row, column) instanceof Zelle zelle) {
			if (zelle.getTerminfensterModus()) {
				return false;
			}
			return zelle.isEditierbar();
		};
		return false;
	}
}
