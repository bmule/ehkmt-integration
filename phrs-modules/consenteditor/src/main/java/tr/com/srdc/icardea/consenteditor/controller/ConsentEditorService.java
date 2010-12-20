package tr.com.srdc.icardea.consenteditor.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import tr.com.srdc.icardea.consenteditor.ConfigurationParser;
import tr.com.srdc.icardea.consenteditor.controller.message.ConsentDocumentRequest;
import tr.com.srdc.icardea.consenteditor.controller.message.ConsentDocumentResponse;
import tr.com.srdc.icardea.consenteditor.controller.message.ConsentOperationResponse;
import tr.com.srdc.icardea.consenteditor.controller.message.GeneralConsentRequest;
import tr.com.srdc.icardea.consenteditor.controller.message.MyListResponse;
import tr.com.srdc.icardea.consenteditor.controller.message.PatientIDRequest;
import tr.com.srdc.icardea.consenteditor.controller.message.PatientIDResponse;
import tr.com.srdc.icardea.consenteditor.controller.message.RegisterConsentDocumentRequest;
import tr.com.srdc.icardea.consenteditor.controller.message.ResourceListResponse;
import tr.com.srdc.icardea.consenteditor.controller.message.SaveUpdateResourceListRequest;
import tr.com.srdc.icardea.consenteditor.controller.message.SaveUpdateSubjectListRequest;
import tr.com.srdc.icardea.consenteditor.controller.message.SubjectListResponse;
import tr.com.srdc.icardea.consenteditor.model.ActionElements;
import tr.com.srdc.icardea.consenteditor.model.Condition;
import tr.com.srdc.icardea.consenteditor.model.ConsentDocument;
import tr.com.srdc.icardea.consenteditor.model.ConsentRule;
import tr.com.srdc.icardea.consenteditor.model.ConsentRuleTarget;
import tr.com.srdc.icardea.consenteditor.model.ResourceElement;
import tr.com.srdc.icardea.consenteditor.model.ResourceElements;
import tr.com.srdc.icardea.consenteditor.model.SubjectElement;
import tr.com.srdc.icardea.consenteditor.model.SubjectElements;
import tr.com.srdc.icardea.consenteditor.util.JAXBUtil;
import tr.com.srdc.icardea.consenteditor.util.ModelUtil;

public class ConsentEditorService {

	private ConfigurationParser confParser;
	private PHRInterface phrInterface;

	public ConsentEditorService() {
		confParser = new ConfigurationParser();
		phrInterface = new PHRJPAImpl();
	}

	public ConsentOperationResponse setCurrentConsentDocumentOfPatient(
			GeneralConsentRequest request) {
		String consentID = request.getConsentID();
		ConsentDocument consentDocument = phrInterface.getConsentDocumentById(consentID);

		phrInterface.setCurrentConsentDocumentofPatient(consentDocument);

		ConsentOperationResponse tempResult = new ConsentOperationResponse();

		tempResult.message = consentID;
		return tempResult;
	}

	public ConsentDocumentResponse getConsentDocuments(
			ConsentDocumentRequest request) {
		ConsentDocumentResponse response = new ConsentDocumentResponse();
		// PHRImpl phrInterface = new PHRImpl();
		if (phrInterface == null) {
			phrInterface = new PHRJPAImpl();
		}
		List<ConsentDocument> cdList = phrInterface.getConsentDocuments(request.getPatientID());
		response.setConsentDocuments(cdList);
		System.out.println("CD Size:" + cdList.size());
		return response;
	}

	public ConsentOperationResponse deleteConsentDocument(
			GeneralConsentRequest request) {
		String consentID = request.getConsentID();
		phrInterface.deleteConsentDocument(consentID);
		return new ConsentOperationResponse();
	}

	public ResourceListResponse getResourceList(GeneralConsentRequest request) {
		ResourceListResponse resourceResponse = new ResourceListResponse();
		String patientID = request.getPatientID();
		ResourceElements resources = phrInterface.getResourceList(patientID);
		List<ResourceElement> temp = resources.getResourceElement();
		resourceResponse.setTempList(temp);
		return resourceResponse;
	}

	public SubjectListResponse getSubjectList(GeneralConsentRequest request) {
		SubjectListResponse subjectListResponse = new SubjectListResponse();
		String patientID = request.getPatientID();
		System.out.println("getSubJeCtLiSt1");
		System.out.println(patientID);
		SubjectElements subjects = phrInterface.getSubjectList(patientID);
		List<SubjectElement> temp = subjects.getSubjectElement();
		subjectListResponse.setTempList(temp);
		return subjectListResponse;
	}

