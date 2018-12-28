package enigma;

import java.util.Collection;

import static enigma.EnigmaException.*;

/** Class that represents a complete enigma machine.
 *  @author Ben Chen
 */
class Machine {

    /** A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     *  and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     *  available rotors. */
    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        if (numRotors <= 1) {
            throw new EnigmaException("illegal "
                    + "number of rotors.");
        }
        if (pawls < 0 || pawls >= numRotors) {
            throw new EnigmaException("illegal "
                    + "number of pawls.");
        }
        _alphabet = alpha;
        _numRotors = numRotors;
        _numPawls = pawls;
        _allRotors = allRotors;
    }

    /** Return the number of rotor slots I have. */
    int numRotors() {
        return _numRotors;
    }

    /** Return the number pawls (and thus rotating rotors) I have. */
    int numPawls() {
        return _numPawls;
    }

    /** Set my rotor slots to the rotors named ROTORS from my set of
     *  available rotors (ROTORS[0] names the reflector).
     *  Initially, all rotors are set at their 0 setting. */
    void insertRotors(String[] rotors) throws EnigmaException {
        int pawl = 0;
        int pos = 0;
        if (rotors.length != numRotors()) {
            throw new EnigmaException("Incorrect number of rotors.");
        }
        for (String i: rotors) {
            int eq = 0;
            for (String j: rotors) {
                if (i.equals(j)) {
                    eq += 1;
                }
            }
            if (eq > 1) {
                throw new EnigmaException("cannot repeat rotors.");
            }
            Rotor r = getRotor(i);
            if (r == null) {
                throw new EnigmaException("No such rotor.");
            }
            if (r.rotates()) {
                if (pos < numRotors() - numPawls()) {
                    throw new EnigmaException("Moving "
                            + "rotor placed too early.");
                }
                pawl += 1;
            } else {
                if (r.reflecting()) {
                    if (pos != 0) {
                        throw new EnigmaException("Wrong "
                                + "place for reflector.");
                    }
                }
                if (pos >= numRotors() - numPawls()) {
                    throw new EnigmaException("Fixed "
                            + "rotor placed too late.");
                }
            }
            pos += 1;
        }
        if (pawl > numPawls()) {
            throw new EnigmaException("Number of pawls exceed limit.");
        }
        if (!getRotor(rotors[0]).reflecting()) {
            throw new EnigmaException("First rotor has to be reflector.");
        }
        _rotors = rotors;
    }

    /** Set my rotors according to SETTING, which must be a string of
     *  numRotors()-1 upper-case letters. The first letter refers to the
     *  leftmost rotor setting (not counting the reflector).  */
    void setRotors(String setting) {
        int n = 0;
        for (String i: rotors()) {
            Rotor r = getRotor(i);
            if (!r.reflecting()) {
                r.set(setting.charAt(n));
                n += 1;
            }
        }
    }

    /** Set the plugboard to PLUGBOARD. */
    void setPlugboard(Permutation plugboard) {
        for (int i = 0; i < plugboard.alphabet().size(); i += 1) {
            if (plugboard.permute(plugboard.permute(i)) != i) {
                throw new EnigmaException(
                        "plugboard perm has to be pair or identity.");
            }
        }
        _plugboard = plugboard;
    }

    /** Returns the result of converting the input character C (as an
     *  index in the range 0..alphabet size - 1), after first advancing
     *  the machine. */
    int convert(int c) {
        advance();
        if (_plugboard != null) {
            c = _plugboard.permute(c);
        }
        for (int i = rotors().length - 1; i > 0; i -= 1) {
            c = getRotor(rotors()[i]).convertForward(c);
        }
        for (int i = 0; i < rotors().length; i += 1) {
            c = getRotor(rotors()[i]).convertBackward(c);
        }
        if (_plugboard != null) {
            c = _plugboard.invert(c);
        }
        return c;
    }

    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    String convert(String msg) {
        String result = "";
        msg = msg.toUpperCase();
        for (int i = 0; i < msg.length(); i += 1) {
            char curr = msg.charAt(i);
            if (curr == ' ') {
                result += "";
            } else {
                result += _alphabet.toChar(convert(_alphabet.toInt(curr)));
            }
        }
        return result;
    }

    /** Returns the rotor with the name s.
     * @param s possible name of rotor. */
    Rotor getRotor(String s) {
        for (Rotor i: _allRotors) {
            if (i.name().equals(s)) {
                return i;
            }
        }
        return null;
    }

    /** Advances the rotors accordingly. */
    void advance() {
        Rotor begin = getRotor(rotors()[rotors().length - 1]);
        boolean notch = begin.atNotch();
        begin.advance();
        for (int i = rotors().length - 2; i >= 1; i -= 1) {
            begin = getRotor(rotors()[i]);
            Rotor next = getRotor(rotors()[i - 1]);
            boolean notch2 = begin.atNotch();
            if (notch || notch2 && next.rotates()) {
                notch = begin.atNotch();
                begin.advance();
            } else {
                notch = begin.atNotch();
            }
        }
    }

    /** return list of rotors. */
    String[] rotors() {
        return _rotors;
    }

    /** Common alphabet of my rotors. */
    private final Alphabet _alphabet;

    /** Number of rotors. */
    private int _numRotors;

    /** Number of pawls. */
    private int _numPawls;

    /** Collection of all rotors. */
    private Collection<Rotor> _allRotors;

    /** List of all rotors. */
    private String[] _rotors;

    /** Permutation of plugboard. */
    private Permutation _plugboard;
}
