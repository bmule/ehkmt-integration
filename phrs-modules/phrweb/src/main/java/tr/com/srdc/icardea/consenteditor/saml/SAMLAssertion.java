package tr.com.srdc.icardea.consenteditor.saml;



import org.joda.time.DateTime;
import org.opensaml.saml2.core.Action;
import org.opensaml.saml2.core.Assertion;
import org.opensaml.saml2.core.Attribute;
import org.opensaml.saml2.core.AttributeStatement;
import org.opensaml.saml2.core.AttributeValue;
import org.opensaml.saml2.core.AuthnContext;
import org.opensaml.saml2.core.AuthnContextClassRef;
import org.opensaml.saml2.core.AuthnStatement;
import org.opensaml.saml2.core.AuthzDecisionStatement;
import org.opensaml.saml2.core.Conditions;
import org.opensaml.saml2.core.DecisionTypeEnumeration;
import org.opensaml.saml2.core.Issuer;
import org.opensaml.saml2.core.NameID;
import org.opensaml.saml2.core.Statement;
import org.opensaml.saml2.core.Subject;
import org.opensaml.xml.Configuration;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.XMLObjectBuilder;
import org.opensaml.xml.schema.XSAny;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
Simple examples of coding to the OpenSAML API.
Methods here can write and parse each of the three
main assertion types: authentication, authorization decision, and attributes.