	// public ConsentRuleResponse getMyCurrentConsentDocument(
	// GeneralConsentRequest request) {
	// ConsentRuleResponse response = new ConsentRuleResponse();
	// try {
	// ConsentDocumentMetaDataType consentID = ConsentEditorDatabaseManager
	// .getInstance().retrieveCurrentConsentDetails(
	// request.getPatientID());
	// String filepath = ConsentEditorDatabaseManager.getInstance()
	// .retrieveFormPath(consentID.getId());
	// if (filepath != null) {
	//
	// ConsentDocument vocs = (ConsentDocument) JAXBUtil.unmarshall(
	// filepath);
	//
	// List<ConsentRule> temp = vocs.getConsentRule();
	// response.setConsentRules(temp);
	// response.setId(consentID.getId());
	// response.setDefinition(consentID.getDefinition());
	// response.setExpirationDate(consentID.getExpirationDate());
	//
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// return response;
	// }

	public MyListResponse getActionList(GeneralConsentRequest request) {
		MyListResponse actionResponse = new MyListResponse();
		String patientID = request.getPatientID();
		ActionElements actions = phrInterface.getActionList(patientID);
		List<String> temp = actions.getAction();
		actionResponse.setTempList(temp);
		return actionResponse;
	}

	public ConsentOperationResponse registerConsentDocument(
			RegisterConsentDocumentRequest request) {
		Date now = new Date();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		ConsentDocument consentDocument = request.getConsentDocument();
		if (consentDocument.getConsentDocumentMetaData().getCreationDate() == null) {
			consentDocument.getConsentDocumentMetaData().setCreationDate(
					df.format(now));
		}
		consentDocument.getConsentDocumentMetaData().setModificationDate(
				df.format(now));
		phrInterface.registerConsentDocument(consentDocument);

		ConsentOperationResponse tempResult = new ConsentOperationResponse();

		tempResult.message = consentDocument.getId();
		return tempResult;
	}

	public PatientIDResponse getPatientID(PatientIDRequest request) {
		System.out.println("GETPATIENTID");
		PatientIDResponse response = new PatientIDResponse();
		response.setPatientID(phrInterface.getPatientId());
		return response;
	}

	public ConsentDocumentResponse getCurrentConsentDocumentByPatientId(
			ConsentDocumentRequest request) {
		ConsentDocumentResponse response = new ConsentDocumentResponse();
		List<ConsentDocument> cdList = new ArrayList();
		ConsentDocument cd = phrInterface.getCurrentConsentDocumentByPatientId(
								request.getPatientID());
		cdList.add(cd);
		response.setConsentDocuments(cdList);
		return response;
	}

	public ConsentDocumentResponse saveSubjectList(
			SaveUpdateSubjectListRequest request) {
		ConsentDocumentResponse response = new ConsentDocumentResponse();

		SubjectElements subjectElements = new SubjectElements();
		subjectElements.setSubjectElement(request.getSubjectList());
		subjectElements.setPatientId(request.getPatientID());
		System.out.println("SUBJECTS SIZE");
		System.out.println(subjectElements.getSubjectElement().size());
		phrInterface.saveSubjectList(subjectElements);

		return response;
	}

	public ConsentDocumentResponse saveResourceList(
			SaveUpdateResourceListRequest request) {
		ConsentDocumentResponse response = new ConsentDocumentResponse();

		ResourceElements resourceElements = new ResourceElements();
		resourceElements.setResourceElement(request.getResourceList());
		resourceElements.setPatientId(request.getPatientID());
		phrInterface.saveResourceList(resourceElements);

		return response;
	}

