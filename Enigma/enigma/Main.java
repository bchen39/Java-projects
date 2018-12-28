package enigma;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

import static enigma.EnigmaException.*;

/** Enigma simulator.
 *  @author Ben Chen
 */
public final class Main {

    /** Process a sequence of encryptions and decryptions, as
     *  specified by ARGS, where 1 <= ARGS.length <= 3.
     *  ARGS[0] is the name of a configuration file.
     *  ARGS[1] is optional; when present, it names an input file
     *  containing messages.  Otherwise, input comes from the standard
     *  input.  ARGS[2] is optional; when present, it names an output
     *  file for processed messages.  Otherwise, output goes to the
     *  standard output. Exits normally if there are no errors in the input;
     *  otherwise with code 1. */
    public static void main(String... args) {
        try {
            new Main(args).process();
            return;
        } catch (EnigmaException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }

    /** Check ARGS and open the necessary files (see comment on main). */
    Main(String[] args) {
        if (args.length < 1 || args.length > 3) {
            throw error("Only 1, 2, or 3 command-line arguments allowed");
        }

        _config = getInput(args[0]);

        if (args.length > 1) {
            _input = getInput(args[1]);
        } else {
            _input = new Scanner(System.in);
        }

        if (args.length > 2) {
            _output = getOutput(args[2]);
        } else {
            _output = System.out;
        }
    }

    /** Constructor used for testing.
     * @param config scanner with machine configs.
     * @param input scanner of the input to the machine.
     * @param output PrintStream of output. */
    Main(Scanner config, Scanner input, PrintStream output) {
        _config = config;
        _input = input;
        _output = output;
    }

    /** Return a Scanner reading from the file named NAME. */
    private Scanner getInput(String name) {
        try {
            return new Scanner(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Return a PrintStream writing to the file named NAME. */
    private PrintStream getOutput(String name) {
        try {
            return new PrintStream(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Configure an Enigma machine from the contents of configuration
     *  file _config and apply it to the messages in _input, sending the
     *  results to _output. */
    private void process() {
        Machine m = readConfig();
        int loop = 0;
        String curr = "";
        String setting;
        String msg;
        if (!_input.hasNext("\\*")) {
            throw new EnigmaException(
                    "setting has to start with asterisk");
        }
        while (_input.hasNextLine()) {
            if (loop == 0) {
                curr = _input.nextLine();
            }
            setting = curr;
            setUp(m, setting);
            if (_input.hasNextLine()) {
                curr = _input.nextLine();
                while (!curr.contains("*")
                        && _input.hasNextLine()) {
                    msg = m.convert(curr);
                    printMessageLine(msg);
                    curr = _input.nextLine();
                }
                loop += 1;
            }
        }
        if (curr.startsWith("*")) {
            setUp(m, curr);
        } else {
            msg = m.convert(curr);
            printMessageLine(msg);
        }

    }

    /** Return an Enigma machine configured from the contents of configuration
     *  file _config. */
    private Machine readConfig() {
        try {
            int numRot = 0, numPawl = 0;
            char begin, end;
            ArrayList<Rotor> r = new ArrayList<>();
            String curr = _config.nextLine();
            if (curr.equals("")) {
                throw new EnigmaException(
                        "alphabet cannot be empty");
            }
            Scanner sc = new Scanner(new StringReader(curr));
            String curralt = sc.next();
            if (curralt.length() != 3) {
                for (int i = 0; i < curr.length() - 1; i += 1) {
                    char st = curr.charAt(i);
                    if (st == '(' || st == ')'
                            || st == '-' || st == ' ') {
                        throw new EnigmaException(
                                "wrong alphabet format");
                    }
                }
                _alphabet = new CharRangeAlter(curralt);
            } else {
                if (curralt.charAt(1) == '-') {
                    begin = curr.charAt(0);
                    end = curr.charAt(2);
                    _alphabet = new CharacterRange(begin, end);
                } else {
                    for (int i = 0; i < curr.length() - 1; i += 1) {
                        char st = curr.charAt(i);
                        if (st == '(' || st == ')'
                                || st == '-' || st == ' ') {
                            throw new EnigmaException(
                                    "wrong alphabet format");
                        }
                    }
                }
            }
            numRot = _config.nextInt();
            numPawl = _config.nextInt();
            while (_config.hasNext()) {
                r.add(readRotor());
            }
            return new Machine(_alphabet, numRot, numPawl, r);
        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }
    }

    /** Return a rotor, reading its description from _config. */
    private Rotor readRotor() {
        try {
            String nametemp = _config.next();
            if (nametemp.contains("(") || nametemp.contains(")")) {
                throw new EnigmaException("wrong name format");
            }
            String name = "";
            for (int i = 0; i < nametemp.length(); i += 1) {
                String curr = nametemp.substring(i, i + 1);
                String lower = "abcdefghijklmnopqrstuvwxyz";
                if (lower.contains(curr)) {
                    name += curr.toUpperCase();
                } else {
                    name += curr;
                }
            }
            String setting = _config.next();
            String notch = setting.substring(1);
            String perm0 = "";
            while (_config.hasNext(".*\\(([.]*.*)+\\).*")) {
                perm0 += _config.next();
            }
            Permutation perm = new Permutation(perm0, _alphabet);
            switch (setting.charAt(0)) {
            case 'M': return new MovingRotor(name, perm, notch);
            case 'N': return new FixedRotor(name, perm);
            case 'R': return new Reflector(name, perm);
            default: throw new EnigmaException("bad rotor description.");
            }
        } catch (NoSuchElementException excp) {
            throw error("bad rotor description");
        }
    }

    /** Set M according to the specification given on SETTINGS,
     *  which must have the format specified in the assignment. */
    private void setUp(Machine M, String settings) {
        String[] rotors = new String[M.numRotors()];
        String setting = "";
        String plug = "";
        int n = 0;
        StringReader s = new StringReader(settings);
        Scanner set = new Scanner(s);
        while (set.hasNext()) {
            set.next();
            for (int i = 0; i < M.numRotors(); i += 1) {
                rotors[i] = set.next();
                if (!set.hasNext()) {
                    throw new EnigmaException(
                            "wrong setting format");
                }
            }
            if (set.hasNext()) {
                setting = set.next();
                if (setting.length() != M.numRotors() - 1) {
                    throw new EnigmaException(
                            "unsupported setting format.");
                }
            }
            while (set.hasNext()) {
                plug += set.next();
            }
        }
        M.insertRotors(rotors);
        M.setRotors(setting);
        M.setPlugboard(new Permutation(plug, _alphabet));
    }

    /** Print MSG in groups of five (except that the last group may
     *  have fewer letters). */
    private void printMessageLine(String msg) {
        String result = "";
        while (msg.length() > 5) {
            result = result.concat(msg.substring(0, 5) + " ");
            msg = msg.substring(5);
        }
        result = result.concat(msg + "\n");
        _output.append(result);
        tr += result;
    }

    /** Test process().
     * @return tr message for testing. */
    public String testProcess() {
        process();
        return tr;
    }

    /** Alphabet used in this machine. */
    private Alphabet _alphabet;

    /** Source of input messages. */
    private Scanner _input;

    /** Source of machine configuration. */
    private Scanner _config;

    /** File for encoded/decoded messages. */
    private PrintStream _output;

    /** Test result. */
    private String tr = "";
}
