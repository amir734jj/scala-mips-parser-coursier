SRC = Driver.scala MipsParser.scala PrettyPrinter.scala Register.scala Token.scala
JARS = $(shell cs fetch org.scala-lang.modules:scala-parser-combinators_3:2.1.0)

all: build

.PHONY: build
build:
	scala -cp $(JARS) $(SRC)
