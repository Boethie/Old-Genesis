package genesis.util.noise

import net.minecraft.util.math.MathHelper
import java.util.function.DoubleBinaryOperator
import java.util.function.DoubleUnaryOperator

/**
 * A 3d buffered noise generator. Supports basic arithmetic operations, which affect the result of filling an array. All operators return a new
 * noise provider without modifying existing one.
 *
 * Implementation note: the positions given to fill are supposed to be propagated as-is without any modifications. Modifying them may work but it
 * likely to negatively affect performance.
 */
abstract class BufferedNoiseProvider3D {
    private var currentStartX: Int = Int.MIN_VALUE
    private var currentStartZ: Int = Int.MIN_VALUE

    operator fun plus(value: BufferedNoiseProvider3D): BufferedNoiseProvider3D =
            NoiseBinaryOperator(this, value, op = DoubleBinaryOperator { a, b -> a + b })

    operator fun times(value: BufferedNoiseProvider3D): BufferedNoiseProvider3D =
            NoiseBinaryOperator(this, value, op = DoubleBinaryOperator { a, b -> a * b })

    operator fun div(value: BufferedNoiseProvider3D): BufferedNoiseProvider3D =
            NoiseBinaryOperator(this, value, op = DoubleBinaryOperator { a, b -> a / b })

    operator fun minus(value: BufferedNoiseProvider3D): BufferedNoiseProvider3D =
            NoiseBinaryOperator(this, value, op = DoubleBinaryOperator { a, b -> a - b })

    operator fun plus(value: Double): BufferedNoiseProvider3D =
            NoiseUnaryOperator(this, op = DoubleUnaryOperator { a -> a + value })

    operator fun times(value: Double): BufferedNoiseProvider3D =
            NoiseUnaryOperator(this, op = DoubleUnaryOperator { a -> a * value })

    operator fun div(value: Double): BufferedNoiseProvider3D =
            NoiseUnaryOperator(this, op = DoubleUnaryOperator { a -> a / value })

    operator fun minus(value: Double): BufferedNoiseProvider3D =
            NoiseUnaryOperator(this, op = DoubleUnaryOperator { a -> a - value })

    operator fun invoke(buffer: NoiseBuffer3D, startX: Int, startY: Int, startZ: Int) {
        fill(buffer, startX, startY, startZ)
    }

    fun clamp(min: Double, max: Double) = NoiseUnaryOperator(this, op = DoubleUnaryOperator { a -> MathHelper.clamp(a, min, max) })

    inline infix fun op(crossinline op: (Int, Int, Int, Double) -> Double) = opImpl(Int3DoubleToDoubleFunction({ x, y, z, v -> op(x, y, z, v) }))

    fun opImpl(op: Int3DoubleToDoubleFunction) = NoiseXYZUnaryOperator(this, op)

    fun buffered(sizeX: Int, sizeY: Int, sizeZ: Int) = NoiseBuffer(this, sizeX, sizeY, sizeZ)

    fun fill(buffer: NoiseBuffer3D, startX: Int, startY: Int, startZ: Int) {
        fillPrepare(startX, startY, startZ)
        doFill(buffer)
    }

    protected open fun doFill(buffer: NoiseBuffer3D) {
        buffer.fillFrom(this)
    }

    protected abstract fun get(x: Int, y: Int, z: Int): Double

    protected fun fillPrepare(startX: Int, startY: Int, startZ: Int) {
        // this is so that a provider used many times in the graph won't be pre-filled many times
        if (currentStartX != startX || currentStartZ != startZ) {
            fillPrepareImpl(startX, startY, startZ)
            currentStartX = startX
            currentStartZ = startZ
        }
    }

    protected open fun fillPrepareImpl(startX: Int, startY: Int, startZ: Int) {
        // NO-OP by default
    }

    class NoiseSource(private val src: (Int, Int, Int) -> Double): BufferedNoiseProvider3D() {

        private var startX: Int = 0
        private var startY: Int = 0
        private var startZ: Int = 0

        override fun get(x: Int, y: Int, z: Int) = src(x + startX, y + startY, z + startZ)

        override fun fillPrepareImpl(startX: Int, startY: Int, startZ: Int) {
            this.startX = startX
            this.startY = startY
            this.startZ = startZ
        }
    }

    class NoiseBinaryOperator(
            private val noise1: BufferedNoiseProvider3D,
            private val noise2: BufferedNoiseProvider3D,
            private val op: DoubleBinaryOperator): BufferedNoiseProvider3D() {

        override fun get(x: Int, y: Int, z: Int) =
                op.applyAsDouble(noise1.get(x, y, z), noise2.get(x, y, z))

        override fun fillPrepareImpl(startX: Int, startY: Int, startZ: Int) {
            noise1.fillPrepare(startX, startY, startZ)
            noise2.fillPrepare(startX, startY, startZ)
        }
    }

    class NoiseUnaryOperator(
            private val noise1: BufferedNoiseProvider3D,
            private val op: DoubleUnaryOperator) : BufferedNoiseProvider3D() {
        override fun get(x: Int, y: Int, z: Int) =
                op.applyAsDouble(noise1.get(x, y, z))

        override fun fillPrepareImpl(startX: Int, startY: Int, startZ: Int) {
            noise1.fillPrepare(startX, startY, startZ)
        }
    }

    class NoiseXYZUnaryOperator(
            private val noise1: BufferedNoiseProvider3D,
            private val op: Int3DoubleToDoubleFunction) : BufferedNoiseProvider3D() {
        private var startX: Int = 0
        private var startY: Int = 0
        private var startZ: Int = 0

        override fun get(x: Int, y: Int, z: Int) =
                op.applyAsDouble(x + startX, y + startY, z + startZ, noise1.get(x, y, z))

        override fun fillPrepareImpl(startX: Int, startY: Int, startZ: Int) {
            noise1.fillPrepare(startX, startY, startZ)
            this.startX = startX
            this.startY = startY
            this.startZ = startZ
        }
    }

    class NoiseBuffer(private val src: BufferedNoiseProvider3D, xSize: Int, ySize: Int, zSize: Int) : BufferedNoiseProvider3D() {

        private var data: NoiseBuffer3D = NoiseBuffer3D(xSize, ySize, zSize)

        override fun get(x: Int, y: Int, z: Int) = data[x, y, z]

        override fun fillPrepareImpl(startX: Int, startY: Int, startZ: Int) {
            src.fillPrepare(startX, startY, startZ)
            src.doFill(data)
        }
    }
}