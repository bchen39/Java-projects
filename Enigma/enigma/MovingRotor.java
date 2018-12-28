package enigma;


/** Class that represents a rotating rotor in the enigma machine.
 *  @author Ben Chen
 */
class MovingRotor extends Rotor {

    /** A rotor named NAME whose permutation in its default setting is
     *  PERM, and whose notches are at the positions indicated in NOTCHES.
     *  The Rotor is initally in its 0 setting (first character of its
     *  alphabet).
     */
    MovingRotor(String name, Permutation perm,
                String notches) throws EnigmaException {
        super(name, perm);
        if (notches.length() > perm.alphabet().size()) {
            throw new EnigmaException("Number of notches exceed limit.");
        }
        for (int i = 0; i < notches.length() - 1; i += 1) {
            String curr = notches.substring(i, i + 1);
            char currch = notches.charAt(i);
            if (notches.substring(i + 1).contains(curr)) {
                throw new EnigmaException("Repeated notches.");
            }
            if (!alphabet().contains(currch)) {
                throw new EnigmaException(
                        "Notches needs to be part of alphabet");
            }
        }
        _notches = notches;
    }

    @Override
    boolean rotates() {
        return true;
    }

    @Override
    boolean atNotch() {
        for (int i = 0; i < _notches.length(); i += 1) {
            if (setting() == alphabet().toInt(_notches.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    @Override
    void advance() {
        set((setting() + 1) % alphabet().size());
    }

    /** Notches of the rotor. */
    private String _notches;

}
