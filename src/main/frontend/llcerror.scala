package frontend

object llcerror {
  final val INTERNAL_ERROR = "An Internal Error Has Occurred! (Apologies)"
  final val FILE_IO_ERROR = "A File IO Error Has Occurred!"
  final def FILE_NOT_FOUND(filename: String) =
    s"The File ${filename} Cannot Be Found!"
  type LLCError = String
}
