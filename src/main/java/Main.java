import controllers.*;
import models.Endereco;
import models.Pessoa;
import models.Usuario;
import repositories.EnderecoRepository;
import repositories.PessoaRepository;
import repositories.UsuarioRepository;
import views.*;

import javax.swing.*;
import java.time.LocalDate;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;

public class Main {

    public static void main(String[] args) {
        // Estilo visual nativo
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}

        final boolean isPopulated = SeedersController.populate();

        if (isPopulated) {
            SwingUtilities.invokeLater(() -> {
                System.out.println("Funcionou");
                LoginController controller = new LoginController();
                controller.start();
            });
        } else {
            System.out.println("Sem dados para iniciar!");
        }


    }

}