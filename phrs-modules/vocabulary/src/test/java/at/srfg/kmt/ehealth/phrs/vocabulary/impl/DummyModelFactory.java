/*
 * Project :iCardea
 * File : ModelFactory.java
 * Encoding : UTF-8
 * Date : Mar 18, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.vocabulary.impl;


import at.srfg.kmt.ehealth.phrs.vocabulary.api.Constants;
import at.srfg.kmt.ehealth.phrs.vocabulary.model.ControlledItem;
import java.util.UUID;


/**
 * Used to build several <b>test</b> purposed data models.
 * 
 * @version 0.1
 * @since 0.1
 * @author Mihai
 */
final class DummyModelFactory {

    /**
     * Don't let anybody to instantiate this class.
     * 
     */
    private DummyModelFactory() {
        // UNIMPLEMENTED
    }

    static String createUniqueString(String prefix) {
        final StringBuffer result = new StringBuffer();
        result.append(prefix);
        result.append(".");
        final UUID uuid = UUID.randomUUID();
        result.append(uuid.toString());
        return result.toString();
    }


    static ControlledItem createControlledItem() {

        final String code = createUniqueString("code");
        final String prefLabel = createUniqueString("prefferedLabel");
        final ControlledItem controlledItem = 
                new ControlledItem(Constants.SNOMED, "SNOMED", code, prefLabel);


        return controlledItem;
    }

    static ControlledItem createControlledItem(String label, String code) {
        final ControlledItem controlledItem = new ControlledItem(Constants.SNOMED, "SNOMED", code);
        controlledItem.setPrefLabel(label);

        return controlledItem;
    }
}
