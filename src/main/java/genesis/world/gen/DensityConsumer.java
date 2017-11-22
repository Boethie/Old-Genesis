package genesis.world.gen;

@FunctionalInterface
public interface DensityConsumer {

    void consume(double value, int blockX, int blockY, int blockZ);
}