package com.github.richardflee.voyager.fileio;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * This class encapsulates Voyager log file attributes
 */

public class VoyagerFileAttributes {
	
	private Path path = null;
	private String filename = "";
	private boolean exists = false;
	
	public VoyagerFileAttributes(Path path) {
		this.updatePath(path);
	}


	/**
	 * Sets fields to path parameters, otherwise default values of path is null
	 * 
	 * @param path Path to Voyager log file
	 */
	public void updatePath(Path path) {
		this.path = path;
		if (path != null)  {
			this.exists = Files.exists(path);
			this.filename = (this.exists) ? path.getFileName().toString() : "";
		}
	}
	
	public Path getPath() {
		return path;
	}

	public boolean isExists() {
		return exists;
	}

	public String getFilename() {
		return filename;
	}


	@Override
	public String toString() {
		return this.path.toAbsolutePath().toString();
	}
}
