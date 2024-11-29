package controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnection {
    private static final String URL = "jdbc:mysql://200.195.171.124:3306/grupo06_integrador";
    private static final String USER = "grupo06"; // Substitua pelo seu usuário
    private static final String PASSWORD = "xyZ4d6lGuJ4EfOz0"; // Substitua pela sua senha

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void main(String[] args) {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SHOW TABLES")) {
            if (conn != null) {
                System.out.println("Conexão com o banco de dados bem-sucedida!");

                while (rs.next()) {
                    System.out.println("Tabela: " + rs.getString(1));
                }
            } else {
                System.out.println("Falha ao conectar ao banco de dados.");
            }
        } catch (SQLException e) {
            System.err.println("Erro ao conectar ao banco de dados:");
            e.printStackTrace();
        }
    }
}
