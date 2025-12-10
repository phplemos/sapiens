package views;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class AlunoListView extends JDialog {

    private final JTable tabelaAlunos;
    private final DefaultTableModel tableModel;
    private final JButton btnNovo;
    private final JButton btnEditar;
    private final JButton btnExcluir;
    private final JButton btnBuscar;
    private final JTextField txtBusca;
    private final JButton btnHistorico;

    public AlunoListView(Window parent) {
        super(parent, Dialog.ModalityType.APPLICATION_MODAL);
        setTitle("Sapiens - Gestão de Alunos");
        setSize(800, 600);
        setLocationRelativeTo(parent); // Centralizar na tela
        setLayout(new BorderLayout(10, 10));

        JPanel painelTopo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txtBusca = new JTextField(20);
        btnBuscar = new JButton("Buscar por Nome/CPF");
        painelTopo.add(new JLabel("Pesquisar:"));
        painelTopo.add(txtBusca);
        painelTopo.add(btnBuscar);
        add(painelTopo, BorderLayout.NORTH);


        String[] colunas = {"ID", "Nome Completo", "CPF", "Email"};

        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaAlunos = new JTable(tableModel);
        tabelaAlunos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(tabelaAlunos);
        add(scrollPane, BorderLayout.CENTER);

        JPanel painelBotoes = new JPanel();
        painelBotoes.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 10));

        btnNovo = new JButton("Novo Aluno");
        btnEditar = new JButton("Editar Aluno");
        btnExcluir = new JButton("Excluir Aluno");
        btnHistorico = new JButton("📄 Ver Perfil/Histórico");
        painelBotoes.add(btnHistorico);
        painelBotoes.add(btnNovo);
        painelBotoes.add(btnEditar);
        painelBotoes.add(btnExcluir);

        add(painelBotoes, BorderLayout.SOUTH);
    }


    public JButton getBtnHistorico() { return btnHistorico; }
    public JTable getTabelaAlunos() {
        return tabelaAlunos;
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    public JTextField getTxtBusca() {
        return txtBusca;
    }

    public JButton getBtnBuscar() {
        return btnBuscar;
    }

    public JButton getBtnNovo() {
        return btnNovo;
    }

    public JButton getBtnEditar() {
        return btnEditar;
    }

    public JButton getBtnExcluir() {
        return btnExcluir;
    }
}
