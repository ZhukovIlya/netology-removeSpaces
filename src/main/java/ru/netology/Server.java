package ru.netology;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private static final String IP = "127.0.0.1";
    private static final int PORT = 34543;

    public static void main(String[] args) throws IOException {
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.bind(new InetSocketAddress(IP, PORT));
        ExecutorService threadPool = Executors.newFixedThreadPool(4);

        while (true) {
            SocketChannel socketChannel = serverChannel.accept();
            threadPool.submit(removeSpaces(socketChannel));

        }
    }

    public static Runnable removeSpaces(SocketChannel socketChannel) {
        return () -> {
            try {
                ByteBuffer inputBuffer = ByteBuffer.allocate(2 << 11);

                while (socketChannel.isConnected()) {
                    int bytesCount = socketChannel.read(inputBuffer);
                    if (bytesCount == -1) break;
                    String msg = new String(inputBuffer.array(), 0, bytesCount, StandardCharsets.UTF_8);
                    inputBuffer.clear();
                    socketChannel.write(ByteBuffer.wrap(msg.replaceAll(" ", "").getBytes(StandardCharsets.UTF_8)));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
    }

}
