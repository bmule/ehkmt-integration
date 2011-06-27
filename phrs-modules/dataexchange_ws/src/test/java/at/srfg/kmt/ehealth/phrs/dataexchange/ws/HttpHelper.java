/*
 * Project :iCardea
 * File : HttpHelper.java
 * Encoding : UTF-8
 * Date : May 16, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.dataexchange.ws;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.Map;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;


/**
 *
 * @version 0.1
 * @since 0.1
 * @author mradules
 */
final class HttpHelper {

    private static final String PREFIX = "http://";

    /**
     * Don't let anybody to instantiate this class.
     */
    private HttpHelper() {
        // UNIMPLEMENTED
    }

    static private String buildURI(String host, int port, String contextPath,
            String application, Map<String, String> queryParameters) throws UnsupportedEncodingException {

        final StringBuilder result = new StringBuilder();
        result.append(PREFIX);
        result.append(host);
        result.append(":");
        result.append(port);
        result.append("/");
        result.append(contextPath);
        result.append("/");
        result.append(application);
        
        if (queryParameters == null) {
            return result.toString();
        }

        if (!queryParameters.isEmpty()) {
            result.append("?");
        }

        for (Map.Entry<String, String> entry : queryParameters.entrySet()) {
            final String key = entry.getKey();
            final String value = entry.getValue();
            if (value != null) {
                result.append(URLEncoder.encode(key, "utf-8"));
                result.append("=");

                result.append(URLEncoder.encode(value, "utf-8"));
                result.append("&");
            }
        }

        if (result.charAt(result.length() - 1) == '&') {
            result.deleteCharAt(result.length() - 1);
        }

        return result.toString();
    }

    static HttpGet buildGET(String host, int port, String contextPath,
            String applicationPath, Map<String, String> queryParameters)
            throws URISyntaxException, IOException {

        final String uriStr =
                buildURI(host, port, contextPath, applicationPath, queryParameters);
        final URI uri = new URI(uriStr);

        final HttpGet result = new HttpGet(uri);
        return result;
    }

    static HttpResponse excecute(HttpRequestBase method) throws IOException {
        final HttpClient httpClient = new DefaultHttpClient();
        final HttpResponse response = httpClient.execute(method);
        return response;
    }

    static HttpGet buildGET(String host, int port, String contextPath,
            String application, Map<String, String> queryParameters,
            String acceptType) throws URISyntaxException, IOException {

        final String uriStr =
                buildURI(host, port, contextPath, application, queryParameters);
        final URI uri = new URI(uriStr);

        final HttpGet result = new HttpGet(uri);
        if (acceptType != null) {
            result.setHeader("Accept", acceptType);
        }
        
        return result;
    }
}
