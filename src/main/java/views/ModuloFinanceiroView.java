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

        // O Painel de Abas Principal
        JTabbedPane abasPrincipais = new JTabbedPane();
        abasPrincipais.setFont(new Font("Arial", Font.BOLD, 14));

        // --- ABA 1: MENSALIDADES (ALUNOS) ---
        MensalidadesView panelMensalidades = new MensalidadesView();
        // Inicializa o Controller passando o Painel
        new MensalidadesController(panelMensalidades);

        // Adiciona à aba com um ícone (opcional)
        abasPrincipais.addTab("Mensalidades & Alunos", panelMensalidades);

        // --- ABA 2: FLUXO DE CAIXA (GERAL) ---
        FluxoCaixaView panelFluxo = new FluxoCaixaView();
        // Inicializa o Controller passando o Painel
        new FinanceiroGeralController(panelFluxo);

        abasPrincipais.addTab("Fluxo de Caixa & Despesas", panelFluxo);

        add(abasPrincipais, BorderLayout.CENTER);
    }
}