SRC = Register Token MipsParser PrettyPrinter Driver
DRIVER = Driver
VERSION = "scala-parser-combinators_2.13:1.1.2"
JARS = $(shell cs fetch org.scala-lang.modules:${VERSION})

all: build run

.PHONY: build
build: $(addsuffix .class,$(SRC))

%.class: %.scala
	scalac -cp .:$(JARS) $<

.PHONY: run
run: ${DRIVER}.class
	scala ${DRIVER}

.PHONY: clean
clean:
	rm -f *.class
