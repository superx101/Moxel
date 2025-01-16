package expression

import kotlin.math.PI
import kotlin.test.Test

fun replaceMultiple(expression: String, replacements: Map<String, String>): String {
    var result = expression
    replacements.forEach { (oldValue, newValue) ->
        result = result.replace(oldValue.toRegex(), "#TEMP_$oldValue#")
    }
    replacements.forEach { (oldValue, newValue) ->
        result = result.replace("#TEMP_$oldValue#", newValue)
    }
    return result
}

class ExpressionRotateTest {
    private fun rotateExpression(expression: String, matrix: Array<DoubleArray>): String {
        val newX = "(${matrix[0][0]}*x+${matrix[0][1]}*y+${matrix[0][2]}*z)"
        val newY = "(${matrix[1][0]}*x+${matrix[1][1]}*y+${matrix[1][2]}*z)"
        val newZ = "(${matrix[2][0]}*x+${matrix[2][1]}*y+${matrix[2][2]}*z)"

        val replacements = mapOf(
            "x" to newX,
            "y" to newY,
            "z" to newZ
        )
        return replaceMultiple(expression, replacements)
    }

    @Test
    fun testRotateExpression() {
        val expression = "x^2 + y^2 + (z * 2)^2 = 1"
        val rotationMatrix = RotationMatrix(0.0, PI / 4, 0.0).getMatrix()

        val rotatedExpression = rotateExpression(expression, rotationMatrix)

        println(rotatedExpression)
    }
}