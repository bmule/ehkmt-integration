package at.srfg.kmt.ehealth.phrs.core;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.Morphia;
import com.mongodb.Mongo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RepoDatastore {

    static boolean TEST = true;
    private static final Logger LOGGER = LoggerFactory.getLogger(RepoDatastore.class);

//    private static DocRepository m_instance;
//
//    static {
//        staticInit();
//    }
//
//    protected static void staticInit() {
//        try {
//            m_instance = new DocRepository();
//        } catch (Exception ex) {
//            LOGGER.warn(ex.getMessage(), ex);
//            throw new IllegalStateException(ex);
//        }
//    }


    private Mongo mongo;
    private int mongoPort = 27017;
    private String mongoHost = "127.0.0.1";

    private Datastore datastore;
    private String dataStoreName;
    private String   morphiaPackageName;
    //private Morphia morphia;

    private RepoDatastore(Mongo mongo, String dataStoreName, String morphiaPackageName) {
        this.mongo=mongo;
        this.dataStoreName=dataStoreName;
        this.morphiaPackageName= morphiaPackageName;
        Morphia morphia = new Morphia();
        datastore = morphia.createDatastore(mongo,
                dataStoreName, null, null);
        mapPackage(morphia, morphiaPackageName);
    }

    private RepoDatastore(String mongoHost, int mongoPort, String dataStoreName, String morphiaPackageName) {
        this.mongoHost=mongoHost;
        this.mongoPort=mongoPort;
        try {
            Mongo mongo = new Mongo(mongoHost, mongoPort);

            Morphia morphia = new Morphia();
            this.dataStoreName=dataStoreName;
            datastore = morphia.createDatastore(mongo,
                    dataStoreName, null, null);
            this.morphiaPackageName= morphiaPackageName;
        } catch (Exception e) {
            LOGGER.error("Database setup Mongo Morphia setup",e);
        }

    }

    public  void shutdown(){


    }
    /*
    public static Morphia initMorphi(String mongoHost, String mongoPort8
    )
    private void initDataStored(String phrs_database_name, String morphiaPackageName) {

        try {
            mongo = new Mongo(mongoHost, mongoPort);

            morphia = new Morphia();

            // TODO user password mongodb
            datastore = morphia.createDatastore(mongo,
                    phrs_database_name, null, null);


            mapPackage(morphiaPackageName);


            if (datastore == null) {

                throw new Exception();
            }

        } catch (Exception e) {

            LOGGER.error("Morphia/ MongoDB failed . is MongoDB started? mongohost=" + mongoHost + " mongoPort=" + mongoPort,
                    e);

        }
    }*/
    public void mapPackage(Morphia morphia,String packageName){
        if(morphia!=null && packageName !=null){
            try{
             morphia.mapPackage(packageName);
            } catch(Exception e){
                LOGGER.error("error Morphia on package="+packageName,e);
            }
        }
    }
    public Mongo getMongo() {

        return mongo;
    }

    public Datastore getDatastore() {
        return datastore;
    }

    public  boolean getSystemStatus() {
        boolean systemStatusInterop=false;
        boolean systemStatus=false;

        try{
            if(mongo != null){
                systemStatus=true;
            }
        } catch (Exception e) {

            LOGGER.error(" getSystemStatus or mongoDB not available ", e);
        }
        return systemStatus && systemStatusInterop;
    }
}
