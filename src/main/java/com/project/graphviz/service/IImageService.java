package com.project.graphviz.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import com.project.graphviz.model.FileResponse;
import com.project.graphviz.model.Image;

public interface IImageService {

	FileResponse uploadAndstore(File file, String parameters, String graphviz_id, String fileName) throws IOException;

	Image saveFile(String uid, File file, String fileName, String parameters, String graphviz_id);

	Path getImagePath(String imageName);

}
