#!/usr/bin/env kscript

// Usage:
// - kscript generate-favicon.kts
// - svgexport favicon.svg favicon180.png 180:180
//   # generate png of size 180x180
//   # Install svgexport via "npm install svgexport -g"
// - convert -density 300 favicon.svg -define icon:auto-resize favicon.ico

import java.io.File

data class Point(val x: Float, val y: Float) {
    override fun toString(): String {
        return "$x $y"
    }
}

fun polygon(points: List<Point>, fill: String): String {
    val pointsString = points.joinToString(", ")
    return """<polygon points="$pointsString" fill="$fill" stroke="$fill"/>"""
    // stroke="$fill" がないと、imagemagickで変換する際に黒い線が出てしまう
}

enum class Character {
    Lambda, V
}

fun getPoints(
    upperLeft: Point,
    width: Float,
    height: Float,
    lineWidth: Float,
    character: Character,
): List<Point> {
    val yCoord = height * (width - 2 * lineWidth) / (width - lineWidth)
    return listOf(
        Point(lineWidth, height),
        Point(0f, height),
        Point((width - lineWidth) / 2, 0f),
        Point((width + lineWidth) / 2, 0f),
        Point(width, height),
        Point(width - lineWidth, height),
        Point(width / 2, height - yCoord),
    ).map {
        Point(it.x + upperLeft.x, it.y + upperLeft.y)
    }.map {
        when (character) {
            Character.Lambda -> it
            Character.V -> Point(it.x, height + upperLeft.y * 2 - it.y)
        }
    }
}

fun main() {
    val lineWidth = 18f
    // You can edit margins freely since they just add margin to the image.
    val verticalMargin = 0f
    val horizontalMargin = 0f
    // Two characters (Λ and V) are drawn in a box of size 100x100.
    // Do not edit paddings since they have effects on the shape of characters.
    val verticalPadding = 5f
    val horizontalPadding = 5f
    val characterWidth = 50f
    val pointsLambda = getPoints(
        Point(horizontalMargin + horizontalPadding, verticalMargin + verticalPadding),
        width = characterWidth,
        height = 100f - 2 * verticalPadding,
        lineWidth = lineWidth,
        character = Character.Lambda)
    val pointsV = getPoints(
        Point(
            100f + horizontalMargin - horizontalPadding - characterWidth,
            verticalMargin + verticalPadding
        ),
        width = characterWidth,
        height = 100f - 2 * verticalPadding,
        lineWidth = lineWidth,
        character = Character.V)
    val viewBoxWidth = 100f + horizontalMargin * 2
    val viewBoxHeight = 100f + verticalMargin * 2
    val svg = """
        <?xml version="1.0"?>
        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0, 0, $viewBoxWidth, $viewBoxHeight" width="256" height="256">
          ${polygon(pointsLambda, "#c7b83c")}
          ${polygon(pointsV, "#7e6ca8")}
        </svg>
    """.trimIndent()
    writeToFile(svg)
}

fun writeToFile(text: String) {
    val filename = "img/favicon.svg"
    File(filename).writeText(text)
}

main()
