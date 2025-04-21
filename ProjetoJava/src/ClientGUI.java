import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;

public class ClientGUI extends JFrame {
    private JTextArea chatArea;
    private JTextField inputField;
    private PrintWriter saida;

    public ClientGUI(String ipServidor) {
        setTitle("Cliente");
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

        conectarAoServidor(ipServidor);
    }

    private void conectarAoServidor(String ip) {
        new Thread(() -> {
            try (Socket socket = new Socket(ip, 12345);
                 BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter saida = new PrintWriter(socket.getOutputStream(), true)) {

                this.saida = saida; // Atribuir ao atributo da classe
                chatArea.append("Conectado ao servidor!\n");

                String linha;
                while ((linha = entrada.readLine()) != null) {
                    chatArea.append("Servidor: " + linha + "\n");
                }
            } catch (IOException e) {
                chatArea.append("Erro ao conectar: " + e.getMessage() + "\n");
            }
        }).start();
    }

    public static void main(String[] args) {
        String ip = JOptionPane.showInputDialog("Digite o IP do servidor:");
        if (ip != null && !ip.isEmpty()) {
            new ClientGUI(ip);
        }
    }
}