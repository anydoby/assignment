package assigment.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Represents nodes; any type can fit in.
 * 
 * @author sergey
 *
 */
public class Node
{

    private static final int REMOVED = -1;

    private NodeType type;

    private String content;

    private List<Node> node = new ArrayList<>();

    private int depth;

    private Node parent;

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

    /**
     * @return the node type
     */
    @XmlAttribute
    public NodeType getType()
    {
        return type;
    }

    /**
     * Sets the node type
     * 
     * @param type
     */
    public void setType(NodeType type)
    {
        this.type = type;
    }

    /**
     * @return child nodes
     */
    public List<Node> getNode()
    {
        return node;
    }

    /**
     * Sets the nodes list
     * 
     * @param node
     */
    public void setNode(List<Node> node)
    {
        this.node = node;
    }

    /**
     * Creates a stub with the specified content
     * 
     * @param content
     * @return the new node
     */
    public static Node stub(String content)
    {
        Node newNode = newNode(content);
        newNode.setType(NodeType.stub);
        return newNode;
    }

    private static Node newNode(String content)
    {
        Node n = new Node();
        n.setContent(content);
        return n;
    }

    /**
     * Creates a condition with the specified content
     * 
     * @param content
     * @return the new node
     */
    public static Node condition(String content)
    {
        Node newNode = newNode(content);
        newNode.setType(NodeType.condition);
        return newNode;
    }

    /**
     * Creates action node with the specified content
     * 
     * @param content
     * @return the new node
     */
    public static Node action(String content)
    {
        Node newNode = newNode(content);
        newNode.setType(NodeType.action);
        return newNode;
    }

    /**
     * This property is not serialized.
     * 
     * @return level of nesting of this node
     */
    @XmlTransient
    public int getDepth()
    {
        return depth;
    }

    /**
     * Sets the level of nesting
     * 
     * @param depth
     */
    public void setDepth(int depth)
    {
        this.depth = depth;
    }

    /**
     * This property is not serialized.
     * 
     * @return the parent node
     */
    @XmlTransient
    public Node getParent()
    {
        return parent;
    }

    /**
     * Sets the parent to this node.
     * 
     * @param parent
     */
    public void setParent(Node parent)
    {
        this.parent = parent;
    }

    /**
     * Adds a child node and returns this.
     * 
     * @param node
     * @return this node
     */
    public Node addChild(Node node)
    {
        node.setParent(this);
        node.setDepth(depth + 1);
        getNode().add(node);
        return this;
    }

    @Override
    public String toString()
    {
        return type + "(" + depth + ")" + (content != null ? "'" + content + "'" : "") + (node.isEmpty() ? "" : node);
    }

    /**
     * Removes this node from the parent if it is attached.
     */
    public void remove()
    {
        if (parent != null)
        {
            parent.getNode().remove(this);
            parent = null;
            depth = REMOVED;
        }
    }

    /**
     * Returns <code>true</code> for nodes that were removed from parent by invoking {@link #remove()}.
     * 
     * @return <code>true</code> if node was removed
     */
    public boolean isRemoved()
    {
        return depth == REMOVED;
    }
}
