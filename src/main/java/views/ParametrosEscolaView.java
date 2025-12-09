package views;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ParametrosEscolaView extends JDialog {

    public JTextField txtNomeEscola;
    public JSpinner spnMediaAprovacao;
    public JSpinner spnMediaRecuperacao;
    public JSpinner spnLimiteFaltas;
    public JButton btnSalvar, btnCancelar;

    public ParametrosEscolaView(Window parent) {
        super(parent, ModalityType.APPLICATION_MODAL);
        setTitle("Configurações Globais da Escola");
        setSize(400, 350);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        JPanel painelForm = new JPanel(new GridLayout(4, 2, 10, 20));
        painelForm.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Nome da Escola
        painelForm.add(new JLabel("Nome da Instituição:"));
        txtNomeEscola = new JTextField();
        painelForm.add(txtNomeEscola);

        // Média Aprovação (Spinner de 0.0 a 10.0)
        painelForm.add(new JLabel("Média para Aprovação:"));
        spnMediaAprovacao = new JSpinner(new SpinnerNumberModel(7.0, 0.0, 10.0, 0.5));
        painelForm.add(spnMediaAprovacao);

        // Média Recuperação
        painelForm.add(new JLabel("Média mínima (Recuperação):"));
        spnMediaRecuperacao = new JSpinner(new SpinnerNumberModel(4.0, 0.0, 10.0, 0.5));
        painelForm.add(spnMediaRecuperacao);

        // Limite de Faltas
        painelForm.add(new JLabel("Limite Máximo de Faltas:"));
        spnLimiteFaltas = new JSpinner(new SpinnerNumberModel(20, 0, 300, 1));
        painelForm.add(spnLimiteFaltas);

        add(painelForm, BorderLayout.CENTER);

        // Botões
        JPanel painelSul = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnSalvar = new JButton("Salvar Alterações");
        btnCancelar = new JButton("Cancelar");

        // Destaque para o botão salvar
        btnSalvar.setBackground(new Color(46, 139, 87));
        btnSalvar.setForeground(Color.WHITE);

        painelSul.add(btnCancelar);
        painelSul.add(btnSalvar);
        add(painelSul, BorderLayout.SOUTH);
    }
}