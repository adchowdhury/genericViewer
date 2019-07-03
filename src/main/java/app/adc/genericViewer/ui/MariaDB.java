package app.adc.genericViewer.ui;

import java.awt.GridLayout;
import java.util.Properties;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class MariaDB extends JPanel implements RemoteService {

	private JTextField		txtHostname		= null;
	private JTextField		txtPortNumber	= null;
	private JTextField		txtUserName		= null;
	private JPasswordField	txtPassword		= null;

	public MariaDB() {
		init();
	}

	private void init() {
		setLayout(new GridLayout(4, 2));
		
		txtHostname = new JTextField();
		txtHostname.setToolTipText("Provide host name here");
		txtHostname.setColumns(25);
		
		txtPortNumber = new JTextField("3306");
		txtPortNumber.setToolTipText("Provide port number here");
		txtPortNumber.setColumns(25);
		
		txtUserName = new JTextField();
		txtUserName.setToolTipText("Provide username here");
		txtUserName.setColumns(25);
		
		txtPassword = new JPasswordField();
		txtPassword.setToolTipText("Provide password here");
		txtPassword.setColumns(25);
		
		add(new JLabel("Host name"));
		add(txtHostname);
		
		add(new JLabel("Port number"));
		add(txtPortNumber);
		
		add(new JLabel("Username"));
		add(txtUserName);
		
		add(new JLabel("Password"));
		add(txtPassword);
	}

	public JPanel getConfigUI(Properties a_savedProperies) {
		return this;
	}

	public Properties getProperties() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getDisplayText() {
		return "Maria DB";
	}

	@Override
	public String toString() {
		return getDisplayText();
	}

	public AbstractViewer getViewer() {
		return new SQLViewer(null, getProperties());
	}

}