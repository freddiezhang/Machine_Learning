import sys
import math
from logsum import log_sum

def main(argv):
    devlines = readfiles(argv[1])
    translines = readfiles(argv[2])
    emitlines = readfiles(argv[3])
    priorlines = readfiles(argv[4])
    aij = {}
    for line in translines:
        words = line.strip().split()
        probs = {}
        for wd in words[1:]:
            prob = wd.split(':')
            probs[prob[0]] = math.log(float(prob[1]))
        aij[words[0]] = probs
    bjk = {}
    for line in emitlines:
        words = line.strip().split()
        probs = {}
        for wd in words[1:]:
            prob = wd.split(':')
            probs[prob[0]] = math.log(float(prob[1]))
        bjk[words[0]] = probs
    pi = {}
    for line in priorlines:
        words = line.strip().split()
        pi[words[0]] = math.log(float(words[1]))
    states = bjk.keys()
    for line in devlines:
        alpha = []
        content = line.strip().split()
        alphanext = {}
        for st in states:
            alphanext[st] = pi[st] + bjk[st][content[0]]
        alpha.append(alphanext)
        index = 0
        for word in content[1:]:
            index += 1
            alphanew = {}
            for st in states:
                alphanew[st] = 0.0
                for oldst in states:
                    if alphanew[st] == 0.0:
                        alphanew[st] = alpha[-1][oldst] + aij[oldst][st]
                    else:
                        alphanew[st] = log_sum(alphanew[st], alpha[-1][oldst] + aij[oldst][st])
                alphanew[st] += bjk[st][content[index]]
            alpha.append(alphanew)
        result = 0.0
        for st in states:
            if result == 0.0:
                result = alpha[-1][st]
            else:
                result = log_sum(result, alpha[-1][st])
        print result
    
def readfiles(str):
    file = open(str,'r')
    try:
        lines = file.readlines()
    finally:
        file.close()
    return lines

main(sys.argv)