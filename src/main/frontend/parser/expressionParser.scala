package frontend

import ast._
import lexer._, implicits.implicitSymbol

import parsley.Parsley
import parsley.expr.precedence
import parsley.expr.Ops
import parsley.expr.InfixL

object expressionParser {
    lazy val parseExpression: Parsley[Expr] = 
        precedence(atom)(
            Ops(InfixL)(Addition from "+")
        )
    private lazy val atom: Parsley[Expr] = 
        Int32Literal(integer32)
}