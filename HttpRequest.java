/**
 * HttpRequest - HTTP request container and parser
 *
 * $Id: HttpRequest.java,v 1.2 2003/11/26 18:11:53 kangasha Exp $
 *
 */

import java.io.*;
import java.net.*;
import java.util.*;

public class HttpRequest {
    /** Help variables */
    final static String CRLF = "\r\n";
    final static int HTTP_PORT = 80;
    /** Store the request parameters */
    String method;
    String URI;
    String version;
    String headers = "";
    String postData = "";
    /** Server and port */
    private String host;
    private int port;
    private boolean hasBody = false;
    private int lengthOfBody = 0;

    /** Create HttpRequest by reading it from the client socket */
    public HttpRequest(BufferedReader from) {
        String firstLine = "";
        try {
            firstLine = from.readLine();
            //System.out.println("firstline: " + firstLine);
        } catch (IOException e) {
            System.out.println("Error reading request line: " + e);
        }

        String[] tmp = firstLine.split(" ");
        method = tmp[0]; // filled in
        URI = tmp[1];   // filled in
        version = tmp[2];   // filled in

        if (!method.equals("GET")) {
            System.out.println("Error: Method not GET");
        }
        if (method.equals("POST")) {
            System.out.println("Method is POST");
            hasBody = true;
        }
        try {
            String line = from.readLine();
            while (line.length() != 0) {

                // get the length of the body for post request
                if (line.startsWith("Content-Length") || line.startsWith("Content-length"))
                {
                    String[] tmpStr = line.split(" ");
                    lengthOfBody = Integer.parseInt(tmpStr[1]);
                }

                //System.out.println("other line: " + line);
                headers += line + CRLF;
                /* We need to find host header to know which server to
                 * contact in case the request URI is not complete. */
                if (line.startsWith("Host:")) {
                    tmp = line.split(" ");
                    if (tmp[1].indexOf(':') > 0) {
                        String[] tmp2 = tmp[1].split(":");
                        host = tmp2[0];
                        port = Integer.parseInt(tmp2[1]);
                    } else {
                        host = tmp[1];
                        port = HTTP_PORT;
                    }
                }
                line = from.readLine();
            }

            // if we have a post request to parse
            if (hasBody)
            {
                System.out.println("Parsing post request... ");
                char[] body = new char[lengthOfBody];
                from.read(body, 0, lengthOfBody);
                postData = new String(body);
            }
        } catch (IOException e) {
            System.out.println("Error reading from socket: " + e);
            return;
        }

        System.out.println("Host to contact is: " + host + " at port " + port);
    }

    /** Return host for which this request is intended */
    public String getHost() {
        return host;
    }

    /** Return port for server */
    public int getPort() {
        return port;
    }

    /**
     * Convert request into a string for easy re-sending.
     */
    public String toString() {
        String req = "";

        req = method + " " + URI + " " + version + CRLF;
        req += headers;
        /* This proxy does not support persistent connections */
        req += "Connection: close" + CRLF;
        req += CRLF;

        return req;
    }
}