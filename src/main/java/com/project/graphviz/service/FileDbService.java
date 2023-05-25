package com.project.graphviz.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.project.graphviz.model.FileDb;
import com.project.graphviz.model.FileResponse;

//@Service
public class FileDbService implements IFileDbService {

	protected Path fileStoragePath;
	protected String fileStorageLocation;

	public FileDbService(@Value("${file.storage.location:temp}") String fileStorageLocation) {
		this.fileStorageLocation = fileStorageLocation;
		fileStoragePath = Paths.get(fileStorageLocation).toAbsolutePath().normalize();

		try {
			Files.createDirectories(fileStoragePath);
		} catch (IOException e) {
			throw new RuntimeException("Issue in creating file directory");
		}
	}

	@Override
	public FileResponse storeFile(MultipartFile file) {
		final String uid = UUID.randomUUID().toString();
		String fileName = StringUtils.cleanPath(file.getOriginalFilename()) + uid;
		Path filePath = Paths.get(fileStoragePath + "/" + fileName);
		try {
			Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			throw new RuntimeException("Issue in storing the file", e);
		}
		FileDb fileDb = saveFile(uid, file, fileName);
		return mapToFileResponse(fileDb);
	}

	@Override
	public FileDb saveFile(String uid, MultipartFile file, String fileName) {
		return new FileDb();
	}

	@Override
	public Resource downloadFile(String fileName) {

		Path path = Paths.get(fileStorageLocation).toAbsolutePath().resolve(fileName);

		Resource resource;
		try {
			resource = new UrlResource(path.toUri());

		} catch (MalformedURLException e) {
			throw new RuntimeException("Issue in reading the file", e);
		}

		if (resource.exists() && resource.isReadable()) {
			return resource;
		} else {
			throw new RuntimeException("the file doesn't exist or not readable");
		}
	}

	@Override
	public FileResponse mapToFileResponse(FileDb fileDb) {
		String url = ServletUriComponentsBuilder.fromCurrentContextPath().path("/graph/download/")
				.path(fileDb.getName()).toUriString();
		return new FileResponse(fileDb.getId(), fileDb.getType(), fileDb.getName(), url, fileDb.getSize(),
				fileDb.getCreatedAt());
	}

	@Override
	public String getFileStorageLocation() {
		return fileStorageLocation;
	}

//	@Override
//	public String modifyImage(FileDb fileDb) {
//		fileDbRepository.save(fileDb);
//		return fileDb.getName();
//	}
//

//
//	@Override
//	public FileDb findFirstByName(String name) {
//		List<FileDb> list = fileDbRepository.findByName(name);
//		return list.isEmpty() ? null : fileDbRepository.findByName(name).get(0);
//	}

}
