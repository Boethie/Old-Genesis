package genesis.util.noise;

/*
 * Stores 3d noise data in one dimensional array, hiding confusing index calculation.
 */
public class NoiseBuffer3D {

    private final int sizeX;
    private final int sizeY;
    private final int sizeZ;

    private final int strideX;
    private final int strideZ;

    // the values in the array go first bottom-up, then increasing Z, then increasing X
    private double[] data;

    public NoiseBuffer3D(int sizeX, int sizeY, int sizeZ) {
        this.data = new double[sizeX * sizeY * sizeZ];
        //noinspection SuspiciousNameCombination
        this.strideX = sizeY;
        this.strideZ = strideX * sizeZ;

        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.sizeZ = sizeZ;
    }

    public double get(int x, int y, int z) {
        return data[idx(x, y, z)];
    }

    public void set(int x, int y, int z, double value) {
        data[idx(x, y, z)] = value;
    }

    private int idx(int x, int y, int z) {
        return y + z * strideX + x * strideZ;
    }

    public void fillFrom(BufferedNoiseProvider3D noise) {
        int idx = 0;
        for (int x = 0; x < sizeX; x++) {
            for (int z = 0; z < sizeZ; z++) {
                for (int y = 0; y < sizeY; y++) {
                    assert idx == idx(x, y, z);
                    this.data[idx] = noise.get(x, y, z);
                    idx++;
                }
            }
        }
    }
}
