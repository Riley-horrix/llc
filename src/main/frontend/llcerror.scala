package frontend

object llcerror {
  final val INTERNAL_ERROR = "An Internal Error Has Occurred! (Apologies)"
  final val FILE_IO_ERROR = "A File IO Error Has Occurred!"
  final def FILE_NOT_FOUND(filename: String) =
    s"The File ${filename} Cannot Be Found!"

  final val SYNTAX_ERROR = 100
  final val INTERNAL_ERROR_TRANSLATE_POINTER = -7
  type LLCError = String
}
