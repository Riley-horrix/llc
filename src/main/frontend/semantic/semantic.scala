package frontend

import llc.ast._
import llcerror._
import scope._
import scala.annotation.tailrec

object semantic {

  /** Run semantic analysis on a program, either returning a semanitc error, or
    * scoping information.
    */
  def analyse(program: LinalFile): Either[LLCError, Scope] = program match {
    case LinalFile(programElements) => ???
  }

  @tailrec
  private def analyseElements(
      elements: List[ProgramElement],
      scope: Scope
  ): Either[LLCError, Scope] = elements match {
    case Nil => Right(scope)
    case head :: tail =>
      analyseElement(head, scope) match {
        case None      => analyseElements(tail, scope)
        case Some(err) => Left(err)
      }
  }

  /** Semantically analyse a single program element in a given scope. */
  private def analyseElement(
      element: ProgramElement,
      scope: Scope
  ): Option[LLCError] = element match {
    case FunctionDefinition(name, params, funcType, body) => ???
    case VariableDefinition(varType, name, value)         => ???
    case LibInclude(include)                              => ???
    case LocalInclude(include)                            => ???
  }
}
