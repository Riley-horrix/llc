package frontend

import llc.ast._
import scope._
import symbolTable._

object functionSemantics {

  def analyseFunction(function: FunctionDefinition, scope: SymbolTable) =
    function match {
      case FunctionDefinition(name, params, funcType, body) => ???
    }
}
