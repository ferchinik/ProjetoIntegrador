package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class TelaCadastrarUsuario extends JFrame {

    public TelaCadastrarUsuario() {
        // Configurações da janela
        setTitle("Cadastrar Novo Usuário");
        setSize(1024, 768);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);

        // Cor de fundo
        getContentPane().setBackground(Color.WHITE);

        // Título
        JLabel titulo = new JLabel("Cadastrar Novo Usuário", JLabel.CENTER);
        titulo.setFont(new Font("Inter", Font.BOLD, 48));
        titulo.setBounds(0, 50, 1024, 50);
        add(titulo);

        // Labels e campos de entrada
        JLabel labelNome = new JLabel("Nome:");
        labelNome.setFont(new Font("Inter", Font.PLAIN, 24));
        labelNome.setBounds(300, 150, 100, 30);
        add(labelNome);

        JTextField campoNome = new JTextField();
        campoNome.setBounds(400, 150, 300, 30);
        add(campoNome);

        JLabel labelEmail = new JLabel("E-mail:");
        labelEmail.setFont(new Font("Inter", Font.PLAIN, 24));
        labelEmail.setBounds(300, 200, 100, 30);
        add(labelEmail);

        JTextField campoEmail = new JTextField();
        campoEmail.setBounds(400, 200, 300, 30);
        add(campoEmail);

        JLabel labelSenha = new JLabel("Senha:");
        labelSenha.setFont(new Font("Inter", Font.PLAIN, 24));
        labelSenha.setBounds(300, 250, 100, 30);
        add(labelSenha);

        JPasswordField campoSenha = new JPasswordField();
        campoSenha.setBounds(400, 250, 300, 30);
        add(campoSenha);

        JLabel labelTelefone = new JLabel("Telefone:");
        labelTelefone.setFont(new Font("Inter", Font.PLAIN, 24));
        labelTelefone.setBounds(300, 300, 100, 30);
        add(labelTelefone);

        JTextField campoTelefone = new JTextField();
        campoTelefone.setBounds(400, 300, 300, 30);
        add(campoTelefone);

        // Botão para cadastrar
        JButton botaoCadastrar = new JButton("Cadastrar");
        botaoCadastrar.setBackground(Color.decode("#48586f"));
        botaoCadastrar.setForeground(Color.WHITE);
        botaoCadastrar.setFont(new Font("Inter", Font.BOLD, 24));
        botaoCadastrar.setBounds(400, 400, 300, 50);
        add(botaoCadastrar);

        // Botão Voltar
        JButton botaoVoltar = new JButton("Voltar");
        botaoVoltar.setBackground(Color.decode("#48586f"));
        botaoVoltar.setForeground(Color.WHITE);
        botaoVoltar.setFont(new Font("Inter", Font.BOLD, 24));
        botaoVoltar.setBounds(400, 470, 300, 50);
        add(botaoVoltar);

        // Ação do botão de cadastrar
        botaoCadastrar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nome = campoNome.getText().trim();
                String email = campoEmail.getText().trim();
                String senha = new String(campoSenha.getPassword()).trim();
                String telefone = campoTelefone.getText().trim();

                // Verificação de campos vazios
                if (nome.isEmpty() || email.isEmpty() || senha.isEmpty() || telefone.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Por favor, preencha todos os campos.");
                    return; // Interrompe a execução se algum campo estiver vazio
                }

                // Chama o método para cadastrar usuário
                if (cadastrarUsuario(nome, email, senha, telefone)) {
                    JOptionPane.showMessageDialog(null, "Usuário cadastrado com sucesso!");
                    dispose(); // Fecha a tela de cadastro
                } else {
                    JOptionPane.showMessageDialog(null, "Erro ao cadastrar usuário. Tente novamente.");
                }
            }
        });

        // Ação do botão Voltar
        botaoVoltar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Fecha a tela de cadastro
                // Aqui você pode chamar a tela principal novamente, se necessário
                new TelaLogin().setVisible(true); // Exemplo, substitua com sua tela principal
            }
        });
    }

    // Método para cadastrar usuário no banco de dados
    private boolean cadastrarUsuario(String nome, String email, String senha, String telefone) {
        boolean sucesso = false;

        // Conexão com o banco de dados
        String url = "jdbc:mysql://localhost:3306/projeto_integrador"; // URL do banco de dados
        String usuarioBD = "root"; // Substitua pelo seu usuário do banco de dados
        String senhaBD = "25102002"; // Substitua pela sua senha do banco de dados

        try {
            Connection conn = DriverManager.getConnection(url, usuarioBD, senhaBD);
            String sql = "INSERT INTO usuario (nome, email, senha, telefone) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, nome);
            stmt.setString(2, email);
            stmt.setString(3, senha);
            stmt.setString(4, telefone);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                sucesso = true; // Usuário cadastrado
            }

            // Fechar conexões
            stmt.close();
            conn.close();
        } catch (Exception ex) {
            ex.printStackTrace(); // Tratar exceções de forma apropriada
        }

        return sucesso;
    }

    public static void main(String[] args) {
        TelaCadastrarUsuario tela = new TelaCadastrarUsuario();
        tela.setVisible(true);
    }
}
