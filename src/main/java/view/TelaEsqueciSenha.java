package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TelaEsqueciSenha extends JFrame {

    public TelaEsqueciSenha() {
        // Configurações da janela
        setTitle("Esqueci a Senha");
        setSize(1024, 768);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);

        // Cor de fundo
        getContentPane().setBackground(Color.decode("#ffffc0"));

        // Título
        JLabel titulo = new JLabel("Esqueci a Senha", JLabel.CENTER);
        titulo.setFont(new Font("Inter", Font.BOLD, 48));
        titulo.setBounds(0, 50, 1024, 50);
        add(titulo);

        // Label e campo de entrada para o e-mail
        JLabel labelEmail = new JLabel("Digite seu e-mail:");
        labelEmail.setFont(new Font("Inter", Font.PLAIN, 24));
        labelEmail.setBounds(300, 150, 200, 30);
        add(labelEmail);

        JTextField campoEmail = new JTextField();
        campoEmail.setBounds(300, 200, 400, 30);
        add(campoEmail);

        // Botão para enviar instruções de recuperação de senha
        JButton botaoEnviar = new JButton("Enviar");
        botaoEnviar.setBackground(Color.decode("#48586f"));
        botaoEnviar.setForeground(Color.WHITE);
        botaoEnviar.setFont(new Font("Inter", Font.BOLD, 24));
        botaoEnviar.setBounds(300, 250, 400, 50);
        add(botaoEnviar);

        // Botão Voltar
        JButton botaoVoltar = new JButton("Voltar");
        botaoVoltar.setBackground(Color.decode("#48586f"));
        botaoVoltar.setForeground(Color.WHITE);
        botaoVoltar.setFont(new Font("Inter", Font.BOLD, 24));
        botaoVoltar.setBounds(300, 320, 400, 50);
        add(botaoVoltar);

        // Ação do botão Enviar
        botaoEnviar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = campoEmail.getText().trim();

                // Aqui você chamaria seu método para enviar instruções de recuperação
                if (!email.isEmpty()) {
                    // Enviar instruções para o e-mail
                    JOptionPane.showMessageDialog(null, "Instruções para recuperação de senha enviadas para: " + email);
                } else {
                    JOptionPane.showMessageDialog(null, "Por favor, preencha o campo de e-mail.");
                }
            }
        });

        // Ação do botão Voltar
        botaoVoltar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Fecha a tela de esqueci a senha
                new TelaLogin().setVisible(true); // Volta para a tela de login
            }
        });
    }

    public static void main(String[] args) {
        TelaEsqueciSenha tela = new TelaEsqueciSenha();
        tela.setVisible(true);
    }
}
