package com.web.warriors.web.client;

import java.io.ObjectInputStream;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.warriors.game.objects.Hostage;
import com.web.warriors.game.objects.Player;
import com.web.warriors.game.objects.Team;
import java.util.List;

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
                // System.out.println("Server sent: " + message);

                try {
                    Map<String, Object> data = mapper.readValue(message, Map.class);
                    String type = (String) data.get("type");
                    switch (type) {
                        case "set_id":
                            int x = (int) data.get("id");
                            Team team = Team.NONE;
                            switch (data.get("team").toString()) {
                                case "Terrorists":
                                    team = Team.Terrorists;
                                    break;
                                case "CounterTerrorists":
                                    team = Team.CounterTerrorists;
                                    break;
                            }
                            client.init(x,team);
                            break;
                        case "updates":
                            List<Player> players = mapper.readValue(mapper.writeValueAsString(data.get("players")), new TypeReference<List<Player>>() {});
                            client.updatePlayers(players);
                            List<Hostage> hostages = mapper.readValue(mapper.writeValueAsString(data.get("hostages")), new TypeReference<List<Hostage>>() {});
                            client.updateHostages(hostages);
                            break;
                        case "remove_player":
                            client.removePlayer((int)data.get("id"));
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
            e.printStackTrace();
        }
    }

    public void stop() {
        try {
            objectIn.close();
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
