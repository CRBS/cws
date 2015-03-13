
declare theVersion=`grep "<version>" pom.xml | head -n 1 | sed "s/^ *<version>//" | sed "s/<\/.*$//"`
declare theJar="cws-war/target/cws-war-${theVersion}-jar-with-dependencies.jar"

declare queue="coleslaw_shadow.q"
declare baseurl="http://localhost:8080"
declare baseDir=`pwd`
declare workflowDir="$baseDir/w/workflows"
declare execDir="$baseDir/w/exec"
declare keplerSh="/home/churas/bin/kepler-2.4/kepler.sh"
declare panCast="/usr/local/bin/panfishcast"
declare panStat="/usr/local/bin/panfishstat"
declare theLogin="chris"
declare theToken="dc5902078cfa40b980229662c2e0c226"


declare helpEmail="support@slashsegmentation.com"
declare bccEmail="support@slashsegmentation.com"
declare project="test"
declare portalName="test Portal"
declare portalUrl="https://localhost/workflowservice"



mkdir -p $workflowDir
mkdir -p $execDir

java -jar "$theJar"  --registerjar "$theJar" --wfdir $workflowDir --execdir $execDir --syncwithcluster $baseurl --kepler $keplerSh --queue $queue --panfishcast $panCast --panfishstat $panStat --project "$project" --portalname "$portalName" --portalurl "$portalUrl" --helpemail "$helpEmail" --bccemail "$bccEmail" --login $theLogin --token $theToken

exit $?

