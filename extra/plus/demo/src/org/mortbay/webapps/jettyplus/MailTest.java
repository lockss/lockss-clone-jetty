// ========================================================================
// $Id: MailTest.java,v 1.5 2004/05/09 20:31:12 gregwilkins Exp $
// Copyright 1999-2004 Mort Bay Consulting Pty. Ltd.
// ------------------------------------------------------------------------
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at 
// http://www.apache.org/licenses/LICENSE-2.0
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
// ========================================================================

package org.mortbay.webapps.jettyplus;

import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mortbay.util.LogSupport;

/**
 * MailTest.java
 *
 *
 * Created: Fri May 30 23:29:50 2003
 *
 * @author <a href="mailto:janb@wafer">Jan Bartel</a>
 * @version 1.0
 */
public class MailTest extends HttpServlet
{
    private static Log log = LogFactory.getLog(MailTest.class);

    public static final String DATE_FORMAT = "EEE, d MMM yy HH:mm:ss Z";
    SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);


    public void doGet (HttpServletRequest request,
                       HttpServletResponse response) 
        throws ServletException, IOException
    {
        response.setContentType ("text/html");
        Writer writer = response.getWriter();
        writer.write ("<HTML><TITLE>Mail Sending Test</TITLE>");
        
        try
        {
            InitialContext ctx = new InitialContext();

            Session session = (Session)ctx.lookup ("java:comp/env/mail/Session");
            
            // create a message
            Message msg = new MimeMessage(session);
            
            String sender = request.getParameter("sender");
            String recipient = request.getParameter("recipient");

            if (sender == null)
                throw new ServletException ("No sender configured");
            if (sender.trim().equals(""))
                throw new ServletException ("No sender configured");

            if (recipient == null)
                throw new ServletException ("No recipient configured");
            if (recipient.trim().equals(""))
                throw new ServletException ("No recipient configured");
                

            log.info("Sender="+sender);
            log.info("Recipient="+recipient);

            // set the from and to address
            InternetAddress addressFrom = new InternetAddress(sender);
            msg.setFrom(addressFrom);
            
           
            msg.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
            msg.setSubject("JettyPlus Mail Test Succeeded");
            msg.setContent("The test of the JettyPlus Mail Service @ "+new Date()+" has been successful.", "text/plain");
            msg.addHeader ("Date", dateFormat.format(new Date()));
            Transport.send(msg);

            writer.write ("Congratulations, your test of the JettyPlus Mail Service succeeded. Your recipient should now have mail");
        }
        catch (Throwable e)
        {
            log.warn(LogSupport.EXCEPTION,e);
            writer.write ("<font color=red>Test failed: "+e+"</font>");
        }

        writer.write ("</BODY></HTML>");
    }
    
} // MailTest
