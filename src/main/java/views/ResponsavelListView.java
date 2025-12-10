package views;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ResponsavelListView extends JDialog {

    private final JTable tabelaResponsaveis;
    private final DefaultTableModel tableModel;
    private final JButton btnNovo;
    private final JButton btnEditar;
    private final JButton btnExcluir;
    private final JButton btnBuscar;
    private final JTextField txtBusca;

    public ResponsavelListView(Window parent) {
        super(parent, Dialog.ModalityType.APPLICATION_MODAL);
        setTitle("Sapiens - Gestão de Alunos");
        setSize(800, 600);
        setLocationRelativeTo(parent);
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
        tabelaResponsaveis = new JTable(tableModel);
        tabelaResponsaveis.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(tabelaResponsaveis);
        add(scrollPane, BorderLayout.CENTER);

        JPanel painelBotoes = new JPanel();
        painelBotoes.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 10));

        btnNovo = new JButton("Novo Responsavel");
        btnEditar = new JButton("Editar Responsavel");
        btnExcluir = new JButton("Excluir Responsavel");

        painelBotoes.add(btnNovo);
        painelBotoes.add(btnEditar);
        painelBotoes.add(btnExcluir);

        add(painelBotoes, BorderLayout.SOUTH);
    }

    public JButton getBtnBuscar() { return  btnBuscar; }
    public JTextField getTxtBusca() { return txtBusca; }

    public JTable getTabelaResponsaveis() {
        return tabelaResponsaveis;
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
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