#!/bin/sh
# Test redirecting from CGI
# $Id: redirect.sh,v 1.1 2003/09/16 04:03:32 gregwilkins Exp $
echo "Status: 302 Moved"
echo "Location: http://${HTTP_HOST}/"
echo
