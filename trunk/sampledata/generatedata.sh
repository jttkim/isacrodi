#!/bin/sh

function do_run ()
{
 echo $*
 if $* ; then
   true
 else
   exit 1
 fi
}


function get_trainingdata()
{
  for((i=1; i<=${cdrs}; i++))
  {
    filename=`printf 'isacrodi_cdr%02d' $i`
    do_run java -jar isacrodi-cmdtool.jar cdrgen ${filename}'.txt' ${categoricaltypesfiles} ${rndseed} ${nrandom} ${filename}'_train.txt' 
  }
}


function get_testingdata()
{
  rndseed=`expr $rndseed + 1`
  for((i=1; i<=${cdrs}; i++))
  {
    filename=`printf 'isacrodi_cdr%02d' $i`
    disorder1=$(grep -w "expertDiagnosis" ${filename}'.txt' | awk '{print$2} ')
    disorder2=$(grep -w "expertDiagnosis" ${filename}'.txt' | awk '{print$3} ')
    disorder=`printf '%s %s' $disorder1 $disorder2`
    echo $disorder
    sed ' s/'"$disorder"'//g' ${filename}'.txt' > ${filename}'_unlabelled.txt'
    do_run java -jar isacrodi-cmdtool.jar cdrgen ${filename}'_unlabelled.txt' ${categoricaltypesfiles} ${rndseed} ${nrandom} ${filename}'_test.txt'
    rm -r ${filename}'_unlabelled.txt'

  }
}


function import_trainingdata()
{
  for((i=1; i<=${icdrs}; i++))
  {
    filename=`printf 'isacrodi_cdr%02d' $i`
    do_run java -jar isacrodi-import.jar ${filename}'_train.txt'
  }
}


function import_testingdata()
{
  for((i=1; i<=${cdrs}; i++))
  {
    filename=`printf 'isacrodi_cdr%02d' $i`
    do_run java -jar isacrodi-import.jar ${filename}'_test.txt'
  }
}


function updatejars()
{
  do_run cp ../import/target/isacrodi-import.jar .
  do_run java -jar isacrodi-import.jar isacrodi_users.txt
  do_run java -jar isacrodi-import.jar isacrodi_crops.txt
  do_run java -jar isacrodi-import.jar isacrodi_disorders.txt
  do_run java -jar isacrodi-import.jar isacrodi_numerictypes.txt
  do_run java -jar isacrodi-import.jar isacrodi_categoricaltypes.txt
  do_run java -jar isacrodi-import.jar isacrodi_imagetypes.txt
  do_run java -jar isacrodi-import.jar isacrodi_procedures.txt
}


rndseed=1
cdrs=5
nrandom=10
icdrs=5
categoricaltypesfiles='isacrodi_categoricaltypes.txt'


while getopts s:c:n:f:d opt
do
  case "$opt" in
  s) rndseed="$OPTARG";;
  c) cdrs="$OPTARG";;
  n) nrandom="$OPTARG";;
  f) categoricaltypefiles="$OPTARG";;
  d) isdef=1;;
  \?) help_ani;;
  esac
done


get_trainingdata
get_testingdata
updatejars
import_trainingdata
import_testingdata
