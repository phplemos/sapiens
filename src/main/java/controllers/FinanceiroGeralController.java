package controllers;

import enums.TipoTransacao;
import models.CategoriaFinanceira;
import models.Transacao;
import repositories.CategoriaFinanceiraRepository;
import repositories.TransacaoRepository;
import views.FluxoCaixaView;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class FinanceiroGeralController {

    private final FluxoCaixaView view;
    private final TransacaoRepository transacaoRepo;
    private final CategoriaFinanceiraRepository categoriaRepo;

    public FinanceiroGeralController(FluxoCaixaView view) {
        this.view = view;
        this.transacaoRepo = new TransacaoRepository();
        this.categoriaRepo = new CategoriaFinanceiraRepository();

        configurarTabelaAlertas(); // RF053
        initController();
    }

    private void initController() {
        listarTransacoes();

        // RF045: Receita
        view.getBtnNovaReceita().addActionListener(e -> adicionarTransacao(TipoTransacao.RECEITA));
        // RF045 e RF051: Despesa / Conta a Pagar
        view.getBtnNovaDespesa().addActionListener(e -> adicionarTransacao(TipoTransacao.DESPESA));

        view.getBtnBaixar().addActionListener(e -> baixarConta());
        view.getBtnExcluir().addActionListener(e -> excluirTransacao());

        // RF047: Recibo
        view.getBtnRecibo().addActionListener(e -> emitirRecibo());

        // RF048: Fluxo de Caixa
        view.getBtnAtualizarRelatorio().addActionListener(e -> calcularFluxoCaixa());

        // RF050: Exportar
        view.getBtnExportarCSV().addActionListener(e -> exportarCSV());
    }

    // --- LISTAGEM E ALERTAS (RF053) ---
    private void listarTransacoes() {
        view.getTableModel().setRowCount(0);
        List<Transacao> lista = transacaoRepo.listarTodos();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (Transacao t : lista) {
            String categoria = buscarNomeCategoria(t.getCategoriaId());

            // Lógica de Status
            String status = "PENDENTE";
            if (t.getDataPagamento() != null) status = "PAGO";
            else if (t.getDataTransacao().isBefore(LocalDate.now())) status = "ATRASADO";

            view.getTableModel().addRow(new Object[]{
                    t.getId(),
                    t.getDescricao(),
                    categoria,
                    t.getDataTransacao().format(fmt), // Vencimento ou Competência
                    String.format("%.2f", t.getValor()),
                    t.getTipo(),
                    status
            });
        }
        calcularFluxoCaixa(); // Atualiza saldo automaticamente
    }

    // Configura Cores na Tabela (RF053 - Alerta Visual)
    private void configurarTabelaAlertas() {
        view.getTabela().setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);

                String status = table.getValueAt(row, 6).toString(); // Coluna Status
                String tipo = table.getValueAt(row, 5).toString();   // Coluna Tipo

                if ("ATRASADO".equals(status)) {
                    c.setForeground(Color.RED);
                    c.setFont(c.getFont().deriveFont(Font.BOLD));
                } else if ("PAGO".equals(status)) {
                    c.setForeground(new Color(0, 100, 0)); // Verde Escuro
                } else if ("PENDENTE".equals(status) && "DESPESA".equals(tipo)) {
                    // Alerta Amarelo para contas próximas?
                    // Pode adicionar lógica de data aqui se quiser
                    c.setForeground(Color.ORANGE.darker());
                } else {
                    c.setForeground(Color.BLACK);
                }
                return c;
            }
        });
    }

    // --- ADICIONAR (RF045, RF046, RF051) ---
    private void adicionarTransacao(TipoTransacao tipo) {
        // Formulário simples usando JOptionPane ou JDialog personalizado
        // Vou usar inputs sequenciais para simplificar o código, mas o ideal é uma View dedicada.

        String descricao = JOptionPane.showInputDialog(view, "Descrição da " + tipo + ":");
        if (descricao == null || descricao.trim().isEmpty()) return;

        String valorStr = JOptionPane.showInputDialog(view, "Valor (R$):");
        if (valorStr == null) return;

        // Seleção de Categoria (RF046)
        List<CategoriaFinanceira> categorias = categoriaRepo.listarTodos().stream()
                .filter(c -> c.getTipo() == tipo) // Filtra categorias pelo tipo
                .toList();

        // Se não tiver categorias, cria genérica
        Object[] catsArray = categorias.isEmpty() ? new Object[]{"Geral"} : categorias.stream().map(CategoriaFinanceira::getNome).toArray();

        Object catSelecionada = JOptionPane.showInputDialog(view, "Categoria:", "Categorização",
                JOptionPane.QUESTION_MESSAGE, null, catsArray, catsArray[0]);

        int catId = resolverIdCategoria(catSelecionada, categorias); // Helper para achar ID

        // Data (Contas a Pagar Futuras RF051)
        String dataStr = JOptionPane.showInputDialog(view, "Data de Vencimento/Competência (dd/MM/yyyy):",
                LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        try {
            Transacao t = new Transacao();
            t.setDescricao(descricao);
            t.setValor(Double.parseDouble(valorStr.replace(",", ".")));
            t.setTipo(tipo);
            t.setCategoriaId(catId);
            t.setDataTransacao(LocalDate.parse(dataStr, DateTimeFormatter.ofPattern("dd/MM/yyyy")));

            // Pergunta se já foi pago
            int pago = JOptionPane.showConfirmDialog(view, "Já foi pago/recebido?", "Status", JOptionPane.YES_NO_OPTION);
            if (pago == JOptionPane.YES_OPTION) {
                t.setDataPagamento(LocalDate.now());
            } else {
                t.setDataPagamento(null); // Fica como Conta a Pagar/Receber
            }

            transacaoRepo.salvar(t);
            listarTransacoes();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Erro nos dados: " + ex.getMessage());
        }
    }

    // --- BAIXAR CONTA (RF051 - Fluxo de Pagamento) ---
    private void baixarConta() {
        int row = view.getTabela().getSelectedRow();
        if (row == -1) return;

        int id = (int) view.getTabela().getValueAt(row, 0);
        transacaoRepo.buscarPorId(id).ifPresent(t -> {
            if (t.getDataPagamento() != null) {
                JOptionPane.showMessageDialog(view, "Já está baixado.");
                return;
            }
            t.setDataPagamento(LocalDate.now());
            transacaoRepo.editar(t);
            listarTransacoes();
            JOptionPane.showMessageDialog(view, "Baixa realizada com sucesso!");
        });
    }

    // --- RECIBO (RF047) ---
    private void emitirRecibo() {
        int row = view.getTabela().getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(view, "Selecione uma transação.");
            return;
        }
        int id = (int) view.getTabela().getValueAt(row, 0);
        Transacao t = transacaoRepo.buscarPorId(id).orElseThrow();

        if (t.getDataPagamento() == null) {
            JOptionPane.showMessageDialog(view, "Só é possível emitir recibo de contas pagas.");
            return;
        }

        String html = String.format("<html><body style='width: 300px'>" +
                        "<h2 style='text-align:center'>RECIBO</h2><hr>" +
                        "<b>Valor:</b> R$ %.2f<br>" +
                        "<b>Descrição:</b> %s<br>" +
                        "<b>Data Pagamento:</b> %s<br>" +
                        "<b>Tipo:</b> %s<br><br>" +
                        "<i>Assinado eletronicamente pelo Sistema Sapiens</i>" +
                        "</body></html>",
                t.getValor(), t.getDescricao(), t.getDataPagamento(), t.getTipo());

        JOptionPane.showMessageDialog(view, new JLabel(html), "Emissão de Recibo", JOptionPane.PLAIN_MESSAGE);
    }

    // --- FLUXO DE CAIXA (RF048) ---
    private void calcularFluxoCaixa() {
        List<Transacao> todas = transacaoRepo.listarTodos();

        double entradas = 0;
        double saidas = 0;

        for (Transacao t : todas) {
            // Só conta no fluxo de caixa se JÁ FOI PAGO (Regime de Caixa)
            // Se quiser Regime de Competência, remova o check de getDataPagamento
            if (t.getDataPagamento() != null) {
                if (t.getTipo() == TipoTransacao.RECEITA) {
                    entradas += t.getValor();
                } else {
                    saidas += t.getValor();
                }
            }
        }

        view.setSaldos(
                String.format("R$ %.2f", entradas),
                String.format("R$ %.2f", saidas),
                String.format("R$ %.2f", entradas - saidas)
        );
    }

    // --- EXPORTAR CSV (RF050) ---
    private void exportarCSV() {
        try {
            String path = "relatorio_financeiro.csv"; // Poderia usar JFileChooser
            PrintWriter pw = new PrintWriter(new FileWriter(path));

            pw.println("ID;Descricao;Categoria;Vencimento;Pagamento;Valor;Tipo");

            for (Transacao t : transacaoRepo.listarTodos()) {
                pw.printf("%d;%s;%d;%s;%s;%.2f;%s%n",
                        t.getId(), t.getDescricao(), t.getCategoriaId(),
                        t.getDataTransacao(), t.getDataPagamento(),
                        t.getValor(), t.getTipo());
            }
            pw.close();
            JOptionPane.showMessageDialog(view, "Exportado com sucesso para: " + path);

            // Opcional: Abrir o arquivo automaticamente
            // Desktop.getDesktop().open(new File(path));

        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Erro ao exportar: " + e.getMessage());
        }
    }

    // Helper para Excluir
    private void excluirTransacao() {
        int row = view.getTabela().getSelectedRow();
        if (row == -1) return;
        int id = (int) view.getTabela().getValueAt(row, 0);
        transacaoRepo.excluir(id); // Implementar excluir no BaseRepository ou aqui
        listarTransacoes();
    }

    // Helper simulado para categoria
    private String buscarNomeCategoria(int id) {
        return categoriaRepo.buscarPorId(id).map(CategoriaFinanceira::getNome).orElse("Geral");
    }

    private int resolverIdCategoria(Object nomeSelecionado, List<CategoriaFinanceira> lista) {
        if (nomeSelecionado == null) return 0;
        return lista.stream()
                .filter(c -> c.getNome().equals(nomeSelecionado.toString()))
                .findFirst()
                .map(CategoriaFinanceira::getId)
                .orElse(0);
    }
}