package app.adc.genericViewer.ui;

import java.awt.BorderLayout;
import java.net.URI;
import java.util.Properties;

import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;

public class SVNViewer extends AbstractViewer {

	private Properties		props			= null;
	private JTable			tableComments	= null;
	private JTextArea		comments		= null;
	private JList<String>	lstStructure	= null;

	public SVNViewer(URI a_source, Properties a_props) {
		super(a_source);
		props = a_props;
		
		init();
	}

	private void init() {
		tableComments = new JTable();
		comments = new JTextArea();
		lstStructure = new JList<String>();
		
		
		JSplitPane split1 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(tableComments), new JScrollPane(comments));
		
		JTabbedPane tab = new JTabbedPane();
		tab.addTab("Log", split1);
		tab.addTab("Explorer", new JScrollPane(lstStructure));
		
		add(tab, BorderLayout.CENTER);
	}

}