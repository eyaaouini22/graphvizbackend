package com.project.graphviz.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.project.graphviz.model.FileDb;
import com.project.graphviz.model.FileResponse;
import com.project.graphviz.model.GraphvizFile;
import com.project.graphviz.model.Image;
import com.project.graphviz.repository.ImageRepository;

@Service
public class ImageService extends FileDbService implements IImageService {

	@Autowired
	private ImageRepository imageRepository;

	public ImageService(@Value("${file.storage.location.image:temp}") String fileStorageLocation) {
		super(fileStorageLocation);
		fileStoragePath = Paths.get(fileStorageLocation).toAbsolutePath().normalize();

		try {
			Files.createDirectories(fileStoragePath);
		} catch (IOException e) {
			throw new RuntimeException("Issue in creating file directory");
		}
	}

	@Override
	public Image saveFile(String uid, File file, String fileName, String parameters, String graphviz_id) {
		final Image image = new Image(uid, fileName, "Image", file.length(), LocalDate.now(), parameters,
				new GraphvizFile(graphviz_id, null, null, null, null));
		imageRepository.save(image);
		return image;
	}

	@Override
	public FileResponse uploadAndstore(File file, String parameters, String graphvizName, String graphviz_id)
			throws IOException {
		final String uid = UUID.randomUUID().toString();
		final String fileName = uid + ".png";
		Path filePath = Paths.get(fileStoragePath + "/" + fileName);
		Files.move(file.toPath(), filePath, StandardCopyOption.REPLACE_EXISTING);
		FileDb fileDb = saveFile(uid, file, fileName, parameters, graphviz_id);
		return mapToFileResponse(fileDb);
	}

	@Override
	public Path getImagePath(String imageName) {

		return Paths.get(fileStorageLocation).toAbsolutePath().resolve(imageName);
	}

}
