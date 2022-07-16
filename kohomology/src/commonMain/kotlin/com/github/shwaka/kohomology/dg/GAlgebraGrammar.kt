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

public class GAlgebraGrammar<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    private val gAlgebra: GAlgebra<D, B, S, V, M>,
    private val generators: List<Pair<String, GVector<D, B, S, V>>>
) : Grammar<GVectorOrZero<D, B, S, V>>() {
    private val zero by literalToken("zero")
    private val gen by regexToken("(" + this.generators.joinToString("|") { Regex.escape(it.first) } + ")")
    private val int by regexToken("-?\\d+")
    private val lpar by literalToken("(")
    private val rpar by literalToken(")")
    private val mul by literalToken("*")
    private val pow by literalToken("^")
    // val div by literalToken("/")
    private val minus by literalToken("-")
    private val plus by literalToken("+")
    private val ws by regexToken("\\s*", ignore = true)

    private fun Pair<Int, Int>.toScalar(): S {
        val (p, q) = this
        val field = this@GAlgebraGrammar.gAlgebra.field
        return field.context.run {
            p.toScalar() / q.toScalar()
        }
    }

    private val genParser: Parser<GVectorOrZero<D, B, S, V>> by (
        gen use {
            this@GAlgebraGrammar.generators.find { it.first == text }?.second
                ?: throw Exception("This can't happen!")
        }
        ) or (zero use { this@GAlgebraGrammar.gAlgebra.zeroGVector })
    private val intParser: Parser<Int> by int use { text.toInt() }
    private val minusParser: Parser<GVectorOrZero<D, B, S, V>> by (
        skip(minus) and parser(::termParser) map { this.gAlgebra.context.run { -it } }
        )
    private val termParser: Parser<GVectorOrZero<D, B, S, V>> by genParser or minusParser or
        (skip(lpar) and parser(::rootParser) and skip(rpar))
    private val powerParser: Parser<GVectorOrZero<D, B, S, V>> by (
        termParser and skip(pow) and intParser map { (gVector, n) ->
            this.gAlgebra.context.run { gVector.pow(n) }
        }
        ) or termParser
    // scalarParser cannot be Parser<S> since S is not a reified type parameter.
    // c.f. 'and' is declared as follows:
    //   fun <reified T> Parser<T>.and(other)
    private val scalarParser: Parser<Pair<Int, Int>> by int use { Pair(text.toInt(), 1) }
    private val scalarMulParser: Parser<GVectorOrZero<D, B, S, V>> by (
        scalarParser and skip(mul) and powerParser map { (n, gVector) ->
            this.gAlgebra.context.run { n.toScalar() * gVector }
        }
        ) or (
        powerParser and skip(mul) and scalarParser map { (gVector, n) ->
            this.gAlgebra.context.run { n.toScalar() * gVector }
        }
        ) or powerParser
    private val mulChain: Parser<GVectorOrZero<D, B, S, V>> by leftAssociative(scalarMulParser, mul) { a, _, b ->
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
