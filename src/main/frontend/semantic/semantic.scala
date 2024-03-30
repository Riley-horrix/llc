package frontend

import llc.ast._
import llcerror._
import scope._

import scala.annotation.tailrec

object semantic {

  /** Run semantic analysis on a program, either returning a semanitc error, or
    * scoping and symbol table information. Returns an LLCError object and a
    * scope.
    */
  def analyse(
      program: LinalFile,
      scope: Scope = new Scope()
  ): (LLCError, Scope) = program match {
    case LinalFile(programElements) =>
      (analyseElements(programElements, scope, new LLCError()), scope)
  }

  /** Recursively analyse elements from the elements list, passing in the scope
    * for each.
    */
  @tailrec
  private def analyseElements(
      elements: List[ProgramElement],
      scope: Scope,
      errBuilder: LLCError
  ): LLCError = elements match {
    case Nil => errBuilder
    case head :: tail => {
      analyseElement(head, scope, errBuilder)
      analyseElements(tail, scope, errBuilder)
    }
  }

  /** Semantically analyse a single program element in a given scope. */
  private def analyseElement(
      element: ProgramElement,
      scope: Scope,
      errBuilder: LLCError
  ): Unit = element match {
    case FunctionDefinition(name, params, funcType, body) => ???
    case VariableDefinition(varType, name, value)         => ???
    case LibInclude(include)                              => ???
    case LocalInclude(include)                            => ???
  }
}
