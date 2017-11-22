package genesis.util.noise;

public class NoiseBuffer2D {

    private final int sizeX;
    private final int sizeZ;

    private final int strideZ;

    // the values in the array go first bottom-up, then increasing Z, then increasing X
    private double[] data;

    public NoiseBuffer2D(int sizeX, int sizeZ) {
        this.data = new double[sizeX * sizeZ];
        //noinspection SuspiciousNameCombination
        this.strideZ = sizeX;

        this.sizeX = sizeX;
        this.sizeZ = sizeZ;
    }

    public double get(int x, int z) {
        return data[idx(x, z)];
    }

    public void set(int x, int z, double value) {
        data[idx(x, z)] = value;
    }

    private int idx(int x, int z) {
        return z + x * strideZ;
    }
}
