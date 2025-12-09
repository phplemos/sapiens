package views;

import views.components.ComboItem;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class PeriodoLetivoFormView extends JDialog {
    private JTextField txtNome;
    private JComboBox<ComboItem> cbAnoEscolar; // Dropdown de Anos
    private JTextField txtDataInicio;
    private JTextField txtDataFim;
    private JButton btnSalvar, btnCancelar;
    private int idParaEdicao = 0;

    public PeriodoLetivoFormView(JDialog parent) {
        super(parent, "Período Letivo", ModalityType.APPLICATION_MODAL);
        setSize(400, 350);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        JPanel painelCampos = new JPanel(new GridLayout(8, 1, 5, 5)); // 8 linhas
        painelCampos.setBorder(new EmptyBorder(10, 10, 10, 10));

        painelCampos.add(new JLabel("Ano Escolar:"));
        cbAnoEscolar = new JComboBox<>();
        painelCampos.add(cbAnoEscolar);

        painelCampos.add(new JLabel("Nome (ex: 1º Bimestre):"));
        txtNome = new JTextField();
        painelCampos.add(txtNome);

        painelCampos.add(new JLabel("Data Início (AAAA-MM-DD):"));
        txtDataInicio = new JTextField();
        painelCampos.add(txtDataInicio);

        painelCampos.add(new JLabel("Data Fim (AAAA-MM-DD):"));
        txtDataFim = new JTextField();
        painelCampos.add(txtDataFim);

        add(painelCampos, BorderLayout.CENTER);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnSalvar = new JButton("Salvar");
        btnCancelar = new JButton("Cancelar");
        painelBotoes.add(btnSalvar); painelBotoes.add(btnCancelar);
        add(painelBotoes, BorderLayout.SOUTH);
    }

    // Getters e Setters
    public String getNome() { return txtNome.getText(); }
    public void setNome(String s) { txtNome.setText(s); }

    public String getDataInicio() { return txtDataInicio.getText(); }
    public void setDataInicio(String s) { txtDataInicio.setText(s); }

    public String getDataFim() { return txtDataFim.getText(); }
    public void setDataFim(String s) { txtDataFim.setText(s); }

    public int getIdParaEdicao() { return idParaEdicao; }
    public void setIdParaEdicao(int id) { this.idParaEdicao = id; }

    public JButton getBtnSalvar() { return btnSalvar; }
    public JButton getBtnCancelar() { return btnCancelar; }

    // Métodos do Combo
    public void adicionarAnoCombo(ComboItem item) { cbAnoEscolar.addItem(item); }
    public int getAnoSelecionadoId() {
        ComboItem item = (ComboItem) cbAnoEscolar.getSelectedItem();
        return (item != null) ? item.getId() : 0;
    }
    public void setAnoSelecionado(int id) {
        for (int i = 0; i < cbAnoEscolar.getItemCount(); i++) {
            if (cbAnoEscolar.getItemAt(i).getId() == id) {
                cbAnoEscolar.setSelectedIndex(i);
                return;
            }
        }
    }

    public void limparFormulario() {
        txtNome.setText("");
        txtDataInicio.setText("");
        txtDataFim.setText("");
        idParaEdicao = 0;
        if (cbAnoEscolar.getItemCount() > 0) cbAnoEscolar.setSelectedIndex(0);
    }
}