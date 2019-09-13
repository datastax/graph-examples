#!/bin/bash

cd `dirname $0`

SRC_DIR="../src/"
TGT_DIR="../target/"
JAR_FILE="traversal-builder.jar"

findJarFile() {
  jarFile=$(find $TGT_DIR -name "traversal-builder-*-jar-with-dependencies.jar" -printf '%T+ %p\n' | sort -r | head -n1 | awk '{print $2}')
}

recompile() {
  pushd ../ > /dev/null
  mvn package -q 2> /dev/null
  popd > /dev/null
  findJarFile
  cp -p $jarFile $JAR_FILE
}

if [ -d $TGT_DIR ]; then
  findJarFile
  if [ -f $jarFile ]; then
    cp -p $jarFile $JAR_FILE
    newestSourceFile=$(find $SRC_DIR -type f -printf '%T+ %p\n' | sort -r | head -n1 | awk '{print $2}')
    if [ $newestSourceFile -nt $JAR_FILE ]; then
      recompile
    fi
  else
    recompile
  fi
else
  recompile
fi

java -cp traversal-builder.jar com.datastax.examples.builder.App
