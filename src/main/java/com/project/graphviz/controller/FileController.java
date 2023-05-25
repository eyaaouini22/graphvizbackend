package com.project.graphviz.controller;

import static guru.nidi.graphviz.model.Factory.graph;
import static guru.nidi.graphviz.model.Factory.node;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
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
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.project.graphviz.model.FileDb;
import com.project.graphviz.model.FileResponse;
import com.project.graphviz.model.GraphvizFile;
import com.project.graphviz.model.Image;
import com.project.graphviz.model.MyRecord;
import com.project.graphviz.repository.ImageRepository;
import com.project.graphviz.service.IFileDbService;
import com.project.graphviz.service.IGraphvizService;
import com.project.graphviz.service.IImageService;

import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.Graph;
import guru.nidi.graphviz.model.Node;

@CrossOrigin
@RestController
@RequestMapping("graph")
public class FileController {

	@Autowired
	private IGraphvizService graphvizService;
	@Autowired
	private IImageService imageService;
	@Autowired
	private ImageRepository imageRepository;
	private Logger logger = LoggerFactory.getLogger(FileController.class);

	@PostMapping("/upload")
	public FileResponse uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
		return ((IFileDbService) graphvizService).storeFile(file);
	}

	@ResponseBody
	@GetMapping("/{id}")
	public FileDb getFile(@PathVariable String id) {
		return graphvizService.getFileById(id);

	}

	@ResponseBody
	@GetMapping("/list")
	public List<FileResponse> getFileList() {
		return graphvizService.getFileList();
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
		command.add(((IFileDbService) graphvizService).getFileStorageLocation() + fileName);
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

		Resource resource = ((IFileDbService) graphvizService).downloadFile(fileName);

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

	@GetMapping("/renderByte")
	public ResponseEntity<byte[]> renderImageByte(@RequestParam("fileName") String fileName,
			@RequestParam("params") String params) {
		byte[] bytes = null;
		final GraphvizFile graphvizFile = graphvizService.findFirstByName(fileName);
		final List<Image> imageList = imageRepository.FindImages(params, graphvizFile.getId());
		final Path path = Paths.get("output.png");

		if (CollectionUtils.isEmpty(imageList)) {

			final String[] paramsArray = params.split(" ");
			final List<String> command = new ArrayList<>();
			command.add("dot");
			command.add("-Tpng");
			command.add("-o");
			command.add("output.png");
			command.add(((IFileDbService) graphvizService).getFileStorageLocation() + fileName);
			command.addAll(Arrays.asList(paramsArray));
			final ProcessBuilder processBuilder = new ProcessBuilder(command);

			try {
				final Process process = processBuilder.start();
				int exitCode = process.waitFor();
				if (exitCode != 0)
					logger.error("Error opening file.");
			} catch (IOException | InterruptedException e) {
				logger.error(e.getMessage());
			}

			// Return the rendered graph as a Base64 encoded string

			try {
				bytes = Files.readAllBytes(path);
//				MultipartFile multipartFile = MultipartFileUtils.convertToMultipartFile("/path/to/file.jpg");
				imageService.uploadAndstore(path.toFile(), params, fileName, graphvizFile.getId());

			} catch (IOException e) {
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			}

		} else {
			try {
				bytes = Files.readAllBytes(imageService.getImagePath(imageList.get(0).getName()));
			} catch (IOException e) {
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

			}
		}
		bytes = Base64.getEncoder().encode(bytes);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.IMAGE_PNG);
		headers.setContentLength(bytes.length);
		return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
	}

	@GetMapping("/render")
	public ResponseEntity<String> renderImage(@RequestParam("fileName") String fileName,
			@RequestParam("params") String params) {
		String imageName = "";
		final GraphvizFile graphvizFile = graphvizService.findFirstByName(fileName);
		final List<Image> imageList = imageRepository.FindImages(params, graphvizFile.getId());
		final Path path = Paths.get("output.png");

		if (CollectionUtils.isEmpty(imageList)) {

			final String[] paramsArray = params.split(" ");
			final List<String> command = new ArrayList<>();
			command.add("dot");
			command.add("-Tpng");
			command.add("-o");
			command.add("output.png");
			command.add(((IFileDbService) graphvizService).getFileStorageLocation() + fileName);
			command.addAll(Arrays.asList(paramsArray));
			final ProcessBuilder processBuilder = new ProcessBuilder(command);

			try {
				final Process process = processBuilder.start();
				int exitCode = process.waitFor();
				if (exitCode != 0)
					logger.error("Error opening file.");
			} catch (IOException | InterruptedException e) {
				logger.error(e.getMessage());
			}

			// Return the rendered graph as a Base64 encoded string

			try {
				imageName = imageService.uploadAndstore(path.toFile(), params, fileName, graphvizFile.getId())
						.getName();
			} catch (IOException e) {
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			}

		} else

			imageName = imageList.get(0).getName();

		return new ResponseEntity<>(imageService.getImagePath(imageName).toString(), HttpStatus.OK);
	}

	@GetMapping("/jsonToGraph")
	public String getGraph(@RequestBody MyRecord record) throws Exception {
//		ObjectMapper objectMapper = new ObjectMapper();
//		MyRecord record = objectMapper.readValue(json, MyRecord.class);
		List<String> stringlIST = new ArrayList<String>();
		stringlIST.add("eya");
		stringlIST.add("hello");
		List<Node> nodeList = new ArrayList<Node>();
		stringlIST.forEach(i -> nodeList.add(node(i)));

		Graph graph = graph("example").directed().with(node(record.getProperty1()).link(node("hiiiii")));
//	      .with(edge(record.getProperty1()).to(record.getProperty2()))
//	      .engine(Engine.DOT)
//	      .render(Format.SVG)
//	      .toGraph();
		Graphviz.fromGraph(graph).render(Format.DOT)
				.toFile(new File(((IFileDbService) graphvizService).getFileStorageLocation() + "record"));
//	    byte[] imageBytes = 
//	    HttpHeaders headers = new HttpHeaders();
//	    headers.setContentType(MediaType.IMAGE_PNG);
//	    headers.setContentLength(imageBytes.length);
		return "record dot file created ";
//	    return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
	}

	public String myTest() throws Exception {
//		ObjectMapper objectMapper = new ObjectMapper();
//		MyRecord record = objectMapper.readValue(json, MyRecord.class);
		List<String> stringlIST = new ArrayList<String>();
		stringlIST.add("eya");
		stringlIST.add("hello");
		List<Node> nodeList = new ArrayList<Node>();
		stringlIST.forEach(i -> nodeList.add(node(i)));

		Graph graph = graph("example").directed().with(node("test").link(node("hiiiii")));
//	      .with(edge(record.getProperty1()).to(record.getProperty2()))
//	      .engine(Engine.DOT)
//	      .render(Format.SVG)
//	      .toGraph();
		Graphviz.fromGraph(graph).render(Format.DOT)
				.toFile(new File(((IFileDbService) graphvizService).getFileStorageLocation() + "record"));
//	    byte[] imageBytes = 
//	    HttpHeaders headers = new HttpHeaders();
//	    headers.setContentType(MediaType.IMAGE_PNG);
//	    headers.setContentLength(imageBytes.length);
		return "record dot file created ";
//	    return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
	}
}
