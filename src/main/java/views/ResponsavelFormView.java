package views;

import models.Pessoa;
import repositories.PessoaRepository;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.Optional;

public class ResponsavelFormView extends JDialog {

    private final PessoaRepository pessoaRepo;

    private final JTextField txtNome;
    private final JTextField txtCpf;
    private final JTextField txtDataNasc;
    private final JTextField txtRg;
    private final JTextField txtTelefone;
    private final JTextField txtEmail;

    private final JTextField txtCep;
    private final JTextField txtLogradouro;
    private final JTextField txtNumero;
    private final JTextField txtBairro;
    private final JTextField txtCidade;
    private final JTextField txtEstado;

    private final JButton btnSalvar;
    private final JButton btnCancelar;

    private int pessoaIdParaEdicao = 0;

    public ResponsavelFormView(Window parent) {
        super(parent, Dialog.ModalityType.APPLICATION_MODAL);
        setSize(600, 500);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        JPanel painelCampos = new JPanel();
        painelCampos.setLayout(new GridLayout(0, 4, 10, 10));
        painelCampos.setBorder(new EmptyBorder(10, 10, 10, 10));

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

        add(new JScrollPane(wrapperCampos), BorderLayout.CENTER);

        JPanel painelBotoes = new JPanel();
        painelBotoes.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 10));

        btnSalvar = new JButton("Salvar");
        btnCancelar = new JButton("Cancelar");

        painelBotoes.add(btnSalvar);
        painelBotoes.add(btnCancelar);

        add(painelBotoes, BorderLayout.SOUTH);

        this.pessoaRepo = new PessoaRepository();

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

    public boolean validateForm(boolean isEdicao){
        if(txtNome.getText().isEmpty()){
            System.out.println(txtNome.getText());
            JOptionPane.showMessageDialog(null, "Preencha o nome do Responsavel!");
            return false;
        }
        if(txtCpf.getText().isEmpty()){
            JOptionPane.showMessageDialog(null, "Preencha o cpf do Responsavel!");
            return false;
        } else {
            if(isEdicao){
                return true;
            }
            Optional<Pessoa> temCadastro = this.pessoaRepo.buscarPorCPF(txtCpf.getText());
            if(temCadastro.isPresent()){
                JOptionPane.showMessageDialog(null, "CPF ja cadastrado!");
                return false;
            }
        }
        if(txtRg.getText().isEmpty()){
            JOptionPane.showMessageDialog(null, "Preencha o rg do Responsavel!");
            return true;
        }
        if(txtDataNasc.getText().isEmpty()){
            JOptionPane.showMessageDialog(null, "Preencha o data de nascimento do Responsavel!");
            return false;
        }
        if(txtTelefone.getText().isEmpty()){
            JOptionPane.showMessageDialog(null, "Preencha o telefone do Responsavel!");
            return false;
        }
        if(txtEmail.getText().isEmpty()){
            JOptionPane.showMessageDialog(null, "Preencha o email do Responsavel!");
            return true;
        }
        if(txtLogradouro.getText().isEmpty()){
            JOptionPane.showMessageDialog(null, "Preencha o logradouro do Responsavel!");
            return false;
        }
        if(txtNumero.getText().isEmpty()){
            JOptionPane.showMessageDialog(null, "Preencha o numero do Responsavel!");
            return false;
        }
        if(txtBairro.getText().isEmpty()){
            JOptionPane.showMessageDialog(null, "Preencha o bairro do Responsavel!");
            return false;
        }
        if(txtCidade.getText().isEmpty()){
            JOptionPane.showMessageDialog(null, "Preencha o cidade do Responsavel!");
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
        pessoaIdParaEdicao = 0;
    }
}