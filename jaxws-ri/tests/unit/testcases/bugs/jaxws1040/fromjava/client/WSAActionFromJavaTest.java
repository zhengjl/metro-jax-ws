/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2012 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * http://glassfish.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
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

package bugs.jaxws1040.fromjava.client;

import com.sun.xml.ws.developer.MemberSubmissionAddressingFeature;
import junit.framework.TestCase;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.HandlerResolver;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.PortInfo;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.util.*;

/**
 * Simple WS just to verify MemberSubmissionAddressing configurationTODO: Write some description here ...
 *
 * @author Miroslav Kos (miroslav.kos at oracle.com)
 */
public class WSAActionFromJavaTest extends TestCase {

    public void test() {
        WSAActionFromJavaService service = new WSAActionFromJavaService();
        setupCheckingHanlder(service);

        WSAActionFromJava port = service.getWSAActionFromJavaPort(new MemberSubmissionAddressingFeature(true));
        port.echo("test");
    }

    private void setupCheckingHanlder(WSAActionFromJavaService service) {
        service.setHandlerResolver(new HandlerResolver() {
            @Override
            @SuppressWarnings("unchecked")
            public List<Handler> getHandlerChain(PortInfo portInfo) {
                List<Handler> list = new ArrayList<Handler>();
                list.add(new CheckingHandler());
                return list;
            }
        });
    }

    private static class CheckingHandler implements SOAPHandler<SOAPMessageContext> {
        @Override
        public boolean handleMessage(SOAPMessageContext ctx) {
            QName qname = new QName("http://schemas.xmlsoap.org/ws/2004/08/addressing", "Action");
            try {
                Iterator elems = ctx.getMessage().getSOAPPart().getEnvelope().getHeader().getChildElements(qname);
                if (!elems.hasNext()) {
                    throw new RuntimeException("Required addressing header not found!");
                }
            } catch (SOAPException e) {
                e.printStackTrace();
                throw new RuntimeException("Required addressing header not found!");
            }
            return true;
        }

        @Override
        public Set<QName> getHeaders() {
            return null;
        }

        @Override
        public boolean handleFault(SOAPMessageContext context) {
            return false;
        }

        @Override
        public void close(MessageContext context) {
        }
    }
}