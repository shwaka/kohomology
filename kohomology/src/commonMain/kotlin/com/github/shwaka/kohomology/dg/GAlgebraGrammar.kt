package com.github.shwaka.kohomology.dg

import com.github.h0tk3y.betterParse.combinators.and
import com.github.h0tk3y.betterParse.combinators.leftAssociative
import com.github.h0tk3y.betterParse.combinators.map
import com.github.h0tk3y.betterParse.combinators.or
import com.github.h0tk3y.betterParse.combinators.skip
import com.github.h0tk3y.betterParse.combinators.use
import com.github.h0tk3y.betterParse.grammar.Grammar
import com.github.h0tk3y.betterParse.grammar.parser
import com.github.h0tk3y.betterParse.lexer.literalToken
import com.github.h0tk3y.betterParse.lexer.regexToken
import com.github.h0tk3y.betterParse.parser.Parser
import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.vectsp.BasisName

class GAlgebraGrammar<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    private val gAlgebra: GAlgebra<B, D, S, V, M>,
    private val generators: List<Pair<String, GVector<B, D, S, V>>>
) : Grammar<GVectorOrZero<B, D, S, V>>() {
    val zero by literalToken("zero")
    val gen by regexToken("(" + this.generators.joinToString("|") { it.first } + ")")
    val int by regexToken("\\d+")
    val lpar by literalToken("(")
    val rpar by literalToken(")")
    val mul by literalToken("*")
    val pow by literalToken("^")
    // val div by literalToken("/")
    val minus by literalToken("-")
    val plus by literalToken("+")
    val ws by regexToken("\\s*", ignore = true)

    val genParser: Parser<GVectorOrZero<B, D, S, V>> by (
        gen use {
            this@GAlgebraGrammar.generators.find { it.first == text }?.second
                ?: throw Exception("This can't happen!")
        }
        ) or (zero use { this@GAlgebraGrammar.gAlgebra.zeroGVector })
    val intParser: Parser<Int> by int use { text.toInt() }
    val minusParser: Parser<GVectorOrZero<B, D, S, V>> by (
        skip(minus) and parser(::termParser) map { this.gAlgebra.context.run { -it } }
        )
    val termParser: Parser<GVectorOrZero<B, D, S, V>> by genParser or minusParser or
        (skip(lpar) and parser(::rootParser) and skip(rpar))
    val powerParser: Parser<GVectorOrZero<B, D, S, V>> by (
        termParser and skip(pow) and intParser map { (gVector, n) ->
            this.gAlgebra.context.run { gVector.pow(n) }
        }
        ) or termParser
    val scalarMulParser: Parser<GVectorOrZero<B, D, S, V>> by (
        intParser and skip(mul) and powerParser map { (n, gVector) ->
            this.gAlgebra.context.run { n * gVector }
        }
        ) or (
        powerParser and skip(mul) and intParser map { (gVector, n) ->
            this.gAlgebra.context.run { n * gVector }
        }
        ) or powerParser
    val mulChain: Parser<GVectorOrZero<B, D, S, V>> by leftAssociative(scalarMulParser, mul) { a, _, b ->
        this.gAlgebra.context.run { a * b }
    }
    val subSumChain: Parser<GVectorOrZero<B, D, S, V>> by leftAssociative(mulChain, plus or minus use { type }) { a, op, b ->
        this.gAlgebra.context.run {
            when (op) {
                plus -> a + b
                minus -> a - b
                else -> throw Exception("This can't happen!")
            }
        }
    }

    override val rootParser: Parser<GVectorOrZero<B, D, S, V>> by subSumChain
}
