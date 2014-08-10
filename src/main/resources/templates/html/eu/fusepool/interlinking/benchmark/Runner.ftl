<#--
  Licensed to the Apache Software Foundation (ASF) under one or more
  contributor license agreements.  See the NOTICE file distributed with
  this work for additional information regarding copyright ownership.
  The ASF licenses this file to You under the Apache License, Version 2.0
  (the "License"); you may not use this file except in compliance with
  the License.  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
-->
<@namespace ont="http://fusepool.com/ontology/interlinker-benchmark#" />
<@namespace ehub="http://stanbol.apache.org/ontology/entityhub/entityhub#" />
<@namespace cc="http://creativecommons.org/ns#" />
<@namespace dct="http://purl.org/dc/terms/" />

<html>
  <head>
    <title>Interlinker Speed test</title>
    <link type="text/css" rel="stylesheet" href="styles/multi-enhancer.css" />
  </head>

  <body>
    <h1>Test Results</h1>
<p>
Number of files: <@ldpath path="ont:files"/><br/>
Number of triples: <@ldpath path="ont:triples"/><br/>
Found Interlinks: <@ldpath path="ont:foundInterlinks"/><br/>
Interlinker used: <@ldpath path="ont:interlinkerName"/><br/>
Time ellapsed: <@ldpath path="ont:duration"/> ms<br/>
</p>
    <h1>Run new Test</h1>
    <form method="GET" action="<@ldpath path="."/>">
        <label for="files">Files </label><input type="text" name="files" value="<@ldpath path="ont:files"/>"/> <br/>
        <label for="interlinkerName">Interlinker-Name </label><input type="text" name="interlinkerName" value="<@ldpath path="ont:interlinkerName"/>"/> <br/>
        <input type="submit" value="run" />
    </form>

  </body>
</html>

