/*
 * Copyright (c) Orange. All Rights Reserved.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

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
