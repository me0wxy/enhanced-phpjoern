#!/bin/bash

# if your code is in an zip file ,unzip it !
zipname=$1 # cms source code

# unzip
unzip $zipname
dirname=$(echo $zipname | sed 's/\.zip//')

# Gen nodes.csv,rels.csv ,
./phpjoern/php2ast $dirname

# Gen cpg_edges.csv
./phpast2cpg nodes.csv rels.csv

# Gen DB ; HEAP should not lage than the memory of your computer
HEAP=6G

# here the batch_import is accessible in
java -classpath "./batch_import/lib/*" -Xmx$HEAP -Xms$HEAP -XX:-UseGCOverheadLimit -Dfile.encoding="UTF-8" org.neo4j.batchimport.Importer "./phpjoern/conf/batch.properties" $dirname.db nodes.csv rels.csv,cpg_edges.csv

# clear dir
rm *.csv

# mv to GraphBak
#rm -r /mnt/HotPatch/Graph_Bak/$dirname.db
#mv $dirname.db /mnt/HotPatch/Graph_Bak
