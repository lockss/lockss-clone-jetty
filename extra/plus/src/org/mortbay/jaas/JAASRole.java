// ========================================================================
// $Id: JAASRole.java,v 1.2 2004/05/09 20:31:13 gregwilkins Exp $
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

package org.mortbay.jaas;

import java.security.Principal;


public class JAASRole extends JAASPrincipal
{
    
    public JAASRole(String name)
    {
        super (name);
    }

    public boolean equals (Object o)
    {
        if (! (o instanceof Principal))
            return false;

        return getName().equals(((Principal)o).getName());
    }
}
