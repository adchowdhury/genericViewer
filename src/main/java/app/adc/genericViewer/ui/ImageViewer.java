package app.adc.genericViewer.ui;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Graphics;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;

import javax.imageio.ImageIO;
import javax.swing.JComponent;

public class ImageViewer extends AbstractViewer {
	
	private JComponent	displayCanvas	= null;
	private URI			sourceImage		= null;
	
	public ImageViewer(URI source) {
		super(source);
		sourceImage = source;
		init();
	}
	
	private void init() {
		add(displayCanvas = new JComponent() {
			@Override
			public void paint(Graphics g) {
				g.clearRect(0, 0, this.getWidth(), this.getHeight());
				try {
					g.drawImage(ImageIO.read(sourceImage.toURL()), 0,0, null);
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}, BorderLayout.CENTER);
		
		try {
			loadImage();
		} catch (Throwable a_th) {
			// TODO Auto-generated catch block
			a_th.printStackTrace();
		}
	}
	
	private void loadImage() throws Throwable {
        
        Thread th = new Thread(new Runnable() {
			Graphics g = displayCanvas.getGraphics();
			public void run() {
				System.out.println("ImageViewer.loadImage().new Runnable() {...}.run()");
				while(g == null) {
					
					System.out.println("ImageViewer.loadImage().new Runnable() {...}.run(while)");
					try {
						Thread.sleep(100);
						g = displayCanvas.getGraphics();
					} catch (Exception e) {
						e.printStackTrace();
					}
					g = displayCanvas.getGraphics();
				}
				System.out.println("ImageViewer.loadImage().new Runnable() {...}.run(end while)");
				try {
					//displayCanvas.createBufferStrategy(3);
					g.drawImage(ImageIO.read(sourceImage.toURL()), 0,0, null);
				} catch (Throwable a_th) {
					// TODO Auto-generated catch block
					a_th.printStackTrace();
				}
				
			}
		});
        th.start();
	}

}
