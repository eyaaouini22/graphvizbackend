package com.project.graphviz.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.project.graphviz.model.FileDb;
import com.project.graphviz.model.FileResponse;
import com.project.graphviz.model.GraphvizFile;
import com.project.graphviz.repository.GraphvizFileRepository;

@Service
public class GraphvizService extends FileDbService implements IGraphvizService {

	public GraphvizService(@Value("${file.storage.location:temp}") String fileStorageLocation) {
		super(fileStorageLocation);
	}

	@Autowired
	private GraphvizFileRepository graphvizFileRepository;

	@Override
	public FileDb saveFile(String uid, MultipartFile file, String fileName) {
		final GraphvizFile graphvizFile = new GraphvizFile(uid, fileName, file.getContentType(), file.getSize(),
				LocalDate.now());
		graphvizFileRepository.save(graphvizFile);
		return graphvizFile;
	}

	@Override
	public GraphvizFile getFileById(String id) {

		Optional<GraphvizFile> fileOptional = graphvizFileRepository.findById(id);

		if (fileOptional.isPresent()) {
			return fileOptional.get();
		}
		return null;
	}

	@Override
	public List<FileResponse> getFileList() {
		return graphvizFileRepository.findAll().stream().map(this::mapToFileResponse).collect(Collectors.toList());
	}

	@Override
	public GraphvizFile findFirstByName(String name) {
		return graphvizFileRepository.findFirstByName(name);
	}
}
