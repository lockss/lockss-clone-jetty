// ========================================================================
// $Id: DebugInterceptor.java,v 1.4 2004/05/09 20:30:47 gregwilkins Exp $
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

package org.mortbay.j2ee.session;

//----------------------------------------

import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.Map;

import org.jboss.logging.Logger;

//----------------------------------------
/**
 *
 * @author <a href="mailto:jules@mortbay.com">Jules Gosnell</a>
 * @version 1.0
 */
public class
  DebugInterceptor
  extends StateInterceptor
{
  protected static final Logger _log=Logger.getLogger(DebugInterceptor.class);

  // invariant field accessors
  public String
    getId()
    throws RemoteException
    {
      _log.info("getId() -> ()");
      String tmp=super.getId();
      _log.info("getId() <- "+tmp);
      return tmp;
    }

  public int
    getActualMaxInactiveInterval()
    throws RemoteException
    {
      _log.info("getActualMaxInactiveInterval() -> ()");
      int tmp=super.getActualMaxInactiveInterval();
      _log.info("getActualMaxInactiveInterval() <- "+tmp);
      return tmp;
    }

  public long
    getCreationTime()
    throws RemoteException
    {
      _log.info("getCreationTime() -> ()");
      long tmp=super.getCreationTime();
      _log.info("getCreationTime() <- "+tmp);
      return tmp;
    }


  // variant field accessors
  public Map
    getAttributes()
    throws RemoteException
    {
      _log.info("getAttributes() -> ()");
      Map tmp=super.getAttributes();
      _log.info("getAttributes() <- "+tmp);
      return tmp;
    }

  public void
    setAttributes(Map attributes)
    throws RemoteException
    {
      _log.info("setAttributes() -> ("+attributes+")");
      super.setAttributes(attributes);
      _log.info("setAttributes() <- ");
    }

  public long
    getLastAccessedTime()
    throws RemoteException
    {
      _log.info("getLastAccessedTime() -> ()");
      long tmp=super.getLastAccessedTime();
      _log.info("getLastAccessedTime() <- "+tmp);
      return tmp;
    }

  public void
    setLastAccessedTime(long time)
    throws RemoteException
    {
      _log.info("setLastAccessedTime() -> ("+time+")");
      super.setLastAccessedTime(time);
      _log.info("setLastAccessedTime() <- ");
    }

  public int
    getMaxInactiveInterval()
    throws RemoteException
    {
      _log.info("getMaxInactiveInterval() -> ()");
      int tmp=super.getMaxInactiveInterval();
      _log.info("getMaxInactiveInterval() <- "+tmp);
      return tmp;
    }

  public void
    setMaxInactiveInterval(int interval)
    throws RemoteException
    {
      _log.info("setMaxInactiveInterval() -> ("+interval+")");
      super.setMaxInactiveInterval(interval);
      _log.info("setMaxInactiveInterval() <- ");
    }


  // compound fn-ality
  public Object
    getAttribute(String name)
    throws RemoteException
    {
      _log.info("getAttribute() -> ("+name+")");
      Object tmp=super.getAttribute(name);
      _log.info("getAttribute() <- "+tmp);
      return tmp;
    }

  public Object
    setAttribute(String name, Object value, boolean returnValue)
    throws RemoteException
    {
      _log.info("setAttribute() -> ("+name+","+value+","+returnValue+")");
      Object tmp=super.setAttribute(name,value,returnValue);
      _log.info("setAttribute() <- "+tmp);
      return tmp;
    }

  public Object
    removeAttribute(String name, boolean returnValue)
    throws RemoteException
    {
      _log.info("removeAttribute() -> ("+name+","+returnValue+")");
      Object tmp=super.removeAttribute(name,returnValue);
      _log.info("removeAttribute() <- "+tmp);
      return tmp;
    }

  public Enumeration
    getAttributeNameEnumeration()
    throws RemoteException
    {
      _log.info("getAttributeNameEnumeration() -> ()");
      Enumeration tmp=super.getAttributeNameEnumeration();
      _log.info("getAttributeNameEnumeration() <- "+tmp);
      return tmp;
    }

  public String[]
    getAttributeNameStringArray()
    throws RemoteException
    {
      _log.info("getAttributeNameStringArray() -> ()");
      String[] tmp=super.getAttributeNameStringArray();
      _log.info("getAttributeNameStringArray() <- "+tmp);
      return tmp;
    }

  public boolean
    isValid()
    throws RemoteException
    {
      _log.info("isValid() -> ()");
      boolean tmp=super.isValid();
      _log.info("isValid() <- "+tmp);
      return tmp;
    }

  //  public Object clone() { return this; } // Stateless
}
