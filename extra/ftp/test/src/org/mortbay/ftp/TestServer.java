// ========================================================================
// $Id: TestServer.java,v 1.3 2004/05/09 20:30:43 gregwilkins Exp $
// Copyright 1996-2004 Mort Bay Consulting Pty. Ltd.
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
// Copyright (c) 1996 Optimus Solutions Pty. Ltd. All rights reserved.

package org.mortbay.ftp;

import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mortbay.util.LineInput;
import org.mortbay.util.TestCase;


// ===========================================================================
class TestServer extends Thread
{
	private static Log log = LogFactory.getLog(TestServer.class);

    TestCase test;
    ServerSocket listen=null;
    int port = -1;
    Socket connection;
    LineInput in;
    Writer out;

    TestServer(TestCase t)
    {
        this.test=t;
        start();

        synchronized(this){
            while (port==-1)
                try{wait(1000);}catch(InterruptedException e){};
        }

    }

    public void run()
    {
        try{
            listen = new ServerSocket(0);
            synchronized(this){
                port = listen.getLocalPort();
                notifyAll();
            }
            log.debug("TestCase server listening");
            connection = listen.accept( );
            log.debug("TestCase server connected");
            in = new LineInput(connection.getInputStream());
            out = new OutputStreamWriter(connection.getOutputStream(),"ISO8859_1");
            out.write(CmdReply.codeServiceReady+" OK\n");
            out.flush();

            // Handle authentication
            String line = in.readLine();
            if(log.isDebugEnabled())log.debug("TestCase server got: "+line);
            test.checkEquals(line,"USER TestUser","Received USER");
            out.write(CmdReply.codeUsernameNeedsPassword+" Need password\n");
            out.flush();

            line = in.readLine();
            if(log.isDebugEnabled())log.debug("TestCase server got: "+line);
            test.checkEquals(line,"PASS TestPass","Received PASS");
            out.write(CmdReply.codeUserLoggedIn+" OK\n");
            out.flush();

            //Handler get file
            line = in.readLine();
            if(log.isDebugEnabled())log.debug("TestCase server got: "+line);
            test.check(line.startsWith("PORT"),"Received PORT");
            out.write(CmdReply.codeCommandOK+" OK\n");
            out.flush();

            int c = line.lastIndexOf(',');
            int dataPort = Integer.parseInt(line.substring(c+1));
            line = line.substring(0,c);
            dataPort += 256 *
                Integer.parseInt(line.substring(line.lastIndexOf(',')+1));
            Socket dataConnection = new Socket(InetAddress.getLocalHost(),
                                               dataPort);
            test.check(true,"DataPort Opened");

            line = in.readLine();
            if(log.isDebugEnabled())log.debug("TestCase server got: "+line);
            test.checkEquals(line,"RETR TestFileName","Received RETR");
            out.write(CmdReply.codeFileStatusOK+" Data port opened\n");
            out.flush();

            Writer dataOut = new
                OutputStreamWriter(dataConnection.getOutputStream(),"ISO8859_1");

            Thread.sleep(1000);
            dataOut.write("How Now Brown Cow\n");
            dataOut.flush();
            dataOut.close();
            dataConnection.close();
            out.write(CmdReply.codeClosingData+" File transfer complete\n");
            out.flush();

            //Handler put file
            line = in.readLine();
            if(log.isDebugEnabled())log.debug("TestCase server got: "+line);
            test.check(line.startsWith("PORT"),"Received PORT");
            out.write(CmdReply.codeCommandOK+" OK\n");
            out.flush();

            c = line.lastIndexOf(',');
            dataPort = Integer.parseInt(line.substring(c+1));
            line = line.substring(0,c);
            dataPort += 256 *
                Integer.parseInt(line.substring(line.lastIndexOf(',')+1));
            dataConnection = new Socket(InetAddress.getLocalHost(),
                                               dataPort);
            test.check(true,"DataPort Opened");

            line = in.readLine();
            if(log.isDebugEnabled())log.debug("TestCase server got: "+line);
            test.checkEquals(line,"STOR TestFileName","Received STOR");
            out.write(CmdReply.codeFileStatusOK+" Data port opened\n");
            out.flush();

            LineInput dataIn = new
                LineInput(dataConnection.getInputStream());
            String input = dataIn.readLine();
            test.checkEquals(input,"How Now Brown Cow","received file");
            input = dataIn.readLine();
            test.checkEquals(input,null,"received EOF");

            out.write(CmdReply.codeClosingData+" File transfer complete\n");
            out.flush();

            //Handler abort file
            line = in.readLine();
            if(log.isDebugEnabled())log.debug("TestCase server got: "+line);
            test.check(line.startsWith("PORT"),"Received PORT");
            out.write(CmdReply.codeCommandOK+" OK\n");
            out.flush();

            c = line.lastIndexOf(',');
            dataPort = Integer.parseInt(line.substring(c+1));
            line = line.substring(0,c);
            dataPort += 256 *
                Integer.parseInt(line.substring(line.lastIndexOf(',')+1));
            dataConnection = new Socket(InetAddress.getLocalHost(),
                                               dataPort);
            test.check(true,"DataPort Opened");

            line = in.readLine();
            if(log.isDebugEnabled())log.debug("TestCase server got: "+line);
            test.checkEquals(line,"RETR TestFileName","Received RETR");
            out.write(CmdReply.codeFileStatusOK+" Data port opened\n");
            out.flush();

            dataOut = new OutputStreamWriter(dataConnection.getOutputStream(),"ISO8859_1");
            dataOut.write("How Now Brown Cow\n");
            dataOut.flush();
            line = in.readLine();
            if(log.isDebugEnabled())log.debug("TestCase server got: "+line);
            test.check(line.startsWith("ABOR"),"Received ABOR");

            dataOut.close();
            dataConnection.close();
            out.write(CmdReply.codeClosingData+" File transfer aborted\n");
            out.flush();

            line = in.readLine();
            out.write(CmdReply.codeCommandOK+" OK\n");
            out.flush();
            if(log.isDebugEnabled())log.debug("TestCase server got: "+line);
            test.checkEquals("TYPE I",line,"Received TYPE I");

            line = in.readLine();
            out.write(CmdReply.codeCommandOK+" OK\n");
            out.flush();
            if(log.isDebugEnabled())log.debug("TestCase server got: "+line);
            test.checkEquals("TYPE L 8",line,"Received TYPE L 8");

            line = in.readLine();
            out.write(CmdReply.codeCommandOK+" OK\n");
            out.flush();
            if(log.isDebugEnabled())log.debug("TestCase server got: "+line);
            test.checkEquals("TYPE A C",line,"Received TYPE A C");

            log.debug("Tests completed");
        }
        catch (Exception e){
            test.check(false,"Server failed: "+e);
            TestCase.report();
            System.exit(1);
        }
    }
}

