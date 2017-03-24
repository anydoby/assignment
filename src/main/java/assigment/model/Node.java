package assigment.model;

import javax.xml.bind.annotation.XmlSeeAlso;

/**
 * Represents nodes.
 * 
 * @author sergey
 *
 */
@XmlSeeAlso(
{ StubNode.class, ConditionNode.class, ActionNode.class })
public abstract class Node
{

    private String content;

    /**
     * @return text content of the node
     */
    public String getContent()
    {
        return content;
    }

    /**
     * @param content
     */
    public void setContent(String content)
    {
        this.content = content;
    }

}
