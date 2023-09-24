package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import Agentes.AgenteCompradorLibros;

public class InterfazCompradorLibros extends JFrame {
    private AgenteCompradorLibros myAgent;  // El agente comprador asociado
    private JTextField titleField;          // Campo de texto para ingresar el título del libro

    // Constructor de la interfaz gráfica
    public InterfazCompradorLibros(AgenteCompradorLibros a) {
        super(a.getLocalName());

        myAgent = a;

        JPanel p = new JPanel();
        p.setLayout(new BorderLayout());
        p.add(new JLabel("Título del libro a buscar: "), BorderLayout.WEST);
        titleField = new JTextField(15);
        p.add(titleField, BorderLayout.CENTER);
        getContentPane().add(p, BorderLayout.CENTER);

        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    // Método para mostrar la interfaz gráfica
    public void showGui() {
        pack();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int centerX = (int) screenSize.getWidth() / 2;
        int centerY = (int) screenSize.getHeight() / 2;

        setLocation(centerX - getWidth() / 2, centerY - getHeight() / 2);
        super.setVisible(true);
    }

    // Método para obtener el título del libro ingresado en la interfaz
    public String getBookTitle() {
        return titleField.getText().trim();
    }
}
