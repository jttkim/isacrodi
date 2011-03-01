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
do_run java -jar isacrodi-import.jar isacrodi_cdrs.bk
do_run java -jar isacrodi-import.jar export_305.txt
do_run java -jar isacrodi-import.jar export_335.txt
do_run java -jar isacrodi-import.jar export_387.txt
do_run java -jar isacrodi-import.jar export_434.txt
do_run java -jar isacrodi-import.jar export_456.txt
do_run java -jar isacrodi-import.jar export_479.txt
do_run java -jar isacrodi-import.jar export_501.txt
do_run java -jar isacrodi-import.jar export_523.txt
do_run java -jar isacrodi-import.jar export_545.txt
do_run java -jar isacrodi-import.jar export_568.txt
do_run java -jar isacrodi-import.jar export_588.txt
do_run java -jar isacrodi-import.jar export_610.txt
do_run java -jar isacrodi-import.jar export_633.txt
do_run java -jar isacrodi-import.jar export_650.txt
do_run java -jar isacrodi-import.jar export_668.txt
do_run java -jar isacrodi-import.jar isacrodi_procedures.txt

