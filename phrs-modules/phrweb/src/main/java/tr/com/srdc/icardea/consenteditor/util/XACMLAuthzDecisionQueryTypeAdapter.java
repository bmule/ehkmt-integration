package tr.com.srdc.icardea.consenteditor.util;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.opensaml.xacml.profile.saml.XACMLAuthzDecisionQueryType;
import org.opensaml.xacml.profile.saml.impl.XACMLAuthzDecisionQueryTypeMarshaller;
import org.opensaml.xacml.profile.saml.impl.XACMLAuthzDecisionQueryTypeUnmarshaller;

import tr.com.srdc.icardea.consenteditor.saml.MyXACMLAuthzDecisionQueryType;

public class XACMLAuthzDecisionQueryTypeAdapter extends
		XmlAdapter<MyXACMLAuthzDecisionQueryType, XACMLAuthzDecisionQueryType> {

	@Override
	public MyXACMLAuthzDecisionQueryType marshal(XACMLAuthzDecisionQueryType v)
			throws Exception {
		MyXACMLAuthzDecisionQueryType myXACMLAuthzDecisionQueryType = new MyXACMLAuthzDecisionQueryType();
		myXACMLAuthzDecisionQueryType.xacmlAuthzDecisionQueryType = new XACMLAuthzDecisionQueryTypeMarshaller().marshall(v);
		return myXACMLAuthzDecisionQueryType;
	}

	@Override
	public XACMLAuthzDecisionQueryType unmarshal(MyXACMLAuthzDecisionQueryType v)
			throws Exception {
		return (XACMLAuthzDecisionQueryType) new XACMLAuthzDecisionQueryTypeUnmarshaller().unmarshall(v.xacmlAuthzDecisionQueryType);		
	}

}
