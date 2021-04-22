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
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.vectsp.BasisName

class GAlgebraGrammar<B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    private val gAlgebra: GAlgebra<B, S, V, M>,
    private val generators: List<Pair<String, GVector<B, S, V>>>
) : Grammar<GVectorOrZero<B, S, V>>() {
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

    val genParser: Parser<GVectorOrZero<B, S, V>> by gen use {
        this@GAlgebraGrammar.generators.find { it.first == text }?.second
            ?: throw Exception("This can't happen!")
    }
    val intParser: Parser<Int> by int use { text.toInt() }
    val minusParser: Parser<GVectorOrZero<B, S, V>> by (
        skip(minus) and parser(::termParser) map {
            when (it) {
                is ZeroGVector -> it
                is GVector -> this.gAlgebra.context.run { -it }
            }
        }
        )
    val termParser: Parser<GVectorOrZero<B, S, V>> by genParser or minusParser or
        (skip(lpar) and parser(::rootParser) and skip(rpar))
    val powerParser: Parser<GVectorOrZero<B, S, V>> by (
        termParser and skip(pow) and intParser map { (gVector, n) ->
            if (n == 0)
                this.gAlgebra.unit
            else
                when (gVector) {
                    is ZeroGVector -> gVector
                    is GVector -> this.gAlgebra.context.run { gVector.pow(n) }
                }
        }
        ) or termParser
    val scalarMulParser: Parser<GVectorOrZero<B, S, V>> by powerParser or (
        intParser and skip(mul) and powerParser map { (n, gVector) ->
            when (gVector) {
                is ZeroGVector -> gVector
                is GVector -> this.gAlgebra.context.run { n * gVector }
            }
        }
        )
    val mulChain: Parser<GVectorOrZero<B, S, V>> by leftAssociative(scalarMulParser, mul) { a, _, b ->
        when (a) {
            is ZeroGVector -> a
            is GVector -> when (b) {
                is ZeroGVector -> b
                is GVector -> this.gAlgebra.context.run { a * b }
            }
        }
    }
    val subSumChain: Parser<GVectorOrZero<B, S, V>> by leftAssociative(mulChain, plus or minus use { type }) { a, op, b ->
        when (b) {
            is ZeroGVector -> a
            is GVector -> this.gAlgebra.context.run {
                when (a) {
                    is ZeroGVector -> if (op == plus) b else -b
                    is GVector -> if (op == plus) a + b else a - b
                }
            }
        }
    }

    override val rootParser: Parser<GVectorOrZero<B, S, V>> by subSumChain
}
