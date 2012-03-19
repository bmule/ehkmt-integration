
package at.srfg.kmt.ehealth.phrs.presentation.builder;

/*
@deprecated until primefaces is upgraded, this version of itext
is not supported at the UI level with primefaces 2.2.1
 */
//import com.itextpdf.text.Document;
//import com.itextpdf.text.DocumentException;
//import com.itextpdf.text.Paragraph;
//import com.itextpdf.text.pdf.PdfWriter;
//import com.itextpdf.text.pdf.codec.Base64.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ResourceBundle;


public class ReportToolPdf extends ReportTool {
    private final static Logger LOGGER = LoggerFactory
            .getLogger(ReportToolPdf.class);

    public ReportToolPdf() {
    }

    public ReportToolPdf(ResourceBundle resourceBundle) {
        super(resourceBundle);
    }

    /**
     * Path to the resulting PDF file.
     */
    public static final String RESULT
            = "testbuilder.pdf";

    /**
     * Creates a PDF file:
     *
     * @param args no arguments needed
     */
    public static void main(String[] args)
            throws IOException //DocumentException,
    {
        new ReportToolPdf().createPdf(RESULT);
    }

    /**
     * Creates a PDF document.
     *
     * @param filename the path to the new PDF document
     * @throws DocumentException
     * @throws IOException
     */
    public void createPdf(String filename)
            throws IOException //DocumentException,
    {

        // Document document = new Document();

        // PdfWriter.getInstance(document, new FileOutputStream(filename));

        // document.open();

        // document.add(new Paragraph("Hello World!"));

        // document.close();
    }

    public InputStream createPdf(String ownerUri, String resourceType)
            throws IOException //DocumentException,
    {

        InputStream inputStream = null;
        //Document document = new Document();

        //PhrDocumentBuilder pdb = new PhrDocumentBuilder(resourceType);
        //

        //PdfWriter.getInstance(document, new FileOutputStream(filename));
        //Document document = pdb.getPdf();
        //document.open();

        //document.add(new Paragraph("Hello World!"));

        //document.close();

        return inputStream;

    }

}
