package com.project.graphviz.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import com.project.graphviz.model.FileDb;
import com.project.graphviz.model.FileResponse;

public interface IFileDbService {

//	FileResponse uploadAndstore(MultipartFile file) throws IOException;

//	FileDb getFileById(String id);

//	List<FileResponse> getFileList();

	Resource downloadFile(String fileName);

	FileResponse storeFile(MultipartFile file);

	String getFileStorageLocation();

//	GraphvizFile findFirstByName(String name);
//
//	String modifyImage(FileDb fileDb);
//
//	FileDb findFirstByName(String name);

	FileResponse mapToFileResponse(FileDb fileDb);

	FileDb saveFile(String uid, MultipartFile file, String fileName);

}
