import Register.RegisterT

trait Token {
  override def toString: String = "<nil>"
}

case class LoadImmediateImpl(register: RegisterT, value: Int) extends Token {
  override def toString: String = s"li $register, $value"
}

case class AsciiImpl(var value: String) extends Token {
  override def toString: String = s".ascii $value"
}

case class AsciizImpl(var value: String) extends Token {
  override def toString: String = s".asciiz $value"
}

case class CommentImpl(comment: String) extends Token {
  override def toString: String = s"#$comment"
}

case class DataImpl() extends Token {
  override def toString: String = ".data"
}

case class LabelImpl(name: String) extends Token {
  override def toString: String = s"$name:"
}

case class LoadAddressImpl(register: RegisterT, label: String) extends Token {
  override def toString: String = s"la $register, $label"
}

case class LoadWordImpl(target: RegisterT, offset: Int, source: RegisterT) extends Token {
  override def toString: String = s"lw $target, $offset($source)"
}

case class MoveImpl(r1: RegisterT, r2: RegisterT) extends Token {
  override def toString: String = s"move $r1, $r2"
}

case class SyscallImpl() extends Token {
  override def toString: String = "syscall"
}

case class TextImpl() extends Token {
  override def toString: String = ".text"
}

case class WordImpl(var value: Int) extends Token {
  override def toString: String = s".word $value"
}

case class Program(tokens: Seq[Token])
