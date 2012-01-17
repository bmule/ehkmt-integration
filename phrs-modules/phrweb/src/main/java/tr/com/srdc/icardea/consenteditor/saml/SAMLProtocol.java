package tr.com.srdc.icardea.consenteditor.saml;

import java.io.IOException;

import javax.xml.transform.TransformerException;

import org.joda.time.DateTime;
import org.opensaml.saml2.core.Assertion;
import org.opensaml.saml2.core.Attribute;
import org.opensaml.saml2.core.AttributeQuery;
import org.opensaml.saml2.core.AuthnContext;
import org.opensaml.saml2.core.AuthnContextClassRef;
import org.opensaml.saml2.core.AuthnQuery;
import org.opensaml.saml2.core.Issuer;
import org.opensaml.saml2.core.NameID;
import org.opensaml.saml2.core.RequestAbstractType;
import org.opensaml.saml2.core.RequestedAuthnContext;
import org.opensaml.saml2.core.Response;
import org.opensaml.saml2.core.Subject;
import org.opensaml.xacml.ctx.ActionType;
import org.opensaml.xacml.ctx.AttributeType;
import org.opensaml.xacml.ctx.AttributeValueType;
import org.opensaml.xacml.ctx.RequestType;
import org.opensaml.xacml.ctx.SubjectType;
import org.opensaml.xacml.ctx.impl.RequestTypeImplBuilder;
import org.opensaml.xacml.profile.saml.XACMLAuthzDecisionQueryType;
import org.opensaml.xml.io.MarshallingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
Simple examples of coding to the OpenSAML API.
Methods here can write SAMLP queries and responses for each of the three
main types: authentication, authorization decision, and attributes.

@author Will Provost
 */
/*
Copyright 2006-2009 Will Provost.
All rights reserved by Capstone Courseware, LLC.
 */
