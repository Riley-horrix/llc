package frontend

import llc.ast._
import llcerror._

import scala.collection.mutable

object symbolTable {

  /** Provides mappings for every variable declared in this scope. */
  class SymbolTable {
    private var table: List[mutable.Map[Int, Type]] = List.empty

    /** Sets a variables type to the identifier mapping. If the variable already
      * has a mapping in the current scope, it returns false, otherwise returns
      * true.
      */
    def setIdentType(uid: Int, varType: Type): Boolean =
      identMapping.put(uid, varType) match {
        case None        => true
        case Some(value) => false
      }

    def pushNewScope(): SymbolTable = {
      table = List.empty :: table
      this
    }

    def popCurrentScope(): Scope = {
      val entry = table.headOption match {
        case None        => LLCError.exitGracefully(INTERNAL_ERROR)
        case Some(value) => value
      }
      table = table.last
      new Scope(entry)
    }

    /** Put a variable declaration into the typemap, returns None on success or
      * an error message on failure.
      */
    def declareVariable(
        variable: Ident,
        varType: Type
    ): Option[StringBuilder] = {
      table.headOption match {
        case None => LLCError.exitGracefully(INTERNAL_ERROR)
        case Some(variableMap) =>
          variableMap.put(variable.uid, varType) match {
            case None => None
            case _: Some[Type] =>
              variable_already_declared(variable.name)
          }
      }
    }

    def variableType(variable: Int): Type = {
      searchForVar(variable)
    }
  }
}
