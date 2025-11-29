package controllers.disciplinas;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import views.disciplinas.DisciplinaListView;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            System.out.println("--- Iniciando Teste do Módulo de Disciplinas ---");
            // 1. Cria a View (A janela da lista)
            DisciplinaListView view = new DisciplinaListView();

            // 2. Cria o Controller (A lógica)
            new DisciplinaController(view);

            // 3. Exibe a janela
            view.setVisible(true);

            System.out.println("Janela aberta. Verifique a pasta 'data/disciplinas.json' após salvar.");
        });
    }
}