package assigment.service;

import static assigment.model.Node.action;
import static assigment.model.Node.condition;
import static assigment.model.Node.stub;
import static assigment.service.MrProper.Mode.down;
import static assigment.service.MrProper.Mode.up;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import assigment.model.KnowledgeTable;

public class MrProperTest
{
    MrProper normalizer;
    KnowledgeTable table;

    @Before
    public void pre()
    {
        table = new KnowledgeTable();
        normalizer = new MrProper(table);
    }

    @Test
    public void normalize_EmptyTree() throws Exception
    {
        assertFalse(normalizer.normalize(down));
        assertFalse(normalizer.normalize(up));
    }

    @Test
    public void normalize_OneStub()
    {
        table.getNode().add(stub(null));
        assertTrue(normalizer.normalize(up));
        assertEquals("[stub(0)[stub(1)], condition(0)[action(1)]]", table.getNode().toString());
    }

    @Test
    public void normalize_OneCondition()
    {
        table.getNode().add(condition(null));
        assertTrue(normalizer.normalize(up));
        assertEquals("[stub(0)[stub(1)], condition(0)[action(1)]]", table.getNode().toString());
    }

    @Test
    public void normalize_AddMissingAction()
    {
        table.getNode().add(condition(null).addChild(action(null).addChild(action(null))).addChild(action(null)));

        assertTrue(normalizer.normalize(up));
        assertEquals("[stub(0)[stub(1)[stub(2)]], condition(0)[action(1)[action(2)], action(1)[action(2)]]]",
                table.getNode().toString());
    }

    @Test
    public void normalize_RemoveFromTheMiddle() throws Exception
    {
        table.getNode().add(condition("c1").addChild(action("a1")).addChild(condition("c2")));
        normalizer.normalize(up);

        assertEquals(
                "[stub(0)[stub(1)[stub(2)]], condition(0)'c1'[action(1)'a1'[action(2)], condition(1)'c2'[action(2)]]]",
                table.getNode().toString());

        // condition[0]/action[0]/action[0]
        table.getNode().get(1).getNode().get(0).getNode().get(0).remove();

        normalizer.normalize(down);
        assertEquals("[stub(0)[stub(1)], condition(0)'c1'[action(1)'a1']]", table.getNode().toString());
    }

}
