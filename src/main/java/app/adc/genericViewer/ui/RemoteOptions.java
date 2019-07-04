package app.adc.genericViewer.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class RemoteOptions extends JPanel {

	private JList<RemoteService>			remoteServiceLists	= null;
	private JPanel							configPanel			= null;
	private JButton							btnConnect			= null;
	private JButton							btnCancel			= null;
	private final static RemoteService[]	services			= { new MariaDB() };

	public RemoteOptions() {
		init();
	}

	private void init() {
		setLayout(new BorderLayout());
		
		configPanel = new JPanel();

		btnConnect	= new JButton("Connect");
		btnCancel	= new JButton("Cancel");
		
		btnConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a_event) {
				((JDialog)SwingUtilities.getRoot(RemoteOptions.this)).dispose();
				MenuEventListener.openResource(services[remoteServiceLists.getSelectedIndex()].getViewer(), services[remoteServiceLists.getSelectedIndex()].getTitleText());
			}
		});

		remoteServiceLists = new JList<RemoteService>(new ListModel<RemoteService>() {

			public int getSize() {
				return services.length;
			}

			public RemoteService getElementAt(int index) {
				return services[index];
			}

			public void addListDataListener(ListDataListener l) {
				// TODO Auto-generated method stub

			}

			public void removeListDataListener(ListDataListener l) {
				// TODO Auto-generated method stub

			}

		});
		
		remoteServiceLists.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent a_event) {
				configPanel.removeAll();
				configPanel.add(new JScrollPane(services[a_event.getFirstIndex()].getConfigUI(null)));
			}
		});
		remoteServiceLists.setPreferredSize(new Dimension(150, 100));

		JPanel		pnlButton		= new JPanel();

		JSplitPane	mainSplitPane	= new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(remoteServiceLists),
				configPanel);

		pnlButton.add(btnConnect);
		pnlButton.add(btnCancel);

		add(pnlButton, BorderLayout.SOUTH);
		add(mainSplitPane, BorderLayout.CENTER);
	}
}