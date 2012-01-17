package at.srfg.kmt.ehealth.phrs.jsf.utils;





import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;


/**
 * This phase listener can be used to print debug information about the JSF
 * workflow phase.  This phase listener signals the begging of a phase and
 * the end of a phase; for a complete and successfully JSF workflow the workflow
 * will like like this :
 * <pre>
 * Start Phase :  RESTORE_VIEW 1
 * End Phase :  RESTORE_VIEW 1
 * Start Phase :  APPLY_REQUEST_VALUES 2
 * End Phase :  APPLY_REQUEST_VALUES 2
 * Start Phase :  PROCESS_VALIDATIONS 3
 * END PHASE PROCESS_VALIDATIONS 3
 * Start Phase :  UPDATE_MODEL_VALUES 4
 * End Phase :  UPDATE_MODEL_VALUES 4
 * Start Phase :  INVOKE_APPLICATION 5
 * End Phase :  INVOKE_APPLICATION 5
 * Start Phase :  RENDER_RESPONSE 6
 * End Phase :  RENDER_RESPONSE 6
 * </pre>
 * Please note that no all the complete and successfully workflow are iterating
 * over all the 6 (upper listed) phases, some workflow may jump over some
 * steeps. Please consult the JSF2 documentation for more details. <br/>
 * <b>Usage</b> : To use it add the following lines to the faces-config.xml.
 * <pre>
 * <lifecycle>
 *     <phase-listener>at.srfg.kmt.ehealth.phrs.jsf.utils.OutputPahseListener</phase-listener>
 * </lifecycle>
 * </pre>
 *
 * @author Mihai
 * @version 0.0.1
 * @since 0.0.1
 */
public class OutputPahseListener implements PhaseListener {

    private long startTime;
/*
java.util.IllegalFormatConversionException - f != java.lang.Long
at java.util.Formatter$FormatSpecifier.failConversion(Formatter.java:3999)
String.format
 */
    @Override
    public void afterPhase(PhaseEvent event) {
        try {
			final PhaseId phaseId = event.getPhaseId();
			System.out.println("End Phase : " + phaseId);
			if (event.getPhaseId() == PhaseId.RENDER_RESPONSE) {
			    long endTime = System.nanoTime();
			    long diffMs = (long) ((endTime - startTime) * 0.000001);
			    //final String msg = String.format("Flow execution time is : %5.3f ms", diffMs);
			    //System.out.println(msg);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    }

    @Override
    public void beforePhase(PhaseEvent event) {
        try {
			final PhaseId phaseId = event.getPhaseId();
			System.out.println("Start Phase : " + phaseId);
			if (event.getPhaseId() == PhaseId.RESTORE_VIEW) {
			    startTime = System.nanoTime();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    @Override
    public PhaseId getPhaseId() {
        return PhaseId.ANY_PHASE;
    }
}
