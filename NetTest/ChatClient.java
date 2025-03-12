import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ChatClient {
    private String host;
    private int port;
    private String username;

    public ChatClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void execute() {
        try {
            Socket socket = new Socket(host, port);
            System.out.println("Connected to the chat server");

            new ReadThread(socket).start();
            new WriteThread(socket).start();

        } catch (IOException ex) {
            System.out.println("Error connecting to server: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        String host = "localhost";
        int port = 1234;

        ChatClient client = new ChatClient(host, port);
        client.execute();
    }

    // Thread for reading messages from server
    static class ReadThread extends Thread {
        private BufferedReader reader;

        public ReadThread(Socket socket) {
            try {
                InputStream input = socket.getInputStream();
                reader = new BufferedReader(new InputStreamReader(input));
            } catch (IOException ex) {
                System.out.println("Error getting input stream: " + ex.getMessage());
            }
        }

        public void run() {
            while (true) {
                try {
                    String response = reader.readLine();
                    if (response == null) break;
                    System.out.println("\n" + response);
                    System.out.print("> ");
                } catch (IOException ex) {
                    System.out.println("Connection closed.");
                    break;
                }
            }
        }
    }

    // Thread for sending messages to server
    static class WriteThread extends Thread {
        private PrintWriter writer;
        private Scanner scanner;
        private Socket socket;

        public WriteThread(Socket socket) {
            this.socket = socket;
            scanner = new Scanner(System.in);

            try {
                OutputStream output = socket.getOutputStream();
                writer = new PrintWriter(output, true);
            } catch (IOException ex) {
                System.out.println("Error getting output stream: " + ex.getMessage());
            }
        }

        public void run() {
            System.out.print("Enter your name: ");
            String name = scanner.nextLine();
            writer.println(name);

            String text;
            do {
                System.out.print("> ");
                text = scanner.nextLine();
                writer.println(text);
            } while (!text.equalsIgnoreCase("exit"));

            try {
                socket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
