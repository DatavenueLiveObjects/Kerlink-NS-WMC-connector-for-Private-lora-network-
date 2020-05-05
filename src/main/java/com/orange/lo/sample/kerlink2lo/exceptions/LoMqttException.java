package com.orange.lo.sample.kerlink2lo.exceptions;

public class LoMqttException extends RuntimeException {

    private static final long serialVersionUID = 224334629639217345L;
    
    public LoMqttException() {
        super();
    }
    
    public LoMqttException(String message) {
        super(message);
    }
    
    public LoMqttException(Throwable t) {
        super(t);
    }

}
