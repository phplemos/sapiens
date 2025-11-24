package controllers.professores;

import views.professores.ProfessorListView;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        // Garante que o Swing rode na Thread de Eventos
        SwingUtilities.invokeLater(() -> {

            // 1. Cria a View (A janela)
            ProfessorListView view = new ProfessorListView();

            // 2. Cria o Controller (A lógica)
            ProfessorController controller = new ProfessorController(view);

            // 3. Exibe a View.
            view.setVisible(true);
        });
    }
}