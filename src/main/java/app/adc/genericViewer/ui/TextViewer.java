package app.adc.genericViewer.ui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.io.File;
import java.io.FileReader;
import java.net.URI;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class TextViewer extends AbstractViewer {
	
	private JTextArea txtViewer = null;
	private URI source = null;

	public TextViewer(URI a_source) {
		super(a_source);
		source = a_source;
		init();
		loadContent();
	}

	
	private void init() {
		txtViewer = new JTextArea();
		txtViewer.setEditable(false);
		txtViewer.setFont(new Font("Roboto Mono", Font.PLAIN, 12));
		add(new JScrollPane(txtViewer), BorderLayout.CENTER);
	}
	
	private void loadContent() {
		try {
			txtViewer.read(new FileReader(new File(source)), "File");
		} catch (Throwable a_th) {
			a_th.printStackTrace();
		}
	}
}