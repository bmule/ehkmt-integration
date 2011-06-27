/*
 * Project :iCardea
 * File : BuildNode.java
 * Encoding : UTF-8
 * Date : Jun 21, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.persistence.impl.sesame;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;
import java.util.StringTokenizer;


/**
 * I use this class to generate rdf fragments from properties.
 * This has only test purposes.
 * 
 * mvn exec:java -Dexec.mainClass=at.srfg.kmt.ehealth.phrs.persistence.impl.sesame.BuildNode -Dexec.classpathScope=test</br>
 * @version 0.1
 * @since 0.1
 * @author mradules
 */
public class BuildRdf {

    public static void main(String... args) throws IOException {
        final InputStream termsStream =
                BuildRdf.class.getClassLoader().getResourceAsStream("phrs.contr-vocabulary-items.properties");
        final Properties termsMap = new Properties();
        termsMap.load(termsStream);

        final InputStream tagsStream =
                BuildRdf.class.getClassLoader().getResourceAsStream("phrs.tags.properties");
        final Properties tagsMap = new Properties();
        tagsMap.load(tagsStream);


        final File file = new File("dump.xml");
        final BufferedWriter writer = new BufferedWriter(new FileWriter(file));


        for (final Enumeration<Object> keys = termsMap.keys(); keys.hasMoreElements();) {
            final String key = keys.nextElement().toString().trim();
            final String property = termsMap.getProperty(key).trim();

            if (key.contains("UMLS")) {
                String code = key.substring(key.lastIndexOf("@") + 1);

                final String relations = tagsMap.getProperty(key);
                String relLine = null;

                if (relations != null) {
                    final StringBuilder result = new StringBuilder();
                    for (StringTokenizer rels = new StringTokenizer(relations, ","); rels.hasMoreTokens();) {
                        final String relatedToKey = rels.nextToken().trim();
                        final String relLabel = termsMap.getProperty(relatedToKey);
                        if (relLabel != null) {
                            //System.out.println(key + " is related to value " + relLabel);
                            //System.out.println(property + " is related to value " + relLabel);
                            result.append("<skos:related>");
                            result.append("http://www.icardea.at/phrs/instances/");
                            result.append(buildProperty(relLabel.trim()));
                            result.append("</skos:related>");
                        } else if ("UMLS@C1457887".equals(relatedToKey)) {
                            result.append("<skos:related>");
                            result.append("http://www.icardea.at/phrs/instances/Problem/Symptom");

                            result.append("</skos:related>");
                        } else {
                            System.out.println("-->" + relatedToKey);
                        }
                        
                        relLine = result.toString();
                    }


                }
                final String line = buidUMLS(code, property, relLine);
                writer.write(line);
                writer.newLine();


            }

            if (key.contains("PHRS.CS")) {
                String code = key.substring(key.lastIndexOf("@") + 1);

                final String relations = tagsMap.getProperty(key);
                String relLine = null;
                if (relations != null) {
                    final StringBuilder result = new StringBuilder();
                    for (StringTokenizer rels = new StringTokenizer(relations, ","); rels.hasMoreTokens();) {
                        final String relatedToKey = rels.nextToken().trim();
                        final String relLabel = termsMap.getProperty(relatedToKey);
                        if (relLabel != null) {
                            //System.out.println(key + " is related to value " + relLabel);
                            //System.out.println(property + " is related to value " + relLabel);
                            result.append("<skos:related>");
                            result.append("http://www.icardea.at/phrs/instances/");
                            result.append(buildProperty(relLabel.trim()));
                            result.append("</skos:related>");
                        }
                        relLine = result.toString();
                    }
                }




                final String line = buidPHRS_CS(code, property, relLine);
                writer.write(line);
                writer.newLine();
            }
        }

        writer.close();
    }

    private static String buildRelated(String retlatedTo) {
        final StringBuilder result = new StringBuilder();
        result.append(buildProperty(retlatedTo));
        return result.toString();

    }

    /*
    <rdf:Description rdf:about="http://www.icardea.at/phrs/instances/Problem/Fatigue">
    <skos:prefLabel>Fatigue</skos:prefLabel>
    <skos:related>XXX</skos:related>
    <!--Semantic Sign or Symptom-->
    <icadyCode:code>
    <rdf:Description rdf:about="http://www.icardea.at/phrs/instances/FatigueULMSCode">
    <icadyCode:codeValue>C0015672</icadyCode:codeValue>
    <icadyCode:codeSystem>
    <rdf:Description rdf:about="http://www.icardea.at/phrs/instances/codeSystem/Ulms"/>
    </icadyCode:codeSystem>
    </rdf:Description>
    </icadyCode:code>
    </rdf:Description>
     */
    private static String buidUMLS(String code, String property, String relatedTo) {

        final String realProp = buildProperty(property);
        final StringBuilder result = new StringBuilder();
        result.append("<rdf:Description rdf:about=\"http://www.icardea.at/phrs/instances/");
        result.append(realProp);
        result.append("\">");
        result.append("<skos:prefLabel>");
        result.append(property);
        result.append("</skos:prefLabel>");
        if (relatedTo != null) {
            result.append(relatedTo);
        }

        result.append("<icadyCode:code>");
        result.append("<rdf:Description rdf:about=\"http://www.icardea.at/phrs/instances/");
        result.append(realProp);
        result.append("ULMSCode");
        result.append("\">");
        result.append("<icadyCode:codeValue>");
        result.append(code);
        result.append("</icadyCode:codeValue>");
        result.append("<icadyCode:codeSystem>");
        result.append("<rdf:Description rdf:about=\"http://www.icardea.at/phrs/instances/codeSystem/Ulms\"/>");
        result.append("</icadyCode:codeSystem>");
        result.append("</rdf:Description>");
        result.append("</icadyCode:code>");
        result.append("</rdf:Description>");




        return result.toString();
    }

    private static String buidPHRS_CS(String code, String property, String relatedTo) {

        final String realProp = buildProperty(property);
        final StringBuilder result = new StringBuilder();
        result.append("<rdf:Description rdf:about=\"http://www.icardea.at/phrs/instances/");
        result.append(realProp);
        result.append("\">");
        result.append("<skos:prefLabel>");
        result.append(property);
        result.append("</skos:prefLabel>");

        if (relatedTo != null) {
            result.append(relatedTo);
        }


        result.append("<icadyCode:code>");
        result.append("<rdf:Description rdf:about=\"http://www.icardea.at/phrs/instances/");
        result.append(realProp);
        result.append("PHRSCode");
        result.append("\">");
        result.append("<icadyCode:codeValue>");
        result.append(code);
        result.append("</icadyCode:codeValue>");
        result.append("<icadyCode:codeSystem>");
        result.append("<rdf:Description rdf:about=\"http://www.icardea.at/phrs/instances/codeSystem/PhrsCS\"/>");
        result.append("</icadyCode:codeSystem>");
        result.append("</rdf:Description>");
        result.append("</icadyCode:code>");
        result.append("</rdf:Description>");




        return result.toString();
    }

    private static String buildProperty(String property) {

        if (property.indexOf(" ") == -1) {
            return property;
        }

        final StringBuilder result = new StringBuilder();
        final StringTokenizer stk = new StringTokenizer(property, " ");
        while (stk.hasMoreTokens()) {
            final String nextToken = stk.nextToken();
            final char charAt = nextToken.charAt(0);
            result.append(Character.toUpperCase(charAt));
            result.append(nextToken.substring(1));
        }

        return result.toString();

    }
}
