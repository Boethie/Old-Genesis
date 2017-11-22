package genesis.util.noise;

@FunctionalInterface
public interface Int3DoubleToDoubleFunction {
    double applyAsDouble(int x, int y, int z, double d);
}
