package app.adc.genericViewer.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

public class SVNViewer extends AbstractViewer {

	private Properties							props			= null;
	private JTable								tableComments	= null;
	private JList<SVNLogEntryPath>				logEntry		= null;
	private JTable								tblStructure	= null;
	private RepositoryListTableModel			tableModel		= null;
	private LogTableModel						logTableModel	= null;
	private SVNRepository						repository		= null;

	private int									level			= 0;
	private String								currentPath		= "";

	private JLabel								lblPath			= null;
	private DefaultListModel<SVNLogEntryPath>	logEntryData	= null;

	public SVNViewer(URI a_source, Properties a_props) {
		super(a_source);
		props = a_props;

		init();
		loadSVNData();
	}

	private void init() {
		logEntryData = new DefaultListModel<SVNLogEntryPath>();
		tableComments	= new JTable(logTableModel = new LogTableModel());
		logEntry		= new JList<SVNLogEntryPath>(logEntryData);
		tblStructure	= new JTable(tableModel = new RepositoryListTableModel());
		
		
		((DefaultTableCellRenderer)tableComments.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);
		tableComments.setAutoCreateRowSorter(true);
		tableComments.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tableComments.setColumnSelectionAllowed(false);
		tableComments.setRowHeight(24);
		tableComments.setIntercellSpacing(new Dimension(5, 5));
		
		
		((DefaultTableCellRenderer)tblStructure.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);
		tblStructure.setAutoCreateRowSorter(true);
		tblStructure.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tblStructure.setColumnSelectionAllowed(false);
		tblStructure.setRowHeight(24);
		tblStructure.setIntercellSpacing(new Dimension(5, 5));
		
		tblStructure.getColumnModel().getColumn(0).setCellRenderer(new ImageRenderer());
		tblStructure.getColumnModel().getColumn(0).setPreferredWidth(24);
		tblStructure.getColumnModel().getColumn(0).setMinWidth(24);
		tblStructure.getColumnModel().getColumn(0).setMaxWidth(24);
		tblStructure.getColumnModel().getColumn(0).setResizable(false);
		
		lblPath = new JLabel();
		JPanel pnlExplorer = new JPanel(new BorderLayout()); 
		pnlExplorer.add(new JScrollPane(tblStructure), BorderLayout.CENTER);
		pnlExplorer.add(lblPath, BorderLayout.NORTH);

		JSplitPane	split1	= new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(tableComments), new JScrollPane(logEntry));
		split1.setDividerLocation(250);

		JTabbedPane	tab		= new JTabbedPane();
		tab.addTab("Log", split1);
		tab.addTab("Explorer", pnlExplorer);

		add(tab, BorderLayout.CENTER);
		
		tableComments.addMouseListener(new MouseAdapter() {
		    public void mousePressed(MouseEvent mouseEvent) {
		    	JTable table =(JTable) mouseEvent.getSource();
		    	Point point = mouseEvent.getPoint();
		    	int row = table.rowAtPoint(point);
		    	
		    	if(mouseEvent.getClickCount() < 2 || table.getSelectedRow() < 0 || row < 0) {
		    		return;
		    	}
		    	
		    	loadLogEntryDetail(logTableModel.getDataAt(row));
		    }
		});
		
