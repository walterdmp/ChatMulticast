package br.edu.ifsuldeminas.sd.multicast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class ChatEngine implements Runnable {
    private MulticastSocket socket;
    private InetAddress group;
    private int port;
    private String nickname;
    private MessageContainer container;
    private boolean isRunning = true;

    @SuppressWarnings("deprecation") 
    public ChatEngine(String groupIp, int port, String nickname, MessageContainer container) throws IOException {
        this.port = port;
        this.nickname = nickname;
        this.container = container;
        
        this.group = InetAddress.getByName(groupIp);
        this.socket = new MulticastSocket(port);
        
        this.socket.joinGroup(this.group); 

        new Thread(this).start();
    }

    public void send(String text) throws IOException {
        String messageToSend = "[" + nickname + "] " + text;
        byte[] buffer = messageToSend.getBytes();
        
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, port);
        socket.send(packet);
    }

    @Override
    public void run() {
        byte[] buffer = new byte[1024];
        while (isRunning) {
            try {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet); 
                
                String receivedMsg = new String(packet.getData(), 0, packet.getLength());
                container.newMessage(receivedMsg);
            } catch (IOException e) {
                if (isRunning) {
                    container.newMessage("Erro na rede: " + e.getMessage());
                }
            }
        }
    }

    @SuppressWarnings("deprecation")
    public void disconnect() {
        isRunning = false;
        try {
            if (socket != null && !socket.isClosed()) {
                socket.leaveGroup(group);
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}