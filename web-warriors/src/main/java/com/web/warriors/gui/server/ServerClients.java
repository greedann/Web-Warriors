package com.web.warriors.gui.server;


import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class ServerClients extends JPanel { // panel that will display the clients connected to the server
    private JComboBox<String> clientList;
    private JTextField messageField;
    private JButton messageSelectedButton;
    private JButton messageAllButton;
    private ServerMain serverMain;


    public ServerClients(ServerMain serverMain) {
        super();
        this.serverMain = serverMain;
        clientList = new JComboBox<String>();
        add(clientList);

        // add text field for message
        // add button to send message

        messageField = new JTextField();
        add(messageField);
        
        messageSelectedButton = new JButton("Send Message to selected client");
        add(messageSelectedButton);

        messageAllButton = new JButton("Send Message to All");
        add(messageAllButton);

        messageSelectedButton.addActionListener(e -> {
            sendMessageToSelectedClient(messageField.getText());
        });

        messageAllButton.addActionListener(e -> {
            sendMessageToAllClients(messageField.getText());
        });
        
    }

    public void addClient(Integer clientID) {
        clientList.addItem("Client " + clientID);
    }

    public void sendMessageToSelectedClient(String message) {
        int selectedClient = clientList.getSelectedIndex();
        serverMain.sendToOne(message, selectedClient);
    }

    public void sendMessageToAllClients(String message) {
        serverMain.sendToAll(message);
    }

}
