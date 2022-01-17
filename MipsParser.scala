import Register.{RegisterT, names}

import scala.language.{implicitConversions, postfixOps}
import scala.util.parsing.combinator.RegexParsers
import scala.util.parsing.input.Reader

class MipsParser extends RegexParsers {

  override def skipWhitespace = false

  // Separator which is a comma followed by whitespace or just whitespace
  val separator: Parser[String ~ Option[String ~ String]] = whiteSpace ~ (literal(",") ~ whiteSpace).?

  val number: Parser[Int] =
    """^-?[\d][\d]*""".r ^^ {
      _.toInt
    }

  // Register parser
  val register: Parser[RegisterT] = names
    .foldRight(literal(names.head))((x, xs) => literal(x) ||| xs)

  val ascii: Parser[AsciiImpl] = literal(".ascii") ~ whiteSpace ~ """"([^\\"]|\\.)*"""".r ^^ {
    case _ ~ str => AsciiImpl(str)
  }

  val asciiz: Parser[AsciizImpl] = literal(".asciiz") ~ whiteSpace ~ """"([^\\"]|\\.)*"""".r ^^ {
    case _ ~ str => AsciizImpl(str)
  }

  val data: Parser[DataImpl] = literal(".data") ^^ (_ => new DataImpl)

  val label: Parser[LabelImpl] =
    """[\w]+""".r ~ literal(":") ^^ {
      case name ~ _ => LabelImpl(name)
    }

  val loadImmediate: Parser[LoadImmediateImpl] = literal("li") ~ whiteSpace ~ register ~ separator ~ number ^^ {
    case _ ~ register ~ _ ~ value => LoadImmediateImpl(register, value)
  }

  val loadAddress: Parser[LoadAddressImpl] = literal("la") ~ whiteSpace ~ register ~ separator ~ """[\w]+""".r ^^ {
    case _ ~ register ~ _ ~ label => LoadAddressImpl(register, label)
  }

  val word: Parser[WordImpl] = literal(".word") ~ whiteSpace ~ number ^^ {
    case _ ~ number => WordImpl(number)
  }

  val syscall: Parser[SyscallImpl] = literal("syscall") ^^ (_ => new SyscallImpl)

  val move: Parser[MoveImpl] = literal("move") ~ whiteSpace ~ register ~ separator ~ register ^^ {
    case _ ~ register1 ~ _ ~ register2 => MoveImpl(register1, register2)
  }

  val loadWord: Parser[LoadWordImpl] = literal("lw") ~ whiteSpace ~ register ~ separator ~ number ~ literal("(") ~ register ~ literal(")") ^^ {
    case _ ~ target ~ _ ~ offset ~ _ ~ source ~ _ => LoadWordImpl(target, offset, source)
  }

  val comment: Parser[CommentImpl] = literal("#") ~ ("""[^\n]+""".r ?) ^^ {
    case _ ~ comment => CommentImpl(comment.getOrElse(""))
  }

  val text: Parser[TextImpl] = literal(".text") ^^ (_ => new TextImpl)

  val directive: Parser[Token] = text ||| word ||| data ||| ascii ||| asciiz

  val instruction: Parser[Token] = loadAddress ||| loadImmediate ||| move ||| loadWord

  val misc: Parser[Token] = label ||| comment ||| syscall

  def item: Parser[Token] = directive ||| instruction ||| misc

  def program: Parser[Program] = repsep(item, whiteSpace) ^^ (tokens => Program(tokens))

  def parseCode(code: Reader[Char]): Program = {
    parse(program, code) match {
      case Success(matched, _) => matched
      case Failure(msg, _) => throw new Exception(s"FAILURE: $msg")
      case Error(msg, _) => throw new Exception(s"ERROR: $msg")
    }
  }
}
