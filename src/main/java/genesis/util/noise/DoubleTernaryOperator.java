package genesis.util.noise;

@FunctionalInterface
public interface DoubleTernaryOperator {
    double get(double x, double y, double z);
}
