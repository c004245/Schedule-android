package com.playgilround.schedule.client.model;

import android.support.annotation.NonNull;

public class ResponseMessage {

    private int status;
    private String[] message;

    public void setStatus(int status) {
        this.status = status;
    }

    public void setMessage(String[] message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public String[] getMessage() {
        return message;
    }

    @NonNull
    @Override
    public String toString() {

        StringBuilder builder = new StringBuilder();
        builder.append("{ 'status' = '").append(status).append("', 'message' : '[");
        for(String msg : message) {
            builder.append(msg).append("', ");
        }
        builder.append("]}");
        return builder.toString();
    }
}
