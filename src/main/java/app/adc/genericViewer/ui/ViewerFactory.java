package app.adc.genericViewer.ui;

import java.io.File;
import java.net.URI;

public final class ViewerFactory {
	
	public static AbstractViewer getViewer(URI a_source) {
		if(a_source == null) {
			return null;
		}
		AbstractViewer viewer = null;
		String path = a_source.getRawPath().toLowerCase();
		if(path.endsWith("gif") || path.endsWith("png") || path.endsWith("jpg") || path.endsWith("jpeg")
				|| path.endsWith("tiff")) {
			viewer = new ImageViewer(a_source);
		}else if(path.endsWith("zip") || path.endsWith("tar") || path.endsWith("rar") || path.endsWith("jar")) {
			viewer = new ZipViewer(a_source);
		}else if(path.endsWith("txt") || path.endsWith("css") || path.endsWith("js") || path.endsWith("cs")
				 || path.endsWith("html") || path.endsWith("html") || path.endsWith("xml") || path.endsWith("java")
				 || path.endsWith("info") || path.endsWith("properties") || path.endsWith("conf") || path.endsWith("csv")
				 || path.endsWith("log")) {
			viewer = new TextViewer(a_source);
		}else if(new File(a_source).isDirectory()) {
			viewer = new FolderViewer(a_source);
		}
		
		return viewer;
	}
}
