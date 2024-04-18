package frontend

import llc.ast._
import frontend.scope._

object functionSemantics {

  def analyseFunction(function: FunctionDefinition, scope: Scope) =
    function match {
      case FunctionDefinition(name, params, funcType, body) =>
    }
}
