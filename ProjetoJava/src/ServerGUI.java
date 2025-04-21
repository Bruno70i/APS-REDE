import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;

public class ServerGUI extends JFrame {
    private JTextArea chatArea;
    private JTextField inputField;
    private PrintWriter saida;

    public ServerGUI() {
        setTitle("Servidor");
        setSize(400, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane scroll = new JScrollPane(chatArea);

        inputField = new JTextField();
        inputField.addActionListener(e -> {
            String msg = inputField.getText();
            if (saida != null && !msg.trim().isEmpty()) { // Evitar mensagens em branco
                saida.println(msg);
                chatArea.append("Você: " + msg + "\n");
                inputField.setText("");
            }
        });

        add(scroll, BorderLayout.CENTER);
        add(inputField, BorderLayout.SOUTH);

        setVisible(true);

        iniciarServidor();
    }

    private void iniciarServidor() {
        new Thread(() -> {
            try (ServerSocket servidor = new ServerSocket(12345)) {
                chatArea.append("Servidor aguardando conexão...\n");
                Socket cliente = servidor.accept();
                chatArea.append("Cliente conectado: " + cliente.getInetAddress() + "\n");

                try (BufferedReader entrada = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
                     PrintWriter saida = new PrintWriter(cliente.getOutputStream(), true)) {

                    this.saida = saida; // Atribuir ao atributo da classe

                    String linha;
                    while ((linha = entrada.readLine()) != null) {
                        chatArea.append("Cliente: " + linha + "\n");
                    }
                }
            } catch (IOException e) {
                chatArea.append("Erro no servidor: " + e.getMessage() + "\n");
            }
        }).start();
    }

    public static void main(String[] args) {
        new ServerGUI();
    }
}