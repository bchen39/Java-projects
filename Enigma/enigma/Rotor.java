package enigma;

/** Superclass that represents a rotor in the enigma machine.
 *  @author Ben Chen
 */
class Rotor {

    /** A rotor named NAME whose permutation is given by PERM. */
    Rotor(String name, Permutation perm) {
        _name = name;
        _permutation = perm;
        set(0);
    }

    /** Return my name. */
    String name() {
        return _name;
    }

    /** Return my alphabet. */
    Alphabet alphabet() {
        return _permutation.alphabet();
    }

    /** Return my permutation. */
    Permutation permutation() {
        return _permutation;
    }

    /** Return the size of my alphabet. */
    int size() {
        return _permutation.size();
    }

    /** Return true iff I have a ratchet and can move. */
    boolean rotates() {
        return false;
    }

    /** Return true iff I reflect. */
    boolean reflecting() {
        return false;
    }

    /** Return my current setting. */
    int setting() {
        return _setting;
    }

    /** Set setting() to POSN.  */
    void set(int posn) throws EnigmaException {
        if (posn < 0 || posn >= permutation().alphabet().size()) {
            throw new EnigmaException(
                    "Setting must be between 0 and alphabet size -1.");
        }
        _setting = posn;
    }

    /** Set setting() to character CPOSN. */
    void set(char cposn) throws EnigmaException {
        if (!alphabet().contains(cposn)) {
            throw new EnigmaException("illegal setting.");
        }
        _setting = alphabet().toInt(cposn);
    }

    /** Return the conversion of P (an integer in the range 0..size()-1)
     *  according to my permutation. */
    int convertForward(int p) throws EnigmaException {
        int newp = p + setting();
        newp = permutation().permute(newp);
        int result = (newp - setting()) % size();
        if (result < 0) {
            result += size();
        }
        return result;
    }

    /** Return the conversion of E (an integer in the range 0..size()-1)
     *  according to the inverse of my permutation. */
    int convertBackward(int e) throws EnigmaException {
        int newe = e + setting();
        newe = permutation().invert(newe);
        int result = (newe - setting()) % size();
        if (result < 0) {
            result += size();
        }
        return result;
    }

    /** Returns true iff I am positioned to allow the rotor to my left
     *  to advance. */
    boolean atNotch() {
        return false;
    }

    /** Advance me one position, if possible. By default, does nothing. */
    void advance() {
    }

    @Override
    public String toString() {
        return "Rotor " + _name;
    }

    /** My name. */
    private final String _name;

    /** The permutation implemented by this rotor in its 0 position. */
    private Permutation _permutation;

    /** The setting of the rotor. */
    private int _setting;

}
