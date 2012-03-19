
package at.srfg.kmt.ehealth.phrs.presentation.builder;

/*
@deprecated until primefaces upgraded
This itext version not supported by UI    primefaces 2.2.1
*/

public class Sample {
    //private final static Logger LOGGER = LoggerFactory.getLogger(Sample.class);
	private static String TEST_FILE = "~/testpdf.pdf";
//
//	private static Font FONT_CHAPTER_HEADING = new Font(Font.FontFamily.TIMES_ROMAN, 18,
//			Font.BOLD);
//	private static Font FONT_NORMAL_RED = new Font(Font.FontFamily.TIMES_ROMAN, 12,
//			Font.NORMAL, BaseColor.RED);
//	private static Font FONT_SUBCHAPTER_HEADING = new Font(Font.FontFamily.TIMES_ROMAN, 16,
//			Font.BOLD);
//	private static Font FONT_SMALL_BOLD = new Font(Font.FontFamily.TIMES_ROMAN, 12,
//			Font.BOLD);
//
//        private static Font FONT_NORMAL_BOLD = new Font(Font.FontFamily.TIMES_ROMAN, 12,
//			Font.BOLD);
//	private static Font FONT_SMALL_CONTENT = new Font(Font.FontFamily.TIMES_ROMAN, 10,
//			Font.NORMAL);
//
//        private String aboutOwnerUri,  resourceType,  language, requestorUri =null;
//        private Locale locale=null;
//        private ResourceBundle resBundle=null;
//
//
//        public Sample(String aboutOwnerUri, String resourceType, Locale locale, String requestorUri){
//
//            this.aboutOwnerUri=aboutOwnerUri;
//            this.resourceType=resourceType;
//            this.locale=locale;
//            if(locale==null) locale = Locale.ENGLISH;
//
//            this.language=locale.getLanguage();
//            resBundle = ResourceBundle.getBundle("Messagelabels", locale);
//        }
//
//        public String getLabel(String code,String defaultLabel){
//            String label=null;
//
//
//            if(label==null) label=defaultLabel;
//            return label;
//        }
//	public static void main(String[] args) {
//                String aboutOwnerUri="";
//                String resourceType="";
//
//		try {
//			Document document = new Document();
//			PdfWriter.getInstance(document, new FileOutputStream(TEST_FILE));
//			document.open();
//
//			addMetaData(document,   aboutOwnerUri,  resourceType);
//			addTitlePage(document,  aboutOwnerUri,  resourceType);
//			addContent(document,    aboutOwnerUri,  resourceType);
//			document.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//        public void  processAll(String aboutOwnerUri, String resourceType, Map<String,String> map, ByteArrayOutputStream baosPDF) {
//		try {
//			Document doc = new Document();
//                        //PdfWriter.getInstance(document, new FileOutputStream(FILE));
//			//ByteArrayOutputStream baosPDF = new ByteArrayOutputStream();
//                        if(baosPDF==null) baosPDF = new ByteArrayOutputStream();
//                        PdfWriter docWriter = null;
//                        docWriter = PdfWriter.getInstance(doc, baosPDF);
//
//			doc.open();
//			addMetaData(doc, aboutOwnerUri,resourceType);
//			addTitlePage(doc, aboutOwnerUri,resourceType);
//			addContent(doc, aboutOwnerUri,resourceType);
//			doc.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//                        LOGGER.error("aboutOwnerUri="+aboutOwnerUri+" resourceType="+resourceType,e);
//		}
//	}
//        public Document processCreate(String aboutOwnerUri, String resourceType, Map<String,String> map, boolean close){
//            Document doc = null;
//            try {
//                doc = new Document();
//                ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                PdfWriter.getInstance(doc, baos);
//                doc.open();
//
//                doc.add(new Paragraph("Hello World!"));
//                if (close) {
//                    doc.close();
//                }
//            } catch (DocumentException e) {
//                LOGGER.error("aboutOwnerUri="+aboutOwnerUri+" resourceType="+resourceType,e);
//            } catch (Exception e) {
//                LOGGER.error("aboutOwnerUri="+aboutOwnerUri+" resourceType="+resourceType,e);
//            }
//
//            //write doc to a file...
//            /*
//            FileOutputStream fos = new FileOutputStream(RESULT);
//            fos.write(baos.toByteArray());
//            fos.close();
//            * */
//            return doc;
//        }
//
//	// iText allows to add metadata to the PDF which can be viewed in your Adobe
//	// Reader
//	// under File -> Properties
//	protected static void addMetaData(Document document, String aboutOwnerUri,String resourceType) {
//		document.addTitle("Personal Health Report");
//		document.addSubject("Using iText");
//		document.addKeywords("phr, PDF, health");
//		//document.addAuthor("");
//		//document.addCreator("");
//	}
//
//	protected static void addTitlePage(Document document,String aboutOwnerUri, String resourceType)
//			throws DocumentException {
//		Paragraph preface = new Paragraph();
//		// We add one empty line
//		addEmptyLine(preface, 1);
//		// Lets write a big header
//		preface.add(new Paragraph("Title of the document", FONT_CHAPTER_HEADING));
//
//		addEmptyLine(preface, 1);
//		// Will create: Report generated by: _name, _date
//		preface.add(new Paragraph(
//				"Report generated by: " + System.getProperty("user.name") + ", " + new Date(), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//				FONT_SMALL_BOLD));
//		addEmptyLine(preface, 3);
//		preface.add(new Paragraph(
//				"This document describes something which is very important ",
//				FONT_SMALL_BOLD));
//
//		addEmptyLine(preface, 8);
//
//		preface.add(new Paragraph(
//				"This document is a preliminary version and not subject to your license agreement or any other agreement with vogella.de ;-).",
//				FONT_NORMAL_RED));
//
//		document.add(preface);
//		// Start a new page
//		document.newPage();
//	}
//
//	protected static void addContent(Document document,String aboutOwnerUri, String resourceType) throws DocumentException {
//		Anchor anchor = new Anchor("First Chapter", FONT_CHAPTER_HEADING);
//		anchor.setName("First Chapter");
//
//		// Second parameter is the number of the chapter
//		Chapter catPart = new Chapter(new Paragraph(anchor), 1);
//
//		Paragraph subPara = new Paragraph("Subcategory 1", FONT_SUBCHAPTER_HEADING);
//		Section subCatPart = catPart.addSection(subPara);
//		subCatPart.add(new Paragraph("Hello"));
//
//		subPara = new Paragraph("Subcategory 2", FONT_SUBCHAPTER_HEADING);
//		subCatPart = catPart.addSection(subPara);
//		subCatPart.add(new Paragraph("Paragraph 1"));
//		subCatPart.add(new Paragraph("Paragraph 2"));
//		subCatPart.add(new Paragraph("Paragraph 3"));
//
//		// Add a list
//		createList(subCatPart);
//		Paragraph paragraph = new Paragraph();
//		addEmptyLine(paragraph, 5);
//		subCatPart.add(paragraph);
//
//		// Add a table
//		createTable(subCatPart);
//
//		// Now add all this to the document
//		document.add(catPart);
//
//		// Next section
//		anchor = new Anchor("Second Chapter", FONT_CHAPTER_HEADING);
//		anchor.setName("Second Chapter");
//
//		// Second parameter is the number of the chapter
//		catPart = new Chapter(new Paragraph(anchor), 1);
//
//		subPara = new Paragraph("Subcategory", FONT_SUBCHAPTER_HEADING);
//		subCatPart = catPart.addSection(subPara);
//		subCatPart.add(new Paragraph("This is a very important message"));
//
//		// Now add all this to the document
//		document.add(catPart);
//
//	}
///*
// * list of  headerRowList of I18, issue language...
// */
//	protected static void createTable(Section subCatPart)
//			throws BadElementException {
//		PdfPTable table = new PdfPTable(3);
//
//		// t.setBorderColor(BaseColor.GRAY);
//		// t.setPadding(4);
//		// t.setSpacing(4);
//		// t.setBorderWidth(1);
//
//		PdfPCell c1 = new PdfPCell(new Phrase("Table Header 1"));
//		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
//		table.addCell(c1);
//
//		c1 = new PdfPCell(new Phrase("Table Header 2"));
//		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
//		table.addCell(c1);
//
//		c1 = new PdfPCell(new Phrase("Table Header 3"));
//		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
//		table.addCell(c1);
//		table.setHeaderRows(1);
//
//		table.addCell("1.0");
//		table.addCell("1.1");
//		table.addCell("1.2");
//		table.addCell("2.1");
//		table.addCell("2.2");
//		table.addCell("2.3");
//
//		subCatPart.add(table);
//
//	}
//
//	private static void createList(Section subCatPart) {
//		List list = new List(true, false, 10);
//		list.add(new ListItem("First point"));
//		list.add(new ListItem("Second point"));
//		list.add(new ListItem("Third point"));
//		subCatPart.add(list);
//	}
//
//	protected static void addEmptyLine(Paragraph paragraph, int number) {
//		for (int i = 0; i < number; i++) {
//			paragraph.add(new Paragraph(" "));
//		}
//	}
}

