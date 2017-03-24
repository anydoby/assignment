package assigment.model;

import javax.xml.bind.annotation.XmlType;

/**
 * Represents an action
 * 
 * @author sergey
 *
 */
@XmlType(name = "action")
public class ActionNode extends ConditionSubnode
{

    private ActionNode node;

    /**
     * @return sub-action
     */
    public ActionNode getNode()
    {
        return node;
    }

    /**
     * @param node
     */
    public void setNode(ActionNode node)
    {
        this.node = node;
    }

}
