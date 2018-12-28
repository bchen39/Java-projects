package enigma;
import org.junit.Test;
import static org.junit.Assert.*;


public class CRATest {
    /* ***** TESTING UTILITIES ***** */

    CharRangeAlter reg = new CharRangeAlter("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
    CharRangeAlter nstart = new CharRangeAlter(
            "NOPQRSTUVWXYZABCDEFGHIJKLM");
    CharRangeAlter weird = new CharRangeAlter("BACJDMS@#$");

    /* ***** TESTS ***** */
    @Test
    public void testReg() {
        assertEquals(reg.size(), 26);
        assertFalse(reg.contains('$'));
        assertEquals(reg.toChar(3), 'D');
        assertEquals(reg.toInt('F'), 5);
    }

    @Test
    public void testNstart() {
        assertEquals(nstart.size(), 26);
        assertFalse(nstart.contains('4'));
        assertEquals(nstart.toChar(6), 'T');
        assertEquals(nstart.toInt('K'), 23);
    }

    @Test
    public void testWeird() {
        assertEquals(nstart.size(), 10);
        assertTrue(nstart.contains('$'));
        assertEquals(nstart.toChar(6), 'S');
        assertEquals(nstart.toInt('@'), 7);
    }
}
