package views;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class SelecaoFilhoView extends JDialog {

    private final JTable tabelaFilhos;
    private final DefaultTableModel tableModel;
    private final JButton btnVerBoletim;
    private final JButton btnVerHistorico;
    private final JButton btnCancelar;

    public SelecaoFilhoView(Window parent) {
        super(parent, ModalityType.APPLICATION_MODAL);
        setTitle("Selecionar Dependente");
        setSize(600, 400);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        // --- TOPO ---
        JLabel lblTitulo = new JLabel("Selecione um aluno para visualizar:");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 14));
        lblTitulo.setBorder(new EmptyBorder(10, 10, 10, 10));
        add(lblTitulo, BorderLayout.NORTH);

        // --- CENTRO: Tabela ---
        // Coluna 0: ID (Oculto ou visível), Coluna 1: Nome, Coluna 2: Turma
        String[] colunas = {"ID", "Nome do Aluno", "Turma Atual"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        tabelaFilhos = new JTable(tableModel);
        tabelaFilhos.setRowHeight(25);
        tabelaFilhos.getColumnModel().getColumn(0).setPreferredWidth(30); // ID pequeno
        tabelaFilhos.getColumnModel().getColumn(1).setPreferredWidth(300); // Nome grande

        JScrollPane scroll = new JScrollPane(tabelaFilhos);
        scroll.setBorder(new TitledBorder("Meus Filhos / Dependentes"));
        add(scroll, BorderLayout.CENTER);

        // --- RODAPÉ: Botões ---
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        btnVerHistorico = new JButton("📜 Ver Histórico Completo");
        btnVerBoletim = new JButton("📊 Ver Boletim (Ano Atual)");
        btnCancelar = new JButton("Cancelar");

        // Estilização básica
        btnVerBoletim.setBackground(new Color(100, 149, 237)); // Azul
        btnVerBoletim.setForeground(Color.WHITE);

        painelBotoes.add(btnCancelar);
        painelBotoes.add(btnVerHistorico);
        painelBotoes.add(btnVerBoletim);

        add(painelBotoes, BorderLayout.SOUTH);
    }

    public DefaultTableModel getTableModel() { return tableModel; }
    public JTable getTabelaFilhos() { return tabelaFilhos; }
    public JButton getBtnVerBoletim() { return btnVerBoletim; }
    public JButton getBtnVerHistorico() { return btnVerHistorico; }
    public JButton getBtnCancelar() { return btnCancelar; }
}