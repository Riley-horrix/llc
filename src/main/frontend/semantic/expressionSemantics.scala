package frontend

import llc.ast._
import symbolTable._
import symbolTableError._
import llcerror._
import llc.typeSemantics.commonType
import frontend.llcerror.SemanticErrorBuilder

object expressionSemantics {

  def analyseExpression(expr: Expr, globalScope: SymbolTable)(implicit
      errorBuilder: SemanticErrorBuilder
  ): Option[ActualType] = expr match {
    case arb: ArithBinop => analyseArith(arb, globalScope)
    case un: Unop        => analyseUnop(un, globalScope)
    case atom: Atom      => analyseAtom(atom, globalScope)
  }

  private def analyseArith(
      arithBinop: ArithBinop,
      globalScope: SymbolTable
  )(implicit
      errorBuilder: SemanticErrorBuilder
  ): Option[ActualType] =
    analyseExpression(arithBinop.exprL, globalScope) match {
      case Some(lType) =>
        analyseExpression(arithBinop.exprR, globalScope) match {
          case Some(rType) => commonType(lType, rType, arithBinop)
          case err         => err
        }
      case err => err
    }

  private def analyseUnop(
      unop: Unop,
      globalScope: SymbolTable
  )(implicit
      errorBuilder: SemanticErrorBuilder
  ): Option[ActualType] = analyseExpression(unop.expr, globalScope) match {
    case Some(varType) => commonType(varType, unop)
    case err           => err
  }

  private def analyseAtom(
      atom: Atom,
      globalScope: SymbolTable
  )(implicit
      errorBuilder: SemanticErrorBuilder
  ): Option[ActualType] = atom match {
    case _: Character => Some(CharType)

    case variable: Ident =>
      globalScope.variableType(variable) match {
        case Left(err) =>
          errorBuilder.newError(
            new StringBuilder("Variable not declared"),
            variable.pos
          ); None // TODO: Better msg
        case Right(varType) => Some(varType.baseType)
      }

    case _: IntLiteral => Some(IntType)
  }
}
