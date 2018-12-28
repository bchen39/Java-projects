package enigma;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.Timeout;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.*;

import static enigma.TestUtils.*;

public class MachineTest {
    @Rule
    public Timeout globalTimeout = Timeout.seconds(5);

    /* ***** TESTING UTILITIES ***** */

    private String alpha = UPPER_STRING;
    private Alphabet abc = new CharacterRange('A', 'C');
    private Rotor b = new
            Reflector("B", new Permutation(NAVALA.get("B"), UPPER));
    private Reflector c = new
            Reflector("C", new Permutation(NAVALA.get("C"), UPPER));
    private FixedRotor beta = new
            FixedRotor("Beta", new Permutation(NAVALA.get("Beta"), UPPER));
    private MovingRotor i = new
            MovingRotor("I", new Permutation(NAVALA.get("I"), UPPER), "Q");
    private MovingRotor ii = new
            MovingRotor("II", new Permutation(NAVALA.get("II"), UPPER), "E");
    private MovingRotor iii = new
            MovingRotor("III", new Permutation(NAVALA.get("III"), UPPER), "V");
    private MovingRotor iv = new
            MovingRotor("IV", new Permutation(NAVALA.get("IV"), UPPER), "J");
    private Rotor a1 = new
            Reflector("A1", new Permutation("ABC", abc));
    private Rotor a2 = new
            MovingRotor("A2", new Permutation("ABC", abc), "C");
    private Rotor a3 = new
            MovingRotor("A3", new Permutation("ABC", abc), "C");
    private Rotor a4 = new
            MovingRotor("A4", new Permutation("ABC", abc), "C");
    ArrayList<Rotor> rot;
    /** Set the rotor to the one with given NAME and permutation as
     *  specified by the NAME entry in ROTORS, with given NOTCHES. */
    private MovingRotor setMovingRotor(String name,
                                       HashMap<String, String> rotors,
                          String notches) {
        MovingRotor r = new MovingRotor(name, new
                Permutation(rotors.get(name), UPPER),
                notches);
        return r;
    }

    private void addABC() {
        rot = new ArrayList<Rotor>();
        rot.add(a1);
        rot.add(a2);
        rot.add(a3);
        rot.add(a4);
    }
    private void addRotors() {
        rot = new ArrayList<Rotor>();
        rot.add(b);
        rot.add(c);
        rot.add(i);
        rot.add(ii);
        rot.add(iii);
        rot.add(iv);
        rot.add(beta);
    }

    private void checkallsetting(Machine m, Alphabet alp,
                                 String[] rotors, String setting) {
        int n = 0;
        for (String j: rotors) {
            assertEquals(m.getRotor(j).setting(), alp.toInt(setting.charAt(n)));
            n += 1;
        }
    }
    /* ***** TESTS ***** */
    @Test
    public void checkExampleABC() {
        addABC();
        Machine m = new Machine(abc, 4, 3, rot);
        m.insertRotors(new String[]{"A1", "A2", "A3", "A4"});
        for (int i2 = 0; i2 < 4; i2 += 1) {
            m.advance();
        }
        checkallsetting(m, abc, m.rotors(), "AABB");
        for (int i3 = 0; i3 < 3; i3 += 1) {
            m.advance();
        }
        checkallsetting(m, abc, m.rotors(), "ABAB");
        for (int i4 = 0; i4 < 7; i4 += 1) {
            m.advance();
        }
        checkallsetting(m, abc, m.rotors(), "ACAC");
    }

    @Test
    public void checkExampleAXLE() {
        addRotors();
        Machine m = new Machine(UPPER, 5, 3, rot);
        m.insertRotors(new String[]{"B", "Beta", "III", "IV", "I"});
        assertArrayEquals(m.rotors(), new
                String[]{"B", "Beta", "III", "IV", "I"});
        m.setRotors("AXLE");
        checkallsetting(m, UPPER, m.rotors(), "AAXLE");
        m.setPlugboard(new Permutation("(YF)(ZH)", UPPER));
        assertEquals(m.convert(24), 25);
        m.setRotors("AXLE");
        assertEquals(m.convert("Y"), "Z");
        for (int i5 = 0; i5 < 11; i5 += 1) {
            m.advance();
        }
        checkallsetting(m, UPPER, m.rotors(), "AAXLQ");
        m.advance();
        checkallsetting(m, UPPER, m.rotors(), "AAXMR");
        for (int i6 = 0; i6 < 597; i6 += 1) {
            m.advance();
        }
        checkallsetting(m, UPPER, m.rotors(), "AAXIQ");
        m.advance();
        checkallsetting(m, UPPER, m.rotors(), "AAXJR");
        m.advance();
        checkallsetting(m, UPPER, m.rotors(), "AAYKS");
    }

    @Test
    public void checkHW() {
        addRotors();
        Machine m = new Machine(UPPER, 5, 3, rot);
        m.insertRotors(new String[]{"B", "Beta", "I", "II", "III"});
        m.setPlugboard(new Permutation("", UPPER));
        assertEquals(m.convert("Helloworld"),
                "ILBDAAMTAZ");
        m.setRotors("AAAA");
        assertEquals(m.convert("ILBDAAMTAZ"),
                "HELLOWORLD");
    }

}
