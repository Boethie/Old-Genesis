package genesis.util.noise

class UnbufferedNoiseProvider3D(
        private val source: DoubleTernaryOperator,
        private val octaves: Int = 1,
        private val frequencyX: Double = 1.0, private val frequencyY: Double = 1.0, private val frequencyZ: Double = 1.0) : DoubleTernaryOperator {

    private val maxValInv = 1.0 / (2 - Math.pow(0.5, octaves - 1.0))

    override fun get(x: Double, y: Double, z: Double): Double {
        var currX = x * frequencyX
        var currY = y * frequencyY
        var currZ = z * frequencyZ
        var factor = 1.0

        var v = 0.0
        for (i in 0 until octaves) {
            v += source.get(currX, currY, currZ) * factor
            factor *= 0.5
            currX *= 2
            currY *= 2
            currZ *= 2
        }
        return v * maxValInv
    }

    fun toBuffered() = BufferedNoiseProvider3D.NoiseSource { x, y, z -> this.get(x.toDouble(), y.toDouble(), z.toDouble()) }
}