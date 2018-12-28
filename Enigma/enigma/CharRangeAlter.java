package enigma;

import static enigma.EnigmaException.error;

/** An altered version of CharacterRange consisting of a
 * string of characters in a certain range in
 *  order.
 *  @author Ben Chen
 */
public class CharRangeAlter extends Alphabet {
    /** An alphabet consisting of all characters between FIRST and LAST,
     *  inclusive.
     *  @param s The string of characters in range. */
    CharRangeAlter(String s) {
        if (s.length() == 0) {
            throw new EnigmaException(
                    "alphabet cannot have 0 chars");
        }
        for (int i = 0; i < s.length() - 1; i += 1) {
            String curr = s.substring(i, i + 1);
            String rest = s.substring(i + 1);
            if (rest.contains(curr)) {
                throw new EnigmaException("no repeated characters");
            }
        }
        _chars = s;
    }

    @Override
    int size() {
        return _chars.length();
    }

    @Override
    boolean contains(char ch) {
        for (int i = 0; i < _chars.length(); i += 1) {
            if (_chars.charAt(i) == ch) {
                return true;
            }
        }
        return false;
    }

    @Override
    char toChar(int index) {
        if (index > _chars.length() || index < 0) {
            throw error("character index out of range");
        }
        return _chars.charAt(index);
    }

    @Override
    int toInt(char ch) {
        if (!contains(ch)) {
            throw error("character out of range");
        }
        return _chars.indexOf(ch);
    }

    /** String of characters. */
    private String _chars;
}
