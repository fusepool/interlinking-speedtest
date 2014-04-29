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

import eu.fusepool.datalifecycle.Interlinker;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.apache.clerezza.jaxrs.utils.TrailingSlash;
import org.apache.clerezza.rdf.core.MGraph;
import org.apache.clerezza.rdf.core.TripleCollection;
import org.apache.clerezza.rdf.core.UriRef;
import org.apache.clerezza.rdf.core.access.TcManager;
import org.apache.clerezza.rdf.core.impl.PlainLiteralImpl;
import org.apache.clerezza.rdf.core.serializedform.Parser;
import org.apache.clerezza.rdf.ontologies.RDF;
import org.apache.clerezza.rdf.ontologies.RDFS;
import org.apache.clerezza.rdf.utils.GraphNode;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.felix.scr.annotations.Service;
import org.apache.stanbol.commons.indexedgraph.IndexedMGraph;
import org.apache.stanbol.commons.web.viewable.RdfViewable;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Upload file for which the enhancements are to be computed
 */
@Component
@Service(Object.class)
@Property(name="javax.ws.rs", boolValue=true)
@Path("interlinking-benchmark")
public class Runner {
    
    /**
     * Using slf4j for logging
     */
    private static final Logger log = LoggerFactory.getLogger(Runner.class);
        
    @Reference
    private TcManager tcManager;
    
    @Reference
    private Parser parser;
    
    // Stores bindings to different instances of Interlinker
    @Reference(cardinality = ReferenceCardinality.MANDATORY_MULTIPLE,
            policy = ReferencePolicy.DYNAMIC,
            referenceInterface = Interlinker.class)
    private final Map<String, Interlinker> interlinkers = new HashMap<String, Interlinker>();
    
    @Activate
    protected void activate(ComponentContext context) {
        log.info("The example service is being activated");
    }
    
    @Deactivate
    protected void deactivate(ComponentContext context) {
        log.info("The example service is being activated");
    }
    
    /**
     * This method return an RdfViewable, this is an RDF serviceUri with associated
     * presentational information.
     */
    @GET
    public RdfViewable run(@Context final UriInfo uriInfo, 
            @QueryParam("files") @DefaultValue("100") final int files,
            @QueryParam("interlinkerName") @DefaultValue("silk-patents") final String interlinkerName) 
            throws Exception {
     
        TrailingSlash.enforcePresent(uriInfo);
        final String resourcePath = uriInfo.getAbsolutePath().toString();
        //The URI at which this service was accessed accessed, this will be the 
        //central serviceUri in the response
        final UriRef serviceUri = new UriRef(resourcePath);
        //the in memory graph to which the triples for the response are added
        final MGraph responseGraph = new IndexedMGraph();
        final GraphNode node = new GraphNode(serviceUri, responseGraph);
        //The triples will be added to the first graph of the union
        //i.e. to the in-memory responseGraph
        node.addProperty(RDF.type, Ontology.Benchmark);
        node.addProperty(RDFS.comment, new PlainLiteralImpl("An interlinker benchmark"));
        node.addProperty(Ontology.files, new PlainLiteralImpl(Integer.toString(files)));
        Interlinker interlinker = interlinkers.get(interlinkerName);
        if (interlinker == null) {
            throw new WebApplicationException("No interlinker "+interlinkerName, Response.Status.BAD_REQUEST);
        }
        node.addProperty(Ontology.interlinkerName, new PlainLiteralImpl(interlinker.getName()));
        benchmark(files, interlinker, node);
        //What we return is the GraphNode we created with a template path
        return new RdfViewable("Runner", node, Runner.class);
    }

    
    private void benchmark(int files, Interlinker interlinker, GraphNode node) throws IOException {
        UriRef dataGraphUri = new UriRef(("urn:x-localinstace:/interlinking-benchmark/data/"+ UUID.randomUUID()));
        MGraph dataGraph = tcManager.createMGraph(dataGraphUri);
        try {
            loadFiles(files, dataGraph);
            node.addProperty(Ontology.triples, new PlainLiteralImpl(Integer.toString(dataGraph.size())));
            long start = System.currentTimeMillis();
            TripleCollection interlinks = interlinker.interlink(dataGraph, dataGraph);
            long end = System.currentTimeMillis();
            node.addProperty(Ontology.duration, new PlainLiteralImpl(Long.toString(end-start)));
            node.addProperty(Ontology.foundInterlinks, new PlainLiteralImpl(Long.toString(interlinks.size())));
        } finally {
            tcManager.deleteTripleCollection(dataGraphUri);
        }
    }
    
    /**
     * Bind interlinkers used by this component
     */
    protected void bindInterlinkers(Interlinker interlinker) {
        if (!interlinkers.containsKey(interlinker.getName())) {
            interlinkers.put(interlinker.getName(), interlinker);
            log.debug("Interlinker " + interlinker.getName() + " bound");

        } else {
            log.warn("Interlinker with " + interlinker.getName() + " already bound.");
        }
    }

    /**
     * Unbind interlinkers
     */
    protected void unbindInterlinkers(Interlinker interlinker) {

        if (interlinkers.remove(interlinker.getName()) == null) {
            log.warn("Interlinker " + interlinker + " could not be unbound.");
        }

    }

    private void loadFiles(int files, MGraph dataGraph) throws IOException {
        List<String> uriStrings = LinksRetriever.getLinks("http://raw.fusepool.info/marec/00/");
        Iterator<String> iter = uriStrings.iterator();
        for (int i = 0; i < files; i++) {
           loadFile(dataGraph, new URL(iter.next()));
        }
    }

    private void loadFile(MGraph dataGraph, URL url) throws IOException {
        URLConnection connection = url.openConnection();
        String mediaType = connection.getHeaderField("Content-type");
        if ((mediaType == null) || mediaType.equals("application/octet-stream")) {
            mediaType = guessContentTypeFromUri(url);
        }
        InputStream data = connection.getInputStream();
        parser.parse(dataGraph, data, mediaType, new UriRef(url.toString()));
    }
    
     private String guessContentTypeFromUri(URL url) {
        String contentType = null;
        if (url.getFile().endsWith("ttl")) {
            contentType = "text/turtle";
        } else if (url.getFile().endsWith("nt")) {
            contentType = "text/turtle";
        } else if (url.getFile().endsWith("rdf")) {
            contentType = "application/rdf+xml";
        } else if (url.getFile().endsWith("xml")) {
            contentType = "application/xml";
        }
        return contentType;
    }
    
}
