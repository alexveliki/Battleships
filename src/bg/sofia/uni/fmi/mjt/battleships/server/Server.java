package bg.sofia.uni.fmi.mjt.battleships.server;

import bg.sofia.uni.fmi.mjt.battleships.gameroom.GameRoom;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;

public class Server {
    private static final int SERVER_PORT = 4444;
    protected static final String savedGamesFile = "savedGames.txt";

    public static Map<String, PlayerThread> players = new HashMap<>();
    public static Map<String, GameRoom> gameRooms = new HashMap<>();

    public static void main(String[] args) {

        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {

            System.out.println("Server started and listening for connect requests");
            while (true) {

                PlayerThread playerThread = new PlayerThread(serverSocket.accept());
                new Thread(playerThread).start();

            }

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Server did not start");
        }

    }
}
