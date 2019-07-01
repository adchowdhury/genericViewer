package app.adc.genericViewer.ui;

import java.awt.Desktop;
import java.io.File;
import java.net.URI;

public final class ViewerFactory {

	public static AbstractViewer getViewer(URI source) {
		if(source == null) {
			return null;
		}
		AbstractViewer viewer = null;
		String path = source.getRawPath().toLowerCase();
		if(path.endsWith("gif") || path.endsWith("png") || path.endsWith("jpg") || path.endsWith("jpeg")
				|| path.endsWith("tiff")) {
			viewer = new ImageViewer(source);
		}else if(path.endsWith("zip") || path.endsWith("tar") || path.endsWith("rar") || path.endsWith("jar")) {
			viewer = new ZipViewer(source);
		}else if(new File(source).isDirectory()) {
			viewer = new FolderViewer(source);
		}
		
		return viewer;
	}
}
