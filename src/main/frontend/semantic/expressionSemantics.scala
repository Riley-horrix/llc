package frontend

import ast._

object expressionSemantics {

  def analyseExpression(expr: Expr, globalScope: SymbolTable)(implicit
      errorBuilder: LLCError
  ): Unit = ???
}
