package com.project.graphviz.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.graphviz.model.GraphvizFile;

@Repository
public interface GraphvizFileRepository extends JpaRepository<GraphvizFile, String> {
	List<GraphvizFile> findByName(String name);

//	List<FileDb> findByParameters(String parameters);

	GraphvizFile findFirstByName(String name);
}
