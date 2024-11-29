package view;

import model.Usuario;
import controller.DBConnection;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;


import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import javax.swing.table.DefaultTableModel;
import org.jfree.data.category.DefaultCategoryDataset;

public class DashboardPrincipal extends JFrame {
    private Usuario usuario; // Armazena o objeto Usuario logado
    private String nomeUsuario;

    private JLabel labelReceitas;
    private JLabel labelDespesas;
    private JLabel labelSaldo;
    private ChartPanel graficoBarraPanel;

    public DashboardPrincipal(Usuario usuario) {
        this.usuario = usuario;
        this.nomeUsuario = obterNomeUsuario();
        setTitle("Dashboard Principal");
        setSize(1194, 834);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        getContentPane().setBackground(Color.WHITE);

        JPanel painelBoasVindas = criarPainelBoasVindas();

        JPanel wrapperBoasVindas = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        wrapperBoasVindas.setBackground(Color.WHITE);
        wrapperBoasVindas.add(painelBoasVindas);
        add(wrapperBoasVindas, BorderLayout.NORTH);

        JPanel painelGraficos = new JPanel(new GridLayout(1, 1));
        graficoBarraPanel = criarGraficoBarraPanel();
        painelGraficos.add(graficoBarraPanel);
        add(painelGraficos, BorderLayout.CENTER);

        JPanel painelResumo = new JPanel();
        painelResumo.setLayout(new BoxLayout(painelResumo, BoxLayout.Y_AXIS));
        painelResumo.setBackground(Color.WHITE);
        painelResumo.setBorder(new EmptyBorder(20, 20, 20, 20));

        labelReceitas = new JLabel("Receitas: R$ " + obterTotalReceitas());
        labelDespesas = new JLabel("Despesas: R$ " + obterTotalDespesas());
        labelSaldo = new JLabel("Saldo: R$ " + calcularSaldo());

        painelResumo.add(criarBoxResumo(labelReceitas));
        painelResumo.add(Box.createRigidArea(new Dimension(0, 10)));
        painelResumo.add(criarBoxResumo(labelDespesas));
        painelResumo.add(Box.createRigidArea(new Dimension(0, 10)));
        painelResumo.add(criarBoxResumo(labelSaldo));

        painelResumo.setPreferredSize(new Dimension(308, 286));
        add(painelResumo, BorderLayout.EAST);

        JPanel painelBotoes = new JPanel(new FlowLayout());
        painelBotoes.setBackground(Color.WHITE);

        JButton botaoTransacoes = criarBotao("Transações");
        JButton botaoRegistrarDespesa = criarBotao("Registrar Despesa");
        JButton botaoRegistrarReceita = criarBotao("Registrar Receita");
        JButton botaoVisualizarMetas = criarBotao("Metas");
        JButton botaoConfiguracoes = criarBotao("Configurações do Usuário");

        painelBotoes.add(botaoTransacoes);
        painelBotoes.add(botaoRegistrarDespesa);
        painelBotoes.add(botaoRegistrarReceita);
        painelBotoes.add(botaoVisualizarMetas);
        painelBotoes.add(botaoConfiguracoes);

        add(painelBotoes, BorderLayout.SOUTH);

        botaoTransacoes.addActionListener(e -> mostrarTransacoes());

        botaoRegistrarDespesa.addActionListener(e -> registrarDespesa());

        botaoRegistrarReceita.addActionListener(e -> registrarReceita());

        botaoVisualizarMetas.addActionListener(e -> mostrarMetas());

        botaoConfiguracoes.addActionListener(e -> mostrarConfiguracoesUsuario());

        // Iniciar Timer para atualização em tempo real
        Timer timer = new Timer(5000, e -> atualizarDados());
        timer.start();
    }

