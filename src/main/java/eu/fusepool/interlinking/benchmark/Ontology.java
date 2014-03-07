/*
* Licensed to the Apache Software Foundation (ASF) under one or more
* contributor license agreements.  See the NOTICE file distributed with
* this work for additional information regarding copyright ownership.
* The ASF licenses this file to You under the Apache License, Version 2.0
* (the "License"); you may not use this file except in compliance with
* the License.  You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package eu.fusepool.interlinking.benchmark;

import org.apache.clerezza.rdf.core.UriRef;


/**
 * Ideally this should be a dereferenceable ontology on the web. Given such 
 * an ontology a class of constant (similar to this) can be generated with
 * the org.apache.clerezza:maven-ontologies-plugin
 */
public class Ontology {
    
    public static final UriRef Benchmark = new UriRef("http://fusepool.com(ontology/interlinker-benchmark#Benchmark");
    static UriRef files = new UriRef("http://fusepool.com(ontology/interlinker-benchmark#files");
    static UriRef duration = new UriRef("http://fusepool.com(ontology/interlinker-benchmark#duration");
    static UriRef interlinkerName = new UriRef("http://fusepool.com(ontology/interlinker-benchmark#interlinkerName");
    static UriRef triples = new UriRef("http://fusepool.com(ontology/interlinker-benchmark#triples");
    static UriRef foundInterlinks = new UriRef("http://fusepool.com(ontology/interlinker-benchmark#foundInterlinks");
    
}
