package com.github.shwaka.kohomology.dg.parser

// Node of Abstract Syntax Tree
internal sealed interface ASTNode {
    object Zero : ASTNode
    data class Generator(val name: String) : ASTNode
    data class Fraction(val numerator: Int, val denominator: Int) : ASTNode
    data class UnaryMinus(val value: ASTNode) : ASTNode
    data class Power(val base: ASTNode, val exponent: Int) : ASTNode
    data class Multiply(val left: ASTNode, val right: ASTNode) : ASTNode
    data class Subtract(val left: ASTNode, val right: ASTNode) : ASTNode
    data class Sum(val left: ASTNode, val right: ASTNode) : ASTNode
}
