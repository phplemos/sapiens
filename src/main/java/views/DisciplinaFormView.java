package views;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class DisciplinaFormView extends JDialog {

    private final JTextField txtNome;
    private final JTextField txtCodigo;
    private final JTextField txtCargaHoraria;
    private final JTextArea txtDescricao;
    private final JTextArea txtConteudo;

    private final JButton btnSalvar;
    private final JButton btnCancelar;

    private int disciplinaIdParaEdicao = 0;

    public DisciplinaFormView(JDialog parent) {
        super(parent, "Formulário de Disciplina", ModalityType.APPLICATION_MODAL);
        setSize(600, 700);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        JPanel painelCampos = new JPanel(new GridBagLayout());
        painelCampos.setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // 1. Nome
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        painelCampos.add(new JLabel("Nome da Disciplina:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        txtNome = new JTextField();
        painelCampos.add(txtNome, gbc);

        // 2. Código e Carga Horária (na mesma linha visual, ou linhas separadas)
        // Vamos separar para ficar limpo
        gbc.gridy = 1; gbc.gridx = 0;
        gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        painelCampos.add(new JLabel("Código (Sigla):"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        txtCodigo = new JTextField();
        painelCampos.add(txtCodigo, gbc);

        gbc.gridy = 2; gbc.gridx = 0;
        gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        painelCampos.add(new JLabel("Carga Horária (h):"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        txtCargaHoraria = new JTextField(); // Usuário digitará números aqui
        painelCampos.add(txtCargaHoraria, gbc);

        // 3. Descrição (JTextArea)
        gbc.gridy = 3; gbc.gridx = 0;
        gbc.gridwidth = 2; // Ocupa a linha toda
        painelCampos.add(new JLabel("Descrição:"), gbc);

        gbc.gridy = 4;
        gbc.weighty = 0.3; // Ocupa 30% do espaço vertical extra
        gbc.fill = GridBagConstraints.BOTH; // Expande para ambos os lados
        txtDescricao = new JTextArea();
        txtDescricao.setLineWrap(true);
        painelCampos.add(new JScrollPane(txtDescricao), gbc);

        // 4. Conteúdo Programático (JTextArea)
        gbc.gridy = 5;
        gbc.weighty = 0; gbc.fill = GridBagConstraints.NONE; // Reseta peso vertical
        painelCampos.add(new JLabel("Conteúdo Programático:"), gbc);

        gbc.gridy = 6;
        gbc.weighty = 0.7; // Ocupa 70% do espaço vertical extra (maior que descrição)
        gbc.fill = GridBagConstraints.BOTH;
        txtConteudo = new JTextArea();
        txtConteudo.setLineWrap(true);
        painelCampos.add(new JScrollPane(txtConteudo), gbc);


        add(painelCampos, BorderLayout.CENTER);

        // --- Botões ---
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnSalvar = new JButton("Salvar");
        btnCancelar = new JButton("Cancelar");
        painelBotoes.add(btnSalvar);
        painelBotoes.add(btnCancelar);
        add(painelBotoes, BorderLayout.SOUTH);
    }

    // Getters e Setters de Dados
    public String getNome() { return txtNome.getText(); }
    public void setNome(String s) { txtNome.setText(s); }

    public String getCodigo() { return txtCodigo.getText(); }
    public void setCodigo(String s) { txtCodigo.setText(s); }

    public String getCargaHoraria() { return txtCargaHoraria.getText(); }
    public void setCargaHoraria(String s) { txtCargaHoraria.setText(s); }

    public String getDescricao() { return txtDescricao.getText(); }
    public void setDescricao(String s) { txtDescricao.setText(s); }

    public String getConteudo() { return txtConteudo.getText(); }
    public void setConteudo(String s) { txtConteudo.setText(s); }

    public JButton getBtnSalvar() { return btnSalvar; }
    public JButton getBtnCancelar() { return btnCancelar; }

    public void setDisciplinaIdParaEdicao(int id) { this.disciplinaIdParaEdicao = id; }
    public int getDisciplinaIdParaEdicao() { return this.disciplinaIdParaEdicao; }

    public void limparFormulario() {
        txtNome.setText("");
        txtCodigo.setText("");
        txtCargaHoraria.setText("");
        txtDescricao.setText("");
        txtConteudo.setText("");
        disciplinaIdParaEdicao = 0;
    }
}