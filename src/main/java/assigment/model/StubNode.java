package assigment.model;

import javax.xml.bind.annotation.XmlType;

/**
 * This node can only have one child which is also a stub and content.
 * 
 * @author sergey
 *
 */
@XmlType(name = "stub")
public class StubNode extends Node
{

    StubNode node;

}
