package frontend

import llc.ast._
import lexer._, implicits.implicitSymbol

import parsley.Parsley
import parsley.expr.{precedence, Ops, InfixL}
import parsley.expr.Prefix

object expressionParser {

  /** Parses a single expression. This parser uses Parsley's precedence
    * function, reducing to an atom.
    */
  lazy val parseExpression: Parsley[Expr] =
    precedence(atom)(
      Ops(Prefix)(Negate from negateSymbol),
      Ops(InfixL)(Multiplication from "*"),
      Ops(InfixL)(Addition from "+", Subtraction from "-")
    )

  /** Parses a single linal atom. */
  private lazy val atom: Parsley[Expr] =
    IntLiteral(integer64) |
      Ident(ident) |
      Character(character) |
      "(" ~> parseExpression <~ ")"
}
