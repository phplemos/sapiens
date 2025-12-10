package views;

import controllers.FinanceiroGeralController;
import controllers.MensalidadesController;

import javax.swing.*;
import java.awt.*;

public class ModuloFinanceiroView extends JDialog {

    public ModuloFinanceiroView(Window parent) {
        super(parent, Dialog.ModalityType.APPLICATION_MODAL);
        setTitle("Sapiens - Gestão Financeira Completa");
        setSize(1000, 700);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        JTabbedPane abasPrincipais = new JTabbedPane();
        abasPrincipais.setFont(new Font("Arial", Font.BOLD, 14));

        MensalidadesView panelMensalidades = new MensalidadesView();
        new MensalidadesController(panelMensalidades);

        abasPrincipais.addTab("Mensalidades & Alunos", panelMensalidades);

        FluxoCaixaView panelFluxo = new FluxoCaixaView();
        new FinanceiroGeralController(panelFluxo);

        abasPrincipais.addTab("Fluxo de Caixa & Despesas", panelFluxo);

        add(abasPrincipais, BorderLayout.CENTER);
    }
}