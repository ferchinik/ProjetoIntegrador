package view;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class TestaConexao {
    public static void main(String[] args) {
        // URL de conexão com o banco de dados MySQL
        String url = "jdbc:mysql://200.195.171.124:3306/grupo06_integrador"; // Altere conforme necessário
        String usuario = "grupo06"; // Altere conforme necessário
        String senha = "xyZ4d6lGuJ4EfOz0"; // Altere conforme necessário

        try {
            // Estabelecendo a conexão com o banco de dados
            Connection connection = DriverManager.getConnection(url, usuario, senha);
            System.out.println("Conexão realizada com sucesso!");
            connection.close(); // Fechando a conexão
        } catch (SQLException e) {
            System.out.println("Erro ao conectar: " + e.getMessage());
        }
    }
}
