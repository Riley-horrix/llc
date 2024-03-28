# The -f flag is needed to override any existing built compiler
# The --power flag is needed as `package` is an experimental "power user" feature
all: 
	scala-cli --power package . -f -o llc

test:
	scala-cli test . 

# Parser unit tests
parser:
	scala-cli test . --test-only parser_test*

# Semantic checker unit tests
semantic:
	scala-cli test . --test-only semantic_test*

# Frontend integration tests
integration:
	scala-cli test . --test-only integration_test*

# All frontend tests
frontend:
	scala-cli test . --test-only parser_test* semantic_test*
