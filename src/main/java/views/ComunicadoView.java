package views;

import views.components.ComboItem;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ComunicadoView extends JDialog {

    private final JTabbedPane tabbedPane;

    private JTable tabelaEntrada;
    private DefaultTableModel modelEntrada;
    private JTextArea txtLeitura; // Onde aparece o texto da mensagem selecionada

    private JComboBox<ComboItem> cbTurmaDestino;
    private JTextField txtTitulo;
    private JTextArea txtCorpoEnvio;
    private JButton btnEnviar;

    public ComunicadoView(Window parent) {
        super(parent, ModalityType.APPLICATION_MODAL);
        setTitle("Central de Comunicados");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        tabbedPane = new JTabbedPane();

        JPanel painelEntrada = criarPainelEntrada();
        tabbedPane.addTab("📥 Caixa de Entrada", painelEntrada);

        JPanel painelEnvio = criarPainelEnvio();
        tabbedPane.addTab("📤 Enviar Comunicado", painelEnvio);

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel criarPainelEntrada() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBorder(new EmptyBorder(10, 10, 10, 10));

        String[] colunas = {"ID_VINCULO", "ID_MSG", "Data", "Remetente", "Título", "Lido?"};
        modelEntrada = new DefaultTableModel(colunas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabelaEntrada = new JTable(modelEntrada);

        tabelaEntrada.getColumnModel().getColumn(0).setMinWidth(0);
        tabelaEntrada.getColumnModel().getColumn(0).setMaxWidth(0);
        tabelaEntrada.getColumnModel().getColumn(1).setMinWidth(0);
        tabelaEntrada.getColumnModel().getColumn(1).setMaxWidth(0);

        p.add(new JScrollPane(tabelaEntrada), BorderLayout.NORTH); // Tabela em cima

        txtLeitura = new JTextArea();
        txtLeitura.setEditable(false);
        txtLeitura.setBorder(BorderFactory.createTitledBorder("Conteúdo da Mensagem"));
        txtLeitura.setLineWrap(true);

        p.add(new JScrollPane(txtLeitura), BorderLayout.CENTER); // Texto no meio

        return p;
    }

    private JPanel criarPainelEnvio() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel form = new JPanel(new GridLayout(4, 1, 5, 5));

        form.add(new JLabel("Enviar para Turma:"));
        cbTurmaDestino = new JComboBox<>();
        form.add(cbTurmaDestino);

        form.add(new JLabel("Título:"));
        txtTitulo = new JTextField();
        form.add(txtTitulo);

        p.add(form, BorderLayout.NORTH);

        txtCorpoEnvio = new JTextArea();
        txtCorpoEnvio.setBorder(BorderFactory.createTitledBorder("Mensagem"));
        p.add(new JScrollPane(txtCorpoEnvio), BorderLayout.CENTER);

        btnEnviar = new JButton("Enviar Comunicado");
        p.add(btnEnviar, BorderLayout.SOUTH);

        return p;
    }

    public void adicionarTurma(ComboItem item) { cbTurmaDestino.addItem(item); }
    public int getTurmaSelecionadaId() {
        return (cbTurmaDestino.getSelectedItem() != null) ? ((ComboItem)cbTurmaDestino.getSelectedItem()).getId() : 0;
    }

    public String getTituloEnvio() { return txtTitulo.getText(); }
    public String getCorpoEnvio() { return txtCorpoEnvio.getText(); }

    public JTable getTabelaEntrada() { return tabelaEntrada; }
    public DefaultTableModel getModelEntrada() { return modelEntrada; }
    public JTextArea getTxtLeitura() { return txtLeitura; }
    public JButton getBtnEnviar() { return btnEnviar; }
    public JTabbedPane getTabbedPane() { return tabbedPane; }

    public void limparFormEnvio() {
        txtTitulo.setText("");
        txtCorpoEnvio.setText("");
        if(cbTurmaDestino.getItemCount() > 0) cbTurmaDestino.setSelectedIndex(0);
    }
}