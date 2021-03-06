package componenttree;

import gui.Resizable;

import java.awt.Dimension;
import java.awt.LayoutManager;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JComponent;

public class ContainerItem extends ComponentItem implements Iterable<ComponentItem>
{
	private ArrayList<ComponentItem> children; 
	private String layout;

	public ContainerItem(JComponent value, String type, Dimension size)
	{
		super(value, type, size);
		children = new ArrayList<ComponentItem>();
		layout = "";
	}

	public LayoutManager getLayout()
	{
		if(getComponent() instanceof Resizable){
			return ((JComponent)((Resizable)getComponent()).getComp()).getLayout();
		}else
		{
			return getComponent().getLayout();
		}
	}
	
	public String getLayoutType(){
		return layout;
	}

	public void addChildComponent(ComponentItem child)
	{
		children.add(child);
	}

	public boolean removeChildComponent(ComponentItem child)
	{
		return children.remove(child);
	}

	public Iterator<ComponentItem> iterator()
	{
		return children.iterator();
	}
	
	public void setLayout(String that){
		layout = that;
	}

}
