#!/bin/sh

function do_run ()
{
  echo $*
  if $* ; then
    true
  else
    echo "*** error"
    exit 1
  fi
}

#do_run cp import/target/isacrodi-import.jar sampledata/
cd sampledata
#do_run java -jar isacrodi-import.jar -e export_cdr.txt
#do_run java -jar isacrodi-import.jar export_cdr.txt
do_run java -jar isacrodi-import.jar -c export_singlecdr.txt 266
