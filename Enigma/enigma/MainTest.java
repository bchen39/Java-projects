package enigma;

import java.io.StringReader;
import java.util.Scanner;
import org.junit.Test;
import static org.junit.Assert.*;

import static enigma.TestUtils.*;

public class MainTest {

    /* ***** TESTING UTILITIES ***** */

    private String alpha = UPPER_STRING;

    @Test
    public void mainTest() {
        StringReader conf = new StringReader("A-Z\n"
                + "4 2\n"
                + "XD MR 908(ABCDEFGHIJKLMNOPQRSTUVWXYZ)\n"
                + "XDD MUV (ASDFGHJKL)jioadsjf(QWERTYUIO)\n"
                + "XDDD MAKE (LMAOXD)uoiu*^&*7987(KEZCV)\n"
                + "XDDDD MOIU (QHAJSUIWKDMNFLPO)\n"
                + "NO N (UHAVELIGMB)\n"
                + "KEK N (ALBEVFCYODJWUGNMQTZSKPR) (HIX)\n"
                + "U R (AE) (BN) (CK) (DQ) (FU)"
                + " (GY) (HW) (IJ) (LO) (MP)\n"
                + "           (RX) (SZ) (TV)\n"
                + "E R (AC) (QW) (ER) (TY) (UI) (OP) (SD)\n"
                + "                    (FG) (HJ) (KL) (MN)");
        StringReader in = new StringReader(
                "* U KEK XD XDD AAA sdfds(AD)&(*&((FE)\n"
                + "Hello Worldsddf");
        Main m = new Main(new Scanner(conf), new Scanner(in), System.out);
        String msg = m.testProcess();
        assertEquals(msg, "LMEDD CBFWI "
                + "UQEQ\n");
    }

    @Test
    public void exampleTest() {
        StringReader conf = new StringReader("A-Z\n"
                + " 5 3\n"
                + " I MQ      (AELTPHQXRU) (BKNW) (CMOY) (DFG) (IV) (JZ) (S)\n"
                + " II ME     (FIXVYOMW) (CDKLHUP) (ESZ) (BJ)"
                + " (GR) (NT) (A) (Q)\n"
                + " III MV    (ABDHPEJT) (CFLVMZOYQIRWUKXSG) (N)\n"
                + " IV MJ     (AEPLIYWCOXMRFZBSTGJQNH) (DV)&**^^ (KU)\n"
                + " V MZ      (AVOLDRWFIUQ)*&*&(BZKSMNHYC) (EGTJPX)\n"
                + " VI MZM    (AJQDVLEOZWIYTS)uoi (CGMNHFUX) (BPRK) \n"
                + " VII MZM   (ANOUPFRIMBZTLWKSVEGCJYDHXQ) \n"
                + " VIII MZM  (AFLSETWUNDHOZVICQ) (BKJ) (GXY) (MPR)\n"
                + " Beta N    (ALBEVFCYODJWUGNMQTZSKPR) (HIX)\n"
                + " Gamma N   (AFNIRLBSQWVXGUZDKMTPCOYJHE)\n"
                + " B R       (AE) (BN) (CK) (DQ) (FU)"
                + " (GY) (HW) (IJ) (LO) (MP)\n"
                + "           (RX) (SZ) (TV)\n"
                + " C R       (AR) (BD) (CO) (EJ) (FN) (GT) (HK)"
                + " (IV) (LM) (PW)\n"
                + "           (QZ) (SX) (UY)");
        StringReader in = new StringReader("* B BETA I II III AAAA\n"
                + "Hello world\n"
                + "\n"
                + "* B BETA I II III AAAA\n"
                + "ILBDA AMTAZ");
        Main m = new Main(new Scanner(conf), new Scanner(in), System.out);
        String msg = m.testProcess();
        assertEquals(msg, "ILBDA AMTAZ\n"
                + "\nHELLO WORLD\n");
        StringReader conf2 = new StringReader("A-Z\n"
                + " 5 3\n"
                + " I MQ      (AELTPHQXRU) (BKNW) (CMOY) "
                + "&(*&*798(DFG) (IV) (JZ) (S)\n"
                + " II ME     (FIXVYOMW)"
                + " (CDKLHUP) (ESZ) (BJ) (GR) (NT) (A) asdfsa(Q)\n"
                + " III MV    (ABDHPEJT) (CFLVMZOYQIRWUKXSG) (N)\n"
                + " IV MJ     (AEPLIYWCOXMRFZBSTGJQNH) (DV) (KU)\n"
                + " V MZ      (AVOLDRWFIUQ)(BZKSMNHYC) (EGTJPX)\n"
                + " VI MZM    (AJQDVLEOZWIYTS) (CGMNHFUX)iwjeoj()(BPRK) \n"
                + " VII MZM   (ANOUPFRIMBZTLWKSVEGCJYDHXQ) \n"
                + " VIII MZM  (AFLSETWUNDHOZVICQ) (BKJ) (GXY) (MPR)\n"
                + " Beta N    (ALBEVFCYODJWUGNMQTZSKPR) (HIX)\n"
                + " Gamma N   (AFNIRLBSQWVXGUZDKMTPCOYJHE)\n"
                + " B R       (AE) (BN) (CK) (DQ) (FU)"
                + " (GY) (HW) (IJ) (LO) (MP)\n"
                + "           (RX) (SZ) (TV)\n"
                + " C R       (AR) (BD) (CO) (EJ)"
                + " (FN) (GT) (HK) (IV) (LM) (PW)\n"
                + "           (QZ) (SX) (UY)");
        StringReader in2 = new StringReader("* B BETA III II I AAAZ");
        Main m2 = new Main(new Scanner(conf2), new Scanner(in2), System.out);
        String msg2 = m2.testProcess();
        assertEquals(msg2, "");
    }

}
