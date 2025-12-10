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
        if (usuarioLogado.getTipoPerfil() == TipoPerfilUsuario.ALUNO || usuarioLogado.getTipoPerfil() == TipoPerfilUsuario.RESPONSAVEL) {
            if(view.getTabbedPane().getTabCount()>1){
                view.getTabbedPane().remove(1);
            }

        } else {
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
        carregarCaixaEntrada();

        view.getTabelaEntrada().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) lerMensagemSelecionada();
        });

        if (view.getBtnEnviar() != null) {
            view.getBtnEnviar().addActionListener(e -> enviarComunicado());
        }
    }

    private void carregarCaixaEntrada() {
        view.getModelEntrada().setRowCount(0);
        int meuId = usuarioLogado.getPessoaId();

        List<ComunicadoDestinatario> meusDestinos = destRepo.buscarPorDestinatario(meuId);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        for (ComunicadoDestinatario cd : meusDestinos) {
            Optional<Comunicado> msgOpt = msgRepo.buscarPorId(cd.getComunicadoId());

            if (msgOpt.isPresent()) {
                Comunicado msg = msgOpt.get();

                String remetente = "Sistema";
                if (msg.getRemetentePessoaId() > 0) {
                    Optional<Pessoa> p = pessoaRepo.buscarPorId(msg.getRemetentePessoaId());
                    if (p.isPresent()) remetente = p.get().getNomeCompleto();
                } else {
                    remetente = "Admin/Coordenação";
                }

                view.getModelEntrada().addRow(new Object[]{
                        cd.getId(),
                        msg.getId(),
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

        Optional<Comunicado> msgOpt = msgRepo.buscarPorId(idMsg);
        if (msgOpt.isPresent()) {
            view.getTxtLeitura().setText(msgOpt.get().getCorpo());
            view.getTxtLeitura().setCaretPosition(0); // Rola pro topo
        }

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

        Comunicado c = new Comunicado();
        c.setRemetentePessoaId(usuarioLogado.getPessoaId());
        c.setTitulo(titulo);
        c.setCorpo(corpo);
        c.setDataEnvio(LocalDateTime.now());

        c = msgRepo.salvar(c); // Salva e gera ID

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

        }

        JOptionPane.showMessageDialog(view, "Comunicado enviado para " + enviados + " alunos.");
        view.limparFormEnvio();

        carregarCaixaEntrada();
    }

    public static void dispararNotificacaoSistema(int idDestinatario, String titulo, String corpo) {
        ComunicadoRepository msgRepo = new ComunicadoRepository();
        ComunicadoDestinatarioRepository destRepo = new ComunicadoDestinatarioRepository();

        Comunicado c = new Comunicado();
        c.setRemetentePessoaId(0); // 0 = Sistema
        c.setTitulo(titulo);
        c.setCorpo(corpo);
        c.setDataEnvio(java.time.LocalDateTime.now());

        c = msgRepo.salvar(c);

        ComunicadoDestinatario cd = new ComunicadoDestinatario();
        cd.setComunicadoId(c.getId());
        cd.setDestinatarioPessoaId(idDestinatario);
        cd.setLido(false);

        destRepo.salvar(cd);

        System.out.println("LOG: Notificação estática disparada para ID " + idDestinatario);
    }
}