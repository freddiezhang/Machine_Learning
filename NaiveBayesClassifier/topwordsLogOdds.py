import sys
import math
import operator

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
    global conN
    global libN
    conN = 0
    libN = 0
    for value in cons.values():
        conN += value
    for value in libs.values():
        libN += value
    liblogodds = {}
    conlogodds = {}
    for key,value in libs.iteritems():
        prob0 = (value + 1.0) / (libN + len(vocabulary))
        if cons.has_key(key):
            prob1 = (cons[key] + 1.0) / (conN + len(vocabulary))
        else:
            prob1 = 1.0 / (conN + len(vocabulary))
        liblogodds[key] = math.log(prob0 / prob1)
    for key,value in cons.iteritems():
        prob0 = (value + 1.0) / (conN + len(vocabulary))
        if libs.has_key(key):
            prob1 = (libs[key] + 1.0) / (libN + len(vocabulary))
        else:
            prob1 = 1.0 / (libN + len(vocabulary))
        conlogodds[key] = math.log(prob0 / prob1)
    sortedcon = sorted(conlogodds.items(),key = operator.itemgetter(1),reverse = True)
    sortedlib = sorted(liblogodds.items(),key = operator.itemgetter(1),reverse = True)
    i = 0
    for tuple in sortedlib:
        if i >= 20:
            break
        print "liberalword" + str(i+1)+": " + str("%.04f"%tuple[1])
#         print tuple[0]
        i +=1
    i = 0
    print
    for tuple in sortedcon:
        if i >= 20:
            break
        print "conservativeword" + str(i+1)+": " + str("%.04f"%tuple[1])
#         print tuple[0]
        i +=1
        
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
