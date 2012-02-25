package at.srfg.kmt.ehealth.phrs.presentation.builder;

import at.srfg.kmt.ehealth.phrs.presentation.utils.HealthyUtils;
import org.joda.time.DateTime;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

public class ReportUtil {
    private final static Logger LOGGER = LoggerFactory.getLogger(ReportUtil.class);

    /**
     * Dummy report, no access
     * @return
     */
    public static StreamedContent reportBuildDummy() {
        return ReportUtil.reportBuildDummy(null, null, null);
    }

    /**
     *
     * @param targetUserId
     * @param idType
     * @param resourceType
     * @return
     */
    public static StreamedContent reportBuildDummy(String targetUserId, String idType, String resourceType) {
        InputStream stream = null;
        StreamedContent reportFile = null;
        String downloadFilename = "/monitor_info_intro.pdf";

        reportFile = new DefaultStreamedContent(stream, "application/pdf", downloadFilename);

        //get default file
        stream = ReportUtil.class.getResourceAsStream(downloadFilename);

        if (stream != null) {
            reportFile = new DefaultStreamedContent(stream, "application/pdf", downloadFilename);

        }
        return reportFile;
    }

    /**
     * Handles the content access and building, streaming
     * @param targetUserId
     * @param isPhrId
     * @param resourceType
     * @param isPermitted
     * @return
     */
    public static StreamedContent handlePermittedReport(
            String targetUserId,
            boolean isPhrId,
            String resourceType,
            boolean isPermitted) {

        StreamedContent reportFile = null;

        String idType = null;

        if (isPhrId) idType = "phrid";

        try {
            if (targetUserId != null && resourceType != null) {
                //
            } else {
                LOGGER.debug(" request error null found : protocolId or phrId=" + isPhrId + " id=" + targetUserId + " resourceType=" + resourceType + " permitted? false");
            }
            LOGGER.debug("creating report for: isPhrId= "+isPhrId+" id="+targetUserId+" resourceType="+resourceType) ;
            reportFile = ReportUtil.reportBuildForResourceType(targetUserId, idType, resourceType, isPermitted);
        } catch (Exception e) {
            LOGGER.error(" targetUserId=" + targetUserId + "idType=" + " resourceType=" + resourceType + " idType=" + idType, e);
        }
        return reportFile;

    }

    public static StreamedContent reportBuildForResourceType(String targetUserId, String idType, String resourceType, boolean permitted) {

        StreamedContent reportFile = null;

        if (targetUserId != null && resourceType != null && permitted) {
            //Build report
        } else {
            permitted = false;
        }

        try {
            InputStream stream;
            String downloadFilename;

            if (permitted) {

                String stamp = HealthyUtils.formatDate(new DateTime(), null);
                downloadFilename = "monitor-phrinfo-" + stamp + ".pdf";
                //get default file
                stream = ReportUtil.class.getResourceAsStream(downloadFilename);

                if (stream != null) {
                    reportFile = new DefaultStreamedContent(stream, "application/pdf", downloadFilename);
                }

            } else {
                // get dummy report
                reportFile = ReportUtil.reportBuildDummy(targetUserId, idType, resourceType);
            }


        } catch (Exception e) {
            LOGGER.error(" targetUserId=" + targetUserId + "idType=" + " resourceType=" + resourceType + " idType=" + idType, e);
        }


        return reportFile;

    }


}
