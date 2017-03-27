package assigment.service;

import static assigment.model.NodeType.action;
import static assigment.model.NodeType.condition;
import static assigment.model.NodeType.stub;
import static com.google.common.base.Preconditions.checkState;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import java.util.function.Predicate;
import java.util.stream.IntStream;

import assigment.model.KnowledgeTable;
import assigment.model.Node;
import assigment.model.NodeType;

/**
 * This class encapsulates logic of tree normalization: adding missing nodes (stubs or actions) in case an out of proper
 * layer condition or action is added.
 * 
 * @author sergey
 *
 */
public class MrProper
{

    private KnowledgeTable table;

    /**
     * Creates new instance.
     * 
     * @param table
     */
    public MrProper(KnowledgeTable table)
    {
        this.table = table;
    }

    /**
     * @return the table this class is managing
     */
    public KnowledgeTable getTable()
    {
        return table;
    }

    /**
     * Auto trim mode. Depending on the operation performed on the table you will want to either cut unwanted nodes or
     * add some missing.
     * 
     * @author sergey
     *
     */
    public enum Mode
    {
        /**
         * will add missing nodes
         */
        up
        {
            @Override
            OptionalInt targetDepth(List<Node> leaves)
            {
                return depths(leaves).max();
            }

            @Override
            void atLeastOneActionPerConditionFails(Node n)
            {
                n.addChild(Node.action(null));
            }

        },
        /**
         * will remove extra nodes
         */
        down
        {
            @Override
            OptionalInt targetDepth(List<Node> leaves)
            {
                return depths(leaves).min();
            }

            @Override
            void atLeastOneActionPerConditionFails(Node n)
            {
                n.remove();
            }

        };

        /**
         * Calculates the target maximum depth during expand/shrink operation. If depth is absent, there must be no
         * further actions upon the nodes.
         * 
         * @param leaves
         * @return the optional depth
         */
        abstract OptionalInt targetDepth(List<Node> leaves);

        void applyRules(KnowledgeTable table)
        {
            onlyOneStubInAStubNode(table);
            atLeastOneConditionForTopLevelStub(table);
            conditionMayContainOnlyConditionsAndActions(table);
            actionsMayOnlyHaveOneChildAction(table);
            eachConditionMustHaveAtLeastOneAction(table, this);
        }

        /**
         * The mode is able to decide what to do with the condition node that is missing a required action child.
         * 
         * @param n the condition node in question
         */
        abstract void atLeastOneActionPerConditionFails(Node n);

    }

    /**
     * Will add/remove the node to the table depening on the mode.
     * 
     * @param mode the mode to take into account
     * @return <code>true</code> if structure was affected
     */
    public boolean normalize(Mode mode)
    {
        addMetaInfo();
        List<Node> leaves = getLeaves();
        if (leaves.isEmpty())
        {
            return false;
        }
        OptionalInt depth = mode.targetDepth(leaves);
        int targetDepth;
        if (depth.isPresent())
        {
            targetDepth = depth.getAsInt();
        }
        else
        {
            return false;
        }
        boolean normalized = false;
        while (true)
        {
            growAndTrim(targetDepth, leaves);
            mode.applyRules(table);
            normalized = true;
            leaves = getLeaves();
            targetDepth = mode.targetDepth(leaves).getAsInt();
            if (depths(leaves).max().equals(depths(leaves).min()))
            {
                break;
            }
        }
        return normalized;
    }

    private static void growAndTrim(int targetDepth, List<Node> leaves)
    {
        // iteratively cut extra leaves off
        leaves.stream().filter(l -> l.getDepth() > targetDepth).forEach(n -> n.getParent().getNode().remove(n));
        // and grow missing
        leaves.stream().filter(l -> l.getDepth() < targetDepth).forEach(MrProper::addChildTo);
    }

    static IntStream depths(List<Node> leaves)
    {
        return leaves.stream().mapToInt(n -> n.getDepth());
    }

