#!/usr/bin/perl

use CGI;
use strict;

print "Content-type: text/html\n\n";

# Get CGI query variables
my $rating = 0;

my $cgi_query = CGI->new();
my $id      = $cgi_query->param("id");
my $version = $cgi_query->param("version");
my $locale  = $cgi_query->param("locale");
my $os      = $cgi_query->param("os");
my $user    = $cgi_query->param("user");
my $ip      = $ENV{'REMOTE_ADDR'};

if(defined($cgi_query->param("rating")))
{
  $rating = $cgi_query->param("rating");
}

if ($version ne "x.xx.xx.xx")
{

    my $date = `date`;
    chomp $date;

    open (LOG, ">>/var/www/html/tablet/logs/tablet.log");

    print LOG "$date\t$ip\t$id\t$version\t$locale\t$rating\t$os\t$user\r\n";

    close LOG;

    `/usr/local/java/bin/java -Djava.ext.dirs=/var/www/html/software_common/lib scri.commons.utils.AppTracker -appname=tablet -live="$date\t$ip\t$id\t$version\t$locale\t$rating\t$os\t$user"`;

}
