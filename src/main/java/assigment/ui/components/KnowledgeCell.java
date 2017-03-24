package assigment.ui.components;

import assigment.ui.model.KnowledgeTableNode;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;

/**
 * @author sergey
 *
 */
public class KnowledgeCell extends TreeCell<KnowledgeTableNode>
{

    private TreeView<KnowledgeTableNode> view;

    /**
     * @param view
     */
    public KnowledgeCell(TreeView<KnowledgeTableNode> view)
    {
        this.view = view;
    }

    @Override
    protected void updateItem(KnowledgeTableNode item, boolean empty)
    {
        super.updateItem(item, empty);
        if (!empty)
        {
            setText(item.toString());
            setContextMenu(item.createMenu());
        }
        else
        {
            setText(null);
            setContextMenu(null);
        }
    }

}
