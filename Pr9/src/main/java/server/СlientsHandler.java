package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import java.sql.SQLException;
import java.util.Arrays;

public class СlientsHandler {

    Server server;
    Socket socket;
    DataInputStream in;
    DataOutputStream out;
    private String name;


    public String getName() {
        return name;
    }

    public СlientsHandler(Server server, Socket socket) {


        try {
            this.server = server;
            this.socket = socket;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
            this.name = "";
        } catch (IOException e) {
            e.printStackTrace();
        }


        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    while (true) {
                        String msg = in.readUTF();


                        if (msg.startsWith("/enter")) {
                            String[] text = msg.split(" ");
                            System.out.println(Arrays.toString(text));

                            try {
                                String nick = server.getBaseAuthService().getNicknameByUsernameAndPassword(text[1], text[2]);

                                if (nick != null) {

                                    if (!server.isNickBusy(nick)) {
                                        name = nick;
                                        sendMsg("/enter");
                                        server.subscribe(СlientsHandler.this);
                                        break;
                                    }

                                    sendMsg("Ник уже в сети");
                                    continue;


                                }
                                sendMsg("Неверный ник/праоль");

                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }

                        if (msg.startsWith("/autor")) {
                            String [] text1 = msg.split(" ");
                            System.out.println(Arrays.toString(text1));

                            try {
                               server.getBaseAuthService().addUserTable(text1[1],text1[2],text1[3]);
                               sendMsg("/autor");

                            } catch (SQLException e) {
                                System.out.println(e.getSQLState());
                                System.out.println(e.getMessage());
                                System.out.println(e.getMessage().substring(e.getMessage().lastIndexOf(".",e.getMessage().length()-1)+1,e.getMessage().length()-1 ));
                                String name = e.getMessage().substring(e.getMessage().lastIndexOf(".",e.getMessage().length()-1)+1,e.getMessage().length()-1 );
                                if(name.equalsIgnoreCase("Name")){
                                    sendMsg(text1[1] + " уже занят");
                                }
                                if(name.equalsIgnoreCase("Login")){
                                    sendMsg(text1[2] + " уже занят");
                                }

                            }

                        }


                    }

                    while (true) {
                        String msg = in.readUTF();
                        if (msg.equals("/end")) {
                            sendMsg(msg);
                            break;
                        }

                        if (msg.startsWith("/w")) {
                            String[] text = msg.split(" ", 3);
                            System.out.println(Arrays.toString(text));

                            server.sendPersonalMessage(text[1], getName() + ": " + text[2]);
                            continue;
                        }

                        server.sendMessageAllClients(getName() + ": " + msg);


                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    server.unSubscribe(СlientsHandler.this);
                    closeConnection();
                }


            }
        }).start();

    }


    public void sendMsg(String msg) {
        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
    }

}
