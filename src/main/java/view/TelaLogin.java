package view;

import model.Usuario;
import controller.DBConnection;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TelaLogin extends JFrame {
    private JTextField campoEmail;
    private JPasswordField campoSenha;

    public TelaLogin() {
        setTitle("Tela de Login");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());

        
        getContentPane().setBackground(Color.WHITE); 
        Font fontDescricao = new Font("Inter", Font.PLAIN, 14);

        
        JLabel labelEmail = new JLabel("Email:");
        labelEmail.setFont(fontDescricao);
        campoEmail = new JTextField(20);

        JLabel labelSenha = new JLabel("Senha:");
        labelSenha.setFont(fontDescricao);
        campoSenha = new JPasswordField(20);

        JButton botaoEntrar = new JButton("Entrar");
        botaoEntrar.setBackground(new Color(0x48586f));
        botaoEntrar.setForeground(Color.WHITE);
        botaoEntrar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                autenticar();
            }
        });

        JButton botaoCadastrar = new JButton("Cadastrar novo usuário");
        botaoCadastrar.setBackground(new Color(0x48586f));
        botaoCadastrar.setForeground(Color.WHITE);
        botaoCadastrar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new TelaCadastrarUsuario().setVisible(true); 
            }
        });

        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(labelEmail, gbc);

        gbc.gridx = 1;
        add(campoEmail, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(labelSenha, gbc);

        gbc.gridx = 1;
        add(campoSenha, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        add(botaoEntrar, gbc);

        gbc.gridx = 1;
        add(botaoCadastrar, gbc);
    }

    private void autenticar() {
        String email = campoEmail.getText().trim();
        String senha = new String(campoSenha.getPassword()).trim();

        if (email.isEmpty() || senha.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, preencha todos os campos.");
            return;
        }

        Usuario usuario = verificarCredenciais(email, senha);

        if (usuario != null) {
            
            DashboardPrincipal dashboardPrincipal = new DashboardPrincipal(usuario);
            dashboardPrincipal.setVisible(true);
            dispose(); 
        } else {
            JOptionPane.showMessageDialog(this, "Credenciais inválidas");
        }
    }

   private Usuario verificarCredenciais(String email, String senha) {
    Usuario usuario = null;

    String sql = "SELECT u.id, c.carteiraid FROM usuario u " +
                 "JOIN carteira c ON u.id = c.usuario_id " +
                 "WHERE u.email = ? AND u.senha = ?";

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, email);
        stmt.setString(2, senha);

        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                int id = rs.getInt("id"); // ID do usuário
                int carteiraId = rs.getInt("carteiraid"); // ID da carteira
                usuario = new Usuario(id, carteiraId); // Criação do objeto usuário
            }
        }

    } catch (SQLException e) {
        System.err.println("Erro ao verificar credenciais:");
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Erro ao conectar ao banco de dados. " + e.getMessage());
    }

    return usuario;
}



    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TelaLogin telaLogin = new TelaLogin();
            telaLogin.setVisible(true);
        });
    }
}
