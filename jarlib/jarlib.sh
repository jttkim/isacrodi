#!/bin/sh

mvn install:install-file -DgroupId=javax.ejb -DartifactId=ejb -Dversion=3.0 -Dpackaging=jar -Dfile=ejb-3.0.jar
mvn install:install-file -DgroupId=classifier -DartifactId=libsvm -Dversion=3.1 -Dpackaging=jar -Dfile=libsvm-3.1.jar
mvn install:install-file -DgroupId=miscutils -DartifactId=json -Dversion=2010-05-25 -Dpackaging=jar -Dfile=json-2010-05-25.jar
mvn install:install-file -DgroupId=statistics -DartifactId=jsc -Dversion=1.0 -Dpackaging=jar -Dfile=jsc.jar

