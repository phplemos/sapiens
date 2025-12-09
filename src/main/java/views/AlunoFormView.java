package views;

import views.components.ComboItem;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class AlunoFormView extends JDialog {

    // Campos de Pessoa
    private JTextField txtNome;
    private JTextField txtCpf;
    private JTextField txtDataNasc;
    private JTextField txtRg;
    private JTextField txtTelefone;
    private JTextField txtEmail;

    // Campos de Endereço
    private JTextField txtCep;
    private JTextField txtLogradouro;
    private JTextField txtNumero;
    private JTextField txtBairro;
    private JTextField txtCidade;
    private JTextField txtEstado;

    private JButton btnSalvar;
    private JButton btnCancelar;

    // Armazena o ID da Pessoa/Aluno que está sendo editado (0 se for novo)
    private int pessoaIdParaEdicao = 0;

    // Combobox AlunoREsponsavel
    private JComboBox<ComboItem> cbResponsavel;

    public AlunoFormView(JDialog parent) {
        // 'true' torna o Dialog "modal" (trava a janela principal)
        super(parent, "Formulário de Aluno", true);

        setSize(600, 500);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        GridBagConstraints gbc = new GridBagConstraints();

        // --- 1. Painel de Campos (Centro) ---
        JPanel painelCampos = new JPanel();
        // GridLayout(0, 4) = N linhas, 4 colunas (Label:Campo | Label:Campo)
        painelCampos.setLayout(new GridLayout(0, 4, 10, 10));
        painelCampos.setBorder(new EmptyBorder(10, 10, 10, 10));

        // --- Sub-painel Dados Pessoais ---
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
        gbc.gridy = 4; // Ajuste o índice da linha conforme seu layout atual
        gbc.gridx = 0;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        painelCampos.add(new JLabel("Responsável:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 3; // Ocupa o resto da linha
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Inicializa o ComboBox
        cbResponsavel = new JComboBox<>();
        painelPessoal.add(cbResponsavel, gbc);

        // --- Sub-painel Endereço ---
        JPanel painelEndereco = new JPanel(new GridLayout(0, 2, 5, 5));
        painelEndereco.setBorder(new TitledBorder("Endereço"));

        painelEndereco.add(new JLabel("CEP:"));
        txtCep = new JTextField();
        painelEndereco.add(txtCep);

        painelEndereco.add(new JLabel("Logradouro:"));
        txtLogradouro = new JTextField();
        painelEndereco.add(txtLogradouro);

        painelEndereco.add(new JLabel("Número:"));
        txtNumero = new JTextField();
        painelEndereco.add(txtNumero);

        painelEndereco.add(new JLabel("Bairro:"));
        txtBairro = new JTextField();
        painelEndereco.add(txtBairro);

        painelEndereco.add(new JLabel("Cidade:"));
        txtCidade = new JTextField();
        painelEndereco.add(txtCidade);

        painelEndereco.add(new JLabel("Estado (UF):"));
        txtEstado = new JTextField();
        painelEndereco.add(txtEstado);

        JPanel wrapperCampos = new JPanel(new BorderLayout());
        wrapperCampos.add(painelPessoal, BorderLayout.NORTH);
        wrapperCampos.add(painelEndereco, BorderLayout.SOUTH);

        // Adiciona o wrapper em uma barra de rolagem
        add(new JScrollPane(wrapperCampos), BorderLayout.CENTER);

        // --- 2. Painel de Botões (Sul) ---
        JPanel painelBotoes = new JPanel();
        painelBotoes.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 10));

        btnSalvar = new JButton("Salvar");
        btnCancelar = new JButton("Cancelar");

        painelBotoes.add(btnSalvar);
        painelBotoes.add(btnCancelar);

        add(painelBotoes, BorderLayout.SOUTH);


    }


    // Getters para os botões
    public JButton getBtnSalvar() { return btnSalvar; }
    public JButton getBtnCancelar() { return btnCancelar; }

    // Getters para os campos (o Controller vai usar para pegar os dados)
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

    public String getBairro() { return txtBairro.getText(); }
    public void setBairro(String bairro) { txtBairro.setText(bairro); }

    public String getCep() { return txtCep.getText(); }
    public void setCep(String cep) { txtCep.setText(cep); }

    public String getCidade() { return txtCidade.getText(); }
    public void setCidade(String cidade) { txtCidade.setText(cidade); }

    public  String getEstado() { return txtEstado.getText(); }
    public void setEstado(String estado) { txtEstado.setText(estado); }

    public String getLogradouro() { return txtLogradouro.getText(); }
    public void setLogradouro(String logradouro) { txtLogradouro.setText(logradouro);
    }
    public String getNumero() { return txtNumero.getText(); }
    public void setNumero(String numero) { txtNumero.setText(numero); }


    public void setPessoaIdParaEdicao(int id) {
        this.pessoaIdParaEdicao = id;
    }

    public int getPessoaIdParaEdicao() {
        return this.pessoaIdParaEdicao;
    }

    public void adicionarResponsavelCombo(ComboItem item) {
        cbResponsavel.addItem(item);
    }

    /**
     * Retorna o ID do responsável selecionado (ou 0 se nenhum)
     */
    public int getResponsavelSelecionadoId() {
        ComboItem item = (ComboItem) cbResponsavel.getSelectedItem();
        if (item != null) {
            return item.getId();
        }
        return 0;
    }

    /**
     * Define qual responsável está selecionado (usado na Edição)
     */
    public void setResponsavelSelecionado(int id) {
        for (int i = 0; i < cbResponsavel.getItemCount(); i++) {
            ComboItem item = cbResponsavel.getItemAt(i);
            if (item.getId() == id) {
                cbResponsavel.setSelectedIndex(i);
                return;
            }
        }
    }
    public boolean validateForm(){
        if(txtNome.getText().isEmpty()){
            System.out.println(txtNome.getText());
            JOptionPane.showMessageDialog(null, "Preencha o nome do aluno!");
            return false;
        }
        if(txtCpf.getText().isEmpty()){
            JOptionPane.showMessageDialog(null, "Preencha o cpf do aluno!");
            return false;
        }
        if(txtRg.getText().isEmpty()){
            // JOptionPane.showMessageDialog(null, "Preencha o rg do aluno!");
            return true; // Por enquanto liberado
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
            // JOptionPane.showMessageDialog(null, "Preencha o email do aluno!");
            return true; // Por enquanto liberado
        }
        if(txtLogradouro.getText().isEmpty()){
            JOptionPane.showMessageDialog(null, "Preencha o logradouro do aluno!");
            return false;
        }
        if(txtNumero.getText().isEmpty()){
            JOptionPane.showMessageDialog(null, "Preencha o numero do aluno!");
            return false;
        }
        if(txtBairro.getText().isEmpty()){
            JOptionPane.showMessageDialog(null, "Preencha o bairro do aluno!");
            return false;
        }
        if(txtCidade.getText().isEmpty()){
            JOptionPane.showMessageDialog(null, "Preencha o cidade do aluno!");
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
        txtCep.setText("");
        txtLogradouro.setText("");
        txtNumero.setText("");
        txtBairro.setText("");
        txtCidade.setText("");
        txtEstado.setText("");
        pessoaIdParaEdicao = 0; // Reseta o ID
        if (cbResponsavel.getItemCount() > 0) {
            cbResponsavel.setSelectedIndex(0);
        }
    }
}