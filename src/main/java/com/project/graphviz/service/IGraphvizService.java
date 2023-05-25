package com.project.graphviz.service;

import java.util.List;

import com.project.graphviz.model.FileResponse;
import com.project.graphviz.model.GraphvizFile;

public interface IGraphvizService {

	List<FileResponse> getFileList();

	GraphvizFile getFileById(String id);

	GraphvizFile findFirstByName(String name);

}
