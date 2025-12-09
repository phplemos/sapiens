package controllers;

import enums.TipoPerfilUsuario;
import models.*;
import repositories.*;
import views.components.ComboItem;
import views.ComunicadoView;

import javax.swing.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class ComunicadoController {

    private final ComunicadoView view;
    private final Usuario usuarioLogado;

    // Repositórios
    private final ComunicadoRepository msgRepo;
    private final ComunicadoDestinatarioRepository destRepo;
    private final TurmaRepository turmaRepo;
    private final MatriculaRepository matriculaRepo;
    private final PessoaRepository pessoaRepo;

    public ComunicadoController(ComunicadoView view, Usuario usuario) {
        this.view = view;
        this.usuarioLogado = usuario;

        this.msgRepo = new ComunicadoRepository();
        this.destRepo = new ComunicadoDestinatarioRepository();
        this.turmaRepo = new TurmaRepository();
        this.matriculaRepo = new MatriculaRepository();
        this.pessoaRepo = new PessoaRepository();

        configurarAcesso();
        initController();
    }

    private void configurarAcesso() {
        // Se for ALUNO, remove a aba de envio (index 1)
        if (usuarioLogado.getTipoPerfil() == TipoPerfilUsuario.ALUNO || usuarioLogado.getTipoPerfil() == TipoPerfilUsuario.RESPONSAVEL) {
            if(view.getTabbedPane().getTabCount()>1){
                view.getTabbedPane().remove(1);
            }

        } else {
            // Se for Admin/Prof, carrega as turmas no combo
            carregarTurmas();
        }
    }

    private void carregarTurmas() {
        List<Turma> turmas = turmaRepo.listarTodos();
        view.adicionarTurma(new ComboItem(0, "Selecione uma turma..."));
        for (Turma t : turmas) {
            view.adicionarTurma(new ComboItem(t.getId(), t.getNome()));
        }
    }

    private void initController() {
        // Carrega Entrada
        carregarCaixaEntrada();

        // Listener de Leitura (Clique na tabela)
        view.getTabelaEntrada().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) lerMensagemSelecionada();
        });

        // Listener de Envio
        if (view.getBtnEnviar() != null) {
            view.getBtnEnviar().addActionListener(e -> enviarComunicado());
        }
    }

    private void carregarCaixaEntrada() {
        view.getModelEntrada().setRowCount(0);
        int meuId = usuarioLogado.getPessoaId();

        // 1. Busca mensagens destinadas a mim
        List<ComunicadoDestinatario> meusDestinos = destRepo.buscarPorDestinatario(meuId);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        for (ComunicadoDestinatario cd : meusDestinos) {
            Optional<Comunicado> msgOpt = msgRepo.buscarPorId(cd.getComunicadoId());

            if (msgOpt.isPresent()) {
                Comunicado msg = msgOpt.get();

                // Busca nome do remetente
                String remetente = "Sistema";
                if (msg.getRemetentePessoaId() > 0) {
                    Optional<Pessoa> p = pessoaRepo.buscarPorId(msg.getRemetentePessoaId());
                    if (p.isPresent()) remetente = p.get().getNomeCompleto();
                } else {
                    remetente = "Admin/Coordenação";
                }

                view.getModelEntrada().addRow(new Object[]{
                        cd.getId(),       // ID do vínculo (pra marcar lido)
                        msg.getId(),      // ID da msg
                        msg.getDataEnvio().format(fmt),
                        remetente,
                        msg.getTitulo(),
                        cd.isLido() ? "Sim" : "NÃO"
                });
            }
        }
    }

    private void lerMensagemSelecionada() {
        int row = view.getTabelaEntrada().getSelectedRow();
        if (row == -1) return;

        int idVinculo = (int) view.getTabelaEntrada().getValueAt(row, 0);
        int idMsg = (int) view.getTabelaEntrada().getValueAt(row, 1);

        // 1. Mostrar o corpo da mensagem
        Optional<Comunicado> msgOpt = msgRepo.buscarPorId(idMsg);
        if (msgOpt.isPresent()) {
            view.getTxtLeitura().setText(msgOpt.get().getCorpo());
            view.getTxtLeitura().setCaretPosition(0); // Rola pro topo
        }

        // 2. Marcar como LIDO se ainda não foi
        String statusLido = (String) view.getTabelaEntrada().getValueAt(row, 5);
        if ("NÃO".equals(statusLido)) {
            destRepo.marcarComoLido(idVinculo);
            view.getTabelaEntrada().setValueAt("Sim", row, 5); // Atualiza visualmente
        }
    }

    private void enviarComunicado() {
        int turmaId = view.getTurmaSelecionadaId();
        String titulo = view.getTituloEnvio();
        String corpo = view.getCorpoEnvio();

        if (turmaId == 0 || titulo.isEmpty() || corpo.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Preencha a Turma, Título e Mensagem.");
            return;
        }

        // 1. Salvar a Mensagem
        Comunicado c = new Comunicado();
        c.setRemetentePessoaId(usuarioLogado.getPessoaId());
        c.setTitulo(titulo);
        c.setCorpo(corpo);
        c.setDataEnvio(LocalDateTime.now());

        c = msgRepo.salvar(c); // Salva e gera ID

        // 2. Distribuir para todos os alunos da turma
        // Busca matrículas ativas da turma
        List<Matricula> matriculas = matriculaRepo.listarTodos().stream()
                .filter(m -> m.getTurmaId() == turmaId)
                .toList();

        int enviados = 0;
        for (Matricula m : matriculas) {
            ComunicadoDestinatario cd = new ComunicadoDestinatario();
            cd.setComunicadoId(c.getId());
            cd.setDestinatarioPessoaId(m.getAlunoPessoaId()); // O aluno recebe
            cd.setLido(false);

            destRepo.salvar(cd);
            enviados++;

            // Opcional: Enviar também para os Responsáveis (usando AlunoResponsavelRepo)
        }

        JOptionPane.showMessageDialog(view, "Comunicado enviado para " + enviados + " alunos.");
        view.limparFormEnvio();

        // Atualiza minha própria caixa de entrada (caso eu tenha mandado pra mim mesmo por teste)
        carregarCaixaEntrada();
    }

    public static void dispararNotificacaoSistema(int idDestinatario, String titulo, String corpo) {
        ComunicadoRepository msgRepo = new ComunicadoRepository();
        ComunicadoDestinatarioRepository destRepo = new ComunicadoDestinatarioRepository();

        // 1. Cria a Mensagem
        models.Comunicado c = new models.Comunicado();
        c.setRemetentePessoaId(0); // 0 = Sistema
        c.setTitulo(titulo);
        c.setCorpo(corpo);
        c.setDataEnvio(java.time.LocalDateTime.now());

        c = msgRepo.salvar(c);

        // 2. Cria o Vínculo
        models.ComunicadoDestinatario cd = new models.ComunicadoDestinatario();
        cd.setComunicadoId(c.getId());
        cd.setDestinatarioPessoaId(idDestinatario);
        cd.setLido(false);

        destRepo.salvar(cd);

        System.out.println("LOG: Notificação estática disparada para ID " + idDestinatario);
    }
}