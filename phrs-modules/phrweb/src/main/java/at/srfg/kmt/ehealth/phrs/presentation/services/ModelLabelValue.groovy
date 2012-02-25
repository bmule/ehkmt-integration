package at.srfg.kmt.ehealth.phrs.presentation.services



public class ModelLabelValue implements Serializable {

    String id
    //lowercase ISO 639
    String label
    String lang
    String sortOrder
    String type


    public ModelLabelValue() {}


    public ModelLabelValue(String id, String label, String language) {
        this.id = id;
        this.label = label;
        lang = language
    }

    public ModelLabelValue(String id, String label, String language, String sortOrder) {
        this.id = id;
        this.label = label;
        lang = language
        this.sortOrder = sortOrder
    }

    public ModelLabelValue(String id, String label) {
        this.id = id;
        this.label = label;
    }

    def getString() {
        return label ? label : id
    }

    static ModelLabelValue createNameValue(String name, String value) {

        new ModelLabelValue(name, value)
    }

    static ModelLabelValue createNameValue(String name, String value, String lang) {

        new ModelLabelValue(name, value, lang)
    }

    static List createIdValue(String id, String value, List list) {
        try {
            ModelLabelValue lv = ModelLabelValue.createNameValue(id, value)

            if (list) {
                list.add(lv)
            }
        } catch (Exception e) {

        }
    }

    static List createIdValue(String id, String value, String lang, List list) {
        try {
        ModelLabelValue lv = ModelLabelValue.createNameValue(id, value, lang)

            if (list) {
                list.add(lv)
            }
        } catch (Exception e) {

        }
    }
    /**
     * Inputs a string list, and create a simple list where id values are the same
     *
     * @param list
     * @return
     */
    static List<ModelLabelValue> createIdValues(List<String> list) {

        List<ModelLabelValue> out = new ArrayList()
        for (j in out) {
            String name = list[j]
            out.add(ModelLabelValue.createNameValue(name, name))
        }
        return out
    }

    static List<ModelLabelValue> createIdValues(Map<String, String> source) {
        List<ModelLabelValue> out = new ArrayList()

        source.keySet().each { key ->
            println "funct key=" + key + " val=" + source[key]
            out.add(ModelLabelValue.createNameValue(key, source[key]))
        }

        return out
    }
    /**
     * return id
     * @return
     */
    public String getValue() {
        return id
    }
    /**
     * sets id
     * @param value
     */
    public void setValue(String value) {
        id = value
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((label == null) ? 0 : label.hashCode());
        result = prime * result + ((lang == null) ? 0 : lang.hashCode());
        return result;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ModelLabelValue other = (ModelLabelValue) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (label == null) {
            if (other.label != null)
                return false;
        } else if (!label.equals(other.label))
            return false;
        if (lang == null) {
            if (other.lang != null)
                return false;
        } else if (!lang.equals(other.lang))
            return false;
        return true;
    }
    /*
    public boolean equals(Object obj) {
        if (!(obj instanceof ModelLabelValue)) {
            return false;
        }
        //BasePhrsModel thing = (BasePhrsModel) obj;

        return (this.id == obj.id);

    }*/


}
