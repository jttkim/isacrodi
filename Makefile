JBOSS_DEPLOYDIR	= $(JBOSS_HOME)/server/default/deploy
package :
	mvn -o clean
	mvn -o package

doc :
	mvn -o install
	mvn -o javadoc:javadoc
	make -C docs

deploy : package
	cp ear/target/isacrodi.ear $(JBOSS_DEPLOYDIR)

undeploy :
	rm -f isacrodi.ear $(JBOSS_DEPLOYDIR)

tgz : clean
	rm -f isacrodi_trunk.tgz
	cd ../.. ; tar -zcvf isacrodi_trunk.tgz isacrodi/trunk
	mv ../../isacrodi_trunk.tgz .

clean :
	mvn clean
	make -C docs clean

.PHONY : package deploy undeploy tgz doc clean

