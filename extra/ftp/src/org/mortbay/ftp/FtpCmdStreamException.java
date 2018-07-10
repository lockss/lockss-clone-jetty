// ========================================================================
// $Id: FtpCmdStreamException.java,v 1.2 2004/05/09 20:30:35 gregwilkins Exp $
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

public class FtpCmdStreamException extends FtpException
{
    /* ------------------------------------------------------------------ */
    public String input=null;
    
    
    /* ------------------------------------------------------------------ */
    FtpCmdStreamException()
    {
        super("Unexpected close of FTP command channel");
    }
    
    /* ------------------------------------------------------------------ */
    FtpCmdStreamException(String message, String input)
    {
        super(message);
        this.input=input;
    }
}