public class SAMLProtocol
	extends SAMLAssertion {

	private static final String QUERY_SUFFIX = "Query.xml";
	private static final String RESPONSE_SUFFIX = "Response.xml";
	public static Logger logger = LoggerFactory.getLogger(SAMLProtocol.class);

	private static void die() {
		logger.info("Need arguments:");
		logger.info("  <query|response> <authn|attr|authz> <simple-name>");
		System.exit(-1);
	}

	/**
	Parses the command line for instructions to write a SAML query or
	response in one of the three main types, and for a base filename.
	The command methods will automatically append either "Query.xml" or
	"Response.xml" to the base name.
	 */
	public static void main(String[] args)
		throws Exception {
		if (args.length < 2) {
			die();
		}

		String command = args[0];
		String type = args[1];
		String filename = args.length > 2 ? args[2] : null;

		SAMLProtocol handler = new SAMLProtocol();

		if (command.equals("query") && type.equals("authn")) {
			handler.printToFile(handler.createStockAuthnQuery(),
				filename != null ? filename + QUERY_SUFFIX : null);
		} else if (command.equals("response") && type.equals("authn")) {
			handler.printResponse(handler.createStockAuthnAssertion(), filename);
		} else if (command.equals("query") && type.equals("attr")) {
			handler.printToFile(handler.createStockAttributeQuery(),
				filename != null ? filename + QUERY_SUFFIX : null);
		} else if (command.equals("response") && type.equals("attr")) {
			handler.printResponse(handler.createStockAttributeAssertion(), filename);
		} else if (command.equals("query") && type.equals("authz")) {
			handler.printToFile(handler.createStockAuthzDecisionQuery(),
				filename != null ? filename + QUERY_SUFFIX : null);
		} else if (command.equals("response") && type.equals("authz")) {
			handler.printResponse(handler.createStockAuthzDecisionAssertion(), filename);
		} else {
			die();
		}
	}

	/**
	Helper method to generate and pretty-print a response, based on a given
	query (for our inResponseTo value) and an assertion.
	 */
	public void printResponse(Assertion assertion, String filename)
		throws IOException, MarshallingException, TransformerException {
		Response response = createResponse(assertion);

		Issuer issuer = create(Issuer.class, Issuer.DEFAULT_ELEMENT_NAME);
		issuer.setValue("http://somecom.com/SomeJavaAssertingParty");
		response.setIssuer(issuer);

		if (filename != null) {
			try {
				RequestAbstractType query = (RequestAbstractType) readFromFile(filename + QUERY_SUFFIX);
				response.setInResponseTo(query.getID());
			} catch (Exception ex) {
				logger.info("Couldn't read corresponding query file; "
					+ "InResponseTo will be missing.");
			}
		}

		printToFile(response,
			filename != null ? filename + RESPONSE_SUFFIX : null);
	}

	/**
	Creates a file whose contents are a SAML authentication query.
	 */
	public AuthnQuery createStockAuthnQuery()
		throws Exception {
		DateTime now = new DateTime();
		Issuer issuer = create(Issuer.class, Issuer.DEFAULT_ELEMENT_NAME);
		issuer.setValue("http://somecom.com/SomeJavaRelyingParty");

		NameID nameID = create(NameID.class, NameID.DEFAULT_ELEMENT_NAME);
		nameID.setValue("harold_dt");

		Subject subject = create(Subject.class, Subject.DEFAULT_ELEMENT_NAME);
		subject.setNameID(nameID);

		AuthnContextClassRef ref = create(AuthnContextClassRef.class,
			AuthnContextClassRef.DEFAULT_ELEMENT_NAME);
		ref.setAuthnContextClassRef(AuthnContext.PPT_AUTHN_CTX);

		RequestedAuthnContext authnContext = create(RequestedAuthnContext.class,
			RequestedAuthnContext.DEFAULT_ELEMENT_NAME);
		authnContext.getAuthnContextClassRefs().add(ref);

		AuthnQuery query = create(AuthnQuery.class, AuthnQuery.DEFAULT_ELEMENT_NAME);
		query.setID("AuthnQuery12345789");
		query.setIssueInstant(now);
		query.setIssuer(issuer);
		query.setSubject(subject);
		query.setRequestedAuthnContext(authnContext);

		return query;
	}

	/**
	Creates a file whose contents are a SAML attribute query.
	 */
	public AttributeQuery createStockAttributeQuery()
		throws Exception {
		DateTime now = new DateTime();
		Issuer issuer = create(Issuer.class, Issuer.DEFAULT_ELEMENT_NAME);
		issuer.setValue("http://somecom.com/SomeJavaRelyingParty");

		NameID nameID = create(NameID.class, NameID.DEFAULT_ELEMENT_NAME);
		nameID.setValue("harold_dt");

		Subject subject = create(Subject.class, Subject.DEFAULT_ELEMENT_NAME);
		subject.setNameID(nameID);

		Attribute attribute1 = create(Attribute.class, Attribute.DEFAULT_ELEMENT_NAME);
		attribute1.setName("FullName");

		Attribute attribute2 = create(Attribute.class, Attribute.DEFAULT_ELEMENT_NAME);
		attribute2.setName("JobTitle");

		AttributeQuery query = create(AttributeQuery.class, AttributeQuery.DEFAULT_ELEMENT_NAME);
		query.setID("AttrQuery12345789");
		query.setIssueInstant(now);
		query.setIssuer(issuer);
		query.setSubject(subject);
		query.getAttributes().add(attribute1);
		query.getAttributes().add(attribute2);

		return query;
	}

	/**
	Creates a file whose contents are a SAML authorization-decision query.
	 */
	public XACMLAuthzDecisionQueryType createStockAuthzDecisionQuery()
		throws Exception {
		DateTime now = new DateTime();
		Issuer issuer = create(Issuer.class, Issuer.DEFAULT_ELEMENT_NAME);
		issuer.setValue("http://somecom.com/SomeJavaRelyingParty");

		AttributeType subjectAttr = create(AttributeType.class, AttributeType.DEFAULT_ELEMENT_NAME);
		subjectAttr.setAttributeID("urn:oasis:names:tc:xacml:1.0:subject:subject-id");
		subjectAttr.setDataType("http://www.w3.org/2001/XMLSchema#string");

		AttributeValueType subjectAttrValue = create(AttributeValueType.class, AttributeValueType.DEFAULT_ELEMENT_NAME);
		subjectAttrValue.setValue("ROLECODE:DOCTOR");
		subjectAttr.getAttributeValues().add(subjectAttrValue);

		SubjectType subject = create(SubjectType.class, SubjectType.DEFAULT_ELEMENT_NAME);
		subject.getAttributes().add(subjectAttr);



		ActionType action = create(ActionType.class, ActionType.DEFAULT_ELEMENT_NAME);

		AttributeType actionAttr = create(AttributeType.class, AttributeType.DEFAULT_ELEMENT_NAME);
		actionAttr.setAttributeID("urn:oasis:names:tc:xacml:1.0:subject:action-id");
		actionAttr.setDataType("http://www.w3.org/2001/XMLSchema#string");

		AttributeValueType actionAttrValue = create(AttributeValueType.class, AttributeValueType.DEFAULT_ELEMENT_NAME);
		actionAttrValue.setValue("read");
		actionAttr.getAttributeValues().add(actionAttrValue);

		action.getAttributes().add(actionAttr);





		XACMLAuthzDecisionQueryType query = create(XACMLAuthzDecisionQueryType.class,
			XACMLAuthzDecisionQueryType.DEFAULT_ELEMENT_NAME_XACML20);
		RequestType request = new RequestTypeImplBuilder().buildObject();
		request.getSubjects().add(subject);
		request.setAction(action);
		query.setID("AuthzQuery12345789");
		query.setIssueInstant(now);
		query.setIssuer(issuer);
		query.setRequest(request);

		return query;
	}
}
