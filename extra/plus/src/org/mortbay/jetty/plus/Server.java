// ========================================================================
// $Id: Server.java,v 1.10 2004/10/01 14:28:30 gregwilkins Exp $
// Copyright 2002-2004 Mort Bay Consulting Pty. Ltd.
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

package org.mortbay.jetty.plus;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mortbay.jetty.servlet.WebApplicationContext;
import org.mortbay.util.LogSupport;
import org.mortbay.util.MultiException;
import org.mortbay.util.Resource;

/* ------------------------------------------------------------ */
/** The Jetty HttpServer.
 *
 * This specialization of org.mortbay.jetty.Server adds knowledge
 * about JNDI and Transaction Management
 * 
 * @author Miro Halas
 */
public class Server extends org.mortbay.jetty.Server 
{
    private static final String[] _configClassNames = new String[] {"org.mortbay.jetty.plus.PlusWebAppContext$Configuration", "org.mortbay.jetty.servlet.JettyWebConfiguration"};
    static Log log = LogFactory.getLog(Server.class);

    private  ArrayList _serviceList;
    



    /* ------------------------------------------------------------ */
    /** Constructor. 
     */
    public Server ()
    {
       // Don't reset the instance to null since Jetty classes using XML are
       // inialized out of order and it would reset the value to null.
        setWebApplicationConfigurationClassNames(_configClassNames);
    }
    
    /* ------------------------------------------------------------ */
    /** Constructor. 
     * @param configuration The filename or URL of the XML
     * configuration file.
     */
    public Server (
       String configuration
    ) throws IOException
    {
        super(configuration);
        setWebApplicationConfigurationClassNames(_configClassNames);
    }
    
    /* ------------------------------------------------------------ */
    /** Constructor. 
     * @param configuration The filename or URL of the XML
     * configuration file.
     */
    public Server(
       Resource configuration
    ) throws IOException
    {
        super(configuration);
        setWebApplicationConfigurationClassNames(_configClassNames);
    }
    
    /* ------------------------------------------------------------ */
    /** Constructor. 
     * @param configuration The filename or URL of the XML
     * configuration file.
     */
    public Server(
       URL configuration
    ) throws IOException
    {
        super(configuration);
        setWebApplicationConfigurationClassNames(_configClassNames);
    }



    /* ------------------------------------------------------------ */
    /**
     * Add a Service to a Server. Examples are transaction service,
     * mail service etc
     *
     * @param service eg TMService, MailService
     */
    public void addService (Service service)
    { 
        if (_serviceList == null)
            _serviceList = new ArrayList(5);

        _serviceList.add (service);
        addComponent(service);
        if(log.isDebugEnabled())log.debug("Service List contains: "+_serviceList.size()+" services");
    }



    /* ------------------------------------------------------------ */
    /** Start all handlers then listeners.
     * If a subcomponent fails to start, it's exception is added to a
     * org.mortbay.util.MultiException and the start method continues.
     * @exception MultiException A collection of exceptions thrown by
     * start() method of subcomponents of the HttpServer. 
     */
    protected synchronized void doStart()
        throws Exception
    {

       //iterate over all the services and start them in order
       if (_serviceList != null)
       {
         MultiException mex = new MultiException();
         Iterator itor = _serviceList.iterator();
         while (itor.hasNext())
         {
             try
             {
                 ((Service)itor.next()).start();
             }
             catch (Exception e)
             {
                 mex.add(e);
             }
         }
         mex.ifExceptionThrowMulti();
       }



       // Now start the rest of Jetty
       super.doStart();
    }

    /* ------------------------------------------------------------ */
    /** Stop all listeners then all contexts.
     * @param graceful If true and statistics are on for a context,
     * then this method will wait for requestsActive to go to zero
     * before stopping that context.
     */
    protected synchronized void doStop()
        throws InterruptedException
    {
       // First stop rest of jetty 
       super.doStop();

       // now stop all the services, in the reverse order to starting
       if (_serviceList!=null && _serviceList.size()>0)
       {
           ListIterator listItor = _serviceList.listIterator (_serviceList.size());
           while (listItor.hasPrevious())
           {
               Service s = (Service)listItor.previous();
               if (s!=null)
                   s.stop();
           }
       }
    }

    /* ------------------------------------------------------------ */
    /* ------------------------------------------------------------ */
    /* ------------------------------------------------------------ */
    public static void main(String[] arg)
    {
        String[] dftConfig={"etc/jetty.xml"};
        
        if (arg.length==0)
        {
            log.info("Using default configuration: etc/jetty.xml");
            arg=dftConfig;
        }

        final org.mortbay.jetty.plus.Server[] servers= new org.mortbay.jetty.plus.Server[arg.length];

        // create and start the servers.
        for (int i=0;i<arg.length;i++)
        {
            try
            {
                servers[i] = new org.mortbay.jetty.plus.Server (arg[i]);
                servers[i].start();

            }
            catch(Exception e)
            {
                log.warn(LogSupport.EXCEPTION,e);
            }
        }

        // Create and add a shutdown hook
        if (!Boolean.getBoolean("JETTY_NO_SHUTDOWN_HOOK"))
        {
            try
            {
                Method shutdownHook=
                    java.lang.Runtime.class
                    .getMethod("addShutdownHook",new Class[] {java.lang.Thread.class});
                Thread hook = 
                    new Thread() {
                            public void run()
                            {
                                setName("Shutdown");
                                log.info("Shutdown hook executing");
                                for (int i=0;i<servers.length;i++)
                                {
				    if (servers[i]==null) continue;
                                    try{servers[i].stop();}
                                    catch(Exception e){log.warn(LogSupport.EXCEPTION,e);}
                                }
                                
                                // Try to avoid JVM crash
                                try{Thread.sleep(1000);}
                                catch(Exception e){log.warn(LogSupport.EXCEPTION,e);}
                            }
                        };
                shutdownHook.invoke(Runtime.getRuntime(),
                                    new Object[]{hook});
            }
            catch(Exception e)
            {
                if(log.isDebugEnabled())log.debug("No shutdown hook in JVM ",e);
            }
        }

        // create and start the servers.
        for (int i=0;i<arg.length;i++)
        {
            try{servers[i].join();}
            catch (Exception e){LogSupport.ignore(log,e);}
        }
    }

    /* ------------------------------------------------------------ */
    /** Create a new WebApplicationContext.
     * Ths method is called by Server to creat new contexts for web 
     * applications.  Thus calls to addWebApplication that result in 
     * a new Context being created will return an correct class instance.
     * Derived class can override this method to create instance of its
     * own class derived from WebApplicationContext in case it needs more
     * functionality.
     * @param webApp The Web application directory or WAR file.
     * @return WebApplicationContext
     */
    protected WebApplicationContext newWebApplicationContext(
       String webApp
    )
    {
        return new PlusWebAppContext(webApp);
    }
}
