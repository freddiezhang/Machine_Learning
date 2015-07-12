import sys
import math
from logsum import log_sum
from numpy import Infinity

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
        viterbi = []
        sequence = []
        content = line.strip().split()
        viterbinext = {}
        seq = {}
        for st in states:
            viterbinext[st] = pi[st] + bjk[st][content[0]]
            seq[st] = [st]
        viterbi.append(viterbinext)
        sequence.append(seq)
        index = 0
        for word in content[1:]:
            index += 1
            viterbinew = {}
            seqq = {}
            for st in states:
                viterbinew[st] = -Infinity
                bestst = ''
                for oldst in states:
                    if viterbinew[st] < viterbi[-1][oldst] + aij[oldst][st] + bjk[st][content[index]]:
                        viterbinew[st] = viterbi[-1][oldst] + aij[oldst][st] + bjk[st][content[index]]
                        bestst = oldst
                ll = sequence[-1][bestst][:]
                ll.append(st)
                seqq[st] = ll
            viterbi.append(viterbinew)
            sequence.append(seqq)
        result = -Infinity
        bestst = ''
        for st in states:
            if result < viterbi[-1][st]:
                result = viterbi[-1][st]
                bestst = st
        bestsequence = sequence[-1][bestst][:]
        outstring = ''
        index = 0
        for word in content:
            if index == len(content) - 1:
                outstring += (word +'_' + bestsequence[index])
            else:
                outstring += (word +'_' + bestsequence[index]) + ' '
            index += 1
        print outstring
def readfiles(str):
    file = open(str,'r')
    try:
        lines = file.readlines()
    finally:
        file.close()
    return lines

main(sys.argv)