    private void mostrarMetas() {
    JPanel painelMetas = new JPanel();
    painelMetas.setLayout(new BoxLayout(painelMetas, BoxLayout.Y_AXIS));
    painelMetas.setBackground(Color.WHITE);
    
    String sql = "SELECT nome, descricao, valor, datafinal FROM meta WHERE usuario_id = ?";
    try (Connection connection = DBConnection.getConnection();
         PreparedStatement ps = connection.prepareStatement(sql)) {
        ps.setInt(1, usuario.getId());
        ResultSet rs = ps.executeQuery();
        
        while (rs.next()) {
            String nomeMeta = rs.getString("nome");
            String descricaoMeta = rs.getString("descricao");
            double valorMeta = rs.getDouble("valor");
            Date dataFinal = rs.getDate("datafinal");
            double saldoAtual = calcularSaldo(); // Obtem o saldo atual
            
            JLabel labelMeta = new JLabel(String.format("Meta: %s (Até: %s)", nomeMeta, dataFinal));
            painelMetas.add(labelMeta);
            
            double faltando = valorMeta - saldoAtual;

            DefaultPieDataset dataset = new DefaultPieDataset();
            dataset.setValue("Meta", valorMeta);
            if (faltando > 0) {
                dataset.setValue("Faltando", faltando);
            }

            JFreeChart graficoPizza = ChartFactory.createPieChart(
                "Comparação da Meta",
                dataset,
                true,
                true,
                false
            );

            ChartPanel graficoPanel = new ChartPanel(graficoPizza);
            graficoPanel.setPreferredSize(new Dimension(400, 300));
            painelMetas.add(graficoPanel);
            
            JLabel labelDescricao = new JLabel("Descrição: " + descricaoMeta);
            painelMetas.add(labelDescricao);
            
            // Exibir valores em reais
            if (faltando > 0) {
                JLabel labelValores = new JLabel(String.format("Valor da Meta: R$ %.2f | Faltando: R$ %.2f", valorMeta, faltando));
                painelMetas.add(labelValores);
            }

            painelMetas.add(Box.createRigidArea(new Dimension(0, 20))); // Espaçamento
        }
    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Erro ao obter metas.");
    }

    JScrollPane scrollPane = new JScrollPane(painelMetas);
    scrollPane.setPreferredSize(new Dimension(450, 400));
    JOptionPane.showMessageDialog(this, scrollPane, "Metas", JOptionPane.PLAIN_MESSAGE);
}
    
