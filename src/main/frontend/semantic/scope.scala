package frontend

import llc.ast._

import scala.collection.mutable

object scope {

  /** Provides mappings for every variable declared in this scope. */
  class Scope {
    private val identMapping: mutable.Map[Int, Type] = mutable.Map()

    /** Sets a variables type to the identifier mapping. If the variable already
      * has a mapping in the current scope, it returns false, otherwise returns
      * true.
      */
    def setIdentType(uid: Int, varType: Type): Boolean =
      identMapping.put(uid, varType) match {
        case None        => true
        case Some(value) => false
      }
  }
}
