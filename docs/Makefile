PSFILES 	= isacrodi_notes.ps
PDFFILES	= $(PSFILES:%.ps=%.pdf)
UMLETDIAGRAMS	= $(wildcard *.uxf)
UMLET_EPSFIGS	= $(UMLETDIAGRAMS:%.uxf=%.eps)
EPSFIGS		= $(UMLET_EPSFIGS)

all : $(PSFILES) $(PDFFILES)

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

.PHONY : all clean print

.PRECIOUS : %.eps

