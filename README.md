# llc

The llc is a compiler for the linal programming language, a low-level C style programming language 
specifically designed for ease of use and type safety over matrix and vector operations.

The llc is a standalone compiler, designed and written by Riley Horrix


## Compilation

To build the compiler, run `make` in the root directory, and then to compile a file,
run `./llc <filename>`, or `./llc -h` for options and help.


## Testing

The project uses the scala-test framework to test the functionality of the compiler, 
from unit tests to integration testing.

To run the entire testsuite, run `make test`.


For parser unit tests: `make parser`

For semantic analysis tests: `make semantic`

For frontend integration tests: `make integration`

For frontend tests: `make frontend`