    private JPanel criarPainelBoasVindas() {
        JLabel labelBoasVindas = new JLabel("Bem-vindo, " + nomeUsuario + "!");
        labelBoasVindas.setFont(new Font("Inter", Font.BOLD, 24));
        labelBoasVindas.setForeground(Color.WHITE);

        JPanel painel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0x3D485A));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            }

            @Override
            public Dimension getPreferredSize() {
                Dimension labelSize = labelBoasVindas.getPreferredSize();
                Insets insets = getInsets();
                int width = labelSize.width + 40;
                int height = labelSize.height + 20;
                return new Dimension(width, height);
            }
        };
        painel.setLayout(new GridBagLayout());
        painel.setBorder(new EmptyBorder(10, 20, 10, 20));
        painel.add(labelBoasVindas);

        return painel;
    }

    private JPanel criarBoxResumo(JLabel label) {
        label.setFont(new Font("Inter", Font.PLAIN, 16));
        label.setForeground(Color.WHITE);

        JPanel painel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0xD3AB1C));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            }

            @Override
            public Dimension getPreferredSize() {
                Dimension labelSize = label.getPreferredSize();
                Insets insets = getInsets();
                int width = labelSize.width + 40;
                int height = labelSize.height + 20;
                return new Dimension(width, height);
            }
        };
        painel.setLayout(new GridBagLayout());
        painel.setBorder(new EmptyBorder(10, 20, 10, 20));
        painel.add(label);

        return painel;
    }

    private ChartPanel criarGraficoBarraPanel() {
        JFreeChart graficoBarra = criarGraficoBarra();
        return new ChartPanel(graficoBarra);
    }

    private JFreeChart criarGraficoBarra() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(obterTotalReceitas(), "Receitas", "Mês");
        dataset.addValue(obterTotalDespesas(), "Despesas", "Mês");

        JFreeChart graficoBarra = ChartFactory.createBarChart(
                "Receitas e Despesas",
                "Categoria",
                "Valor",
                dataset
        );

        return graficoBarra;
    }

    private String obterNomeUsuario() {
        String nome = "";
        String sql = "SELECT nome FROM usuario WHERE id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, usuario.getId());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                nome = rs.getString("nome");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao obter o nome do usuário.");
        }
        return nome;
    }

    private double obterTotalReceitas() {
        double total = 0.0;
        String sql = "SELECT SUM(valor) FROM receita WHERE carteira_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, usuario.getCarteiraId());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                total = rs.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao obter total de receitas.");
        }
        return total;
    }

    private double obterTotalDespesas() {
        double total = 0.0;
        String sql = "SELECT SUM(valor) FROM despesa WHERE carteira_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, usuario.getCarteiraId());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                total = rs.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao obter total de despesas.");
        }
        return total;
    }

    private double calcularSaldo() {
    double saldo = 0.0;
    String sql = "SELECT valor FROM caixa WHERE usuario_id = ?";
    try (Connection connection = DBConnection.getConnection();
         PreparedStatement ps = connection.prepareStatement(sql)) {
        ps.setInt(1, usuario.getId());
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            saldo = rs.getDouble("valor");
        }
    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Erro ao obter saldo.");
    }
    return saldo;
}

    private void atualizarDados() {
        labelReceitas.setText("Receitas: R$ " + obterTotalReceitas());
        labelDespesas.setText("Despesas: R$ " + obterTotalDespesas());
        labelSaldo.setText("Saldo: R$ " + calcularSaldo());
        graficoBarraPanel.setChart(criarGraficoBarra());
    }

 private void mostrarTransacoes() {
    // Painel principal
    JPanel painelTransacoes = new JPanel();
    painelTransacoes.setLayout(new BorderLayout());
    painelTransacoes.setBackground(Color.WHITE);

    // ComboBox para seleção da categoria
    String[] categorias = {"Todas", "Despesas", "Receitas"};
    JComboBox<String> comboCategorias = new JComboBox<>(categorias);
    painelTransacoes.add(comboCategorias, BorderLayout.NORTH);

    // Modelo da tabela
    DefaultTableModel modeloTabela = new DefaultTableModel(new String[]{"Descrição", "Valor", "Data"}, 0);
    JTable tabelaTransacoes = new JTable(modeloTabela);
    tabelaTransacoes.setFillsViewportHeight(true);

    // Adiciona as transações na tabela
    atualizarTabelaTransacoes(modeloTabela, "Todas");

    // Ação do ComboBox
    comboCategorias.addActionListener(e -> {
        String categoriaSelecionada = (String) comboCategorias.getSelectedItem();
        atualizarTabelaTransacoes(modeloTabela, categoriaSelecionada);
    });

    // Adiciona a tabela em um JScrollPane
    JScrollPane scrollPane = new JScrollPane(tabelaTransacoes);
    painelTransacoes.add(scrollPane, BorderLayout.CENTER);

    // Exibir as transações em um JOptionPane
    JOptionPane.showMessageDialog(this, painelTransacoes, "Transações", JOptionPane.PLAIN_MESSAGE);
}


 private void atualizarTabelaTransacoes(DefaultTableModel modeloTabela, String categoria) {
    modeloTabela.setRowCount(0); // Limpa a tabela antes de adicionar novos dados

    String sql;
    if (categoria.equals("Todas")) {
        sql = "SELECT descricao, valor, dataa FROM despesa WHERE carteira_id = ? " +
              "UNION ALL " +
              "SELECT descricao, valor, dataa FROM receita WHERE carteira_id = ? " +
              "ORDER BY dataa DESC";
    } else if (categoria.equals("Despesas")) {
        sql = "SELECT descricao, valor, dataa FROM despesa WHERE carteira_id = ? ORDER BY dataa DESC";
    } else {
        sql = "SELECT descricao, valor, dataa FROM receita WHERE carteira_id = ? ORDER BY dataa DESC";
    }

    try (Connection connection = DBConnection.getConnection();
         PreparedStatement ps = connection.prepareStatement(sql)) {
        ps.setInt(1, usuario.getCarteiraId());
        if (categoria.equals("Todas")) {
            ps.setInt(2, usuario.getCarteiraId());
        }
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            String descricao = rs.getString("descricao");
            double valor = rs.getDouble("valor");
            Date data = rs.getDate("dataa");

            // Adiciona a linha na tabela
            modeloTabela.addRow(new Object[]{descricao, valor, data});
        }
    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Erro ao obter transações.");
    }
}


    private void registrarDespesa() {
        JTextField campoDescricao = new JTextField(15);
        JTextField campoValor = new JTextField(15);
        JTextField campoData = new JTextField(15); // Formato: YYYY-MM-DD

        JPanel painelInput = new JPanel(new GridLayout(3, 2, 10, 10));
        painelInput.add(new JLabel("Descrição:"));
        painelInput.add(campoDescricao);
        painelInput.add(new JLabel("Valor:"));
        painelInput.add(campoValor);
        painelInput.add(new JLabel("Data (YYYY-MM-DD):"));
        painelInput.add(campoData);

        int resultado = JOptionPane.showConfirmDialog(this, painelInput, "Registrar Despesa", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (resultado == JOptionPane.OK_OPTION) {
            String descricao = campoDescricao.getText().trim();
            String valorStr = campoValor.getText().trim();
            String data = campoData.getText().trim();

            // Validação da entrada
            if (descricao.isEmpty() || valorStr.isEmpty() || data.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Todos os campos devem ser preenchidos!");
                return;
            }

            // Validação da data
            SimpleDateFormat formatoData = new SimpleDateFormat("yyyy-MM-dd");
            formatoData.setLenient(false);
            try {
                formatoData.parse(data);
            } catch (ParseException e) {
                JOptionPane.showMessageDialog(this, "Data inválida! Use o formato YYYY-MM-DD.");
                return;
            }

            // Validação do valor
            double valor;
            try {
                valor = Double.parseDouble(valorStr);
                if (valor < 0) {
                    JOptionPane.showMessageDialog(this, "O valor não pode ser negativo.");
                    return;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Valor inválido! Digite um número válido.");
                return;
            }

            // Registrar despesa no banco de dados
            String sql = "INSERT INTO despesa (descricao, valor, dataa, usuario_id, carteira_id) VALUES (?, ?, ?, ?, ?)";
            try (Connection connection = DBConnection.getConnection();
                 PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, descricao);
                ps.setDouble(2, valor);
                ps.setString(3, data); // Se o banco aceitar String para data, caso contrário, converter para java.sql.Date
                ps.setInt(4, usuario.getId());
                ps.setInt(5, usuario.getCarteiraId());
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Despesa registrada com sucesso!");
                atualizarDados(); // Atualizar os dados após o registro
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erro ao registrar despesa: " + e.getMessage());
            }
        }
    }
    private void registrarReceita() {
    JTextField campoDescricao = new JTextField(15);
    JTextField campoValor = new JTextField(15);
    JTextField campoData = new JTextField(15); // Formato: YYYY-MM-DD

    JPanel painelInput = new JPanel(new GridLayout(3, 2, 10, 10));
    painelInput.add(new JLabel("Descrição:"));
    painelInput.add(campoDescricao);
    painelInput.add(new JLabel("Valor:"));
    painelInput.add(campoValor);
    painelInput.add(new JLabel("Data (YYYY-MM-DD):"));
    painelInput.add(campoData);

    int resultado = JOptionPane.showConfirmDialog(this, painelInput, "Registrar Receita", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
    if (resultado == JOptionPane.OK_OPTION) {
        String descricao = campoDescricao.getText().trim();
        String valorStr = campoValor.getText().trim();
        String data = campoData.getText().trim();

        // Validação da entrada
        if (descricao.isEmpty() || valorStr.isEmpty() || data.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos os campos devem ser preenchidos!");
            return;
        }

        // Validação da data
        SimpleDateFormat formatoData = new SimpleDateFormat("yyyy-MM-dd");
        formatoData.setLenient(false);
        try {
            formatoData.parse(data);
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "Data inválida! Use o formato YYYY-MM-DD.");
            return;
        }

        // Validação do valor
        double valor;
        try {
            valor = Double.parseDouble(valorStr);
            if (valor < 0) {
                JOptionPane.showMessageDialog(this, "O valor não pode ser negativo.");
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Valor inválido! Digite um número válido.");
            return;
        }

        // Registrar receita no banco de dados
        String sql = "INSERT INTO receita (descricao, valor, dataa, usuario_id, carteira_id) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, descricao);
            ps.setDouble(2, valor);
            ps.setString(3, data); // Se o banco aceitar String para data, caso contrário, converter para java.sql.Date
            ps.setInt(4, usuario.getId());
            ps.setInt(5, usuario.getCarteiraId());
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Receita registrada com sucesso!");
            atualizarDados(); // Atualizar os dados após o registro
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao registrar receita: " + e.getMessage());
        }
    }
}
    private void mostrarConfiguracoesUsuario() {
    JTextField campoNome = new JTextField(15);
    JTextField campoEmail = new JTextField(15);
    JPasswordField campoSenha = new JPasswordField(15);
    JCheckBox checkEmail = new JCheckBox("Receber notificações por e-mail");
    JCheckBox checkAlertas = new JCheckBox("Receber alertas");

    // Preenche os campos com os dados do usuário
    campoNome.setText(obterNomeUsuario());
    campoEmail.setText(obterEmailUsuario()); // Crie um método para obter o e-mail do usuário

    JPanel painelInput = new JPanel(new GridLayout(5, 2, 10, 10));
    painelInput.add(new JLabel("Nome:"));
    painelInput.add(campoNome);
    painelInput.add(new JLabel("E-mail:"));
    painelInput.add(campoEmail);
    painelInput.add(new JLabel("Senha:"));
    painelInput.add(campoSenha);
    painelInput.add(checkEmail);
    painelInput.add(checkAlertas);

    int resultado = JOptionPane.showConfirmDialog(this, painelInput, "Configurações do Usuário", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
    if (resultado == JOptionPane.OK_OPTION) {
        String nome = campoNome.getText().trim();
        String email = campoEmail.getText().trim();
        String senha = new String(campoSenha.getPassword()).trim(); // Obtém a senha digitada

        // Validação da entrada
        if (nome.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nome e e-mail devem ser preenchidos!");
            return;
        }

        // Atualiza as configurações no banco de dados
        String sql = "UPDATE usuario SET nome = ?, email = ?, senha = ? WHERE id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, nome);
            ps.setString(2, email);
            ps.setString(3, senha); // Aqui você deve adicionar lógica para tratar a senha adequadamente
            ps.setInt(4, usuario.getId());
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Configurações atualizadas com sucesso!");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao atualizar configurações: " + e.getMessage());
        }
    }
}

// Método para obter o e-mail do usuário
private String obterEmailUsuario() {
    String email = "";
    String sql = "SELECT email FROM usuario WHERE id = ?";
    try (Connection connection = DBConnection.getConnection();
         PreparedStatement ps = connection.prepareStatement(sql)) {
        ps.setInt(1, usuario.getId());
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            email = rs.getString("email");
        }
    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Erro ao obter o e-mail do usuário.");
    }
    return email;
}




    private JButton criarBotao(String texto) {
        JButton botao = new JButton(texto);
        botao.setBackground(new Color(0x48586F));
        botao.setForeground(Color.WHITE);
        botao.setFont(new Font("Inter", Font.BOLD, 16));
        botao.setBorderPainted(false);
        return botao;
    }

    public static void main(String[] args) {
        // Crie uma instância do DashboardPrincipal com um objeto Usuario fictício
        // Para testes, você pode criar um Usuario com IDs válidos do seu banco
        Usuario usuarioTeste = new Usuario(1, 1); // Substitua pelos IDs corretos
        new DashboardPrincipal(usuarioTeste).setVisible(true);
    }
}
