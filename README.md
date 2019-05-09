# Automatex

Automatex is a fast regular expression matcher implemented in java.  
No libraries are required, Graphviz is optional.  

# Supported operations and symbols

## Symbols

Every character is supported.  
'.' acts as a wildcard.  
Every operators and special characters can be escaped using '\\'.  
'\n' matches a new line.  

## Operators

Supported operators:  
 - concatenation
 - '|'
 - '*'
 - '+'
 - '?'
 - '()'
 - '[abc]'
 - '[a-z]'

# Debug mode

If debug mode is enabled:  
The program will generate portable .dot files for NDFA's and NFA's that can be used anywhere.  
It will also generate a picture of that .dot file if Graphviz is installed.  
