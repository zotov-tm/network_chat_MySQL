package server;



import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import java.sql.SQLException;
import java.util.Vector;

public class Server {


    ServerSocket serverSocket;
    Socket socket;
    Vector<СlientsHandler> clients;
    DataInputStream in;
    DataOutputStream out;
    BaseAuthService baseAuthService;


    public Server() {
        clients = new Vector<>();
        baseAuthService = new BaseAuthService();
        try {
            BaseAuthService.connect();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {

            serverSocket = new ServerSocket(8401);
            System.out.println("Сервер запущен");
            while (true) {
                socket = serverSocket.accept();
                System.out.println("Клиент подключился");
                new СlientsHandler(this, socket);


            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
            BaseAuthService.disConnect();
        }
    }

    public BaseAuthService getBaseAuthService() {
        return baseAuthService;
    }

    public void subscribe(СlientsHandler client) {
        clients.add(client);
        nickOnline();
        System.out.println(clients);

    }

    public void unSubscribe(СlientsHandler client) {
        clients.remove(client);
        nickOnline();
        System.out.println(clients);

    }

    public void sendMessageAllClients(String msg) {
        for (СlientsHandler o : clients) {
            o.sendMsg(msg);
        }

    }
    public void sendPersonalMessage(String nick,String msg){
        for (СlientsHandler o:clients) {
            if(o.getName().equals(nick)){
                o.sendMsg(msg);
            }
        }
    }

    public boolean isNickBusy(String nick) {
        for (СlientsHandler o : clients) {
            System.out.println( o.getName() + " " +nick);
            if (o.getName().equals(nick)) {
                return true;
            }
        }
        return false;
    }

    public void nickOnline(){
        String text = "/online ";
        for (СlientsHandler o:clients) {
            text+=o.getName() + " ";
        }
        sendMessageAllClients(text);
    }


    public void closeConnection() {
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
