package app.adc.genericViewer.ui;

import java.net.URI;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.JTree;

public class SQLViewer extends AbstractViewer {

	private JToolBar	tb				= null;
	private JButton		execute			= null;
	private JButton		executeAll		= null;
	private JTextArea	query			= null;
	private JTree		dbExplorer		= null;
	private JTable		resultPane		= null;
	private JTextArea	resultComment	= null;

	public SQLViewer(URI source) {
		super(source);
		init();
	}
	
	public SQLViewer(URI source, Properties props) {
		super(source);
		init();
	}

	private void init() {
		query = new JTextArea();
		dbExplorer = new JTree();
		resultPane = new JTable();
		resultComment = new JTextArea();
		
		
		
		JSplitPane split1 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(query), new JScrollPane(resultPane));
		
	}
}