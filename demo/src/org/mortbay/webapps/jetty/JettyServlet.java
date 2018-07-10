// ===========================================================================
// Copyright (c) 1996 Mort Bay Consulting Pty. Ltd. All rights reserved.
// $Id: JettyServlet.java,v 1.7 2003/10/05 23:46:19 gregwilkins Exp $
// ---------------------------------------------------------------------------

package org.mortbay.webapps.jetty;
import java.io.IOException;
import java.io.Writer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mortbay.html.Include;
import org.mortbay.util.IO;
import org.mortbay.util.LogSupport;
import org.mortbay.util.Resource;

/* ------------------------------------------------------------ */
/** Jetty Demo site servlet.
 *
 * @version $Id: JettyServlet.java,v 1.7 2003/10/05 23:46:19 gregwilkins Exp $
 * @author Greg Wilkins (gregw)
 */
public class JettyServlet extends HttpServlet
{
    private static Log log = LogFactory.getLog(JettyServlet.class);

    public static long __minModTime = System.currentTimeMillis();
    
    /* ------------------------------------------------------------ */
    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) 
        throws ServletException, IOException
    {	
        String path=(String)request.getAttribute("javax.servlet.include.servlet_path");
        if (path==null)
            path=request.getServletPath();
        
        Resource resource=
                Resource.newResource(getServletContext().getResource(path));
        
        if (resource==null || !resource.exists())
        {
            response.sendError(404);
            return;
        }
        
        if(log.isDebugEnabled())log.debug("Resource="+resource);

        JettyPage page = new JettyPage(request.getContextPath(),path);
        if (page.getSection()!=null)
        {
            response.setContentType("text/html");
            page.add(new Include(resource.getInputStream()));
            Writer out=response.getWriter();
            page.write(out);
        }
        else
        {
            String type=getServletContext().getMimeType(resource.getName());
            if (type!=null)
                response.setContentType(type);
            if(resource.length()>0)
                response.setContentLength((int)resource.length());
            IO.copy(resource.getInputStream(),
                    response.getOutputStream());
        }
    }

    /* ------------------------------------------------------------ */
    public long getLastModified(HttpServletRequest request)
    {
        long lm=-1;
        try{
            String path=request.getServletPath();            
            Resource resource=
                Resource.newResource(getServletContext().getResource(path));

            request.setAttribute("JettyResource",resource);
            
            if (resource!=null && resource.exists())
            {
                lm=resource.lastModified();
                if (lm<__minModTime)
                    lm=__minModTime;
            }
        }
        catch(Exception e)
        {
            LogSupport.ignore(log,e);
        }
        return lm;
    }   
}
