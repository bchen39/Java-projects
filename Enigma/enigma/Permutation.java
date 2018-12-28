package enigma;

import static enigma.EnigmaException.*;

/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author Ben Chen */
class Permutation {

    /** Set this Permutation to that specified by CYCLES, a string in the
     *  form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     *  is interpreted as a permutation in cycle notation.  Characters in the
     *  alphabet that are not included in any cycle map to themselves.
     *  Whitespace is ignored. */
    Permutation(String cycles, Alphabet alphabet) throws EnigmaException {
        _alphabet = alphabet;
        for (int i = 1; i < cycles.length() - 1; i += 1) {
            char prev = cycles.charAt(i - 1);
            char curr = cycles.charAt(i);
            char next = cycles.charAt(i + 1);
            if (alphabet.contains(curr)) {
                if (!alphabet.contains(prev)) {
                    if (prev != '(') {
                        throw new EnigmaException("Permutation wrong format.");
                    }
                } else if (!alphabet.contains(next)) {
                    if (next != ')') {
                        throw new EnigmaException("Permutation wrong format.");
                    }
                }
            }
            if (alphabet.contains(prev)) {
                if (cycles.substring(i).contains(cycles.substring(i - 1, i))) {
                    throw new EnigmaException(
                            "Permutation cannot have repeated characters.");
                }
            }
        }
        _perm = cycles;
    }

    /** Add the cycle c0->c1->...->cm->c0 to the permutation, where CYCLE is
     *  c0c1...cm. */
    private void addCycle(String cycle) throws EnigmaException {
        for (int i = 0; i < cycle.length() - 1; i += 1) {
            char curr = cycle.charAt(i);
            if (!alphabet().contains(curr)) {
                throw new EnigmaException("Wrong perm format.");
            }
            if (cycle.substring(i + 1).contains(cycle.substring(i, i + 1))
                    || _perm.contains(cycle.substring(i, i + 1))) {
                throw new EnigmaException(
                        "Permutation cannot have repeated characters.");
            }
        }
        if (!alphabet().contains(
                cycle.charAt(cycle.length() - 1))) {
            throw new EnigmaException("Wrong perm format.");
        }
        _perm += "(" + cycle + ")";
    }

    /** Return the value of P modulo the size of this permutation. */
    final int wrap(int p) throws EnigmaException {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }

    /** Returns the size of the alphabet I permute. */
    int size() throws EnigmaException {
        return alphabet().size();
    }

    /** Return the result of applying this permutation to P modulo the
     *  alphabet size. */
    int permute(int p) throws EnigmaException {
        char rsc = alphabet().toChar(wrap(p));
        int leftmostchar = 0;
        for (int i = 0; i < _perm.length() - 1; i += 1) {
            char curr = _perm.charAt(i);
            char next = _perm.charAt(i + 1);
            if (curr == '(') {
                leftmostchar = i + 1;
            }
            if (rsc == curr) {
                if (next != ')') {
                    return alphabet().toInt(next);
                } else {
                    if (leftmostchar == i) {
                        return p;
                    } else {
                        return alphabet().toInt(_perm.charAt(leftmostchar));
                    }
                }
            }
            if (i == _perm.length() - 2 && rsc == next) {
                return alphabet().toInt(_perm.charAt(0));
            }
        }
        return p;
    }

    /** Return the result of applying the inverse of this permutation
     *  to  C modulo the alphabet size. */
    int invert(int c) throws EnigmaException {
        for (int i = 0; i < size(); i++) {
            if (permute(i) == wrap(c)) {
                return i;
            }
        }
        return c;
    }

    /** Return the result of applying this permutation to the index of P
     *  in ALPHABET, and converting the result to a character of ALPHABET. */
    char permute(char p) {
        if (p == ' ') {
            return p;
        }
        return alphabet().toChar(permute(alphabet().toInt(p)));
    }

    /** Return the result of applying the inverse of this permutation to C. */
    int invert(char c) {
        if (c == ' ') {
            return c;
        }
        return alphabet().toChar(invert(alphabet().toInt(c)));
    }

    /** Return the alphabet used to initialize this Permutation. */
    Alphabet alphabet() {
        return _alphabet;
    }

    /** Return true iff this permutation is a derangement (i.e., a
     *  permutation for which no value maps to itself). */
    boolean derangement() throws EnigmaException {
        for (int i = 0; i < alphabet().size(); i += 1) {
            if (permute(i) == i) {
                return false;
            }
        }
        return true;
    }

    /** Alphabet of this permutation. */
    private Alphabet _alphabet;
    /** The permutation cycle. */
    private String _perm;
}
