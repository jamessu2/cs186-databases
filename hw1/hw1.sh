#!/bin/bash
# bash command-line arguments are accessible as $0 (the bash script), $1, etc.
# echo "Running" $0 "on" $1

# Generate ebooks.csv and tokens.csv
python hw1_first.py $1

# Sort tokens.csv
head -1 tokens.csv > tokens-sort.csv | tail -n+2 tokens.csv | sort -t, -k 2,2 -k 1,1n >> tokens-sort.csv
### head -1 tokens.csv > tokens-sort.csv | tail -n+2 tokens.csv | sort -t, -k 2,2 >> tokens-sort.csv

# Generate token_counts.csv and name_counts.csv
python hw1_second.py tokens-sort.csv

# Remove tokens-sort.csv...?
#rm tokens-sort.csv

exit 0
