class PrettyPrinter(val program: Program) {

  var sb = new StringBuilder()

  def format(): String = {
    program.tokens.zipWithIndex.foreach {
      case (token, i) => formatToken(token, program.tokens.lift(i + 1))
    }

    sb.toString
  }

  var indentCounter = 0

  def indent(): String = (for (_ <- 0 to indentCounter) yield "  ").mkString

  def formatToken(t: Token, lookahead: Option[Token]): Unit = {
    t match {
      case DataImpl() =>
        sb.append('\n')
          .append('\t')
          .append(t)
        indentCounter = 0
      case TextImpl() =>
        sb.append('\n')
          .append('\n')
          .append('\t')
          .append(t)
        indentCounter = 0
      case LabelImpl(name) =>
        sb.append('\n')
        name match {
          case "main" =>
            indentCounter = 0
            sb.append(t)
            indentCounter += 1
          case _ =>
            lookahead match {
              case Some(AsciiImpl(_)) | Some(AsciizImpl(_)) | Some(WordImpl(_)) => sb.append(t)
              case _ =>
                indentCounter -= 1
                sb.append(indent()).append(t)
                indentCounter += 1
            }
        }
      case WordImpl(value) => sb.append('\t').append(t)
      case AsciiImpl(value) => sb.append('\t').append(t)
      case AsciizImpl(value) => sb.append('\t').append(t)
      case CommentImpl(comment) => ()
      case SyscallImpl() => sb.append('\n').append(indent()).append(t).append('\n')
      case MoveImpl(r1, r2) => sb.append('\n').append(indent()).append(t)
      case LoadAddressImpl(register, label) => sb.append('\n').append(indent()).append(t)
      case LoadImmediateImpl(register, value) => sb.append('\n').append(indent()).append(t)
      case LoadWordImpl(target, offset, source) => sb.append('\n').append(indent()).append(t)
    }
  }
}