    static void addChildTo(Node n)
    {
        switch (n.getType())
        {
            case stub:
                n.addChild(Node.stub(null));
                break;
            case action:
                n.addChild(Node.action(null));
                break;
            case condition:
                n.addChild(Node.action(null));
                break;
        }
    }

    private List<Node> getLeaves()
    {
        List<Node> results = new ArrayList<>();
        recurse(table.getNode(), n -> n.getNode().isEmpty(), n -> results.add(n));
        return results;
    }

    /*
     * This is not listed in the specification, but without it we may have a table with only one stub and no condition
     * which does not make sense
     */
    static void atLeastOneConditionForTopLevelStub(KnowledgeTable table)
    {
        if (table.getNode().size() == 1)
        {
            table.getNode().add(Node.condition(null));
        }
    }

    static void eachConditionMustHaveAtLeastOneAction(KnowledgeTable table, Mode mode)
    {
        recurse(table.getNode(),
                n -> n.getType() == condition && n.getNode().stream().noneMatch(c -> c.getType() == action),
                n -> mode.atLeastOneActionPerConditionFails(n));
    }

    static void actionsMayOnlyHaveOneChildAction(KnowledgeTable table)
    {
        recurse(table.getNode(), node -> node.getType() == action && !node.getNode().isEmpty(), a -> {
            Node child = a.getNode().get(0);
            a.getNode().clear();
            if (child.getType() == action)
            {
                a.addChild(child);
            }
        });
    }

    static void recurse(List<Node> node, Predicate<Node> filter, Callback nodeHandler)
    {
        for (int i = 0; i < node.size(); i++ )
        {
            Node n = node.get(i);
            if (filter.test(n))
            {
                nodeHandler.handle(n);
            }
            if (n.isRemoved())
            {
                i-- ;
                continue;
            }
            recurse(n.getNode(), filter, nodeHandler);
        }
    }

    interface Callback
    {
        void handle(Node n);
    }

    static void conditionMayContainOnlyConditionsAndActions(KnowledgeTable table)
    {
        recurse(table.getNode(), c -> c.getType() == condition,
                c -> c.getNode().removeIf(n -> n.getType() != action && n.getType() != condition));
    }

    static void onlyOneStubInAStubNode(KnowledgeTable table)
    {
        if (!table.getNode().isEmpty())
        {
            long count = table.getNode().stream().filter(n -> n.getType() == NodeType.stub).count();
            checkState(count <= 1, "There must be only one top level stub in a table.");
            if (count == 0)
            {
                table.getNode().add(0, Node.stub(null));
            }
            Node firsNode = table.getNode().get(0);
            checkState(firsNode.getType() == NodeType.stub, "First element in a table must be a stub");
            recurse(firsNode.getNode(), n -> !n.getNode().isEmpty(), n -> {
                Node child = n.getNode().get(0);
                n.getNode().clear();
                if (child.getType() == stub)
                {
                    n.addChild(child);
                }
            });
        }
    }

    void addMetaInfo()
    {
        addMetaInfo(table.getNode(), 0, null);
    }

    private void addMetaInfo(List<Node> nodes, int depth, Node parent)
    {
        for (Node n : nodes)
        {
            n.setParent(parent);
            n.setDepth(depth);
            if (!n.getNode().isEmpty())
            {
                addMetaInfo(n.getNode(), depth + 1, n);
            }
        }
    }

    /**
     * @return the maximum level of nesting in the knowledge table
     */
    public int getMaxDepth()
    {
        OptionalInt max = depths(getLeaves()).max();
        return max.orElse(0);
    }

    /**
     * Naive traversal of nodes is explained by the application being designed for demonstration purposes.
     * 
     * @param layer the layer to extract
     * @return list of items belonging to the layer, never a <code>null</code>
     */
    public List<Node> getLayer(int layer)
    {
        List<Node> result = new ArrayList<>();
        recurse(table.getNode(), n -> n.getDepth() == layer, n -> result.add(n));
        return result;
    }

}
