package com.orange.lo.sample.kerlink2lo.exceptions;

public class ParseException extends RuntimeException {

    private static final long serialVersionUID = -6618484419833313225L;

    public ParseException() {
        super();
    }
    
    public ParseException(String message) {
        super(message);
    }
    
    public ParseException(Throwable t) {
        super(t);
    }

}
