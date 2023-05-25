package com.project.graphviz.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.project.graphviz.model.Image;

@Repository
public interface ImageRepository extends JpaRepository<Image, String> {

	@Query(value = "SELECT * FROM image WHERE  parameters = :parameters and graphviz_id = :graphviz_id", nativeQuery = true)
	public List<Image> FindImages(@Param("parameters") String parameters, @Param("graphviz_id") String graphviz_id);

//	@Query(value = "SELECT i FROM Image i Join o.graphvizFile g WHERE  o.parameters = :parameters and g.name:= name")
//	public List<Image> FindParametersAndGraphvizName(@Param("parameters") String parameters,
//			@Param("name") String name);
}
