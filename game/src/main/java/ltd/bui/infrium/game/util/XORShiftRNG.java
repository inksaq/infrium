package ltd.bui.infrium.game.util;

import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

public class XORShiftRNG extends Random {
    // Lock to prevent concurrent modification of the RNG's internal state.
    private final ReentrantLock lock = new ReentrantLock();

    private final byte[] seed;

    // Previously used an array for state but using separate fields proved to be
    // faster.
    private int state1;
    private int state2;
    private int state3;
    private int state4;
    private int state5;

    /**
     * Creates an RNG and seeds it with the specified seed data.
     *
     * @param seed
     *            The seed data used to initialise the RNG.
     */
    public XORShiftRNG() {
        this.seed = new byte[SEED_SIZE_BYTES];
        SEED_GENERATOR.nextBytes(seed);
        int[] state = convertBytesToInts(seed);
        this.state1 = state[0];
        this.state2 = state[1];
        this.state3 = state[2];
        this.state4 = state[3];
        this.state5 = state[4];
    }

    /**
     * {@inheritDoc}
     */
    public byte[] getSeed() {
        return seed.clone();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected int next(int bits) {
        try {
            lock.lock();
            int t = (state1 ^ (state1 >> 7));
            state1 = state2;
            state2 = state3;
            state3 = state4;
            state4 = state5;
            state5 = (state5 ^ (state5 << 6)) ^ (t ^ (t << 13));
            int value = (state2 + state2 + 1) * state5;
            return value >>> (32 - bits);
        } finally {
            lock.unlock();
        }
    }

    // Mask for casting a byte to an int, bit-by-bit (with
    // bitwise AND) with no special consideration for the sign bit.
    private static final int BITWISE_BYTE_TO_INT = 0x000000FF;

    private static Random SEED_GENERATOR = new Random();
    private static final int SEED_SIZE_BYTES = 20; // Needs 5 32-bit integers.
    private static final long serialVersionUID = -1843001897066722618L;

    /**
     * Take four bytes from the specified position in the specified block and
     * convert them into a 32-bit int, using the big-endian convention.
     *
     * @param bytes
     *            The data to read from.
     * @param offset
     *            The position to start reading the 4-byte int from.
     * @return The 32-bit integer represented by the four bytes.
     */
    public static int convertBytesToInt(byte[] bytes, int offset) {
        return (BITWISE_BYTE_TO_INT & bytes[offset + 3]) | ((BITWISE_BYTE_TO_INT & bytes[offset + 2]) << 8)
                | ((BITWISE_BYTE_TO_INT & bytes[offset + 1]) << 16) | ((BITWISE_BYTE_TO_INT & bytes[offset]) << 24);
    }

    /**
     * Convert an array of bytes into an array of ints. 4 bytes from the input
     * data map to a single int in the output data.
     *
     * @param bytes
     *            The data to read from.
     * @return An array of 32-bit integers constructed from the data.
     * @since 1.1
     */
    public static int[] convertBytesToInts(byte[] bytes) {
        if (bytes.length % 4 != 0) {
            throw new IllegalArgumentException("Number of input bytes must be a multiple of 4.");
        }
        int[] ints = new int[bytes.length / 4];
        for (int i = 0; i < ints.length; i++) {
            ints[i] = convertBytesToInt(bytes, i * 4);
        }
        return ints;
    }
}
