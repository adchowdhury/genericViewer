package app.adc.genericViewer.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import app.adc.genericViewer.ui.IConstant.MariaDBConstants;

public class SQLViewer extends AbstractViewer {

	private JToolBar			tb				= null;
	private JButton				execute			= null;
	private JButton				executeAll		= null;
	private JTextArea			queryPane		= null;
	private JTree				dbExplorer		= null;
	private JTable				resultPane		= null;
	private JTextArea			resultComment	= null;

	private Properties			props			= null;

	private DBElement			rootNode		= null;

	private DBResultTableModel	tableModel		= null;

	private JComboBox<String>	dbList			= null;

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
		queryPane		= new JTextArea();
		dbExplorer		= new JTree(rootNode = new DBElement(null, "Databases", new Vector<TreeNode>()));
		resultPane		= new JTable(tableModel = new DBResultTableModel());
		resultComment	= new JTextArea();
		tb				= new JToolBar();
		execute			= new JButton("Execute");
		dbList = new JComboBox<String>();
		
		
		tb.add(dbList);
		tb.add(execute);
		execute.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a_action) {
				executeQuery();
			}
		});
		
		dbList.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a_action) {
				System.out.println("SQLViewer.init().new ActionListener() {...}.actionPerformed()");
			}
		});
		
		
		execute.setFocusable(false);
		
		JTabbedPane resultTab = new JTabbedPane();
		resultTab.addTab("Result", new JScrollPane(resultPane));
		resultTab.addTab("Log", new JScrollPane(resultComment));

		JSplitPane	split1	= new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(queryPane), resultTab);
		JSplitPane	split2	= new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(dbExplorer), split1);
		((DefaultTableCellRenderer)resultPane.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);
		
		resultPane.setAutoCreateRowSorter(false);
		resultPane.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		resultPane.setColumnSelectionAllowed(false);
		resultPane.setRowHeight(24);
		resultPane.setIntercellSpacing(new Dimension(5, 5));

		split1.setDividerLocation(200);
		split2.setDividerLocation(150);

		add(tb, BorderLayout.NORTH);
		add(split2, BorderLayout.CENTER);

	}

	private void executeQuery() {
		try {
			tableModel.clearAll();
			
			Connection conn = getConnection();
			if (conn == null) {
				System.out.println("SQLViewer.connectToDB(not connected)");
				return;
			}
			String		strSQLQuery	= queryPane.getSelectedText();
			if(strSQLQuery == null || strSQLQuery.trim().length() < 1) {
				resultComment.append("No query");
				return;
			}
			
			if(dbList.getSelectedIndex() < 0) {
				resultComment.append("No DB selected");
				return;
			}
			
			conn.setCatalog(dbList.getSelectedItem().toString());
			
			Statement			stmt	= conn.createStatement();
			ResultSet			rs		= stmt.executeQuery(strSQLQuery);
			ResultSetMetaData	rsmd	= rs.getMetaData();
			for(int colCounter = 1; colCounter <= rsmd.getColumnCount(); colCounter++) {
				tableModel.AddColumn(rsmd.getColumnLabel(colCounter));
			}
			Map<String, Object> rowData;
			while(rs.next()) {
				rowData = new HashMap<String, Object>();
				for(int colCounter = 1; colCounter <= rsmd.getColumnCount(); colCounter++) {
					rowData.put(rsmd.getColumnLabel(colCounter), rs.getObject(colCounter));
				}
				tableModel.addRowData(rowData);
			}
			rs.close();
			stmt.close();
			tableModel.fireTableDataChanged();
			tableModel.fireTableStructureChanged();
			resultPane.repaint();
		} catch (Throwable a_th) {
			a_th.printStackTrace();
			resultComment.append(a_th.getMessage());
		}

	}
	
	class DBResultTableModel extends DefaultTableModel{
		private List<String>				colsList	= new ArrayList<String>();
		private List<Map<String, Object>>	data		= new ArrayList<Map<String, Object>>();
		
		public void AddColumn(String a_strColumnName) {
			colsList.add(a_strColumnName);
		}
		
		public void clearAll() {
			colsList.clear();
			data.clear();
		}
		
		public void addRowData(Map<String, Object> a_data) {
			data.add(a_data);
		}
		
		@Override
		public int getColumnCount() {
			return colsList.size();
		}
		
		@Override
		public String getColumnName(int a_column) {
			return colsList.get(a_column);
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
		
		@Override
		public Object getValueAt(int a_row, int a_column) {
			return data.get(a_row).get(colsList.get(a_column));
		}
	}

	private Connection getConnection() {
		Connection conn = null;

		try {
			Class.forName("org.mariadb.jdbc.Driver");

			if (props.getProperty(MariaDBConstants.Password.getText()) == null) {
				conn = DriverManager
						.getConnection("jdbc:mariadb://" + props.getProperty(MariaDBConstants.DBHost.getText()) + ":"
								+ props.getProperty(MariaDBConstants.DBPort.getText()) + "?user="
								+ props.getProperty(MariaDBConstants.Username.getText()));
			} else {
				conn = DriverManager.getConnection(
						"jdbc:mariadb://" + props.getProperty(MariaDBConstants.DBHost.getText()) + ":"
								+ props.getProperty(MariaDBConstants.DBPort.getText()),
						props.getProperty(MariaDBConstants.Username.getText()),
						props.getProperty(MariaDBConstants.Password.getText()));
			}
		} catch (Throwable a_th) {
			a_th.printStackTrace();
		}

		return conn;
	}

	// jdbc:mysql://localhost:3306/?user=root
	private void connectToDB() {

		try {
			Connection conn = getConnection();
			if (conn == null) {
				System.out.println("SQLViewer.connectToDB(not connected)");
				return;
			}

			System.out.println("SQLViewer.connectToDB(connected)");
			DBElement			rootNode	= new DBElement(null, "Databases", new Vector<TreeNode>());

			DatabaseMetaData	dbm			= conn.getMetaData();
			ResultSet			rs			= dbm.getCatalogs();

			travarseResultSet(rs, rootNode, "TABLE_CAT");
			rs.close();
			Enumeration<TreeNode>	e	= rootNode.getChildren().elements();
			DBElement				dbe	= null;
			while (e.hasMoreElements()) {
				DBElement dbeCatalog = (DBElement) e.nextElement();
				dbeCatalog.getChildren().add(dbe = new DBElement(dbeCatalog, "Tables", null));

				rs = dbm.getTables(dbeCatalog.displayText, null, null, new String[] { "TABLE" });
				travarseResultSet(rs, dbe, "TABLE_NAME");

				dbeCatalog.getChildren().add(dbe = new DBElement(dbeCatalog, "Views", null));
				rs = dbm.getTables(dbeCatalog.displayText, null, null, new String[] { "VIEW" });
				travarseResultSet(rs, dbe, "TABLE_NAME");
			}

			// rs = dbm.getTables(null,null,null,null);
			// travarseResultSet(rs);

			dbExplorer.setModel(new DefaultTreeModel(rootNode));
		} catch (Throwable a_th) {
			a_th.printStackTrace();
		}
	}

	private void travarseResultSet(ResultSet rs, DBElement parent, String displayColumnName) throws Throwable {
		if (rs == null || rs.isClosed()) {
			return;
		}

		ResultSetMetaData	rsmd	= rs.getMetaData();
		int					c		= rsmd.getColumnCount();
		while (rs.next()) {
			parent.getChildren().add(new DBElement(parent, rs.getString(displayColumnName), new Vector<TreeNode>()));

			for (int i = 1; i <= c; i++) {
				System.out.println(rsmd.getColumnLabel(i) + " - " + rs.getObject(i));
			}
			if("TABLE_CAT".equalsIgnoreCase(displayColumnName)) {
				dbList.addItem(rs.getString(displayColumnName));
			}
			System.out.println("==========");
		}
	}

	class DBElement implements TreeNode {

		private Vector<TreeNode>	children	= null;
		private TreeNode			parent		= null;
		private String				displayText	= null;

		public DBElement(TreeNode a_parent, String a_displayText, Vector<TreeNode> a_children) {
			children	= a_children;
			parent		= a_parent;
			displayText	= a_displayText;

			if (children == null) {
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