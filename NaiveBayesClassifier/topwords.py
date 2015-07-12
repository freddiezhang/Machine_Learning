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
    sortedcon = sorted(cons.items(),key = operator.itemgetter(1),reverse = True)
    sortedlib = sorted(libs.items(),key = operator.itemgetter(1),reverse = True)
    i = 0
    for tuple in sortedlib:
        if i >= 20:
            break
        prob = (tuple[1] + 1.0) / (libN + len(vocabulary))
        print "liberalword" + str(i+1)+": " + str("%.04f"%prob)
#         print tuple[0]
        i +=1
    i = 0
    print
    for tuple in sortedcon:
        if i >= 20:
            break
        prob = (tuple[1] + 1.0) / (conN + len(vocabulary))
        print "conservativeword" + str(i+1)+": " + str("%.04f"%prob)
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
