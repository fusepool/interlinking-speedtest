/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.fusepool.interlinking.benchmark;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author Reto
 */
public class LinksRetriever {
    public static void main(String... args) throws MalformedURLException, IOException {
        for (String s : getLinks("http://raw.fusepool.info/marec/00/")) {
            System.out.println(s);
        }
    }
    
    public static List<String> getLinks(String urlString) throws IOException {
        URL url = new URL(urlString);   
        //InputStream in = url.openStream();
        String html = IOUtils.toString(url);
        //ByteArrayOutputStream baos 
        //System.out.print(html);
        Pattern pattern = Pattern.compile("(<a href=\")(.*?)(\">)");
        Matcher matcher = pattern.matcher(html);
        List<String> links = new LinkedList<String>();
        while (matcher.find()) {
            String linkTarget = matcher.group(2);
            if (linkTarget.endsWith("rdf")) {
                links.add(urlString+linkTarget);
            }
        }
        return links;
    }
}
