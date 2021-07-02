#!/bin/bash
dirname=$1
# Gen nodes.csv,rels.csv

/usr/bin/php8.0 /home/sec/php8.0/phpjoern/src/Parser.php $dirname
