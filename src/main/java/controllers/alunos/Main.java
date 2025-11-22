package controllers.alunos;

import views.alunos.AlunoFormView;
import views.alunos.AlunoListView;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        // Garante que o Swing rode na Thread de Eventos
        SwingUtilities.invokeLater(() -> {

            // 1. Cria a View (A janela)
            AlunoListView view = new AlunoListView();

            // 2. Cria o Controller (A lógica)
            //    e passa a View para ele no construtor.
            AlunoController controller = new AlunoController(view);

            // 3. Exibe a View.
            // O construtor do controller já foi executado e
            // o método 'atualizarTabela()' já foi chamado.
            view.setVisible(true);
        });
    }
}