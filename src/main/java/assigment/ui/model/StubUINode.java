package assigment.ui.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

/**
 * @author sergey
 *
 */
public class StubUINode implements KnowledgeTableNode
{

    private StringProperty nameProperty = new SimpleStringProperty();

    private ObjectProperty<StubUINode> child = new SimpleObjectProperty<>();

    private ContextMenu menu;

    /**
     * @param name
     */
    public StubUINode(String name)
    {
        nameProperty.set(name);
        menu = new ContextMenu();
        MenuItem addStub = new MenuItem("Add a stub");
        addStub.disableProperty().bind(child.isNotNull());
        addStub.setOnAction(e -> child.set(new StubUINode("A child")));
        menu.getItems().add(addStub);
    }

    @Override
    public StringProperty nameProperty()
    {
        return nameProperty;
    }

    @Override
    public String toString()
    {
        return "KnowledgeTable";
    }

    @Override
    public ContextMenu createMenu()
    {
        return menu;
    }

}
