# llc

The llc is a compiler for the linal programming language, a C / Java style programming language 
specifically designed for ease of use and type safety over matrix and vector operations.

The llc compiles linal files into executables after translating it into C, and using a 
user specified C compiler on the result.


## Compilation

To build the compiler, run `make` in the root directory, and then to compile a file,
run `./llc <filename>`, or `./llc -h` for options and help.


## Testing

The project uses the scala-test framework to test the functionality of the compiler, 
from unit tests to integration testing.

To run the entire testsuite, run `make test`.


For lexer unit tests: `make lexer`
</br>
For parser unit tests: `make parser`
</br>
For semantic analysis tests: `make semantic`

For frontend tests: `make frontend`
