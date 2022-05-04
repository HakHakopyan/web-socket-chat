package com.hakop.websocketchat.model;

public class SocketSessionInfo {
    String userId;
    String sessionId;

    public SocketSessionInfo(String userId, String sessionId) {
        this.userId = userId;
        this.sessionId = sessionId;
    }

    public String getUserId() {
        return userId;
    }

    public String getSessionId() {
        return sessionId;
    }
}