	public ConsentOperationResponse createXACMLPolicy(
			GeneralConsentRequest request) {
		ConsentOperationResponse response = new ConsentOperationResponse();
		try {
			String consentID = request.getConsentID();
			response.message = "/download/" + confParser.getXacmlDocuments()
					+ "PC-" + consentID + ".xml";
			String filePath = confParser.getXacmlDocuments();
			URL myURL = ConsentEditorService.class.getClassLoader()
					.getResource(filePath);
			filePath = myURL.toString();
			if (filePath.startsWith("file:/")) {
				filePath = filePath.substring(6);
			}
			System.out.println(filePath);
			File filePPS1 = new File(filePath + "PC-" + consentID + ".xml");
			String filePath2 = confParser.getAbstractPath()
					+ confParser.getXacmlDocuments() + "PC-" + consentID
					+ ".xml";
			File filePPS2 = new File(filePath2);
			File[] filePPSs = new File[2];
			filePPSs[0] = filePPS1;
			filePPSs[1] = filePPS2;

			for (int t = 0; t < 2; t++) {
				File filePPS = filePPSs[t];
				filePPS.createNewFile();
				FileOutputStream fileOS = null;
				fileOS = new FileOutputStream(filePPS);

				PrintStream fileWriter = new PrintStream(fileOS);

				// header tag and namespaces
				fileWriter
						.println("<PolicySet "
								+ "xmlns=\"urn:oasis:names:tc:xacml:2.0:policy:schema:os\" "
								+ "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
								+ "xsi:schemaLocation=\"urn:oasis:names:tc:xacml:2.0:policy:schema:os "
								+ "http://docs.oasis-open.org/xacml/access_control-xacml-2.0-policy-schema-os.xsd\" "
								+ "PolicySetId=\""
								+ consentID
								+ "\" "
								+ "PolicyCombiningAlgId=\"urn:oasis:names:tc:xacml:1.0:policy-combining-algorithm:deny-overrides\">");
				ConsentDocument consentDocument = phrInterface
								.getConsentDocumentById(consentID);
				fileWriter.println("<Description>"
						+ consentDocument.getConsentDocumentMetaData()
								.getDefinition() + "</Description>");
				fileWriter.println("<Target/>");
				for (int i = 0; i < consentDocument.getConsentRule().size(); i++) {
					ConsentRule consentRule = consentDocument.getConsentRule()
							.get(i);
					ConsentRuleTarget consentRuleTarget = consentRule
							.getConsentRuleTarget();
					fileWriter
							.println("<Policy "
									+ "xmlns=\"urn:oasis:names:tc:xacml:2.0:policy:schema:os\" "
									+ "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
									+ "PolicyId=\""
									+ consentRule.getId()
									+ "\" "
									+ "RuleCombiningAlgId=\"urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:permit-overrides\">");
					fileWriter.println("<Description>"
							+ consentRule.getDescription() + "</Description>");
					fileWriter.println("<Target>");
					fileWriter.println("<Subjects>");
					for (int j = 0; j < consentRuleTarget.getSubjectElements()
							.getSubjectElement().size(); j++) {
						SubjectElement subjectElement = consentRuleTarget
								.getSubjectElements().getSubjectElement()
								.get(j);
						fileWriter.println("<Subject>");
						fileWriter
								.println("<SubjectMatch MatchId=\"urn:oasis:names:tc:xacml:1.0:function:string-equal\">");
						fileWriter
								.println("<AttributeValue DataType=\"http://www.w3.org/2001/XMLSchema#string\">"
										+ ModelUtil
												.findSubjectId(subjectElement)
										+ "</AttributeValue>");
						fileWriter
								.println("<SubjectAttributeDesignator AttributeId=\"urn:oasis:names:tc:xacml:1.0:subject:subject-id\" DataType=\"http://www.w3.org/2001/XMLSchema#string\"/>");
						fileWriter.println("</SubjectMatch>");
						fileWriter.println("</Subject>");
					}
					fileWriter.println("</Subjects>");
					fileWriter.println("<Resources>");
					for (int j = 0; j < consentRuleTarget.getResourceElements()
							.getResourceElement().size(); j++) {
						ResourceElement resourceElement = consentRuleTarget
								.getResourceElements().getResourceElement()
								.get(j);
						fileWriter.println("<Resource>");
						fileWriter
								.println("<ResourceMatch MatchId=\"urn:oasis:names:tc:xacml:1.0:function:string-equal\">");
						fileWriter
								.println("<AttributeValue DataType=\"http://www.w3.org/2001/XMLSchema#string\">"
										+ resourceElement.getId()
										+ "</AttributeValue>");
						fileWriter
								.println("<ResourceAttributeDesignator AttributeId=\"urn:oasis:names:tc:xacml:1.0:resource:resource-id\" DataType=\"http://www.w3.org/2001/XMLSchema#string\"/>");
						fileWriter.println("</ResourceMatch>");
						fileWriter.println("</Resource>");
					}
					fileWriter.println("</Resources>");
					fileWriter.println("<Actions>");
					for (int j = 0; j < consentRuleTarget.getActionElements()
							.getAction().size(); j++) {
						String actionElement = consentRuleTarget
								.getActionElements().getAction().get(j);
						fileWriter.println("<Action>");
						fileWriter
								.println("<ActionMatch MatchId=\"urn:oasis:names:tc:xacml:1.0:function:string-equal\">");
						fileWriter
								.println("<AttributeValue DataType=\"http://www.w3.org/2001/XMLSchema#string\">"
										+ actionElement + "</AttributeValue>");
						fileWriter
								.println("<ActionAttributeDesignator AttributeId=\"urn:oasis:names:tc:xacml:1.0:action:action-id\" DataType=\"http://www.w3.org/2001/XMLSchema#string\"/>");
						fileWriter.println("</ActionMatch>");
						fileWriter.println("</Action>");
					}
					fileWriter.println("</Actions>");
					fileWriter.println("</Target>");
					String effect = "";
					if (consentRule.getIsAllow() == 1) {
						effect = "Permit";
					} else {
						effect = "Deny";
					}
					fileWriter.println("<Rule RuleId=\"" + consentRule.getId()
							+ ":rule1\"" + " Effect=\"" + effect + "\">");
					for (int j = 0; j < consentRule.getCondition().size(); j++) {
						Condition condition = consentRule.getCondition().get(j);
						fileWriter.println("<Condition>");
						fileWriter
								.println("<Apply FunctionId=\"urn:oasis:names:tc:xacml:1.0:function:and\">");
						fileWriter
								.println("<Apply FunctionId=\"urn:oasis:names:tc:xacml:1.0:function:dateTime-greater-than\">");
						fileWriter
								.println("<EnvironmentAttributeDesignator AttributeId=\"urn:oasis:names:tc:xacml:1.0:environment:current-date\" DataType=\"http://www.w3.org/2001/XMLSchema#date\"/>");
						fileWriter
								.println("<AttributeValue DataType=\"http://www.w3.org/2001/XMLSchema#dateTime\">"
										+ condition.getTimeConstraint()
												.getAllowFrom()
										+ "</AttributeValue>");
						fileWriter.println("</Apply>");
						fileWriter
								.println("<Apply FunctionId=\"urn:oasis:names:tc:xacml:1.0:function:dateTime-less-than\">");
						fileWriter
								.println("<EnvironmentAttributeDesignator AttributeId=\"urn:oasis:names:tc:xacml:1.0:environment:current-date\" DataType=\"http://www.w3.org/2001/XMLSchema#date\"/>");
						fileWriter
								.println("<AttributeValue DataType=\"http://www.w3.org/2001/XMLSchema#dateTime\">"
										+ condition.getTimeConstraint()
												.getAllowUntil()
										+ "</AttributeValue>");
						fileWriter.println("</Apply>");
						fileWriter.println("</Apply>");
						fileWriter.println("</Condition>");
					}
					fileWriter.println("</Rule>");
					if (consentRule.getObligation() != null) {
						fileWriter.println("<Obligations>");
						for (int j = 0; j < consentRule.getObligation()
								.getSentEmailTo().getTo().size(); j++) {
							fileWriter
									.println("<Obligation ObligationId=\"urn:oasis:names:tc:xacml:example:obligation:email\" FulfillOn=\"Permit\">");
							fileWriter
									.println("<AttributeAssignment AttributeId=\"urn:oasis:names:tc:xacml:2.0:example:attribute:mailto\" DataType=\"http://www.w3.org/2001/XMLSchema#string\">"
											+ consentRule.getObligation()
													.getSentEmailTo().getTo()
													.get(j)
											+ "</AttributeAssignment>");
							fileWriter
									.println("<AttributeAssignment AttributeId=\"urn:oasis:names:tc:xacml:2.0:example:attribute:text\" DataType=\"http://www.w3.org/2001/XMLSchema#string\">Your medical record has been accessed by:"
											+ "</AttributeAssignment>");
							fileWriter
									.println("<AttributeAssignment AttributeId=\"urn:oasis:names:tc:xacml:2.0:example:attribute:text\" DataType=\"http://www.w3.org/2001/XMLSchema#string\">");
							fileWriter
									.println("<SubjectAttributeDesignator AttributeId=\"urn:oasis:names:tc:xacml:1.0:subject:subject-id\" DataType=\"http://www.w3.org/2001/XMLSchema#string\"/>");
							fileWriter.println("</AttributeAssignment>");
							fileWriter.println("</Obligation>");
						}
						fileWriter.println("</Obligations>");
					}
					fileWriter.println("</Policy>");
				}
				fileWriter.println("</PolicySet>");
				fileWriter.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return response;
	}
}
