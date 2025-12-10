package views;

import enums.TurnoTurma; // Certifique-se de ter esse Enum
import views.components.ComboItem;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class TurmaFormView extends JDialog {

    private final JTextField txtNome;
    private final JComboBox<TurnoTurma> cbTurno;
    private final JComboBox<ComboItem> cbSerie;
    private final JComboBox<ComboItem> cbAno;

    private final JButton btnSalvar, btnCancelar;
    private int idParaEdicao = 0;

    public TurmaFormView(Window parent) {
        super(parent, Dialog.ModalityType.APPLICATION_MODAL);
        setSize(450, 400);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        JPanel painelCampos = new JPanel(new GridLayout(8, 1, 5, 5));
        painelCampos.setBorder(new EmptyBorder(10, 10, 10, 10));

        painelCampos.add(new JLabel("Nome da Turma (ex: Turma A, 101):"));
        txtNome = new JTextField();
        painelCampos.add(txtNome);

        painelCampos.add(new JLabel("Turno:"));
        cbTurno = new JComboBox<>(TurnoTurma.values()); // Popula com MANHA, TARDE, NOITE
        painelCampos.add(cbTurno);

        painelCampos.add(new JLabel("Série:"));
        cbSerie = new JComboBox<>();
        painelCampos.add(cbSerie);

        painelCampos.add(new JLabel("Ano Letivo:"));
        cbAno = new JComboBox<>();
        painelCampos.add(cbAno);

        add(painelCampos, BorderLayout.CENTER);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnSalvar = new JButton("Salvar");
        btnCancelar = new JButton("Cancelar");
        painelBotoes.add(btnSalvar);
        painelBotoes.add(btnCancelar);
        add(painelBotoes, BorderLayout.SOUTH);
    }

    public String getNome() { return txtNome.getText(); }
    public void setNome(String s) { txtNome.setText(s); }

    public TurnoTurma getTurno() { return (TurnoTurma) cbTurno.getSelectedItem(); }
    public void setTurno(TurnoTurma t) { cbTurno.setSelectedItem(t); }

    public void adicionarSerie(ComboItem item) { cbSerie.addItem(item); }
    public int getSerieId() {
        ComboItem item = (ComboItem) cbSerie.getSelectedItem();
        return (item != null) ? item.getId() : 0;
    }

    public void setSerieId(int id) {
        for (int i=0; i<cbSerie.getItemCount(); i++) {
            if (cbSerie.getItemAt(i).getId() == id) {
                cbSerie.setSelectedIndex(i);
                return;
            }
        }
    }

    public void adicionarAno(ComboItem item) { cbAno.addItem(item); }
    public int getAnoId() {
        ComboItem item = (ComboItem) cbAno.getSelectedItem();
        return (item != null) ? item.getId() : 0;
    }
    public void setAnoId(int id) {
        for (int i=0; i<cbAno.getItemCount(); i++) {
            if (cbAno.getItemAt(i).getId() == id) {
                cbAno.setSelectedIndex(i);
                return;
            }
        }
    }

    public int getIdParaEdicao() { return idParaEdicao; }
    public void setIdParaEdicao(int id) { this.idParaEdicao = id; }

    public JButton getBtnSalvar() { return btnSalvar; }
    public JButton getBtnCancelar() { return btnCancelar; }

    public void limparFormulario() {
        txtNome.setText("");
        cbTurno.setSelectedIndex(0);
        if(cbSerie.getItemCount() > 0) cbSerie.setSelectedIndex(0);
        if(cbAno.getItemCount() > 0) cbAno.setSelectedIndex(0);
        idParaEdicao = 0;
    }
}