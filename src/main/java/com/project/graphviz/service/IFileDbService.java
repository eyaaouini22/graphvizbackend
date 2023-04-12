package com.project.graphviz.service;

import java.io.IOException;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import com.project.graphviz.model.FileDb;
import com.project.graphviz.model.FileResponse;

public interface IFileDbService {

	FileResponse uploadAndstore(MultipartFile file) throws IOException;

	FileDb getFileById(String id);

	List<FileResponse> getFileList();

	Resource downloadFile(String fileName);

	FileResponse storeFile(MultipartFile file);

	String getFileStorageLocation();

}
