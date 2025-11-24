package controllers.responsaveis;

import views.professores.ProfessorListView;
import views.responsaveis.ResponsavelListView;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        // Garante que o Swing rode na Thread de Eventos
        SwingUtilities.invokeLater(() -> {

            // 1. Cria a View (A janela)
            ResponsavelListView view = new ResponsavelListView();

            // 2. Cria o Controller (A lógica)
            ResponsavelController controller = new ResponsavelController(view);

            // 3. Exibe a View.
            view.setVisible(true);
        });
    }
}