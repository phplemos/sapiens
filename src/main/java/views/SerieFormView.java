package views;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class SerieFormView extends JDialog {
    private final JTextField txtNome;
    private final JButton btnSalvar, btnCancelar;
    private int idParaEdicao = 0;

    public SerieFormView(JDialog parent) {
        super(parent, "Série", ModalityType.APPLICATION_MODAL);
        setSize(350, 180);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        JPanel painelCampos = new JPanel(new GridLayout(2, 1, 5, 5));
        painelCampos.setBorder(new EmptyBorder(10, 10, 10, 10));

        painelCampos.add(new JLabel("Nome da Série (ex: 1º Ano Médio):"));
        txtNome = new JTextField();
        painelCampos.add(txtNome);
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
    public int getIdParaEdicao() { return idParaEdicao; }
    public void setIdParaEdicao(int id) { this.idParaEdicao = id; }
    public JButton getBtnSalvar() { return btnSalvar; }
    public JButton getBtnCancelar() { return btnCancelar; }
    public void limparFormulario() { txtNome.setText(""); idParaEdicao = 0; }
}