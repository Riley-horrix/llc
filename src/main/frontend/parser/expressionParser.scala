package frontend

import ast._
import lexer._, implicits.implicitSymbol

import parsley.Parsley
import parsley.expr.{precedence, Ops, InfixL}
import parsley.expr.Prefix

object expressionParser {

  def brackets[A](arg: Parsley[A]): Parsley[A] = "(" ~> arg <~ ")"

  lazy val parseExpression: Parsley[Expr] =
    precedence(atom)(
      Ops(Prefix)(Negate from negateSymbol),
      Ops(InfixL)(Multiplication from "*"),
      Ops(InfixL)(Addition from "+", Subtraction from "-")
    )

  private lazy val atom: Parsley[Expr] =
    IntLiteral(integer64) |
      Ident(ident) |
      "(" ~> parseExpression <~ ")"
}
