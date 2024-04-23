package frontend

import llc.ast._
import symbolTable._
import symbolTableError._
import llcerror._

object expressionSemantics {

  def analyseExpression(expr: Expr, globalScope: SymbolTable)(implicit
      errorBuilder: LLCError
  ): Type = expr match {
    case arb: ArithBinop => analyseArith(arb, globalScope)
    case un: Unop        => analyseUnop(un, globalScope)
    case atom: Atom      => analyseAtom(atom, globalScope)
  }

  private def analyseArith(
      arithBinop: ArithBinop,
      globalScope: SymbolTable
  ): Type = arithBinop match {
    case Addition(exprL, exprR)       => ???
    case Subtraction(exprL, exprR)    => ???
    case Multiplication(exprL, exprR) => ???
  }

  private def analyseUnop(
      unop: Unop,
      globalScope: SymbolTable
  ): Type = unop match {
    case Negate(expr) => ???
  }

  private def analyseAtom(
      atom: Atom,
      globalScope: SymbolTable
  ): Type = ???
}
