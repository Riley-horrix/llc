package frontend

import ast._
import parsley.generic
import parsley.Parsley

object disambiguators {

  sealed trait ParseType
  object ParseInt32 extends ParseType
  object ParseChar extends ParseType

  // case class PrimitiveTypeDisambiguator(prefixModifiers: List[TypeModifier], parsedType: ParseType, postfixModifiers: List[TypeModifier])
  object PrimitiveTypeDisambiguator extends generic.ParserBridge3[List[TypeModifier], ParseType, List[TypeModifier], Primitive] {

    override def apply(preMods: List[TypeModifier], parseType: ParseType, postMods: List[TypeModifier]): Primitive = parseType match {
      case ParseInt32 => IntType(preMods, Size32)
      case ParseChar => CharType(preMods)
    }
  }

}