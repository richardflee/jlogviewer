package com.github.richardflee.voyager.extractor;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import com.github.richardflee.voyager.fileio.VoyagerPaths;
import com.github.richardflee.voyager.log_objects.VoyagerLogExtractor;
import com.github.richardflee.voyager.log_objects.VoyagerLogMatchers;

class VoyagerLogExtractorTest {

	private static final String resourcesFolder = "C:\\Users\\rlee1\\eclipse-workspace\\voyager\\jlogviewer\\src\\test\\resources\\log";
	private static final String extractsFolder = "C:\\Users\\rlee1\\eclipse-workspace\\voyager\\jlogviewer\\log\\extracts";

	private static final String Log2021_12_11 = Paths.get(resourcesFolder, "2021_12_11_Voyager.log").toString();
	private static final String Log2021_12_12 = Paths.get(resourcesFolder, "2021_12_12_Voyager.log").toString();

	private static final String Extract2021_12_11 = Paths.get(extractsFolder, "2021_12_11_Voyager.extracts.log")
			.toString();
	private static final String Comment2021_12_11 = Paths.get(extractsFolder, "2021_12_11_Voyager.comments.log")
			.toString();

	private static final Path resourcesExtractsPath = Paths.get(resourcesFolder, "extracts",
			"2021_12_11_Voyager.extracts.log");
	private static final Path resourcesCommentsPath = Paths.get(resourcesFolder, "extracts",
			"2021_12_11_Voyager.comments.log");

	private static VoyagerPaths vp = new VoyagerPaths();
	private static VoyagerLogMatchers logMatchers = new VoyagerLogMatchers();
	private static VoyagerLogExtractor extractor = new VoyagerLogExtractor(logMatchers);

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
		Files.copy(resourcesExtractsPath, Paths.get(Extract2021_12_11), StandardCopyOption.REPLACE_EXISTING);
		Files.copy(resourcesCommentsPath, Paths.get(Comment2021_12_11), StandardCopyOption.REPLACE_EXISTING);
	}

	@AfterEach
	void tearDown() throws Exception {
	}

//	@Test
//	void temp() {
//		try {
//			Files.copy(resourcesExtractsPath, Paths.get(Extract2021_12_11), StandardCopyOption.REPLACE_EXISTING);
//			Files.copy(resourcesCommentsPath, Paths.get(Comment2021_12_11), StandardCopyOption.REPLACE_EXISTING);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//	}

	@Order(1)
	@DisplayName("Verifies doImportFromVoyagerLogFile count start date 2021_12_11")
	@Test
	void testLogCounts_2021_12_11() {		
		vp.updateLogPaths(Log2021_12_11);		
		logMatchers.getMatchers().stream().forEach(p -> p.setSelected(true));
		var extracts = extractor.getTableExtractsFromFiles(vp);
		assertEquals(5, extracts.size());
	
		logMatchers.getMatchers().stream().forEach(p -> p.setSelected(false));
		extracts = extractor.getTableExtractsFromFiles(vp);
		assertEquals(0, extracts.size());
	}

	@Order(2)
	@DisplayName("Verifies doImportFromVoyagerLogFile count end date 2021_12_12")
	@Test
	void tesLogCounts_2021_12_12() {
		vp.updateLogPaths(Log2021_12_12);		
		logMatchers.getMatchers().stream().forEach(p -> p.setSelected(true));
		var extracts = extractor.getTableExtractsFromFiles(vp);
		assertEquals(3, extracts.size());
		
		logMatchers.getMatchers().stream().forEach(p -> p.setSelected(false));
		extracts = extractor.getTableExtractsFromFiles(vp);
		assertEquals(0, extracts.size());
	}
	
	//@Disabled("temp")
	@Order(3)
	@DisplayName("Verifies doSaveLogExtractsToFile + doImportFromExtractsFiles")
	@Test
	void testDeselected_Counts_2021_12_11() {
		//deselected
		vp.updateLogPaths(Log2021_12_11);
		
		logMatchers.getMatchers().stream().forEach(p -> p.setSelected(false));
		extractor.getTableExtractsFromFiles(vp);

		extractor.saveLogExtractsToFiles(vp);
		
		vp.updateExtractsPaths(Extract2021_12_11);
		assertEquals(3, extractor.getTableExtractsFromFiles(vp).size());
		assertEquals(0, extractor.getSelectedExtracts().size());
		assertEquals(3, extractor.getCommentExtracts().size());
	}
	
	@Order(4)
	@DisplayName("Verifies doSaveLogExtractsToFile + doImportFromExtractsFiles")
	@Test
	void testSelected_Counts_2021_12_11() {
		// selected
		vp.updateLogPaths(Log2021_12_11);
		logMatchers.getMatchers().stream().forEach(p -> p.setSelected(true));
		extractor.getTableExtractsFromFiles(vp);

		extractor.saveLogExtractsToFiles(vp);
		
		vp.updateExtractsPaths(Extract2021_12_11);
		assertEquals(8, extractor.getTableExtractsFromFiles(vp).size());
		assertEquals(5, extractor.getSelectedExtracts().size());
		assertEquals(3, extractor.getCommentExtracts().size());	
	}
	
	

}
