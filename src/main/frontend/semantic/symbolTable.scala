package frontend

import llc.ast._
import llcerror._
import scope._

import scala.collection.mutable
import scala.annotation.tailrec

object symbolTableError extends Enumeration {
  type symbolTableError = Value
  val VARIABLE_NOT_DECLARED, VARIABLE_ALREADY_DECLARED = Value
}

import symbolTableError._

object symbolTable {

  /** Provides mappings for every variable declared in this scope. */
  class SymbolTable {
    private var table: List[mutable.Map[Int, Type]] = List.empty

    /** Sets a variables type to the identifier mapping. If the variable already
      * has a mapping in the current scope, it returns false, otherwise returns
      * true.
      */
    def setIdentType(uid: Int, varType: Type): Boolean =
      table.headOption match {
        case None => LLCError.exitGracefully(INTERNAL_ERROR)
        case Some(mapping) =>
          mapping.put(uid, varType) match {
            case None        => true
            case Some(value) => false
          }
      }

    def pushNewScope(): SymbolTable = {
      table = mutable.Map[Int, Type]() :: table
      this
    }

    def popCurrentScope(): Scope = {
      val entry = table.headOption match {
        case None        => LLCError.exitGracefully(INTERNAL_ERROR)
        case Some(value) => value
      }
      table = table.tail
      new Scope(entry.toMap)
    }

    /** Put a variable declaration into the typemap, returns None on success or
      * an error message on failure.
      */
    def declareVariable(
        variable: Ident,
        varType: Type
    ): Option[symbolTableError] = {
      table.headOption match {
        case None => LLCError.exitGracefully(INTERNAL_ERROR)
        case Some(variableMap) =>
          variableMap.put(variable.uid, varType) match {
            case None => None
            case _: Some[Type] =>
              Some(VARIABLE_ALREADY_DECLARED)
          }
      }
    }

    def variableType(variable: Ident): Either[symbolTableError, Type] = {
      searchForVar(variable.uid)
    }

    private def searchForVar(
        variable: Int
    ): Either[symbolTableError, Type] =
      searchForVarRec(variable, table)

    @tailrec
    private def searchForVarRec(
        variable: Int,
        list: List[mutable.Map[Int, Type]]
    ): Either[symbolTableError, Type] = list match {
      case head :: next =>
        head.get(variable) match {
          case None          => searchForVarRec(variable, list.tail)
          case Some(varType) => Right(varType)
        }
      case Nil => Left(VARIABLE_NOT_DECLARED)
    }
  }
}
