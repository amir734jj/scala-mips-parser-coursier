SRC = Register Token MipsParser PrettyPrinter Driver
DRIVER = Driver
VERSION = "scala-parser-combinators_2.13:1.1.2"
JARS = $(shell cs fetch org.scala-lang.modules:${VERSION} | grep "scala-parser-combinators")

all: build run

.PHONY: build
build: $(addsuffix .class,$(SRC))

%.class: %.scala
	scalac -cp .:$(JARS) $<

.PHONY: run
run: ${DRIVER}.class
	scala  -cp .:$(JARS) $(basename $<)

.PHONY: clean
clean:
	rm -f *.class
