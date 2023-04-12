package com.project.graphviz.service;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.project.graphviz.model.FileDb;
import com.project.graphviz.model.FileResponse;
import com.project.graphviz.repository.FileDbRepository;

@Service

public class FileDbService implements IFileDbService {

	@Autowired
	private FileDbRepository fileDbRepository;
	private Path fileStoragePath;
	private String fileStorageLocation;

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
	public FileResponse uploadAndstore(MultipartFile file) throws IOException {
		String fileName = file.getOriginalFilename();
		file.transferTo(new File(fileStorageLocation + fileName));
		FileDb fileDb = saveFile(file, fileName);
		return mapToFileResponse(fileDb);
	}

	@Override
	public FileResponse storeFile(MultipartFile file) {
		String fileName = StringUtils.cleanPath(file.getOriginalFilename());
		Path filePath = Paths.get(fileStoragePath + "/" + fileName);
		try {
			Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			throw new RuntimeException("Issue in storing the file", e);
		}
		FileDb fileDb = saveFile(file, fileName);
		return mapToFileResponse(fileDb);
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

	private FileDb saveFile(MultipartFile file, String fileName) {
		final FileDb fileDb = new FileDb(UUID.randomUUID().toString(), fileName, file.getContentType());
		fileDbRepository.save(fileDb);
		return fileDb;
	}

	@Override
	public FileDb getFileById(String id) {

		Optional<FileDb> fileOptional = fileDbRepository.findById(id);

		if (fileOptional.isPresent()) {
			return fileOptional.get();
		}
		return null;
	}

	@Override
	public List<FileResponse> getFileList() {
		return fileDbRepository.findAll().stream().map(this::mapToFileResponse).collect(Collectors.toList());
	}

	private FileResponse mapToFileResponse(FileDb fileDb) {
		String url = ServletUriComponentsBuilder.fromCurrentContextPath().path("/graph/download/")
				.path(fileDb.getName()).toUriString();
		return new FileResponse(fileDb.getId(), fileDb.getType(), fileDb.getName(), url);
	}

	@Override
	public String getFileStorageLocation() {
		return fileStorageLocation;
	}

}
