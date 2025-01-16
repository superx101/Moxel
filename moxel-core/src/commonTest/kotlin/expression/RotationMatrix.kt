package expression

import kotlin.math.cos;
import kotlin.math.sin;

class RotationMatrix(
    private val alpha: Double,
    private val beta: Double,
    private val gamma: Double
) {
    fun getMatrix(): Array<DoubleArray> {
        val rzAlpha = arrayOf(
            doubleArrayOf(cos(alpha), -sin(alpha), 0.0),
            doubleArrayOf(sin(alpha), cos(alpha), 0.0),
            doubleArrayOf(0.0, 0.0, 1.0)
        )

        val rxBeta = arrayOf(
            doubleArrayOf(1.0, 0.0, 0.0),
            doubleArrayOf(0.0, cos(beta), -sin(beta)),
            doubleArrayOf(0.0, sin(beta), cos(beta))
        )

        val rzGamma = arrayOf(
            doubleArrayOf(cos(gamma), -sin(gamma), 0.0),
            doubleArrayOf(sin(gamma), cos(gamma), 0.0),
            doubleArrayOf(0.0, 0.0, 1.0)
        )

        val tempMatrix = multiplyMatrices(rzAlpha, rxBeta)
        return multiplyMatrices(tempMatrix, rzGamma)
    }

    private fun multiplyMatrices(a: Array<DoubleArray>, b: Array<DoubleArray>): Array<DoubleArray> {
        val result = Array(3) { DoubleArray(3) }
        for (i in 0..2) {
            for (j in 0..2) {
                result[i][j] = 0.0
                for (k in 0..2) {
                    result[i][j] += a[i][k] * b[k][j]
                }
            }
        }
        return result
    }
}