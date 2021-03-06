//while very hard to understand at first, this class is the backbone of the whole project. Here is where the money is! This class builds the gui that we see and 
//the user interacts with. Also, all the drag listeners are here.  I attempted to separate them from this class but found it hard to do so.
//If you are trying to change the overall gui and how is looks, it is done here.

package gui;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.SystemFlavorMap;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceContext;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import componenttree.ComponentItem;
import componenttree.ContainerItem;

import codegenerating.Generator;


public class GUI extends JFrame implements ActionListener, ChangeListener, DragGestureListener, DragSourceListener, DropTargetListener, Transferable{
		private HashMap<String, LayoutManager> layoutMap;
		private static final long serialVersionUID = 1L;  
        private JMenuBar MenuBar;
        private JMenu File,Layout;
        private JMenuItem Open,Save,SaveAs,New,Close,Border,Grid,Flow,Absolute;
        protected JPanel trashBin;
        protected ComponentsPanel compPanel;
        protected UserGUI userFrame, curFrame;
        private JScrollPane compScroll;
        private JSplitPane split;
        private JTabbedPane userTab;
        private Generator gen;
        private String SavingDirectory = System.getProperty("user.dir") + java.io.File.separator+"src"+java.io.File.separator+"codegenerating";
        static final DataFlavor[] dataflavor = { null };
		Object object;			
		static {
			try {
				dataflavor[0] = new DataFlavor(
						DataFlavor.javaJVMLocalObjectMimeType);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
        
	public GUI() throws FileNotFoundException{
		layoutMap = new HashMap<String, LayoutManager>();
		layoutMap.put("Border Layout", new BorderLayout());
		layoutMap.put("Grid Layout", new GridLayout(5,5));
		layoutMap.put("Flow Layout", new FlowLayout());
		layoutMap.put("Absolute Layout", null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800,800);
        setLocation(200, 100);
        setTitle("Swing GUI Builder");
        setLayout(new BorderLayout());
        createComponentList(); 
        createUsersGUI();
        createMenu();
        gen = new Generator();
	}
	
	private void createUsersGUI(){
        userFrame = new UserGUI();                 
        userTab = new JTabbedPane();
        userTab.add(userFrame);
        split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, userTab, compScroll);
        split.setDividerLocation(600);
        add(split);        
        curFrame = userFrame;
        userTab.addChangeListener(this);
		setUpTrashBin();
	}
	
	private void setUpTrashBin(){
		trashBin = new JPanel();
        trashBin.setBackground(Color.white);
        JLabel label = new JLabel("Components from GUI");
        trashBin.add(label);
        trashBin.setPreferredSize(new Dimension(800, 50));
        add(trashBin, BorderLayout.SOUTH); 
	}
	
	private void createComponentList(){
        compPanel = new ComponentsPanel();
        compPanel.setPreferredSize(new Dimension(200,700));
        compScroll = new JScrollPane(compPanel);  
        addDragListeners();
	}
	
	private void createMenu(){
        MenuBar = new JMenuBar();
        add(MenuBar, BorderLayout.NORTH);
        createFileMenu();
        createLayoutMenu();
	}
	private void createFileMenu(){
        File = new JMenu("File");
        Open = new JMenuItem("Open");
        Save = new JMenuItem("Save");
        SaveAs = new JMenuItem("Save As");
        New = new JMenuItem("New");
        Close = new JMenuItem("Close");
        File.add(new JSeparator());
        File.add(New); File.add(Open); File.add(Save); File.add(SaveAs); File.add(Close); 
        MenuBar.add(File);        
        New.addActionListener(this);Open.addActionListener(this);Save.addActionListener(this);SaveAs.addActionListener(this);Close.addActionListener(this);
	}

	private void createLayoutMenu(){
        Layout = new JMenu("Layout Select");
        Border = new JMenuItem("BorderLayout");
        Grid = new JMenuItem("GridLayout");
        Flow = new JMenuItem("FlowLayout");
        Absolute = new JMenuItem("Absolute Positioning");
        Layout.add(new JSeparator());
        Layout.add(Border);Layout.add(Grid);Layout.add(Flow);Layout.add(Absolute);
        Border.addActionListener(this);Grid.addActionListener(this);Flow.addActionListener(this);Absolute.addActionListener(this);
        MenuBar.add(Layout);       
	}
	
	private void setTabName(String name){
		int cur = userTab.getTabCount()-1;
		userTab.setSelectedIndex(cur);
		userTab.setTitleAt(cur, name);
		curFrame.setName(name);
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		if(evt.getSource() == New){	
			newAction();
		}
		if(evt.getSource() == Open){	
	        openAction();        
		}
		if(evt.getSource() == Save){
			if(!curFrame.isSaved()){
				saveFile();
			}
			else{
				generateUserGUI(curFrame.getName(), SavingDirectory);
			}
	    } 
		if(evt.getSource() == SaveAs){	
			saveFile();
		}
		if(evt.getSource() == Close){
			closeAction();
		}
		if(evt.getSource() == Border){
			curFrame.layoutBorderSetter(curFrame.getMomContainer());
			changedLayout();
		}
		if(evt.getSource() == Grid)
		{
			String result = JOptionPane.showInputDialog(this, "Enter the grid dimensions in the form x y ");
			result = result.trim();
			String[] numbers = result.split(" ");
			if(numbers.length!=2) return;
			curFrame.layoutGridSetter(curFrame.getMomContainer(),Integer.parseInt(numbers[0]),Integer.parseInt(numbers[1]));
			changedLayout();
		}
		if(evt.getSource() == Flow){
			curFrame.layoutFlowSetter(curFrame.getMomContainer());
			changedLayout();
		}
		if(evt.getSource() == Absolute){
			curFrame.layoutAbsolute(curFrame.getMomContainer());
			changedLayout();
		}
	}
		
	private void newAction(){
		String name = JOptionPane.showInputDialog("What do you want to name your GUI?");
		UserGUI newFrame = new UserGUI();
		userTab.add(newFrame);
		userTab.getComponentAt(userTab.getSelectedIndex()).setName(name);
		curFrame = newFrame;
		setTabName(name);
	}
	
	private void openAction(){
		JFileChooser chooser = new JFileChooser(SavingDirectory);
        chooser.setDialogTitle("Pick the file you wish to load");       
        int result = chooser.showDialog(null, "Select");
        if(result != JFileChooser.APPROVE_OPTION){ 
        } 
	}
	
	private void closeAction(){
		int answer = JOptionPane.showConfirmDialog(this, "Are you sure you wish to close?" );
		if(answer == 0){			
			if(userTab.getTabCount() > 1){
				userTab.remove(userTab.getSelectedIndex());
			}
			else{
				System.exit(0);
			}
			updateGUI();
		}
	}
	
	private void generateUserGUI(String saveasName, String saveDir){
		if(saveasName.length() >= 1){			
			gen.setTreeGenerated(curFrame.getTreeStruct().getRoot());
			gen.addToFrame(curFrame.getTreeStruct().getRoot());		
			String code = gen.getCode(curFrame.getName(), saveasName);
			gen.putCodeInFile(code, saveasName, saveDir);
		}
		else{
			String filename = JOptionPane.showInputDialog("What would you like to name your file?");		
			if(filename == null || filename.length() < 1){
				JOptionPane.showMessageDialog(this, "You must name your file. Please try again");
			}	
			else{
				gen.setTreeGenerated(curFrame.getTreeStruct().getRoot());
				gen.addToFrame(curFrame.getTreeStruct().getRoot());	
				String code = gen.getCode(curFrame.getName(), filename);
				gen.putCodeInFile(code, filename, saveDir);
			}
		}
	}
	
	private void saveFile(){
		JFileChooser fileChooser = new JFileChooser(SavingDirectory);    
        fileChooser.showSaveDialog(null);
        File f = fileChooser.getSelectedFile();
        if(f != null ) {
        	if(canSaveFile(f)) { 
        		if(f.getName().length() > 0){
        			curFrame.setName(f.getName());
        			setTabName(f.getName());
        			generateUserGUI(f.getName(), SavingDirectory);
        			curFrame.changeSaved();        			
        			
        		}
        		else{
        			saveFile();
        		}
            }
        } 
        else {
                JOptionPane.showMessageDialog(this, "No file selected");
        }       
	}	

	private Boolean canSaveFile(File f) {
        Boolean CanSave = rightDirectory(f);
        if(!CanSave) JOptionPane.showMessageDialog(this, "You can not save to that directory.", "Bad directory", JOptionPane.PLAIN_MESSAGE);
        return CanSave;
	}	

	private Boolean rightDirectory(File f) {
        if (f.getParent().equalsIgnoreCase(SavingDirectory)){
                return true;
        } else {
                return false;
        }
	}
	
	protected void updateGUI(){
		repaint();
		validate();
	}
	
	public boolean isInteger(String str) {
	    int size = str.length();
	    for (int i = 0; i < size; i++) {
	        if (!Character.isDigit(str.charAt(i))) {
	            return false;
	        }
	    }
	    return size > 0;
	}
	
	private void changedLayout(){
		for(Component item : trashBin.getComponents()){
			item.setVisible(false);
		}
		JLabel label = new JLabel("Components from GUI:");
        trashBin.add(label);
		for(JLabel comp : curFrame.getCompList()){
			JLabel temp = new JLabel(comp.getText());
			trashBin.add(temp);
			addDragSourceListener(temp);
			updateGUI();
		}
		curFrame.getCompList().clear();
	}
	
	public Generator getGen(){
		return gen;
	}
	
	//-------------------------------------------BELOW IS DRAG LISTENERS----------------------------------------------------------------------
	
	public void addDragListeners(){
		String[] containers = compPanel.getCmap().getContainers();
		Component[] contComp = compPanel.getCmap().getContainerComps();
		String[] controls = compPanel.getCmap().getControls();
		Component[] controlComp = compPanel.getCmap().getControlComps();
		String[] menus = compPanel.getCmap().getMenus();
		Component[] menuComp = compPanel.getCmap().getMenuComps();
		for(int i = 0; i < containers.length; i++){
            final JLabel curLabel = new JLabel(containers[i]);
            addDragSourceListener(curLabel);
            compPanel.addLabelToContainers(contComp[i], curLabel);
		} 
		for(int i = 0; i < controls.length; i++){
            final JLabel curLabel = new JLabel(controls[i]);
            addDragSourceListener(curLabel);
            compPanel.addLabelToControls(controlComp[i],curLabel);
		}
		for(int i = 0; i < menus.length; i++){
            final JLabel curLabel = new JLabel(menus[i]);
            addDragSourceListener(curLabel);
            compPanel.addLabelToMenus(menuComp[i], curLabel);
		}
	}
	
    public void addDragSourceListener(JComponent comp){
            comp.setFocusable(true);
            comp.setAlignmentX(Component.CENTER_ALIGNMENT);
            comp.setBorder(BorderFactory.createRaisedBevelBorder());            
            
    		DragSource dragSource = new DragSource();
    		new DropTarget(this,DnDConstants.ACTION_MOVE, this);                		
    		dragSource.createDefaultDragGestureRecognizer(comp, DnDConstants.ACTION_MOVE, this);
    		dragSource.addDragSourceListener(null);    		
		}
    
	public Object getTransferData(DataFlavor flavor) {
		if (flavor.isMimeTypeEqual(DataFlavor.javaJVMLocalObjectMimeType)) {
			return object;
		} else {
			return null;
		}
	}

	public DataFlavor[] getTransferDataFlavors() {
		return dataflavor;
	}

	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return flavor.isMimeTypeEqual(DataFlavor.javaJVMLocalObjectMimeType);
	}

