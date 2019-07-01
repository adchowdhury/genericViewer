package app.adc.genericViewer.ui;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.net.URI;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JScrollPane;

public class ImageViewer extends AbstractViewer {
	
	private JComponent	displayCanvas	= null;
	private URI			sourceImage		= null;
	
	public ImageViewer(URI source) {
		super(source);
		sourceImage = source;
		init();
	}
	
	private void init() {
		displayCanvas = new JComponent() {
			@Override
			public void paint(Graphics g) {
				g.clearRect(0, 0, this.getWidth(), this.getHeight());
				g.drawImage(getImage(), 0,0, null);
			}
		};
		add(new JScrollPane(displayCanvas), BorderLayout.CENTER);
		
		try {
			loadImage();
		} catch (Throwable a_th) {
			// TODO Auto-generated catch block
			a_th.printStackTrace();
		}
	}
	
	private Image getImage()  {
		Image img = null;
		try {
			img = ImageIO.read(sourceImage.toURL());
			if(img.getWidth(null) > displayCanvas.getWidth() && img.getWidth(null) > displayCanvas.getWidth()) {
				img = img.getScaledInstance(displayCanvas.getWidth(), displayCanvas.getHeight(), Image.SCALE_FAST);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return img;
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
					g.drawImage(getImage(), 0,0, null);
				} catch (Throwable a_th) {
					// TODO Auto-generated catch block
					a_th.printStackTrace();
				}
				
			}
		});
        th.start();
	}

}
