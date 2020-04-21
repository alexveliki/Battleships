package bg.sofia.uni.fmi.mjt.battleships.client;

import bg.sofia.uni.fmi.mjt.battleships.server.Commands;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private DataOutputStream dos;
    private DataInputStream dis;
    private boolean shouldRun;
    private static final String HOST = "localhost";
    private static final int SERVER_PORT = 4444;
    private static Scanner in = new Scanner(System.in);

    public Client(Socket socket) throws IOException {
        this.socket = socket;
        this.dos = new DataOutputStream(this.socket.getOutputStream());
        this.dis = new DataInputStream(this.socket.getInputStream());
        this.shouldRun = true;
    }


    public void sendName(String name) throws IOException {
        dos.writeUTF("Name: " + name);
        String response = dis.readUTF();
        System.out.println(response);
    }


    public static void main(String[] args) throws IOException {
        Socket socket = new Socket(HOST, SERVER_PORT);
        Client client = new Client(socket);
        System.out.println("Connect successfully");
        System.out.print("Enter name: ");
        String name;
        name = in.nextLine();

        while(name.length() <3 || name.length() > 20) {
            System.out.println("Name must be between 3 and 20 characters.");
            name = in.nextLine();
        }


        client.sendName(name);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (client.shouldRun) {
                    try {
                        String response = client.dis.readUTF();
                        System.out.println(response);
                        if (response.equals("You have exited the game")) {
                            System.out.println("Press enter to continue");
                            client.shouldRun = false;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        }).start();

        while (client.shouldRun) {
            String command = in.nextLine();
            client.dos.writeUTF(command);
        }
        client.dos.close();
        client.dis.close();
        socket.close();

    }

}