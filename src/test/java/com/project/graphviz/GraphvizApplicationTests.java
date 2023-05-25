package com.project.graphviz;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.project.graphviz.controller.FileController;

@SpringBootTest
class GraphvizApplicationTests {
	@Autowired
	FileController fileController;

	@Test
	void contextLoads() throws Exception {
		fileController.myTest();

	}

}
