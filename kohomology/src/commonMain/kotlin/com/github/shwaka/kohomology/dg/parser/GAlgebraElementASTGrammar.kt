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

internal class GAlgebraElementASTGrammar(val generators: List<String>) : Grammar<ASTNode>() {
    // Previously, "0" could not be used as a scalar and "zero" is used for such purpose.
    // Currently, "zero" is unnecessary but left here for compatibility reason.
    private val zero by literalToken("zero")
    private val gen by literalListToken(generators)
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

    private val genParser: Parser<ASTNode>
        by (gen use { ASTNode.Generator(text) }) or
            (zero use { ASTNode.Zero })
    private val intParser: Parser<Int>
        by int use { text.toInt() }
    private val parenParser: Parser<ASTNode>
        by skip(lpar) and parser(::rootParser) and skip(rpar)
    private val minusParser: Parser<ASTNode.UnaryMinus>
        by (skip(minus) and parenParser map { ASTNode.UnaryMinus(it) }) or
            (skip(minus) and parser(::mulChain) map { ASTNode.UnaryMinus(it) })
    // The order to take 'or' is important in scalarParser.
    // In "1/2*x", the whole "1/2" should be considered as a scalar.
    // If 'or' is taken in the other order, only "1" is considered as a scalar
    // and a ParseException is thrown at "/".
    private val fractionParser: Parser<ASTNode.Fraction>
        by (intParser and skip(div) and intParser map { (p, q) -> ASTNode.Fraction(p, q) }) or
            (intParser map { n -> ASTNode.Fraction(n, 1) })
    private val termParser: Parser<ASTNode>
        by fractionParser or genParser or minusParser or parenParser
    private val powerParser: Parser<ASTNode>
        by (termParser and skip(pow) and intParser map { (node, n) -> ASTNode.Power(node, n) }) or
            termParser
    private val mulChain: Parser<ASTNode>
        by leftAssociative(powerParser, mul) { left, _, right ->
            ASTNode.Multiply(left, right)
        }
    private val subSumChain: Parser<ASTNode>
        by leftAssociative(mulChain, plus or minus use { type }) { left, op, right ->
            when (op){
                plus -> ASTNode.Sum(left, right)
                minus -> ASTNode.Subtract(left, right)
                else -> throw Exception("This can't happen!")
            }
        }

    override val rootParser: Parser<ASTNode> by subSumChain
}
