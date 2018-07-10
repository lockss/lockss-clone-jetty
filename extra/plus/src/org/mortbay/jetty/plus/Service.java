// ========================================================================
// $Id: Service.java,v 1.2 2004/05/09 20:31:18 gregwilkins Exp $
// Copyright 2003-2004 Mort Bay Consulting Pty. Ltd.
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

import org.mortbay.util.LifeCycle;


public interface Service extends LifeCycle
{
   
    public void setJNDI (String registration);

    public String getJNDI ();


    public void setName (String name);
    

    public String getName ();
        
    /* ------------------------------------------------------------ */
    /** Start the LifeCycle.
     * @exception Exception An arbitrary exception may be thrown.
     */
    public void start()
        throws Exception;  
    
    /* ------------------------------------------------------------ */
    /** Stop the LifeCycle.
     * The LifeCycle may wait for current activities to complete
     * normally, but it can be interrupted.
     * @exception InterruptedException Stopping a lifecycle is rarely atomic
     * and may be interrupted by another thread.  If this happens
     * InterruptedException is throw and the component will be in an
     * indeterminant state and should probably be discarded.
     */
    public void stop()
        throws InterruptedException;
   
    /* ------------------------------------------------------------ */
    /** 
     * @return True if the LifeCycle has been started. 
     */
    public boolean isStarted();    
}
