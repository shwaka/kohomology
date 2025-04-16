package com.github.shwaka.kohomology.dg.parser

import com.github.h0tk3y.betterParse.combinators.and
import com.github.h0tk3y.betterParse.combinators.leftAssociative
import com.github.h0tk3y.betterParse.combinators.map
import com.github.h0tk3y.betterParse.combinators.or
import com.github.h0tk3y.betterParse.combinators.skip
import com.github.h0tk3y.betterParse.combinators.use
import com.github.h0tk3y.betterParse.grammar.Grammar
import com.github.h0tk3y.betterParse.grammar.parseToEnd
import com.github.h0tk3y.betterParse.grammar.parser
import com.github.h0tk3y.betterParse.lexer.literalToken
import com.github.h0tk3y.betterParse.parser.Parser
import com.github.shwaka.kohomology.util.Identifier
import com.github.shwaka.kohomology.util.PartialIdentifier

private object GAlgebraElementASTGrammarInternal : Grammar<ASTNode>() {
    // Previously, "0" could not be used as a scalar and "zero" is used for such purpose.
    // Currently, "zero" is unnecessary but left here for compatibility reason.
    private val zero by literalToken("zero")
    private val id by charCategoryToken(
        Identifier.firstCharCategoryList,
        PartialIdentifier.charCategoryList,
    )
    private val nat by regexToken("\\d+")
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

    private val idParser: Parser<ASTNode> by
    (id use { ASTNode.Identifier(text) }) or
        (zero use { ASTNode.Zero })

    private val natParser: Parser<ASTNode.NatNumber> by
    nat use { ASTNode.NatNumber(text.toInt()) }

    private val parenParser: Parser<ASTNode> by
    skip(lpar) and parser(::rootParser) and skip(rpar)

    private val minusParser: Parser<ASTNode.UnaryMinus> by
    (skip(minus) and parenParser map { ASTNode.UnaryMinus(it) }) or
        (skip(minus) and parser(::mulDivChain) map { ASTNode.UnaryMinus(it) })

    private val termParser: Parser<ASTNode> by
    natParser or idParser or minusParser or parenParser

    private val powerParser: Parser<ASTNode> by
    (termParser and skip(pow) and nat map { (node, n) -> ASTNode.Power(node, n.text.toInt()) }) or
        termParser

    private val mulDivChain: Parser<ASTNode> by
    leftAssociative(powerParser, mul or div use { type }) { left, op, right ->
        when (op) {
            mul -> ASTNode.Multiply(left, right)
            div -> ASTNode.Divide(left, right)
            else -> throw Exception("This can't happen!")
        }
    }

    private val subSumChain: Parser<ASTNode> by
    leftAssociative(mulDivChain, plus or minus use { type }) { left, op, right ->
        when (op) {
            plus -> ASTNode.Sum(left, right)
            minus -> ASTNode.Subtract(left, right)
            else -> throw Exception("This can't happen!")
        }
    }

    override val rootParser: Parser<ASTNode> by subSumChain
}

internal object GAlgebraElementASTGrammar {
    fun parseToEnd(input: String): ASTNode {
        return GAlgebraElementASTGrammarInternal.parseToEnd(input)
    }
}
