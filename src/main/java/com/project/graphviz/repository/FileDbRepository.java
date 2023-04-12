package com.project.graphviz.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.graphviz.model.FileDb;

@Repository
public interface FileDbRepository extends JpaRepository<FileDb, String> {
	FileDb findByName(String name);
}
