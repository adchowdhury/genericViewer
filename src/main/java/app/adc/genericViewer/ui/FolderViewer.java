package app.adc.genericViewer.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.URI;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

public class FolderViewer extends AbstractViewer {

	private JTree				folder			= null;
	private JTable				files			= null;
	private JTextField			path			= null;
	private File				sourceFolder	= null;
	private FolderTableModel	tableModel		= null;
	private List<File>			displayFiles	= null;
	private SimpleDateFormat	dateFormat	= null;
	
	public FolderViewer(URI source) {
		super(source);
		sourceFolder = new File(source);
		init();
		loadFolder();
	}
	
	private void init() {
		dateFormat	= new SimpleDateFormat("dd-MMM-yyyy hh:mm");
		displayFiles = new ArrayList<File>();
		path = new JTextField();
		path.setEditable(false);
		folder = new JTree(new FolderTreeNode(sourceFolder, null));
		files = new JTable(tableModel = new FolderTableModel());
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(folder), new JScrollPane(files));
		files.getColumnModel().getColumn(1).setCellRenderer(new NumberTableCellRenderer());
		
		add(splitPane, BorderLayout.CENTER);
		add(path, BorderLayout.NORTH);
		
		((DefaultTableCellRenderer)files.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);
		files.setAutoCreateRowSorter(true);
		files.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		files.setColumnSelectionAllowed(false);
		files.setRowHeight(24);
		files.setIntercellSpacing(new Dimension(5, 5));
		
		folder.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent me) {
				
				TreePath tp = folder.getPathForLocation(me.getX(), me.getY());
				if(tp == null) {
					return;
				}
				
				displayFiles.clear();
				
				FolderTreeNode node = (FolderTreeNode)tp.getLastPathComponent();
				path.setText(node.root.getAbsolutePath());
				File[] files = node.root.listFiles();
				for(File f : files) {
					if(f.isFile()) {
						displayFiles.add(f);
					}
				}//end of for loop
				tableModel.fireTableDataChanged();
			}
		});
		
		files.addMouseListener(new MouseAdapter() {
		    public void mousePressed(MouseEvent mouseEvent) {
		        JTable table =(JTable) mouseEvent.getSource();
		        Point point = mouseEvent.getPoint();
		        int row = table.rowAtPoint(point);
		        if (mouseEvent.getClickCount() == 2 && table.getSelectedRow() != -1) {
		        	File f = displayFiles.get(row);
		        	MenuEventListener.openResource(ViewerFactory.getViewer(f.toURI()), f.getName());
		        }
		    }
		});
	}
	
	private void loadFolder() {
		path.setText(sourceFolder.getAbsolutePath());
	}
	
	public class NumberTableCellRenderer extends DefaultTableCellRenderer {

		public NumberTableCellRenderer() {
			setHorizontalAlignment(JLabel.RIGHT);
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			if (value instanceof Number) {
				value = NumberFormat.getNumberInstance().format(value);
			}
			return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		}

	}
	
	class FolderTableModel extends DefaultTableModel{
		
		private String[] columns = new String[] {"Name", "Size",  "Modified" };
		
		@Override
		public int getColumnCount() {
			return columns.length;
		}
		
		@Override
		public String getColumnName(int column) {
			return columns[column];
		}
		
		@Override
		public boolean isCellEditable(int row, int column) {
			return false;
		}
		
		@Override
		public int getRowCount() {
			return displayFiles.size();
		}
		
		@Override
		public Object getValueAt(int row, int column) {
			switch(column) {
				case 0:
					return displayFiles.get(row).getName();
				case 1:
					return displayFiles.get(row).length();
				case 2: 
					return dateFormat.format(new Date(displayFiles.get(row).lastModified()));
			}
			// TODO Auto-generated method stub
			return "";
		}
	}
	
	class FolderTreeNode implements TreeNode{

		private File			root	= null;
		private List<FolderTreeNode>		childs	= null;
		private FolderTreeNode	parent	= null;
		
		public FolderTreeNode(File a_root, FolderTreeNode a_parent) {
			root = a_root;
			parent = a_parent;
			childs = new ArrayList<FolderViewer.FolderTreeNode>();
			File[] files = root.listFiles();
			for(File f : files) {
				if(f.isDirectory()) {
					childs.add(new FolderTreeNode(f, this));
				}
			}
		}

		public TreeNode getChildAt(int childIndex) {
			return childs.get(childIndex);
		}

		public int getChildCount() {
			return childs.size();
		}

		public TreeNode getParent() {
			return parent;
		}

		public int getIndex(TreeNode node) {
			return childs.indexOf(node);
		}

		public boolean getAllowsChildren() {
			return true;
		}

		public boolean isLeaf() {
			return false;
		}

		public Enumeration<FolderTreeNode> children() {
			return Collections.enumeration(childs);
		}
		
		@Override
		public String toString() {
			return root.getName();
		}
		
	}
}
