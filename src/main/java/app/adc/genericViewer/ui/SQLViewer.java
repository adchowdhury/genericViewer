package app.adc.genericViewer.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.net.URI;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import app.adc.genericViewer.ui.IConstant.MariaDBConstants;

public class SQLViewer extends AbstractViewer {

	private JToolBar	tb				= null;
	private JButton		execute			= null;
	private JButton		executeAll		= null;
	private JTextArea	query			= null;
	private JTree		dbExplorer		= null;
	private JTable		resultPane		= null;
	private JTextArea	resultComment	= null;
	
	private Properties props = null;
	
	private DBElement rootNode = null;

	public SQLViewer(URI source, Properties a_props) {
		super(source);
		props = a_props;
		init();
		Thread t = new Thread(new Runnable() {
			
			public void run() {
				connectToDB();
			}
		});
		t.start();
	}

	private void init() {
		query = new JTextArea();
		dbExplorer = new JTree(rootNode = new DBElement(null, "Databases", new Vector<TreeNode>()));
		resultPane = new JTable();
		resultComment = new JTextArea();
		tb = new JToolBar();
		//dbExplorer.setPreferredSize(new Dimension(150, 100));
		
		JSplitPane split1 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(query), new JScrollPane(resultPane));
		JSplitPane split2 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(dbExplorer), split1);
		
		split1.setDividerLocation(200);
		split2.setDividerLocation(150);
		
		add(tb, BorderLayout.NORTH);
		add(split2, BorderLayout.CENTER);
		
	}
	//jdbc:mysql://localhost:3306/?user=root
	private void connectToDB() {
		Connection conn = null;
		try {
			Class.forName("org.mariadb.jdbc.Driver");
			
			if(props.getProperty(MariaDBConstants.Password.getText()) == null) {
				conn = DriverManager.getConnection("jdbc:mariadb://" + props.getProperty(MariaDBConstants.DBHost.getText()) + ":" + props.getProperty(MariaDBConstants.DBPort.getText()) + "/?user=" + props.getProperty(MariaDBConstants.Username.getText()));
			}else {
				conn = DriverManager.getConnection("jdbc:mariadb://" + props.getProperty(MariaDBConstants.DBHost.getText()) + ":" + props.getProperty(MariaDBConstants.DBPort.getText()), 
						props.getProperty(MariaDBConstants.Username.getText()), 
						props.getProperty(MariaDBConstants.Password.getText()));
			}
			
			System.out.println("SQLViewer.connectToDB(connected)");
			DBElement rootNode = new DBElement(null, "Databases", new Vector<TreeNode>());
			
			DatabaseMetaData dbm = conn.getMetaData();
			ResultSet rs = dbm.getCatalogs();
			
			travarseResultSet(rs, rootNode, "TABLE_CAT");
			rs.close();
			Enumeration<TreeNode> e = rootNode.getChildren().elements();
			DBElement dbe = null;
			while(e.hasMoreElements()) {
				DBElement dbeCatalog = (DBElement)e.nextElement();
				dbeCatalog.getChildren().add(dbe = new DBElement(dbeCatalog, "Tables", null));
				
				rs = dbm.getTables(dbeCatalog.displayText, null, null, new String[] {"TABLE"});
				travarseResultSet(rs, dbe, "TABLE_NAME");
				
				dbeCatalog.getChildren().add(dbe = new DBElement(dbeCatalog, "Views", null));
				rs = dbm.getTables(dbeCatalog.displayText, null, null, new String[] {"VIEW"});
				travarseResultSet(rs, dbe, "TABLE_NAME");
			}
			
			//rs = dbm.getTables(null,null,null,null);
			//travarseResultSet(rs);
			
			dbExplorer.setModel(new DefaultTreeModel(rootNode));
		} catch (Throwable a_th) {
			a_th.printStackTrace();
		}
	}
	
	private void travarseResultSet(ResultSet rs, DBElement parent, String displayColumnName) throws Throwable{
		if(rs == null || rs.isClosed()) {
			return;
		}
		
		ResultSetMetaData rsmd = rs.getMetaData();
		int c = rsmd.getColumnCount();
		while(rs.next()) {
			parent.getChildren().add(new DBElement(parent, rs.getString(displayColumnName), new Vector<TreeNode>()));
			
			for(int i=1; i<= c; i++) {
				System.out.println(rsmd.getColumnLabel(i) + " - " + rs.getObject(i));
			}
			System.out.println("==========");
		}
	}
	
	class DBElement implements TreeNode{
		
		private Vector<TreeNode>	children	= null;
		private TreeNode			parent		= null;
		private String				displayText	= null;
		
		public DBElement(TreeNode a_parent, String a_displayText, Vector<TreeNode> a_children) {
			children = a_children;
			parent = a_parent;
			displayText = a_displayText;
			
			if(children == null) {
				children = new Vector<TreeNode>();
			}
		}

		public TreeNode getChildAt(int childIndex) {
			return children.get(childIndex);
		}

		public int getChildCount() {
			return children.size();
		}

		public TreeNode getParent() {
			return parent;
		}

		public int getIndex(TreeNode node) {
			return children.indexOf(node);
		}

		public boolean getAllowsChildren() {
			return false;
		}

		public boolean isLeaf() {
			return false;
		}

		public Enumeration children() {
			// TODO Auto-generated method stub
			return children.elements();
		}
		
		public Vector<TreeNode> getChildren() {
			return children;
		}
		
		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return displayText;
		}
		
	}
}