JBOSS_DEPLOYDIR	= $(JBOSS_HOME)/server/jtktest/deploy
package :
	mvn -o package

doc :
	make -C docs
	mvn -o install
	mvn -o javadoc:javadoc

deploy : package
	cp ear/target/isacrodi.ear $(JBOSS_DEPLOYDIR)

tgz :
	mvn clean
	rm -f isacrodi_trunk.tgz
	cd ../.. ; tar -zcvf isacrodi_trunk.tgz isacrodi/trunk
	mv ../../isacrodi_trunk.tgz .

.PHONY : package deploy tgz doc

