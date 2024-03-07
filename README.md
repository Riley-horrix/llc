# llc

The llc is a compiler for the linal programming language, a C-style programming language 
specifically designed for ease of use and type safety over matrix and vector operations.


## Compilation

To build the compiler, run `make` in the root directory, and then to compile a file,
run `./llc <filename>`, or `./llc -h` for options and help.


## Testing

The project uses the scala-test framework to test the functionality of the compiler, 
from unit tests to integration testing.

To run the entire testsuite, run `make test`.


For lexer unit tests: `make lexer`
For parser unit tests: `make parser`
For semantic analysis tests: `make semantic`

For frontend tests: `make frontend`


