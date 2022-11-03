package terminbuchungssystem;

import java.sql.*;

/**
 * Funktionen zur interaktion mit der Datenbank.
 *
 * @author Nicholas Kappauf
 */

public class Db {

    /**
     * führt eine Anweisung auf der Datenbank aus, welche keine Daten zurückliefert. (INSERT, UPDATE, DELETE)
     *
     * @param command String in gültiger SQL-syntax
     */
    public static void exec(String command, Login login) throws SQLException {
        login.connect();
        Connection connection = login.getConnection();
        Statement stmt = connection.createStatement();
        stmt.executeUpdate(command);
        stmt.close();
        connection.close();
    }

    /**
     * führt eine Query auf der Datenbank aus und gibt das Resultset zurück.
     * Das Statement, das Resultset und die Connection müssen nach
     * Abarbeitung des ResultSets (nicht vorher) wieder geschlossen werden!
     *
     * @param query String in gültiger SQL-syntax
     * @return ResultSet je nach query
     */
    public static ResultSet query(String query, Login login) throws SQLException {
        login.connect();
        Connection connection = login.getConnection();
        Statement stmt = connection.createStatement();
        return stmt.executeQuery(query);
    }
}
