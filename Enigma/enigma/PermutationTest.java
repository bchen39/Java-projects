package enigma;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.Timeout;
import static org.junit.Assert.*;
import org.junit.rules.ExpectedException;

import static enigma.TestUtils.*;

/** The suite of all JUnit tests for the Permutation class.
 *  @author Ben Chen
 */
public class PermutationTest {

    /**
     * Testing time limit.
     */
    @Rule
    public Timeout globalTimeout = Timeout.seconds(5);
    public ExpectedException thrown = ExpectedException.none();

    /* ***** TESTING UTILITIES ***** */

    private Permutation perm;
    private String alpha = UPPER_STRING;

    /**
     * Check that perm has an alphabet whose size is that of
     * FROMALPHA and TOALPHA and that maps each character of
     * FROMALPHA to the corresponding character of FROMALPHA, and
     * vice-versa. TESTID is used in error messages.
     */
    private void checkPerm(String testId,
                           String fromAlpha, String toAlpha) {
        int N = fromAlpha.length();
        assertEquals(testId + " (wrong length)", N, perm.size());
        for (int i = 0; i < N; i += 1) {
            char c = fromAlpha.charAt(i), e = toAlpha.charAt(i);
            assertEquals(msg(testId, "wrong translation of '%c'", c),
                    e, perm.permute(c));
            assertEquals(msg(testId, "wrong inverse of '%c'", e),
                    c, perm.invert(e));
            int ci = alpha.indexOf(c), ei = alpha.indexOf(e);
            assertEquals(msg(testId, "wrong translation of %d", ci),
                    ei, perm.permute(ci));
            assertEquals(msg(testId, "wrong inverse of %d", ei),
                    ci, perm.invert(ei));
        }
    }

    /* ***** TESTS ***** */

    @Test
    public void checkIdTransform() {
        perm = new Permutation("", UPPER);
        checkPerm("identity", UPPER_STRING, UPPER_STRING);
        assertFalse(perm.derangement());
    }

    @Test
    public void checkTransformWeirdCharacter() {
        perm = new Permutation(
                "(ABCDEFG)#@$$(HIJKLMN) (OPQRST) (UVWXY) (Z)", UPPER);
        checkPerm("+1", UPPER_STRING, "BCDEFGAIJKLMNHPQRSTOVWXYUZ");
        assertFalse(perm.derangement());
    }

    @Test
    public void checkWeirdTransform() {
        perm = new Permutation(
                "(AFHUSD)    (BCVY) (XIZNPWGT) (EJKLMOQR)", UPPER);
        checkPerm("Weird", UPPER_STRING, "FCVAJHTUZKLMOPQWREDXSYGIBN");
        assertTrue(perm.derangement());
    }

    @Test
    public void checksingleTransform() {
        perm = new Permutation(
                "ABCDEFGHIJKLMNOPQRSTUVWXYZ", UPPER);
        checkPerm("Single", UPPER_STRING, "BCDEFGHIJKLMNOPQRSTUVWXYZA");
        assertTrue(perm.derangement());
    }
}

