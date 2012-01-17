/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.srfg.kmt.ehealth.phrs.presentation.builder;


import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.codec.Base64.InputStream;
import java.io.FileOutputStream;
import java.io.IOException;


public class BuilderPdfitext {
       /** Path to the resulting PDF file. */
    public static final String RESULT
        = "testbuilder.pdf";
 
    /**
     * Creates a PDF file: 
     * @param    args    no arguments needed
     */
    public static void main(String[] args)
    	throws DocumentException, IOException {
    	new BuilderPdfitext().createPdf(RESULT);
    }
 
    /**
     * Creates a PDF document.
     * @param filename the path to the new PDF document
     * @throws    DocumentException 
     * @throws    IOException 
     */
    public void createPdf(String filename)
	throws DocumentException, IOException {
 
        Document document = new Document();
      
        PdfWriter.getInstance(document, new FileOutputStream(filename));
       
        document.open();
     
        document.add(new Paragraph("Hello World!"));
     
        document.close();
    }
        public InputStream createPdf(String ownerUri,String resourceType)
	throws DocumentException, IOException {
            
        InputStream inputStream=null;
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
