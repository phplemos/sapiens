package views;

import models.Pessoa;
import repositories.PessoaRepository;
import views.components.ComboItem;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.Optional;

public class AlunoFormView extends JDialog {

    private final PessoaRepository pessoaRepository;

    private final JTextField txtNome;
    private final JTextField txtCpf;
    private final JTextField txtDataNasc;
    private final JTextField txtRg;
    private final JTextField txtTelefone;
    private final JTextField txtEmail;

    private final JButton btnSalvar;
    private final JButton btnCancelar;

    private int pessoaIdParaEdicao = 0; //0 se for novo

    private final JComboBox<ComboItem> cbResponsavel;

    public AlunoFormView(Window parent) {
        super(parent, Dialog.ModalityType.APPLICATION_MODAL);
        setSize(600, 500);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        GridBagConstraints gbc = new GridBagConstraints();

        // Painel de Campos (Centro)
        JPanel painelCampos = new JPanel();
        painelCampos.setLayout(new GridLayout(0, 4, 10, 10));
        painelCampos.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Dados Pessoais
        JPanel painelPessoal = new JPanel(new GridLayout(0, 2, 5, 5));
        painelPessoal.setBorder(new TitledBorder("Dados Pessoais"));

        painelPessoal.add(new JLabel("Nome Completo:"));
        txtNome = new JTextField();
        painelPessoal.add(txtNome);

        painelPessoal.add(new JLabel("CPF:"));
        txtCpf = new JTextField();
        painelPessoal.add(txtCpf);

        painelPessoal.add(new JLabel("Data Nasc. (AAAA-MM-DD):"));
        txtDataNasc = new JTextField();
        painelPessoal.add(txtDataNasc);

        painelPessoal.add(new JLabel("RG:"));
        txtRg = new JTextField();
        painelPessoal.add(txtRg);

        painelPessoal.add(new JLabel("Telefone:"));
        txtTelefone = new JTextField();
        painelPessoal.add(txtTelefone);

        painelPessoal.add(new JLabel("Email:"));
        txtEmail = new JTextField();
        painelPessoal.add(txtEmail);
        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        painelCampos.add(new JLabel("Responsável:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        cbResponsavel = new JComboBox<>();
        painelPessoal.add(cbResponsavel, gbc);

        JPanel wrapperCampos = new JPanel(new BorderLayout());
        wrapperCampos.add(painelPessoal, BorderLayout.NORTH);


        add(new JScrollPane(wrapperCampos), BorderLayout.CENTER);

        // Painel de Botões (Sul)
        JPanel painelBotoes = new JPanel();
        painelBotoes.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 10));

        btnSalvar = new JButton("Salvar");
        btnCancelar = new JButton("Cancelar");

        painelBotoes.add(btnSalvar);
        painelBotoes.add(btnCancelar);

        add(painelBotoes, BorderLayout.SOUTH);
        this.pessoaRepository = new PessoaRepository();

    }


    public JButton getBtnSalvar() { return btnSalvar; }
    public JButton getBtnCancelar() { return btnCancelar; }

    public String getNome() { return txtNome.getText(); }
    public void setNome(String nome) { txtNome.setText(nome); }

    public String getCpf() { return txtCpf.getText(); }
    public void setCpf(String cpf) { txtCpf.setText(cpf); }

    public String getRg() { return txtRg.getText(); }
    public void setRg(String rg) { txtRg.setText(rg); }

    public String getDataNasc() { return txtDataNasc.getText(); }
    public void setDataNasc(String dataNasc) { txtDataNasc.setText(dataNasc); }

    public String getTelefone() { return txtTelefone.getText(); }
    public void setTelefone(String telefone) { txtTelefone.setText(telefone); }

    public String getEmail() { return txtEmail.getText(); }
    public void setEmail(String email) { txtEmail.setText(email); }


    public void setPessoaIdParaEdicao(int id) {
        this.pessoaIdParaEdicao = id;
    }

    public int getPessoaIdParaEdicao() {
        return this.pessoaIdParaEdicao;
    }

    public void adicionarResponsavelCombo(ComboItem item) {
        cbResponsavel.addItem(item);
    }

    public int getResponsavelSelecionadoId() {
        ComboItem item = (ComboItem) cbResponsavel.getSelectedItem();
        if (item != null) {
            return item.getId();
        }
        return 0;
    }

    public void setResponsavelSelecionado(int id) {
        for (int i = 0; i < cbResponsavel.getItemCount(); i++) {
            ComboItem item = cbResponsavel.getItemAt(i);
            if (item.getId() == id) {
                cbResponsavel.setSelectedIndex(i);
                return;
            }
        }
    }

    public boolean validateForm(boolean isEdicao ){
        if(txtNome.getText().isEmpty()){
            System.out.println(txtNome.getText());
            JOptionPane.showMessageDialog(null, "Preencha o nome do aluno!");
            return false;
        }
        if(txtCpf.getText().isEmpty()){
            JOptionPane.showMessageDialog(null, "Preencha o cpf do aluno!");
            return false;
        } else {
            if(isEdicao){
                return true;
            }
            Optional<Pessoa> temCadastro = this.pessoaRepository.buscarPorCPF(txtCpf.getText());
            if(temCadastro.isPresent()){
                JOptionPane.showMessageDialog(null, "CPF ja cadastrado!");
                return false;
            }
        }
        if(txtRg.getText().isEmpty()){
            JOptionPane.showMessageDialog(null, "Preencha o rg do aluno!");
            return true;
        }
        if(txtDataNasc.getText().isEmpty()){
            JOptionPane.showMessageDialog(null, "Preencha o data de nascimento do aluno!");
            return false;
        }
        if(txtTelefone.getText().isEmpty()){
            JOptionPane.showMessageDialog(null, "Preencha o telefone do aluno!");
            return false;
        }
        if(txtEmail.getText().isEmpty()){
            JOptionPane.showMessageDialog(null, "Preencha o email do aluno!");
            return false;
        }
        if(cbResponsavel.getSelectedIndex() == 0){
            JOptionPane.showMessageDialog(null, "Obrigatorio selecionar responsavel");
            return false;
        }
        return true;
    }

    public void limparFormulario() {
        txtNome.setText("");
        txtCpf.setText("");
        txtDataNasc.setText("");
        txtRg.setText("");
        txtTelefone.setText("");
        txtEmail.setText("");
        pessoaIdParaEdicao = 0; // Reseta o ID
        if (cbResponsavel.getItemCount() > 0) {
            cbResponsavel.setSelectedIndex(0);
        }
    }
}