	// DragGestureListener method.
	public void dragGestureRecognized(DragGestureEvent dge){
		try{
			dge.getDragSource().startDrag(dge, null, this, this, SystemFlavorMap.getDefaultFlavorMap());
		}
		catch(Exception ex){
			
		}
	}

	// DragSourceListener methods.
	public void dragDropEnd(DragSourceDropEvent dsde) {		
	}

	public void dragEnter(DragSourceDragEvent dsde) {
	}

	public void dragExit(DragSourceEvent dse) {
	}

	public void dragOver(DragSourceDragEvent dsde) {
		object = dsde.getSource();
	}

	public void dropActionChanged(DragSourceDragEvent dsde) {
	}

	// DropTargetListener methods.
	public void dragEnter(DropTargetDragEvent dtde) {
		JLabel lb = new JLabel("test");
		lb.setBounds((int)dtde.getLocation().getX(), (int) dtde.getLocation().getY(), 200, 40);
		lb.setVisible(true);
	}

	public void dragExit(DropTargetEvent dte) {
	}

	public void dragOver(DropTargetDragEvent dtde) {
		dropTargetDrag(dtde);
	}

	public void dropActionChanged(DropTargetDragEvent dtde) {
		dropTargetDrag(dtde);
	}

	public void dropTargetDrag(DropTargetDragEvent dtde) {
		dtde.acceptDrag(dtde.getDropAction());
	}
	
	
	//this is all the action of drag and drop right here. 
	public void drop(DropTargetDropEvent dtde) {
		dtde.acceptDrop(dtde.getDropAction());		
		try {
			Object source = dtde.getTransferable().getTransferData(dataflavor[0]);
			Component component = ((DragSourceContext) source).getComponent();
			Container oldContainer = component.getParent();
			int X = (int) ( (dtde.getLocation().getX() + this.getLocationOnScreen().getX()) - (curFrame.getLocationOnScreen().getX()) );
			int Y = (int) (dtde.getLocation().getY() + this.getLocationOnScreen().getY() - (curFrame.getLocationOnScreen().getY()));
			
			curFrame.setCurLocation(X, Y-30);	//The -30 offsets the location of where we place the component.			
			if(component instanceof JLabel){				
				String compName = ((JLabel) component).getText();
				ComponentsMap comp = compPanel.createNewMap();
				HashMap<String, Component> comps = comp.getComponentsMap();
				Component c = comps.get(compName);
				Dimension d = compPanel.getDimension(compName);
				final Resizable resizer = new Resizable(c, curFrame.getSize());
				if(c instanceof JMenuBar){
					resizer.setContainerItem(new ContainerItem(resizer, compName, d));
					curFrame.addMenuBar(resizer,compName);
				}
				else if(c instanceof JMenu){
					resizer.setComponentItem(new ComponentItem(resizer, compName, d));
					curFrame.addToCompList(((JLabel) component));
					curFrame.addMenu(resizer);
				}
				else if(c instanceof JMenuItem){
					resizer.setComponentItem(new ComponentItem(resizer, compName, d));
					curFrame.addToCompList(((JLabel) component));
					curFrame.addMenuItem(resizer);
				}
				else{
					curFrame.addToCompList(((JLabel) component));
					if(c instanceof JPanel)
					{
						((JPanel) c).setLayout(null);
						resizer.setContainerItem(new ContainerItem(resizer, compName, d));
					}
					else
					{
						resizer.setComponentItem(new ComponentItem(resizer, compName, d));
					}	
					curFrame.changeUserFrame(resizer, d, compName);
					new RightClickMenu(resizer, curFrame, compName.equals("JPanel"), this);	
				}
			}
			oldContainer.validate();
			oldContainer.repaint();
			curFrame.validate();
			curFrame.repaint();
			updateGUI();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		dtde.dropComplete(true);
	}
	@Override
	public void stateChanged(ChangeEvent arg0) {		
		for(Component comp: curFrame.getCompList()){
				trashBin.remove(comp);
		}
		curFrame = (UserGUI) userTab.getComponentAt(userTab.getSelectedIndex());
		updateGUI();
	}
	
	public HashMap<String, LayoutManager> getLayoutMap(){
		return layoutMap;
	}
}
