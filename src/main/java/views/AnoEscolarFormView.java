package views;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class AnoEscolarFormView extends JDialog {

    private final JTextField txtAno;
    private final JComboBox<String> cbStatus;
    private final JButton btnSalvar, btnCancelar;
    private int idParaEdicao = 0;

    public AnoEscolarFormView(JDialog parent) {
        super(parent, "Ano Escolar", ModalityType.APPLICATION_MODAL);
        setSize(350, 250);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        JPanel painelCampos = new JPanel(new GridLayout(4, 1, 5, 5));
        painelCampos.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Campo Ano
        painelCampos.add(new JLabel("Ano Letivo (ex: 2025):"));
        txtAno = new JTextField();
        painelCampos.add(txtAno);

        // Campo Status
        painelCampos.add(new JLabel("Status:"));
        // Lista fixa de status
        String[] statusOpcoes = {"Planejamento", "Ativo", "Finalizado"};
        cbStatus = new JComboBox<>(statusOpcoes);
        painelCampos.add(cbStatus);

        add(painelCampos, BorderLayout.CENTER);

        // Botões
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnSalvar = new JButton("Salvar");
        btnCancelar = new JButton("Cancelar");
        painelBotoes.add(btnSalvar);
        painelBotoes.add(btnCancelar);
        add(painelBotoes, BorderLayout.SOUTH);
    }

    // Getters e Setters
    public String getAno() { return txtAno.getText(); }
    public void setAno(String s) { txtAno.setText(s); }

    public String getStatus() { return (String) cbStatus.getSelectedItem(); }
    public void setStatus(String s) { cbStatus.setSelectedItem(s); }

    public int getIdParaEdicao() { return idParaEdicao; }
    public void setIdParaEdicao(int id) { this.idParaEdicao = id; }

    public JButton getBtnSalvar() { return btnSalvar; }
    public JButton getBtnCancelar() { return btnCancelar; }

    public void limparFormulario() {
        txtAno.setText("");
        cbStatus.setSelectedIndex(0);
        idParaEdicao = 0;
    }
}