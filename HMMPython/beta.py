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
        beta = []
        content = line.strip().split()
        betanext = {}
        for st in states:
            betanext[st] = 0.0
        beta.append(betanext)
        index = len(content)
        for word in content[1:]:
            index -= 1
            betanew = {}
            for st in states:
                betanew[st] = 0.0
                for oldst in states:
                    if betanew[st] == 0.0:
                        betanew[st] = beta[-1][oldst] + aij[st][oldst] + bjk[oldst][content[index]]
                    else:
                        betanew[st] = log_sum(betanew[st], beta[-1][oldst] + aij[st][oldst] + bjk[oldst][content[index]])
            beta.append(betanew)
        result = 0.0
        for st in states:
            if result == 0.0:
                result = pi[st] + bjk[st][content[0]] + beta[-1][st]
            else:
                result = log_sum(result, pi[st] + bjk[st][content[0]] + beta[-1][st])
        print result
    
def readfiles(str):
    file = open(str,'r')
    try:
        lines = file.readlines()
    finally:
        file.close()
    return lines

main(sys.argv)