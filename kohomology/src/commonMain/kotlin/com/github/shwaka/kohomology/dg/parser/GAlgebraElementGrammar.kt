package com.github.shwaka.kohomology.dg.parser

import com.github.h0tk3y.betterParse.combinators.and
import com.github.h0tk3y.betterParse.combinators.leftAssociative
import com.github.h0tk3y.betterParse.combinators.map
import com.github.h0tk3y.betterParse.combinators.or
import com.github.h0tk3y.betterParse.combinators.skip
import com.github.h0tk3y.betterParse.combinators.use
import com.github.h0tk3y.betterParse.grammar.Grammar
import com.github.h0tk3y.betterParse.grammar.parser
import com.github.h0tk3y.betterParse.lexer.literalToken
import com.github.h0tk3y.betterParse.parser.Parser
import com.github.shwaka.kohomology.dg.GAlgebra
import com.github.shwaka.kohomology.dg.GVector
import com.github.shwaka.kohomology.dg.GVectorOrZero
import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.vectsp.BasisName

internal class GAlgebraElementGrammar<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    private val gAlgebra: GAlgebra<D, B, S, V, M>,
    private val generators: List<Pair<String, GVector<D, B, S, V>>>
) : Grammar<GVectorOrZero<D, B, S, V>>() {
    // Previously, "0" could not be used as a scalar and "zero" is used for such purpose.
    // Currently, "zero" is unnecessary but left here for compatibility reason.
    private val zero by literalToken("zero")
    private val gen by literalListToken(this.generators.map { it.first })
    private val int by regexToken("\\d+")
    private val lpar by literalToken("(")
    private val rpar by literalToken(")")
    private val mul by literalToken("*")
    private val pow by literalToken("^")
    private val div by literalToken("/")
    private val minus by literalToken("-")
    private val plus by literalToken("+")

    // ws is not used as a property,
    // but Grammar registers it to the list of parsers by observing "by"-delegations.
    @Suppress("UNUSED")
    private val ws by regexToken("\\s*", ignore = true)

    private fun Pair<Int, Int>.toScalar(): S {
        val (p, q) = this
        val field = this@GAlgebraElementGrammar.gAlgebra.field
        return field.context.run {
            fromIntPair(p, q)
        }
    }

    private fun Pair<Int, Int>.toGVector(): GVectorOrZero<D, B, S, V> {
        val numerator = this.first
        if (numerator == 0) {
            return this@GAlgebraElementGrammar.gAlgebra.zeroGVector
        }
        return this@GAlgebraElementGrammar.gAlgebra.context.run {
            unit * this@toGVector.toScalar()
        }
    }

    private val genParser: Parser<GVectorOrZero<D, B, S, V>> by (
        gen use {
            this@GAlgebraElementGrammar.generators.find { it.first == text }?.second
                ?: throw Exception(
                    "This can't happen! " +
                        "$text not found in generators (${this@GAlgebraElementGrammar.generators})"
                )
        }
        ) or (zero use { this@GAlgebraElementGrammar.gAlgebra.zeroGVector })
    private val intParser: Parser<Int> by int use { text.toInt() }
    private val parenParser: Parser<GVectorOrZero<D, B, S, V>> by (
        skip(lpar) and parser(::rootParser) and skip(rpar)
        )
    private val minusParser: Parser<GVectorOrZero<D, B, S, V>> by (
        skip(minus) and parenParser map { this.gAlgebra.context.run { -it } }
        ) or (
        skip(minus) and parser(::mulChain) map { this.gAlgebra.context.run { -it } }
        )
    // The order to take 'or' is important in scalarParser.
    // In "1/2*x", the whole "1/2" should be considered as a scalar.
    // If 'or' is taken in the other order, only "1" is considered as a scalar
    // and a ParseException is thrown at "/".
    private val scalarParser: Parser<GVectorOrZero<D, B, S, V>> by (
        intParser and skip(div) and intParser map { (p, q) -> Pair(p, q).toGVector() }
        ) or (
        intParser map { n -> Pair(n, 1).toGVector() }
        )
    private val termParser: Parser<GVectorOrZero<D, B, S, V>> by (
        scalarParser or genParser or minusParser or parenParser
        )
    private val powerParser: Parser<GVectorOrZero<D, B, S, V>> by (
        termParser and skip(pow) and intParser map { (gVector, n) ->
            this.gAlgebra.context.run { gVector.pow(n) }
        }
        ) or termParser
    private val mulChain: Parser<GVectorOrZero<D, B, S, V>> by leftAssociative(powerParser, mul) { a, _, b ->
        this.gAlgebra.context.run { a * b }
    }
    private val subSumChain: Parser<GVectorOrZero<D, B, S, V>> by leftAssociative(mulChain, plus or minus use { type }) { a, op, b ->
        this.gAlgebra.context.run {
            when (op) {
                plus -> a + b
                minus -> a - b
                else -> throw Exception("This can't happen!")
            }
        }
    }

    override val rootParser: Parser<GVectorOrZero<D, B, S, V>> by subSumChain
}
