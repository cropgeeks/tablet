#!/usr/bin/perl

use CGI;
use strict;

print "Content-type: text/html\n\n";

# Get CGI query variables
my $cgi_query = CGI->new();
my $id      = $cgi_query->param("id");  
my $version = $cgi_query->param("version");  
my $locale  = $cgi_query->param("locale");
my $os      = $cgi_query->param("os");
my $user    = $cgi_query->param("user");
my $ip_address = $ENV{'REMOTE_ADDR'};


if ($version ne "x.xx.xx.xx")
{

    my $date = `date`;
    chomp $date;

    open (LOG, ">>/var/www/html/tablet/logs/tablet.log");

    print LOG "$date\t$ip_address\t$id\t$version\t$locale\t$os\t$user\r\n";

    close LOG;
}
