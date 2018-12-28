package enigma;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.Timeout;
import static org.junit.Assert.*;

import static enigma.TestUtils.*;
/** Tests for the Rotor Class */
public class RotorTest {
    /** Testing time limit. */
    @Rule
    public Timeout globalTimeout = Timeout.seconds(5);

    /* ***** TESTING UTILITIES ***** */

    private Rotor rotor;

    /** Check that rotor has an alphabet whose size is that of
     *  FROMALPHA and TOALPHA and that maps each character of
     *  FROMALPHA to the corresponding character of FROMALPHA, and
     *  vice-versa. TESTID is used in error messages. */
    private void checkRotor(String testId,
                            String fromAlpha, String toAlpha) {
        int N = fromAlpha.length();
        String alpha = UPPER_STRING;
        assertEquals(testId + " (wrong length)", N, rotor.size());
        for (int i = 0; i < N; i += 1) {
            char c = fromAlpha.charAt(i), e = toAlpha.charAt(i);
            int ci = alpha.indexOf(c), ei = alpha.indexOf(e);
            assertEquals(msg(testId, "wrong translation of %d (%c)", ci, c),
                    ei, rotor.convertForward(ci));
            assertEquals(msg(testId, "wrong inverse of %d (%c)", ei, e),
                    ci, rotor.convertBackward(ei));
        }
    }

    @Test
    public void checkNormalRotor() {
        rotor = new Rotor("Wheelz",
                new Permutation("(ABCDEFGHIJKLMNOPQRSTUVWXYZ)", UPPER));
        checkRotor("+1", UPPER_STRING, "BCDEFGHIJKLMNOPQRSTUVWXYZA");
        rotor.set(1);
        checkRotor("+2", UPPER_STRING, "BCDEFGHIJKLMNOPQRSTUVWXYZA");
    }

    @Test
    public void checkAlteredRotor() {
        rotor = new Rotor("Wheelz2", new Permutation("(AUFPN)", UPPER));
        checkRotor("Weird", UPPER_STRING, "UBCDEPGHIJKLMAONQRSTFVWXYZ");
        rotor.set(5);
        checkRotor("Weirder", UPPER_STRING, "KBCDEFGHVJILMNOAQRSTUPWXYZ");
    }

}
