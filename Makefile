# The -f flag is needed to override any existing built compiler
# The --power flag is needed as `package` is an experimental "power user" feature
all: 
	scala-cli --power package . -f -o llc

test:
	scala-cli test . 

parser:
	scala-cli test . --test-only parser_test*

semantic:
	scala-cli test . --test only semantic_test*

frontend:
	scala-cli test . --test-only parser_test* semantic_test*
