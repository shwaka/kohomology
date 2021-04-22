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
    val gen by regexToken(run {
        "(" + this.generators.joinToString("|") { it.first } + ")"
    })
    val lpar by literalToken("(")
    val rpar by literalToken(")")
    val mul by literalToken("*")
    // val pow by literalToken("^")
    // val div by literalToken("/")
    val minus by literalToken("-")
    val plus by literalToken("+")
    val ws by regexToken("\\s*", ignore = true)

    val genParser: Parser<GVectorOrZero<B, S, V>> by gen use {
        this@GAlgebraGrammar.generators.find { it.first == text }?.second
            ?: throw Exception("This can't happen!")
    }
    val term: Parser<GVectorOrZero<B, S, V>> by genParser or
        (skip(minus) and parser(::term) map {
            when (it) {
                is ZeroGVector -> it
                is GVector -> this.gAlgebra.context.run { -it }
            }
        }) or
        (skip(lpar) and parser(::rootParser) and skip(rpar))

    val mulChain by leftAssociative(term, mul) { a, _, b ->
        when (a) {
            is ZeroGVector -> a
            is GVector -> when (b) {
                is ZeroGVector -> b
                is GVector -> this.gAlgebra.context.run { a * b }
            }
        }
    }
    val subSumChain by leftAssociative(mulChain, plus or minus use { type }) { a, op, b ->
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
