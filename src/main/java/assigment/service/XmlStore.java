package assigment.service;

import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.springframework.stereotype.Component;

import assigment.model.KnowledgeTable;

/**
 * @author sergey
 *
 */
@Component
public class XmlStore
{

    private JAXBContext context;

    public XmlStore() throws JAXBException
    {
        context = JAXBContext.newInstance(KnowledgeTable.class);
    }

    /**
     * Stores the knowledge table to the stream
     * 
     * @param table
     * @param out
     * @throws JAXBException
     */
    public void store(KnowledgeTable table, OutputStream out) throws JAXBException
    {
        Marshaller marshaller = context.createMarshaller();
        marshaller.marshal(table, out);
    }

    /**
     * Loads the knowledge table from the input stream.
     * 
     * @param in
     * @return the database
     * @throws JAXBException in case the document could not be parsed
     */
    public KnowledgeTable load(InputStream in) throws JAXBException
    {
        Unmarshaller unmarshaller = context.createUnmarshaller();
        return unmarshaller.unmarshal(new StreamSource(in), KnowledgeTable.class).getValue();

    }

}
