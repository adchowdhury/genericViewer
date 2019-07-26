package app.adc.genericViewer.ui;

import java.awt.GridLayout;
import java.util.Properties;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class SVNViewerService extends JPanel implements RemoteService {
	
	private JTextField		txtHostName		= null;
	private JTextField		txtHostURL		= null;
	private JTextField		txtUserName		= null;
	private JPasswordField	txtPassword		= null;

	
	public SVNViewerService() {
		init();
	}

	private void init() {
		setLayout(new GridLayout(4, 2));
		
		txtHostName = new JTextField("");
		txtHostName.setToolTipText("Provide host name here");
		txtHostName.setColumns(25);

		txtHostURL = new JTextField("");
		txtHostURL.setToolTipText("Provide host URL here");
		txtHostURL.setColumns(25);
		
		txtUserName = new JTextField("......");
		txtUserName.setToolTipText("Provide username here");
		txtUserName.setColumns(25);
		
		txtPassword = new JPasswordField("......");
		txtPassword.setToolTipText("Provide password here");
		txtPassword.setColumns(25);
		
		add(new JLabel("Host name"));
		add(txtHostName);

		add(new JLabel("Host URL"));
		add(txtHostURL);
		
		add(new JLabel("Username"));
		add(txtUserName);
		
		add(new JLabel("Password"));
		add(txtPassword);
	}

	public JPanel getConfigUI(Properties a_savedProperies) {
		return this;
	}

	public Properties getProperties() {
		Properties props = new Properties();
		
		if(txtHostURL.getText().trim().length() < 1) {
			JOptionPane.showMessageDialog(null, "Please enter hostname");
			txtHostURL.transferFocus();
			throw new RuntimeException("Hostname not provided");
		}
		
		if(txtUserName.getText().trim().length() < 1) {
			JOptionPane.showMessageDialog(null, "Please enter username");
			txtUserName.transferFocus();
			throw new RuntimeException("Username not provided");
		}
		
		props.put(IConstant.SVNConstants.SVNHost.getText(), txtHostURL.getText());
		props.put(IConstant.SVNConstants.Username.getText(), txtUserName.getText());
		props.put(IConstant.SVNConstants.Password.getText(), txtPassword.getText());
		
		return props;
	}

	public String getDisplayText() {
		// TODO Auto-generated method stub
		return "SVN";
	}

	public AbstractViewer getViewer() {
		// TODO Auto-generated method stub
		return new SVNViewer(null, getProperties());
	}

	public String getTitleText() {
		return "SVN";
	}
	
	@Override
	public String toString() {
		return getDisplayText();
	}

}
