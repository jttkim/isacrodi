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


do_run cp import/target/isacrodi-import.jar sampledata/
cd sampledata
do_run java -jar isacrodi-import.jar isacrodi_users.txt
do_run java -jar isacrodi-import.jar isacrodi_crops.txt
do_run java -jar isacrodi-import.jar isacrodi_disorders.txt
do_run java -jar isacrodi-import.jar isacrodi_numerictypes.txt
do_run java -jar isacrodi-import.jar isacrodi_categoricaltypes.txt
do_run java -jar isacrodi-import.jar isacrodi_imagetypes.txt
do_run java -jar isacrodi-import.jar isacrodi_cdrs.txt
do_run java -jar isacrodi-import.jar isacrodi_procedures.txt

