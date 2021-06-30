/*
 * Copyright (c) Orange. All Rights Reserved.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

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
