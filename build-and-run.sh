#!/usr/bin/env bash

set -eu

# I used jdk11-openjdk
PATH_TO_JAVAC="/usr/lib/jvm/java-11-openjdk/bin"

git submodule foreach git fetch
git submodule foreach git checkout -f

if ! [[ -d .git ]]; then
    echo 'you have the run the script from the same location as the ".git" folder'
    exit 0
fi

cd src

echo "=[ Building the code...."
"${PATH_TO_JAVAC}/javac" -Xlint:none -d "../out" Main.java

cd ../out

echo
echo
echo "=[ Running the code..."
"${PATH_TO_JAVAC}/java" Main
