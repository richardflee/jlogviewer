package com.github.richardflee.voyager.viewer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Paths;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.github.richardflee.voyager.fileio.VoyagerPaths;
import com.github.richardflee.voyager.log_objects.VoyagerLogExtractor;
import com.github.richardflee.voyager.log_objects.VoyagerLogMatchers;

class ActionHandlerTest {
	
	private static final String resourcesFolder = "C:\\Users\\rlee1\\eclipse-workspace\\voyager\\jlogviewer\\src\\test\\resources\\log";
	private static final String extractsFolder = "C:\\Users\\rlee1\\eclipse-workspace\\voyager\\jlogviewer\\log\\extracts";
	
	private static final String Log2021_12_11 = Paths.get(resourcesFolder, "2021_12_11_Voyager.log").toString();
	private static final String Log2021_12_12 = Paths.get(resourcesFolder, "2021_12_12_Voyager.log").toString();
	
	private static final String Extract2021_12_11 = Paths.get(extractsFolder, "2021_12_11_Voyager.extracts.log").toString();
	// private static final String Comment2021_12_11 = Paths.get(extractsFolder, "2021_12_11_Voyager.comments.log").toString();
	
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
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@DisplayName("Verifies log records count start date 2021_12_11")
	@Test
	void testLog_Counts_2021_12_11() {
		
		vp.updateLogPaths(Log2021_12_11);
		
		logMatchers.getMatchers().stream().forEach(p -> p.setSelected(true));
		var extracts = extractor.getTableExtractsFromFiles(vp);
		assertEquals(5, extracts.size());
	
		logMatchers.getMatchers().stream().forEach(p -> p.setSelected(false));
		extracts = extractor.getTableExtractsFromFiles(vp);
		assertEquals(0, extracts.size());
	}

	@DisplayName("Verifies log records count end date 2021_12_12")
	@Test
	void tesLogdCounts_2021_12_12() {
		vp.updateLogPaths(Log2021_12_12);
		
		logMatchers.getMatchers().stream().forEach(p -> p.setSelected(true));
		var extracts = extractor.getTableExtractsFromFiles(vp);
		assertEquals(3, extracts.size());
		
		logMatchers.getMatchers().stream().forEach(p -> p.setSelected(false));
		extracts = extractor.getTableExtractsFromFiles(vp);
		assertEquals(0, extracts.size());
	}
	
	@DisplayName("Verifies extracts count start date 2021_12_11")
	@Test
	void testExtractCounts_2021_12_11() {
		vp.updateLogPaths(Log2021_12_11);
		
		logMatchers.getMatchers().stream().forEach(p -> p.setSelected(true));
		var extracts = extractor.getTableExtractsFromFiles(vp);
		extractor.saveLogExtractsToFiles(vp);
		
		extracts.clear();
		vp.updateExtractsPaths(Extract2021_12_11);
		assertEquals(8, extractor.getTableExtractsFromFiles(vp));
		//assertEquals(5, extractor.get(vp));
		
		extracts = extractor.getTableExtractsFromFiles(vp);
		
		System.out.println(extracts.size());
		
		
	}

}
