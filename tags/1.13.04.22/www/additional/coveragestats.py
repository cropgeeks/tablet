#!/usr/bin/env python

###Imports / Variable Initilization##########################################################################################
import os, copy, sys
from optparse import *
commandLine = copy.deepcopy(sys.argv)
cwd = os.getcwd()
cntgMatchRanges = None
progName, progVersion, progArgNum = ('coveragestats.py','1.12', 1)
progUsage = 'python %s [options] <.txt file>' % progName
printLog = []
progDescription = '''
Calculates statistics for each contig in a Tablet output file and outputs them to a tab-delimitated
'.txt' file following the name of their respective contig. The statistics calculated are: number of coverage values (=contig length),
average depth, percent of bases with a depth greater than zero (percent coverage), and maximum  depth. coveragestats.py
requires that contig names have at least one non-numerical character. Created by: Zachary S. L. Foster, Oregon State University''' 
#############################################################################################################################

def errorExit(message=None,exitStatus=1):
    '''Version 1.0
    Is called when the script encounters a fatal error. Prints the debug log to standard out (usually the screen)'''
    
    if message:
        print '%s: Error: %s' % (progName, message)
    else:
        print progName +': Unknown error, printing debug log...'
        for line in debugLog: print line   #print debug log
    sys.exit(exitStatus)

###Command Line Parser#######################################################################################################
cmndLineParser  = OptionParser(usage=progUsage, version="Version %s" % progVersion, description  = progDescription)
#cmndLineParser.add_option("-v", "--verbose",            action="store_true",    default=False,     help="Print progress updates and relevant statistics to the standard output. (Default: run silently)")
#cmndLineParser.add_option("-r", "--save-run-info",      action="store_true",    default=False,     help="Save a txt file of anything printed to the screen. Requires the use of -v/--verbose. (Default: Do not save)")
(options, args) = cmndLineParser.parse_args(commandLine)
if len(args) == 1:
    cmndLineParser.print_help()
    sys.exit(0)
argNum = len(args) - 1   #counts the amount of arguments, negating the 'qualtofa' at the start of the command line
if argNum != progArgNum: errorExit('%s takes exactly %d argument(s); %d supplied' % (progName, progArgNum, argNum), 0)
inPath = args[-1]   #The file path to the input file supplied by the user
#############################################################################################################################

def verbose(toPrint):
    if options.verbose:
        print toPrint,
        printLog.append(toPrint)

def intable(str):
    try:
        int(str)
        return True
    except ValueError: return False

def mkRange(numList):
    '''Function designed to make a list of ranges (e.g. 2-5, 8-10) out of a list of integers (e.g. 2,3,4,5,8,9,10)'''
    outList = []
    if len(numList) > 0:
        start   = numList[0]
        current = numList[0]
        numList.append(numList[-1])
        for num in numList[1:]:
            if (num - 1) != current:
                outList.append([start,current])
                start = num
            current = num
    return outList


#############################################################################################################################
def average(ints):
    return float(sum(ints))/float(len(ints))

def percentCov(ints):
    return float(len(ints) - ints.count(0))/float(len(ints)) * 100

funcList =      [len,   average, percentCov, max]
templateStr =   ['%d',  '%.2f',  '%.2f',     '%d']
schema = ['Name','Contig Length','Average Depth','Percent Coverage','Max Depth']
inHandle = open(inPath, 'r')
outHandle = open(inPath + '_stats.txt', 'w')
header = inHandle.readline().strip()
coverages = []
outHandle.write('\t'.join(schema) + '\n')
while header != '':
    line = inHandle.readline()
    lineParts = line.strip().split(' ')
    if map(intable,lineParts).count(True) == len(lineParts):  #if all text seperated by spaces can be converted to an interger; aka is a list of coverage depths
        coverages += map(int,lineParts)
        continue
    else:
        stats = '\t'.join(templateStr) % tuple([func(coverages) for func in funcList])
        outHandle.write('%s\t%s\n' % (header,stats))
        header = line.strip()
        del coverages
        coverages = []
    del line, lineParts
inHandle.close()
outHandle.close()
###############################################################################################################################
