package assigment.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.nio.file.Files;
import java.nio.file.Paths;

import javax.xml.bind.JAXBException;

import org.junit.Before;
import org.junit.Test;

import assigment.model.KnowledgeTable;

public class XmlStoreTest
{

    private XmlStore store;

    @Before
    public void pre() throws JAXBException
    {
        store = new XmlStore();
    }

    @Test
    public void testLoad() throws Exception
    {
        KnowledgeTable table = store.load(getClass().getResourceAsStream("test.xml"));
        assertNotNull(table);
        assertEquals(new String(Files.readAllBytes(Paths.get(getClass().getResource("expected-test").toURI()))),
                table.getNode().toString());
    }

}
