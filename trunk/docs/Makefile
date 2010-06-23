PSFILES 	= isacrodi_notes.ps
PDFFILES	= $(PSFILES:%.ps=%.pdf)
UMLETDIAGRAMS	= $(wildcard *.uxf)
UMLET_EPSFIGS	= $(UMLETDIAGRAMS:%.uxf=%.eps)
EPSFIGS		= $(UMLET_EPSFIGS)

all : $(PSFILES) $(PDFFILES) apidocs

apidocs :
	rm -rf util import web ejbs
	if test -d ../util/target/site/apidocs ; then cp -r ../util/target/site/apidocs util ; fi
	if test -d ../import/target/site/apidocs ; then cp -r ../import/target/site/apidocs import ; fi
	if test -d ../web/target/site/apidocs ; then cp -r ../web/target/site/apidocs web ; fi
	if test -d ../ejbs/target/site/apidocs ; then cp -r ../ejbs/target/site/apidocs ejbs ; fi

%.dvi : %.tex $(EPSFIGS)
	latex $*
	latex $*

%.dvi : %.tex

%.ps : %.dvi
	dvips -Z -P amz -P cmz -o $*.ps $*

%.pdf : %.ps
	ps2pdf14 -sPAPERSIZE=a4 $< $@

%.eps : %.uxf
	umlet -action=convert -format=eps -filename=$*.uxf

clean :
	rm -f *.dvi *.bbl *.blg *.log *.aux *~ $(PSFILES) $(PDFFILES) $(UMLET_EPSFIGS)

.PHONY : all clean apidocs

.PRECIOUS : %.eps

