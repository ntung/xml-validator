package uk.ac.ebi.ddi.xml.validator.parser.marshaller;

import com.ctc.wstx.stax.WstxOutputFactory;
import org.apache.log4j.Logger;
import uk.ac.ebi.ddi.xml.validator.parser.model.ModelConstants;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.util.HashSet;
import java.util.Set;

public class MarshallerFactory extends WstxOutputFactory{

    private static final Logger logger = Logger.getLogger(MarshallerFactory.class);
    private static MarshallerFactory instance = new MarshallerFactory();
    private static JAXBContext jc = null;

    public  Set<String> emptyElements = new HashSet<String>();

    public static MarshallerFactory getInstance() {
        return instance;
    }

    private MarshallerFactory() {
        emptyElements.add("ref");
    }

    public Marshaller initializeMarshaller() {
        logger.debug("Initializing Marshaller for mzML.");
        try {
            // Lazy caching of JAXB context.
            if(jc == null) {
                jc = JAXBContext.newInstance(ModelConstants.MODEL_PKG);
            }
            //create marshaller and set basic properties

            Marshaller marshaller = jc.createMarshaller();
            marshaller.setProperty(ModelConstants.JAXB_ENCODING_PROPERTY, "UTF-8");
            marshaller.setProperty(ModelConstants.JAXB_FORMATTING_PROPERTY, true);

            // Register a listener that calls before/afterMarshalOperation on ParamAlternative/-List objects.
            // See: ParamAlternative.beforeMarshalOperation and ParamAlternativeList.beforeMarshalOperation
            marshaller.setListener(new ObjectClassListener());

            logger.info("Marshaller initialized");

            return marshaller;

        } catch (JAXBException e) {
            logger.error("MarshallerFactory.initializeMarshaller", e);
            throw new IllegalStateException("Can't initialize marshaller: " + e.getMessage());
        }
    }

}
