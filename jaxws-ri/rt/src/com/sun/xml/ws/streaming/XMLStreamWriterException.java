/*
 * $Id: XMLStreamWriterException.java,v 1.1 2005-05-28 01:03:09 spericas Exp $
 *
 * Copyright (c) 2005 Sun Microsystems, Inc.
 * All rights reserved.
 */

package com.sun.xml.ws.streaming;

import com.sun.xml.ws.util.exception.JAXWSExceptionBase;
import com.sun.xml.ws.util.localization.Localizable;

/**
 * <p> XMLWriterException represents an exception that occurred while writing
 * an XML document. </p>
 * 
 * @see XMLWriter
 * @see com.sun.xml.rpc.util.exception.JAXWSExceptionBase
 * 
 * @author JAX-RPC Development Team
 */
public class XMLStreamWriterException extends JAXWSExceptionBase {

    public XMLStreamWriterException(String key) {
        super(key);
    }

    public XMLStreamWriterException(String key, String arg) {
        super(key, arg);
    }

    public XMLStreamWriterException(String key, Object[] args) {
        super(key, args);
    }

    public XMLStreamWriterException(String key, Localizable arg) {
        super(key, arg);
    }

    public XMLStreamWriterException(Localizable arg) {
        super("xmlwriter.nestedError", arg);
    }

    public String getResourceBundleName() {
        return "com.sun.xml.ws.resources.streaming";
    }
}
