package views;

import views.components.ComboItem;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MatriculaFormView extends JDialog {

    private final JComboBox<ComboItem> cbAluno;
    private final JComboBox<ComboItem> cbTurma;
    private final JButton btnSalvar, btnCancelar;

    public MatriculaFormView(Window parent) {
        super(parent, Dialog.ModalityType.APPLICATION_MODAL);
        setSize(400, 250);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        JPanel painelCampos = new JPanel(new GridLayout(4, 1, 5, 5));
        painelCampos.setBorder(new EmptyBorder(10, 10, 10, 10));

        painelCampos.add(new JLabel("Selecione o Aluno:"));
        cbAluno = new JComboBox<>();
        painelCampos.add(cbAluno);

        painelCampos.add(new JLabel("Selecione a Turma:"));
        cbTurma = new JComboBox<>();
        painelCampos.add(cbTurma);

        add(painelCampos, BorderLayout.CENTER);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnSalvar = new JButton("Matricular");
        btnCancelar = new JButton("Cancelar");
        painelBotoes.add(btnSalvar); painelBotoes.add(btnCancelar);
        add(painelBotoes, BorderLayout.SOUTH);
    }

    public void adicionarAluno(ComboItem item) { cbAluno.addItem(item); }
    public int getAlunoSelecionadoId() {
        ComboItem item = (ComboItem) cbAluno.getSelectedItem();
        return (item != null) ? item.getId() : 0;
    }
    public void adicionarTurma(ComboItem item) { cbTurma.addItem(item); }
    public int getTurmaSelecionadaId() {
        ComboItem item = (ComboItem) cbTurma.getSelectedItem();
        return (item != null) ? item.getId() : 0;
    }

    public JButton getBtnSalvar() { return btnSalvar; }
    public JButton getBtnCancelar() { return btnCancelar; }

    public void limparFormulario() {
        if(cbAluno.getItemCount() > 0) cbAluno.setSelectedIndex(0);
        if(cbTurma.getItemCount() > 0) cbTurma.setSelectedIndex(0);
    }
}