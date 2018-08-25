package com.easyapper.serviceadapter.model;

public class AlterraTravelQuery {
    private String client_key = "hanaYtoFOm6MLNe6";
    private String version = "v1";
    private String session_id;
    private long timestamp;
    private String message;

    public String getClient_key() {
        return client_key;
    }

    public void setClient_key(String client_key) {
        this.client_key = client_key;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getSession_id() {
        return session_id;
    }

    public void setSession_id(String session_id) {
        this.session_id = session_id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "AlterraTravelQuery{" +
                "client_key='" + client_key + '\'' +
                ", version='" + version + '\'' +
                ", session_id='" + session_id + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", message='" + message + '\'' +
                '}';
    }

    public String toJson() {
        return "{" +
                "'client_key':'" + client_key + '\'' +
                ", 'version':'" + version + '\'' +
                ", 'session_id':'" + session_id + '\'' +
                ", 'timestamp':'" + timestamp + '\'' +
                ", 'message':'" + message + '\'' +
                '}';
    }
}
