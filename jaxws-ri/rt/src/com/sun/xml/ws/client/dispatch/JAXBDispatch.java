/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 * 
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License. You can obtain
 * a copy of the License at https://glassfish.dev.java.net/public/CDDL+GPL.html
 * or glassfish/bootstrap/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 * 
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/bootstrap/legal/LICENSE.txt.
 * Sun designates this particular file as subject to the "Classpath" exception
 * as provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code.  If applicable, add the following below the License
 * Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information: "Portions Copyrighted [year]
 * [name of copyright owner]"
 * 
 * Contributor(s):
 * 
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

package com.sun.xml.ws.client.dispatch;

import com.sun.xml.bind.api.JAXBRIContext;
import com.sun.xml.ws.api.addressing.WSEndpointReference;
import com.sun.xml.ws.api.message.Header;
import com.sun.xml.ws.api.message.Headers;
import com.sun.xml.ws.api.message.Message;
import com.sun.xml.ws.api.message.Messages;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.pipe.Tube;
import com.sun.xml.ws.binding.BindingImpl;
import com.sun.xml.ws.client.WSServiceDelegate;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceException;

/**
 * The <code>JAXBDispatch</code> class provides support
 * for the dynamic invocation of a service endpoint operation using
 * JAXB objects. The <code>javax.xml.ws.Service</code>
 * interface acts as a factory for the creation of <code>JAXBDispatch</code>
 * instances.
 *
 * @author WS Development Team
 * @version 1.0
 */
public class JAXBDispatch extends DispatchImpl<Object> {

    private final JAXBContext jaxbcontext;

    public JAXBDispatch(QName port, JAXBContext jc, Service.Mode mode, WSServiceDelegate service, Tube pipe, BindingImpl binding, WSEndpointReference epr) {
        super(port, mode, service, pipe, binding, epr);
        this.jaxbcontext = jc;
    }


    Object toReturnValue(Packet response) {
        try {
            Unmarshaller unmarshaller = jaxbcontext.createUnmarshaller();
            Message msg = response.getMessage();
            switch (mode) {
                case PAYLOAD:
                    return msg.<Object>readPayloadAsJAXB(unmarshaller);
                case MESSAGE:
                    Source result = msg.readEnvelopeAsSource();
                    return unmarshaller.unmarshal(result);
                default:
                    throw new WebServiceException("Unrecognized dispatch mode");
            }
        } catch (JAXBException e) {
            throw new WebServiceException(e);
        }
    }


    Packet createPacket(Object msg) {
        assert jaxbcontext != null;

        try {
            Marshaller marshaller = jaxbcontext.createMarshaller();
            marshaller.setProperty("jaxb.fragment", Boolean.TRUE);

            Message message = (msg == null) ? Messages.createEmpty(soapVersion): Messages.create(marshaller, msg, soapVersion);
            return new Packet(message);
        } catch (JAXBException e) {
            throw new WebServiceException(e);
        }
    }

    public void setOutboundHeaders(Object... headers) {
        if(headers==null)
            throw new IllegalArgumentException();
        Header[] hl = new Header[headers.length];
        for( int i=0; i<hl.length; i++ ) {
            if(headers[i]==null)
                throw new IllegalArgumentException();
            // TODO: handle any JAXBContext.
            hl[i] = Headers.create((JAXBRIContext)jaxbcontext,headers[i]);
        }
        super.setOutboundHeaders(hl);
    }
}