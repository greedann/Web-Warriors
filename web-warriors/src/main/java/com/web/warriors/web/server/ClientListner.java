package com.web.warriors.web.server;

import java.io.ObjectInputStream;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ClientListner implements Runnable {
    ObjectInputStream objectIn;
    Server server;
    int id;
    ObjectMapper mapper = new ObjectMapper();

    public ClientListner(ObjectInputStream objectIn, Server server, int id) {
        this.objectIn = objectIn;
        this.server = server;
        this.id = id;
    }

    public void run() {
        try {
            while (true) {
                String message = objectIn.readObject().toString();
                // System.out.println("Client sent: " + message);
                if (handleMessage(message, id)) { // if true, break the loop
                    server.removeClient(id);
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e);
            // disconnect client from server
            server.removeClient(id);

        }
    }

    private boolean handleMessage(String message, int id) {
        // TODO Auto-generated method stub
        try {
            Map<String, Object> data = mapper.readValue(message, Map.class);
            String type = (String) data.get("type");

            server.handleMessage(data, id);
            if (type.equals("disconnect"))
                return true;
            else
                return false;

        } catch (
        JsonMappingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return false;
    }
}
