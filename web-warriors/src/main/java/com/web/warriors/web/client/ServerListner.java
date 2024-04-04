package com.web.warriors.web.client;

import java.io.ObjectInputStream;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ServerListner implements Runnable {
    ObjectInputStream objectIn;
    Client client;
    ObjectMapper mapper = new ObjectMapper();

    public ServerListner(ObjectInputStream objectIn, Client client) {
        this.objectIn = objectIn;
        this.client = client;
    }

    public void run() {
        try {
            while (true) {
                String message = objectIn.readObject().toString(); // TODO: fix crash here on close
                System.out.println("Server sent: " + message);

                try {
                    Map<String, Object> data = mapper.readValue(message, Map.class);
                    String type = (String) data.get("type");
                    switch (type) {
                        case "set_id":
                            int x = (int) data.get("id");
                            client.setId(x);
                            break;

                        default:
                            break;
                    }

                } catch (JsonMappingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (JsonProcessingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }

    public void stop() {
        try {
            objectIn.close();
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }
}