		tblStructure.addMouseListener(new MouseAdapter() {
		    public void mousePressed(MouseEvent mouseEvent) {
		    	JTable table =(JTable) mouseEvent.getSource();
		    	Point point = mouseEvent.getPoint();
		    	int row = table.rowAtPoint(point);
		    	
		    	if(mouseEvent.getClickCount() < 2 || table.getSelectedRow() < 0 || row < 0) {
		    		return;
		    	}
		    	
		        SVNDirEntry entry = tableModel.getDataAt(row);
		        if(entry.getKind() == SVNNodeKind.DIR) {

	        		String path = "";
	        		//System.out.println("current path[a] : " + currentPath);
	        		if(level == 0) {
	        			path = entry.getName();
	        		}else {
	        			if(row == 0) {
	        				if(currentPath.lastIndexOf('/') < 0) {
	        					path = "";
	        				}else {
	        					path = currentPath.substring(0, currentPath.lastIndexOf('/'));	
	        				}
	        			}else {
	        				path = currentPath + "/" + entry.getName();
	        			}
	        		}
	        		
		        	if(level == 0) {
		        		level++;
		        	}else {
		        		if(row == 0) {
		        			level--;
		        		}else {
		        			level++;
		        		}
		        	}

	        		
		        	try {
						listEntries(path, entry);
					} catch (SVNException a_excp) {
						// TODO Auto-generated catch block
						a_excp.printStackTrace();
					}
		        	//System.out.println("current path[b] : " + currentPath);
		        }
		    }
		});
	}
	
	private void loadLogEntryDetail(SVNLogEntry a_logEntry) {
		Set changedPathsSet = a_logEntry.getChangedPaths().keySet();
		logEntryData.removeAllElements();
		for (Iterator changedPaths = changedPathsSet.iterator(); changedPaths.hasNext();) {
			SVNLogEntryPath entryPath = (SVNLogEntryPath) a_logEntry.getChangedPaths().get(changedPaths.next());
			logEntryData.addElement(entryPath);
		}
		
		//logEntryData.f
	}

	private void loadSVNData() {
		
		try {
			repository = SVNRepositoryFactory.create(SVNURL.parseURIDecoded(props.getProperty(IConstant.SVNConstants.SVNHost.getText())));
			ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(props.getProperty(IConstant.SVNConstants.Username.getText()), props.getProperty(IConstant.SVNConstants.Password.getText()));
			repository.setAuthenticationManager(authManager);
			System.out.println("Repository Root: " + repository.getRepositoryRoot(true));
			System.out.println("Repository UUID: " + repository.getRepositoryUUID(true));

			SVNNodeKind nodeKind = repository.checkPath("", -1);
			
			if (nodeKind == SVNNodeKind.NONE) {
				System.err.println("There is no entry at '" + props.getProperty(IConstant.SVNConstants.SVNHost.getText()) + "'.");
				// System.exit(1);
			} else if (nodeKind == SVNNodeKind.FILE) {
				System.err.println("The entry at '" + props.getProperty(IConstant.SVNConstants.SVNHost.getText()) + "' is a file while a directory was expected.");
				// System.exit(1);
			}else {
				listEntries("", null);
			}
			
			
			 long endRevision = repository.getLatestRevision();
			
			 Collection logEntries = repository.log(new String[] {""}, null, 0, endRevision, true, true);
			 logTableModel.clearAll();
			 for (Iterator entries = logEntries.iterator(); entries.hasNext();) {
				 SVNLogEntry logEntry = (SVNLogEntry) entries.next();
				 logTableModel.addRowData(logEntry);
			 }
			 logTableModel.fireTableDataChanged();
		} catch (Throwable a_th) {
			a_th.printStackTrace();
		}
	}
	

	public void listEntries(String path, SVNDirEntry parent) throws SVNException {
		currentPath = path;
		lblPath.setText("[root]/" + currentPath);
		Collection	entries		= repository.getDir(path, -1, null, (Collection) null);
		Iterator	iterator	= entries.iterator();
		tableModel.clearAll();
		if(path.trim().length() > 0) {
			tableModel.addRowData(parent);
		}
		while (iterator.hasNext()) {
			SVNDirEntry entry = (SVNDirEntry) iterator.next();
			tableModel.addRowData(entry);

			//System.out.println("/" + (path.equals("") ? "" : path + "/") + entry.getName() + " ( author: '" + entry.getAuthor() + "'; revision: " + entry.getRevision() + "; date: " + entry.getDate() + ")");
			
			//if (entry.getKind() == SVNNodeKind.DIR) {
			//	listEntries(repository, (path.equals("")) ? entry.getName() : path + "/" + entry.getName());
			//}
		}
		tableModel.fireTableDataChanged();
	}
	
	class ImageRenderer extends DefaultTableCellRenderer {
		JLabel lbl = new JLabel();

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			lbl.setIcon((ImageIcon) value);
			return lbl;
		}
	}
	

	class LogTableModel extends DefaultTableModel{
		private String[] columns	= new String[] { "Revision", "Author",  "Date", "Comment" };
		private List<SVNLogEntry>	data	= new ArrayList<SVNLogEntry>();
		
		public void clearAll() {
			data.clear();
		}
		
		public void addRowData(SVNLogEntry a_svnLog) {
			data.add(a_svnLog);
		}
		
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
			if(data == null) {
				return 0;
			}
			return data.size();
		}
		
		
		public SVNLogEntry getDataAt(int a_row) {
			return data.get(a_row);
		}

		
		@Override
		public Object getValueAt(int a_row, int a_column) {
			switch(a_column) {
				case 0:
					return data.get(a_row).getRevision();
				case 1:
					return data.get(a_row).getAuthor();
				case 2:
					return data.get(a_row).getDate();
				case 3:
					return data.get(a_row).getMessage();
				default:
					return "";
			}
		}

	}
	
	class RepositoryListTableModel extends DefaultTableModel{
		private String[]					columns	= new String[] { "", "Name", "Author", "Revision", "Date", "Comment" };
		private List<SVNDirEntry>	data	= new ArrayList<SVNDirEntry>();
		
		public void clearAll() {
			data.clear();
		}
		
		public void addRowData(SVNDirEntry a_svnEntry) {
			data.add(a_svnEntry);
		}
		
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
			if(data == null) {
				return 0;
			}
			return data.size();
		}
		
		public SVNDirEntry getDataAt(int a_row) {
			return data.get(a_row);
		}
		
		@Override
		public Object getValueAt(int a_row, int a_column) {
			switch(a_column) {
				case 0:
					if (data.get(a_row).getKind() == SVNNodeKind.DIR) {
						return IConstant.FolderIcon;
					} else {
						return IConstant.FileIcon;
					}
				case 1:
					if(level > 0 && a_row == 0) {
						return "..";
					}
					return data.get(a_row).getName();
				case 2:
					return data.get(a_row).getAuthor();
				case 3:
					return data.get(a_row).getRevision();
				case 4:
					return data.get(a_row).getDate();
				case 5:
					return data.get(a_row).getCommitMessage();
				default :
					return "";
			}
		}
		
	}

}