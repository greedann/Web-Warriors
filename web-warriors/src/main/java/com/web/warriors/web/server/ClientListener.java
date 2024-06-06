package com.web.warriors.web.server;

import java.io.ObjectInputStream;
import java.io.UTFDataFormatException;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ClientListener implements Runnable {
    ObjectInputStream objectIn;
    Server server;
    int id;
    ObjectMapper mapper = new ObjectMapper();

    public ClientListener(ObjectInputStream objectIn, Server server, int id) {
        this.objectIn = objectIn;
        this.server = server;
        this.id = id;
    }

    public void run() {
        String message = "";
        try {
            while (true) {
                message = objectIn.readObject().toString();
                if (processMessage(message, id)) { // if command is "disconnect", break the loop
                    server.removeClient(id);
                    break;
                }
            }
        } catch (java.net.SocketException e) {
            System.out.println("Socket in closed");
        } catch (UTFDataFormatException e) {
            System.out.println("UTFDataFormatException");
            System.err.println("Error reading UTF-8 data: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            // disconnect client from server
            server.removeClient(id);
        }
    }

    private boolean processMessage(String message, int id) {
        try {
            Map<String, Object> data = mapper.readValue(message, new TypeReference<Map<String, Object>>() {
            });
            String type = (String) data.get("type");
            if (type.equals("shoot"))
                server.recastToAllExcept(message, id);
            server.processMessage(data, id);
            if (type.equals("disconnect"))
                return true;
            else
                return false;

        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return false;
    }
}
