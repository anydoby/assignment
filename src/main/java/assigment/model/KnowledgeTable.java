package assigment.model;

import java.util.List;

/**
 * Root of the hierarchy.
 * 
 * @author sergey
 *
 */
public class KnowledgeTable
{

    /**
     * This list will contain 2 elements: the top level stub and a top level condition
     */
    private List<Node> nodes;

    /**
     * @return the child nodes
     */
    public List<Node> getNodes()
    {
        return nodes;
    }

    /**
     * @param nodes
     */
    public void setNodes(List<Node> nodes)
    {
        this.nodes = nodes;
    }

}