@author Will Provost
*/
/*
Copyright 2006-2009 by Will Provost.  
All rights reserved by Capstone Courseware, LLC.
*/
public class SAMLAssertion
    extends SAML
{
	public static Logger logger = LoggerFactory.getLogger(SAMLAssertion.class);
    private static void die ()
    {
        logger.info ("Need arguments:");
        logger.info ("  <write|read> <authn|attr|authz> <filename>");
        System.exit (-1);
    }
    
    /**
    Parse the command line for instructions to write or read one of the
    three types of assertions and for a target filename.  Dispatch to 
    one of the six command methods on this class.
    */
    public static void main (String[] args)
        throws Exception
    {
        if (args.length < 2 || (args[0].equals ("read") && args.length < 3))
            die ();
        
        String command = args[0];
        String type = args[1];
        String filename = args.length > 2 ? args[2] : null;
        
        SAMLAssertion handler = new SAMLAssertion ();
        
        if (command.equals ("write") && type.equals ("authn"))
            handler.printToFile 
                (handler.createStockAuthnAssertion (), filename);
            
        else if (command.equals ("read") && type.equals ("authn"))
            handler.readAuthnAssertion (filename);
                
        else if (command.equals ("write") && type.equals ("attr"))
            handler.printToFile 
                (handler.createStockAttributeAssertion (), filename);
            
        else if (command.equals ("read") && type.equals ("attr"))
            handler.readAttributeAssertion (filename);
                
        else if (command.equals ("write") && type.equals ("authz"))
            handler.printToFile 
                (handler.createStockAuthzDecisionAssertion (), filename);
            
        else if (command.equals ("read") && type.equals ("authz"))
            handler.readAuthzDecisionAssertion (filename);
                
        else
            die ();
    }

    /**
    Creates a file whose contents are a SAML authentication assertion.
    */
    public Assertion createStockAuthnAssertion ()
        throws Exception
    {
        DateTime now = new DateTime ();
        Issuer issuer = create (Issuer.class, Issuer.DEFAULT_ELEMENT_NAME);
        issuer.setValue ("http://mycom.com/MyJavaAuthnService");
        
        NameID nameID = create (NameID.class, NameID.DEFAULT_ELEMENT_NAME);
        nameID.setValue ("harold_dt");
        
        Subject subject = create (Subject.class, Subject.DEFAULT_ELEMENT_NAME);
        subject.setNameID (nameID);
        
        Conditions conditions = create 
            (Conditions.class, Conditions.DEFAULT_ELEMENT_NAME);
        conditions.setNotBefore (now.minusSeconds (15));
        conditions.setNotOnOrAfter (now.plusSeconds (30));
        
        AuthnContextClassRef ref = create (AuthnContextClassRef.class, 
            AuthnContextClassRef.DEFAULT_ELEMENT_NAME);
        ref.setAuthnContextClassRef (AuthnContext.PPT_AUTHN_CTX);
        
        // As of this writing, OpenSAML doesn't model the wide range of
        // authentication context namespaces defined in SAML 2.0.
        // For a real project we'd probably move on to 
        //    XSAny objects, setting QNames and values each-by-each
        //    a JAXB mapping of the required schema
        //    DOM-building
        // For classroom purposes the road ends here ...
        
        AuthnContext authnContext = create 
            (AuthnContext.class, AuthnContext.DEFAULT_ELEMENT_NAME);
        authnContext.setAuthnContextClassRef (ref);

        AuthnStatement authnStatement = create 
            (AuthnStatement.class, AuthnStatement.DEFAULT_ELEMENT_NAME);
        authnStatement.setAuthnContext (authnContext);
        
        Assertion assertion = 
            create (Assertion.class, Assertion.DEFAULT_ELEMENT_NAME);
        assertion.setID ("Assertion12345789");
        assertion.setIssueInstant (now);
        assertion.setIssuer (issuer);
        assertion.setSubject (subject);
        assertion.getStatements ().add (authnStatement);
        
        return assertion;
    }
    
    /**
    Parses a SAML authentication assertion as found in a file,
    and prints out information about issuer, subject, and 
    authentication context.
    */
    public void readAuthnAssertion (String filename)
        throws Exception
    {
        Assertion assertion = (Assertion) readFromFile (filename);
        NameID nameID = assertion.getSubject ().getNameID ();
        
        logger.info ("Assertion issued by " +
            assertion.getIssuer ().getValue ());
        logger.info ("Subject name: " + nameID.getValue ());
        logger.info ("  (Format " + nameID.getFormat () + ")");
        
        logger.info ("Authentication context classes found:");
        for (Statement statement : assertion.getStatements ())
            if (statement instanceof AuthnStatement)
                logger.info ("  " + ((AuthnStatement) statement)
                    .getAuthnContext ().getAuthnContextClassRef ()
                    .getAuthnContextClassRef ());
    }
        
    /**
    Creates a file whose contents are a SAML attribute assertion.
    */
    public Assertion createStockAttributeAssertion ()
        throws Exception
    {
        DateTime now = new DateTime ();
        Issuer issuer = create (Issuer.class, Issuer.DEFAULT_ELEMENT_NAME);
        issuer.setValue ("http://mycom.com/MyJavaAttributeService");
        
        NameID nameID = create (NameID.class, NameID.DEFAULT_ELEMENT_NAME);
        nameID.setFormat (NameID.TRANSIENT);
        nameID.setValue ("ga489Slge8+0nio9=");
        
        Subject subject = create (Subject.class, Subject.DEFAULT_ELEMENT_NAME);
        subject.setNameID (nameID);
        
        Conditions conditions = create 
            (Conditions.class, Conditions.DEFAULT_ELEMENT_NAME);
        conditions.setNotBefore (now.minusMinutes (15));
        conditions.setNotOnOrAfter (now.plusMinutes (30));
        
        // Build attribute values as XMLObjects;
        //  there is an AttributeValue interface, but it's apparently dead code
        XMLObjectBuilder builder = Configuration.getBuilderFactory ()
            .getBuilder (XSAny.TYPE_NAME);

        XSAny value1 = (XSAny) builder.buildObject 
            (AttributeValue.DEFAULT_ELEMENT_NAME);
        value1.setTextContent ("William Whitford Provost");
        
        Attribute attribute1 = create 
            (Attribute.class, Attribute.DEFAULT_ELEMENT_NAME);
        attribute1.setName ("FullName");
        attribute1.getAttributeValues ().add (value1);
        
        XSAny value2 = (XSAny) builder.buildObject 
            (AttributeValue.DEFAULT_ELEMENT_NAME);
        value2.setTextContent ("Grand Poobah");
        
        Attribute attribute2 = create 
            (Attribute.class, Attribute.DEFAULT_ELEMENT_NAME);
        attribute2.setName ("JobTitle");
        attribute2.getAttributeValues ().add (value2);
        
        AttributeStatement statement = create (AttributeStatement.class, 
            AttributeStatement.DEFAULT_ELEMENT_NAME);
        statement.getAttributes ().add (attribute1);
        statement.getAttributes ().add (attribute2);

        Assertion assertion = 
            create (Assertion.class, Assertion.DEFAULT_ELEMENT_NAME);
        assertion.setID ("Assertion12345789");
        assertion.setIssueInstant (now);
        assertion.setIssuer (issuer);
        assertion.setSubject (subject);
        assertion.getStatements ().add (statement);
        
        return assertion;
    }
    
    /**
    Parses a SAML attribute assertion as found in a file,
    and prints out all attributes.
    */
    public void readAttributeAssertion (String filename)
        throws Exception
    {
        Assertion assertion = (Assertion) readFromFile (filename);
        NameID nameID = assertion.getSubject ().getNameID ();
        
        logger.info ("Assertion issued by " +
            assertion.getIssuer ().getValue ());
        logger.info ("Subject name: " + nameID.getValue ());
        logger.info ("  (Format " + nameID.getFormat () + ")");
        
        logger.info("Attributes found:");
        for (Statement statement : assertion.getStatements ())
            if (statement instanceof AttributeStatement)
                for (Attribute attribute : 
                        ((AttributeStatement) statement).getAttributes ())
                {
                    logger.info("  " + attribute.getName () + ": ");
                    for (XMLObject value : attribute.getAttributeValues ())
                        if (value instanceof XSAny)
                            logger.info 
                                (((XSAny) value).getTextContent () + " ");
                    logger.info("");
                }
    }
    
    /**
    Creates a file whose contents are a SAML authorization-decision assertion.
    */
    public Assertion createStockAuthzDecisionAssertion ()
        throws Exception
    {
        DateTime now = new DateTime ();
        Issuer issuer = create (Issuer.class, Issuer.DEFAULT_ELEMENT_NAME);
        issuer.setValue ("http://mycom.com/MyJavaAuthorizationService");
        
        NameID nameID = create (NameID.class, NameID.DEFAULT_ELEMENT_NAME);
        nameID.setFormat (NameID.TRANSIENT);
        nameID.setValue ("ga489Slge8+0nio9=");
        
        Subject subject = create (Subject.class, Subject.DEFAULT_ELEMENT_NAME);
        subject.setNameID (nameID);
        
        Conditions conditions = create 
            (Conditions.class, Conditions.DEFAULT_ELEMENT_NAME);
        conditions.setNotBefore (now.minusMinutes (15));
        conditions.setNotOnOrAfter (now.plusMinutes (30));
        
        Action action = create (Action.class, Action.DEFAULT_ELEMENT_NAME);
        action.setAction ("read");
        action.setNamespace (Action.RWEDC_NS_URI);
        
        AuthzDecisionStatement statement = create 
            (AuthzDecisionStatement.class, 
                AuthzDecisionStatement.DEFAULT_ELEMENT_NAME);
        statement.setResource ("http://mycom.com/Repository/Private");
        statement.setDecision (DecisionTypeEnumeration.PERMIT);
        statement.getActions ().add (action);

        Assertion assertion = 
            create (Assertion.class, Assertion.DEFAULT_ELEMENT_NAME);
        assertion.setID ("Assertion12345789");
        assertion.setIssueInstant (now);
        assertion.setIssuer (issuer);
        assertion.setSubject (subject);
        assertion.getStatements ().add (statement);
        
        return assertion;
    }
    
    /**
    Parses a SAML authorization-decision assertion as found in a file,
    and prints out the essential information.
    */
    public void readAuthzDecisionAssertion (String filename)
        throws Exception
    {
        Assertion assertion = (Assertion) readFromFile (filename);
        NameID nameID = assertion.getSubject ().getNameID ();
        
        logger.info ("Assertion issued by " +
            assertion.getIssuer ().getValue ());
        logger.info ("Subject name: " + nameID.getValue ());
        logger.info ("  (Format " + nameID.getFormat () + ")");
        
        logger.info ("Decisions found:");
        for (Statement statement : assertion.getStatements ())
            if (statement instanceof AuthzDecisionStatement)
            {
                AuthzDecisionStatement authz = 
                    (AuthzDecisionStatement) statement;
                logger.info ("  " + authz.getDecision () + " { ");
                for (Action action : authz.getActions ())
                    logger.info (action.getAction () + " ");
                logger.info ("} on " + authz.getResource ());
            }
    }
}