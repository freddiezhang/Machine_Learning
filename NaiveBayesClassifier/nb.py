import sys
import math

def train():
    argv = sys.argv
    global cons
    global libs
    global vocabulary
    trainlist = readfiles(argv[1])
    trainfiles = []
    for f in trainlist:
        trainfiles.append(f.strip())
    examples = len(trainfiles)
    numcon = 0.0
    numlib = 0.0
    for f in trainfiles:
        file = open(f,'r')
        if("con" in f):
            numcon += 1
            try:
                conVoc = file.readlines()
            finally:
                file.close()
            for word in conVoc:
                nword = word.strip().lower()
                if(cons.has_key(nword)):
                    cons[nword] += 1
                else:
                    cons[nword] = 1
                    vocabulary.add(nword)
        else:
            numlib += 1
            try:
                libVoc = file.readlines()
            finally:
                file.close()
            for word in libVoc:
                nword = word.strip().lower()
                if(libs.has_key(nword)):
                    libs[nword] += 1  
                else:
                    libs[nword] = 1
                    vocabulary.add(nword)
    global priorcon
    global priorlib
    priorcon = numcon / examples
    priorlib = numlib / examples
    global conN
    global libN
    conN = 0
    libN = 0
    for value in cons.values():
        conN += value
    for value in libs.values():
        libN += value
def test():
    argv = sys.argv
    testlist = readfiles(argv[2])
    testfiles = []
    for f in testlist:
        testfiles.append(f.strip())
    totalnum = len(testfiles)
    numError = 0
    for f in testfiles:
        if('con' in f):
            result = getresult(f)
            if result == True:
                print 'C'
            else:
                print 'L'
                numError +=1
        else:
            result = getresult(f)
            if result == True:
                print 'C'
                numError +=1
            else:
                print 'L'      
    print "Accuracy: %.4f" % (1- numError * 1.0 / totalnum)
    
def getresult(f):
    global cons
    global libs
    global vocabulary
    global priorcon
    global priorlib
    global conN
    global libN
    file = open(f,'r')
    try:
        wordlist = file.readlines()
    finally:
        file.close()
    conProb = math.log(priorcon)
    libProb = math.log(priorlib)

    for f in wordlist:
        word = f.strip().lower()
        if word in vocabulary:
            if cons.has_key(word):
                conProb += math.log( (cons[word] + 1.0) / (len(vocabulary)+ conN))
            else:
                conProb += math.log( 1.0 / (len(vocabulary)+ conN))
    for f in wordlist:
        word = f.strip().lower()
        if word in vocabulary:
            if libs.has_key(word):
                libProb += math.log( (libs[word] + 1.0) / (len(vocabulary)+ libN))
            else:
                libProb += math.log( 1.0 / (len(vocabulary)+ libN))
    
    return conProb > libProb
def readfiles(str):
    filelist = open(str,'r')
    try:
        files = filelist.readlines()
    finally:
        filelist.close()
    return files
cons = {}
libs = {}
vocabulary = set()
priorcon = 0.0;
priorlib = 0.0;
train()
test()

