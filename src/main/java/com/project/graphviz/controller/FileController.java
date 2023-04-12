package com.project.graphviz.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.project.graphviz.model.FileDb;
import com.project.graphviz.model.FileResponse;
import com.project.graphviz.service.FileDbService;

@RestController
@RequestMapping("graph")
public class FileController {

	@Autowired
	private FileDbService fileDbService;
	private Logger logger = LoggerFactory.getLogger(FileController.class);

	@PostMapping("/upload")
	public FileResponse uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
		return fileDbService.storeFile(file);
	}

	@ResponseBody
	@GetMapping("/{id}")
	public FileDb getFile(@PathVariable String id) {
		return fileDbService.getFileById(id);

	}

	@ResponseBody
	@GetMapping("/list")
	public List<FileResponse> getFileList() {
		return fileDbService.getFileList();
	}

	@GetMapping("/generate")
	public ResponseEntity<String> generateGraphiz(@RequestParam("file") String fileName,
			@RequestParam("params") String params) {

//		ProcessBuilder processBuilder = new ProcessBuilder("dot", "-Tx11",
//				fileDbService.getFileStorageLocation() + fileName, "-Nfontcolor=green", "-Nshape=rect");

		String[] paramsArray = params.split(" ");
		List<String> command = new ArrayList<>();
		command.add("dot");
		command.add("-Tx11");
		command.add(fileDbService.getFileStorageLocation() + fileName);
		command.addAll(Arrays.asList(paramsArray));
		final ProcessBuilder processBuilder = new ProcessBuilder(command);
		try {
			final Process process = processBuilder.start();
			int exitCode = process.waitFor();
			if (exitCode != 0)
				logger.error("Error opening file.");
		} catch (IOException | InterruptedException e) {
			logger.error(e.getMessage());
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("/download/{fileName}")
	ResponseEntity<Resource> downLoadSingleFile(@PathVariable String fileName, HttpServletRequest request) {

		Resource resource = fileDbService.downloadFile(fileName);

		String mimeType;

		try {
			mimeType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
		} catch (IOException e) {
			mimeType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
		}
		mimeType = mimeType == null ? MediaType.APPLICATION_OCTET_STREAM_VALUE : mimeType;

		return ResponseEntity.ok().contentType(MediaType.parseMediaType(mimeType))
				.header(HttpHeaders.CONTENT_DISPOSITION, "inline;fileName=" + resource.getFilename()).body(resource);
	}

	@GetMapping("/render")
	public ResponseEntity<byte[]> renderGraph(@RequestParam("file") String fileName,
			@RequestParam("params") String params) {

		String[] paramsArray = params.split(" ");

		List<String> command = new ArrayList<>();
		command.add("dot");
		command.add("-Tpng");
		command.add("-o");
		command.add("output.png");
		command.add(fileDbService.getFileStorageLocation() + fileName);
		command.addAll(Arrays.asList(paramsArray));
		final ProcessBuilder processBuilder = new ProcessBuilder(command);

		try {
			final Process process = processBuilder.start();
			int exitCode = process.waitFor();
			if (exitCode != 0)
				System.out.println("Error opening file.");
		} catch (IOException | InterruptedException e) {
			logger.error(e.getMessage());
		}

		// Return the rendered graph as a Base64 encoded string

		try {
			byte[] bytes = Files.readAllBytes(Paths.get("output.png"));

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.IMAGE_PNG);
			headers.setContentLength(bytes.length);
			return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
		} catch (IOException e) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
//	@PostMapping
//	public ResponseEntity<byte[]> generateGraph(@RequestParam("file") MultipartFile file,
//			@RequestParam("params") Map<String, String> params) throws IOException {
//		MutableGraph graph = new Parser().read(file.getInputStream());
//		Graphviz graphviz = Graphviz.fromGraph(graph);
//		graphviz.no
//		graph.graphAttrs().add(
//		graph.
//		graphviz.useEngine(Engine.DOT);
//		params.forEach(graphviz.wi

//		
//	    GraphvizCmdLineEngine engine = new GraphvizCmdLineEngine();
//        Options options = Options.create();
//        options.
//        Rasterizer rasterizer = Rasterizer.CAIRO;
//        EngineResult result = engine.execute(dotSource, options, rasterizer);
//        byte[] renderedGraph = result.getOutput();
//				GraphvizCmdLineEngine engine = new GraphvizCmdLineEngine();
//				 List<String> options = new ArrayList<>();
//			        options.add("-Kdot");
//			        options.add("-Goverlap=scale");
//
//				
//		        byte[] renderedGraph = engine.execute(Graphviz.fromGraph(graph),"",null ).toByteArray();
//				
//		 engine.execute(src, options, rasterizer)
// 		params.forEach(engine::addOption);
//		byte[] renderedGraph = graphviz.render(graph, Format.PNG).toByteArray();
//		HttpHeaders headers = new HttpHeaders();
//		headers.setContentType(MediaType.IMAGE_PNG);
//		headers.setContentLength(renderedGraph.length);
//		return new ResponseEntity<>(renderedGraph, headers, HttpStatus.OK);

}
