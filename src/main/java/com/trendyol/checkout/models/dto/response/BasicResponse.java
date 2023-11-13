package com.trendyol.checkout.models.dto.response;

public class BasicResponse {
    private Object message;
    private boolean result;

    public BasicResponse(Object message, boolean result) {
        this.message = message;
        this.result = result;
    }

    public Object getMessage() {
        return message;
    }

    public void setMessage(Object message) {
        this.message = message;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return message.toString();
    }
}
