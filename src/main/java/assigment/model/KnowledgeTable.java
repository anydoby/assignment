package assigment.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Root of the hierarchy.
 * 
 * @author sergey
 *
 */
@XmlRootElement(name = "knowledgetable")
public class KnowledgeTable
{

    /**
     * This list will contain 2 elements: the top level stub and a top level condition
     */
    private List<Node> node = new ArrayList<>();

    /**
     * @return the child nodes
     */
    public List<Node> getNode()
    {
        return node;
    }

    /**
     * @param nodes
     */
    public void setNode(List<Node> nodes)
    {
        this.node = nodes;
    }

    @Override
    public String toString()
    {
        return node.toString();
    }

}
