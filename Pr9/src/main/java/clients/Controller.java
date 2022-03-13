package clients;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    private final String IP_ADRESS = "localhost";
    private final int PORT = 8401;
    DataInputStream in;
    DataOutputStream out;
    Socket socket;
    boolean authorization;

    @FXML
    TextArea thePanelForDisplayingMessagesOnTheScreen;
    @FXML
    ListView windowForDisplayingTheListOfOnlineClients;


    @FXML
    HBox loginPanel;
    @FXML
    HBox authorizationPanel;
    @FXML
    TextField authorizationPanelMessageEntryWindow1;
    @FXML
    TextField authorizationPanelMessageEntryWindow;
    @FXML
    PasswordField passwordEntryWindowOfTheAuthorizationPanel;

    @FXML
    HBox messageSendingPanel;
    @FXML
    TextField messageEntryWindowOfTheMessageSendingPanel;
    @FXML
    TextField loginInputWindowOfTheLoginPanel;
    @FXML
    PasswordField passwordEntryWindowOfTheLoginPanel;


    @FXML
    HBox loginChangePanel;
    @FXML
    HBox exitPanel;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            socket = new Socket(IP_ADRESS, PORT);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        new Thread(new Runnable() {


            @Override
            public void run() {

                try {
                    while (true) {

                        String msg = in.readUTF();
                        if (msg.startsWith("/")) {
                            if (msg.equals("/end")) {

                                authorization = false;
                                thePanelForDisplayingMessagesOnTheScreen.clear();
                                changeTheWindowView();
                                break;
                            }
                            if (msg.startsWith("/enter")) {
                                thePanelForDisplayingMessagesOnTheScreen.clear();
                                authorization = true;
                                changeTheWindowView();
                            }
                            if (msg.startsWith("/online")) {
                                Platform.runLater(() -> {
                                    String[] text = msg.split(" ");
                                    System.out.println(Arrays.toString(text));
                                    windowForDisplayingTheListOfOnlineClients.getItems().clear();
                                    for (int i = 1; i < text.length; i++) {
                                        windowForDisplayingTheListOfOnlineClients.getItems().add(text[i]);
                                    }


                                });

                            }
                            if(msg.startsWith("/autor")){
                                authorization = false;
                                thePanelForDisplayingMessagesOnTheScreen.clear();
                                thePanelForDisplayingMessagesOnTheScreen.appendText("Вы авторизовались, ввдеите логи, пароль");
                                changeTheWindowView();
                            }
                        } else {
                            thePanelForDisplayingMessagesOnTheScreen.appendText(msg + "\n");
                        }


                    }


//                    while (true) {
//                        String msg = in.readUTF();
//                        System.out.println(msg + "66666666 ");
//                        thePanelForDisplayingMessagesOnTheScreen.appendText(msg + "\n");
//                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    closeConnection();
                }
            }
        }).start();

    }


    public void sendMsgAuthorization(ActionEvent actionEvent) {
        try {
            out.writeUTF("/autor " + authorizationPanelMessageEntryWindow1.getText() + " " + authorizationPanelMessageEntryWindow.getText() + " " + passwordEntryWindowOfTheAuthorizationPanel.getText());
            authorizationPanelMessageEntryWindow1.clear();
            authorizationPanelMessageEntryWindow1.requestFocus();
            authorizationPanelMessageEntryWindow.clear();
            authorizationPanelMessageEntryWindow.requestFocus();
            passwordEntryWindowOfTheAuthorizationPanel.clear();
            passwordEntryWindowOfTheAuthorizationPanel.requestFocus();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void sendMsgEnter(ActionEvent actionEvent) {
        try {
            out.writeUTF("/enter" + " " + loginInputWindowOfTheLoginPanel.getText() + " " + passwordEntryWindowOfTheLoginPanel.getText());
            loginInputWindowOfTheLoginPanel.clear();
            loginInputWindowOfTheLoginPanel.requestFocus();
            passwordEntryWindowOfTheLoginPanel.clear();
            passwordEntryWindowOfTheLoginPanel.requestFocus();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void sendMdgSendMsg(ActionEvent actionEvent) {
        try {
            out.writeUTF(messageEntryWindowOfTheMessageSendingPanel.getText());
            messageEntryWindowOfTheMessageSendingPanel.clear();
            messageEntryWindowOfTheMessageSendingPanel.requestFocus();
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


    private void changeTheWindowView() {
        if (authorization) {
            messageSendingPanel.setVisible(true);
            messageSendingPanel.setManaged(true);
            windowForDisplayingTheListOfOnlineClients.setVisible(true);
            windowForDisplayingTheListOfOnlineClients.setManaged(true);
            authorizationPanel.setVisible(false);
            authorizationPanel.setManaged(false);
            loginChangePanel.setVisible(false);
            loginChangePanel.setManaged(false);
            exitPanel.setVisible(false);
            exitPanel.setManaged(false);
            loginPanel.setVisible(false);
            loginPanel.setManaged(false);
        } else {
            authorizationPanel.setVisible(false);
            authorizationPanel.setManaged(false);
            messageSendingPanel.setVisible(false);
            messageSendingPanel.setManaged(false);
            windowForDisplayingTheListOfOnlineClients.setVisible(false);
            windowForDisplayingTheListOfOnlineClients.setManaged(false);
            loginChangePanel.setVisible(false);
            loginChangePanel.setManaged(false);
            exitPanel.setVisible(false);
            exitPanel.setManaged(false);
            loginPanel.setVisible(true);
            loginPanel.setManaged(true);
        }


    }

    public void showTheAuthorizationPanel(ActionEvent actionEvent) {
        authorizationPanel.setVisible(true);
        authorizationPanel.setManaged(true);
        messageSendingPanel.setVisible(false);
        messageSendingPanel.setManaged(false);
        windowForDisplayingTheListOfOnlineClients.setVisible(false);
        windowForDisplayingTheListOfOnlineClients.setManaged(false);
        loginChangePanel.setVisible(false);
        loginChangePanel.setManaged(false);
        exitPanel.setVisible(false);
        exitPanel.setManaged(false);
        loginPanel.setVisible(false);
        loginPanel.setManaged(false);

    }

    public void showTheLoginPanel(ActionEvent actionEvent) {
        authorizationPanel.setVisible(false);
        authorizationPanel.setManaged(false);
        messageSendingPanel.setVisible(false);
        messageSendingPanel.setManaged(false);
        windowForDisplayingTheListOfOnlineClients.setVisible(false);
        windowForDisplayingTheListOfOnlineClients.setManaged(false);
        loginChangePanel.setVisible(false);
        loginChangePanel.setManaged(false);
        exitPanel.setVisible(false);
        exitPanel.setManaged(false);
        loginPanel.setVisible(true);
        loginPanel.setManaged(true);

    }

    public void showTheLoginChangePanel(ActionEvent actionEvent) {

        loginChangePanel.setVisible(true);
        loginChangePanel.setManaged(true);
        messageSendingPanel.setVisible(false);
        messageSendingPanel.setManaged(false);
        windowForDisplayingTheListOfOnlineClients.setVisible(false);
        windowForDisplayingTheListOfOnlineClients.setManaged(false);
        authorizationPanel.setVisible(false);
        authorizationPanel.setManaged(false);
        exitPanel.setVisible(false);
        exitPanel.setManaged(false);
        loginPanel.setVisible(false);
        loginPanel.setManaged(false);

    }

    public void showTheExitPanel(ActionEvent actionEvent) {

        exitPanel.setVisible(true);
        exitPanel.setManaged(true);
        messageSendingPanel.setVisible(false);
        messageSendingPanel.setManaged(false);
        windowForDisplayingTheListOfOnlineClients.setVisible(false);
        windowForDisplayingTheListOfOnlineClients.setManaged(false);
        authorizationPanel.setVisible(false);
        authorizationPanel.setManaged(false);
        loginChangePanel.setVisible(false);
        loginChangePanel.setManaged(false);
        loginPanel.setVisible(false);
        loginPanel.setManaged(false);
    }

    public void showTheChatPanel(ActionEvent actionEvent) {
        messageSendingPanel.setVisible(true);
        messageSendingPanel.setManaged(true);
        windowForDisplayingTheListOfOnlineClients.setVisible(true);
        windowForDisplayingTheListOfOnlineClients.setManaged(true);
        authorizationPanel.setVisible(false);
        authorizationPanel.setManaged(false);
        loginChangePanel.setVisible(false);
        loginChangePanel.setManaged(false);
        exitPanel.setVisible(false);
        exitPanel.setManaged(false);
        loginPanel.setVisible(false);
        loginPanel.setManaged(false);


    }

    public void disPose() {
        if (out != null) {
            try {
                out.writeUTF("/end");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
