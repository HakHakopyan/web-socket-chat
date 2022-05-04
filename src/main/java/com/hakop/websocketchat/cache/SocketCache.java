package com.hakop.websocketchat.cache;

import com.hakop.websocketchat.model.SocketSessionInfo;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class SocketCache {
    private final Map<String, SocketSessionInfo> clientNameToSocketUserMap = new HashMap<>();

    public void add(String clientName, String socketUser, String sessionId) {
        clientNameToSocketUserMap.put(clientName, new SocketSessionInfo(socketUser, sessionId));
    }

    public SocketSessionInfo getUserSession(String clientName) {
        return clientNameToSocketUserMap.get(clientName);
    }
}
