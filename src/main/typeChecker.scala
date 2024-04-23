package llc

import llc.ast._
import frontend.llcerror.LLCError
import frontend.llcerror.INTERNAL_ERROR

object typeSemantics {

  sealed trait TypeSize
  object Size8 extends TypeSize
  object Size32 extends TypeSize
  object Size64 extends TypeSize
  case class SizeN(numBytes: Int) extends TypeSize

  /** Check the compatability of two types, and returns the casted to type. Else
    * returns Error type.
    */
  def commonType(aType: Type, bType: Type): Type = ???

  /** Returns the storage size related to a specific type. */
  def getSize(varType: Type): TypeSize = varType.baseType match {
    case CharType           => Size8
    case IntType            => Size32
    case VoidType           => LLCError.exitGracefully(INTERNAL_ERROR)
    case matrix: MatrixType => getMatrixSize(matrix)
    case _: PointerType     => Size64
  }

  /** Gets the storage size of a matrix. */
  private def getMatrixSize(matrix: MatrixType): TypeSize = matrix match {
    case MatrixType(matrixType, rows, cols) =>
      numBytes(getSize(matrixType)) * rows * cols match {
        case numb if numb <= 1 => Size8
        case numb if numb <= 4 => Size32
        case numb if numb <= 8 => Size64
        case numb              => SizeN(numb)
      }
  }

  private def numBytes(typeSize: TypeSize): Int = typeSize match {
    case Size8           => 1
    case Size32          => 4
    case Size64          => 8
    case SizeN(numBytes) => numBytes
  }
}
