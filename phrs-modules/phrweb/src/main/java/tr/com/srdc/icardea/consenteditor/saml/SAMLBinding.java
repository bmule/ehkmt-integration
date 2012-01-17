package tr.com.srdc.icardea.consenteditor.saml;

import java.io.IOException;

import javax.xml.transform.TransformerException;

import org.opensaml.saml2.core.Assertion;
import org.opensaml.saml2.core.Issuer;
import org.opensaml.saml2.core.RequestAbstractType;
import org.opensaml.saml2.core.Response;
import org.opensaml.ws.soap.soap11.Body;
import org.opensaml.ws.soap.soap11.Envelope;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.MarshallingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
Simple examples of coding to the OpenSAML API.
Methods here can read and write SAML assertions, queries, and responses
using bindings including SOAP 1.1 over HTTP.
(Nothing in this class performs any actual HTTP messaging, however:
we continue to represent I/O using files and console streams.)

@author Will Provost
*/
/*
Copyright 2009 Will Provost.
All rights reserved by Capstone Courseware, LLC.
*/
public class SAMLBinding
    extends SAMLProtocol
{
    private static final String REQUEST_SUFFIX = "Request.xml";
    private static final String RESPONSE_SUFFIX = "Response.xml";
    public static Logger logger = LoggerFactory.getLogger(SAMLBinding.class);
    private static void die ()
    {
        logger.info ("Need arguments:");
        logger.info 
            ("  <request|response> <authn|attr|authz> [<simple-name>]");
        System.exit (-1);
    }
    
    /**
    Parses the command line for instructions to write a SAML request or
    response in one of the three main types, and for a base filename.
    The command methods will automatically append either "Request.xml" or
    "Response.xml" to the base name.  The file content in each case will be
    a SOAP Envelope, wrapping the request or response as the message body
    according to the SAML 2.0 SOAP binding.
    */
    public static void main (String[] args)
        throws Exception
    {
        if (args.length < 2)
            die ();
        
        String command = args[0];
        String type = args[1];
        String filename = args.length > 2 ? args[2] : null;
        
        SAMLBinding handler = new SAMLBinding ();
        
        if (command.equals ("request") && type.equals ("authn"))
            handler.wrapAndPrintToFile (handler.createStockAuthnQuery (), 
                filename != null ? filename + REQUEST_SUFFIX : null);
            
        else if (command.equals ("response") && type.equals ("authn"))
            handler.wrapAndPrintResponse 
                (handler.createStockAuthnAssertion (), filename);
                
        else if (command.equals ("request") && type.equals ("attr"))
            handler.wrapAndPrintToFile (handler.createStockAttributeQuery (), 
                filename != null ? filename + REQUEST_SUFFIX : null);
            
        else if (command.equals ("response") && type.equals ("attr"))
            handler.wrapAndPrintResponse 
                (handler.createStockAttributeAssertion (), filename);
                
        else if (command.equals ("request") && type.equals ("authz"))
            handler.wrapAndPrintToFile (handler.createStockAuthzDecisionQuery (),  
                filename != null ? filename + REQUEST_SUFFIX : null);
            
        else if (command.equals ("response") && type.equals ("authz"))
            handler.wrapAndPrintResponse 
                (handler.createStockAuthzDecisionAssertion (), filename);
                
        else
            die ();
    }

    /**
    Helper method to generate and pretty-print a SOAP envelope,
    based on an XML object and a filename.
    */
    public void wrapAndPrintToFile (XMLObject object, String filename)
        throws IOException, MarshallingException, TransformerException
    {
        Body body = create (Body.class, Body.DEFAULT_ELEMENT_NAME);
        body.getUnknownXMLObjects ().add (object);
        
        Envelope env = create (Envelope.class, Envelope.DEFAULT_ELEMENT_NAME);
        env.setBody (body);
        
        printToFile (env, filename);
    }

    /**
    Helper method to generate and pretty-print a SOAP response envelope, 
    based on a given request envelope (for our inResponseTo value) 
    and a pre-built assertion.
    */
    public void wrapAndPrintResponse (Assertion assertion, String filename)
        throws IOException, MarshallingException, TransformerException
    {
        Response response = createResponse (assertion);

        Issuer issuer = create (Issuer.class, Issuer.DEFAULT_ELEMENT_NAME);
        issuer.setValue ("http://somecom.com/SomeJavaAssertingParty");
        response.setIssuer (issuer);

        if (filename != null)
            try
            {
                Envelope env = (Envelope)
                    readFromFile (filename + REQUEST_SUFFIX);
                for (XMLObject object : env.getBody ().getUnknownXMLObjects ())
                    if (object instanceof RequestAbstractType)
                        response.setInResponseTo 
                            (((RequestAbstractType) object).getID ());
            }
            catch (Exception ex)
            {
                logger.info ("Couldn't read corresponding query file; " +
                    "InResponseTo will be missing.");
            }

        wrapAndPrintToFile (response, 
            filename != null ? filename + RESPONSE_SUFFIX : null);
    }
}
