package views.components;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class SearchComboBox {

    public static void use(JComboBox<ComboItem> comboBox, List<ComboItem> items) {
        comboBox.setEditable(true);
        JTextField textEditor = (JTextField) comboBox.getEditor().getEditorComponent();

        textEditor.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_ENTER) {
                    return;
                }

                // Pega o texto atual
                String text = textEditor.getText();

                // Filtra a lista original (Case insensitive)
                List<ComboItem> filteredItems = new ArrayList<>();
                for (ComboItem item : items) {
                    if (item.getLabel().toLowerCase().contains(text.toLowerCase())) {
                        filteredItems.add(item);
                    }
                }

                // Atualiza o Model na Thread da UI para evitar erros
                SwingUtilities.invokeLater(() -> {
                    DefaultComboBoxModel<ComboItem> newModel = new DefaultComboBoxModel<>(filteredItems.toArray(new ComboItem[0]));
                    comboBox.setModel(newModel);

                    // Restaura o texto que o usuário digitou
                    textEditor.setText(text);

                    // Se houver itens, abre o popup. Se não, fecha.
                    if (filteredItems.size() > 0) {
                        comboBox.showPopup();
                    } else {
                        comboBox.hidePopup();
                    }
                });
            }
        });

    }

}
