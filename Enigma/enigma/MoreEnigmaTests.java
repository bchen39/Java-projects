/** Some extra tests for Enigma.
 *  @author Ben Chen
 */
package enigma;
import java.util.ArrayList;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.Arrays;

import static enigma.TestUtils.*;
public class MoreEnigmaTests {
    @Test
    public void testInvertChar() {
        Permutation p = new Permutation("(PNH) "
                + "(ABDFIKLZYXW) (JC)", new CharacterRange('A', 'Z'));
        assertEquals(p.invert('B'), 'A');
        assertEquals(p.invert('G'), 'G');
        assertEquals(p.invert('A'), 'W');
        assertEquals(p.invert('J'), 'C');
    }

    @Test
    public void testPermuteChar() {
        Permutation p = new Permutation("(PNH) "
                + "(ABDFIKLZYXW) (JC)", new CharacterRange('A', 'Z'));
        assertEquals(p.permute('A'), 'B');
        assertEquals(p.permute('J'), 'C');
        assertEquals(p.permute('C'), 'J');
        assertEquals(p.permute('W'), 'A');
        assertEquals(p.permute('M'), 'M');

    }

    @Test
    public void testDerangement() {
        Permutation p = new Permutation("(PNH) "
                + "(ABDFIKLZYXW) (JC)", new CharacterRange('A', 'Z'));
        assertFalse(p.derangement());
        Permutation p2 = new Permutation("(ABCDEFGHI"
                + "JKLMNOPQRSTUVWXYZ)", new CharacterRange('A', 'Z'));
        assertTrue(p2.derangement());
        Permutation p3 = new Permutation("(ABCDEFGHI"
                + "JKLMNOPQRSTUVWXY)", new CharacterRange('A', 'Z'));
        assertFalse(p3.derangement());
    }

    @Test
    public void testDoubleStep() {
        Alphabet ac = new CharacterRange('A', 'D');
        Rotor one = new Reflector("R1",
                new Permutation("(AC) (BD)", ac));
        Rotor two = new MovingRotor("R2",
                new Permutation("(ABCD)", ac), "C");
        Rotor three = new MovingRotor("R3",
                new Permutation("(ABCD)", ac), "C");
        Rotor four = new MovingRotor("R4",
                new Permutation("(ABCD)", ac), "C");
        String setting = "AAA";
        Rotor[] machineRotors = {one, two, three, four};
        String[] rotors = {"R1", "R2", "R3", "R4"};
        Machine mach = new Machine(ac, 4, 3,
                new ArrayList<>(Arrays.asList(machineRotors)));
        mach.insertRotors(rotors);
        mach.setRotors(setting);

        assertEquals("AAAA", getSetting(ac, machineRotors));
        mach.convert('a');
        assertEquals("AAAB", getSetting(ac, machineRotors));
        mach.convert('a');
        assertEquals("AAAC", getSetting(ac, machineRotors));
        mach.convert('a');
        assertEquals("AABD", getSetting(ac, machineRotors));
        mach.convert('a');
        assertEquals("AABA", getSetting(ac, machineRotors));
        mach.convert('a');
        assertEquals("AABB", getSetting(ac, machineRotors));
        mach.convert('a');
        assertEquals("AABC", getSetting(ac, machineRotors));
        mach.convert('a');
        assertEquals("AACD", getSetting(ac, machineRotors));
        mach.convert('a');
        assertEquals("ABDA", getSetting(ac, machineRotors));
        mach.convert('a');
    }

    /** Helper method to get the String representation
     *  of the current Rotor settings */
    private String getSetting(Alphabet alph, Rotor[] machineRotors) {
        String currSetting = "";
        for (Rotor r : machineRotors) {
            currSetting += alph.toChar(r.setting());
        }
        return currSetting;
    }
}
