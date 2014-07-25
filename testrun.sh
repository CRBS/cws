

#declare baseURL="http://localhost:8080"
declare baseURL="https://crbsworkflow.appspot.com"
declare user="chris"
declare token="dc5902078cfa40b980229662c2e0c226"
declare usertoken="userlogin=${user}&usertoken=${token}"
echo ""
echo "Add new workflow"
echo "java -jar cws-war/target/cws-war-1.0-SNAPSHOT-jar-with-dependencies.jar --uploadwf cws-war/src/test/resources/example.kar --url ${baseURL} --login ${user} --token ${token}"

echo ""
echo ""
echo "Add a file to workspace"
echo "java -jar cws-war/target/cws-war-1.0-SNAPSHOT-jar-with-dependencies.jar --uploadfile cws-war/src/test/resources/example.kar --url ${baseURL} --login ${user} --token ${token}"
echo ""
echo ""
echo "Add new task be sure to replace XXXX with workflow id"
# This adds a new task,  Be sure to set the workflow id (in XXXX) below!!!
echo "curl -X POST -H \"Content-Type: application/json\" -d '{\"id\":null,\"name\":\"blah\",\"owner\":\"smith\",\"parameters\" : [ {\"name\" : \"CWS_outputdir\",\"value\" : \"well\"  },{\"name\" : \"CWS_user\",\"value\" : \"uh\"  },{\"name\" : \"CWS_jobname\",\"value\" : \"tasky\"  },{\"name\" :\"exampletext\",\"value\" : \"blah\"  }, {\"name\":\"examplecheckbox\",\"value\":\"false\"},{\"name\":\"examplefile\",\"value\":\"/path\"}],\"workflow\":{\"id\":XXXX}}' \"${baseURL}/rest/jobs?${usertoken}\""

echo ""
echo ""
echo "Create a user"
echo "curl -i -X POST -H \"Content-Type: application/json\" -d '{ \"permissions\": 131072,\"createDate\":1404159501623,\"login\":\"bob\",\"token\":\"sometokenxxx\"}' \"${baseURL}/rest/users?${usertoken}\"
echo ""
echo ""
echo "Get a user"
echo "curl -i -X GET \"${baseURL}/rest/users/1?${usertoken}\"

# To preview a workflow first save the json to foo.json and then run this
# 
# curl -i -X POST --form "_formexamplefile=@/home/churas/src/cws/foo.json" http://imafish.dynamic.ucsd.edu/cws/makepreview
#
