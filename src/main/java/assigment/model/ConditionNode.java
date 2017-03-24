package assigment.model;

import java.util.List;

import javax.xml.bind.annotation.XmlType;

/**
 * Represents condition. A condition may have content and any number of other conditions or actions.
 * 
 * @author sergey
 *
 */
@XmlType(name = "condition")
public class ConditionNode extends ConditionSubnode
{

    List<ConditionSubnode> node;

    /**
     * @return the condition subnodes
     */
    public List<ConditionSubnode> getNode()
    {
        return node;
    }

    /**
     * @param node
     */
    public void setNode(List<ConditionSubnode> node)
    {
        this.node = node;
    }

}
