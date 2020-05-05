package com.orange.lo.sample.kerlink2lo.exceptions;

public class EncodingTypeException extends RuntimeException {

    private static final long serialVersionUID = 3790961132846530874L;

    public EncodingTypeException() {
        super();
    }
    
    public EncodingTypeException(String message) {
        super(message);
    }
    
    public EncodingTypeException(Throwable t) {
        super(t);
    }

}
