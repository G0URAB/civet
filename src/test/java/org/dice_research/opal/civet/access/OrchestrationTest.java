package org.dice_research.opal.civet.access;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.dice_research.opal.civet.Config;
import org.dice_research.opal.civet.Orchestration;
import org.dice_research.opal.civet.metrics.DescriptionMetric;
import org.dice_research.opal.civet.metrics.LicenseSpecifiedMetric;
import org.junit.Test;

public class OrchestrationTest {

	/**
	 * Tests calculation of description metric.
	 */
	@Test
	public void test() throws URISyntaxException {

		// Configure endpoint
		Orchestration orchestration = new Orchestration();
		orchestration.getConfiguration().setSparqlQueryEndpoint(Config.sparqlQueryEndpoint);
		orchestration.getConfiguration().setNamedGraph(Config.namedGraph);
		pingEndpoint();

		// Set metrics
		Set<String> metrics = new HashSet<String>();
		String metricDescription = new DescriptionMetric().toString();
		metrics.add(metricDescription);
		// License metric needs distribution
		String licenseSpecifiedMetric = new LicenseSpecifiedMetric().toString();
		metrics.add(licenseSpecifiedMetric);

		// Compute scores
		Map<String, Float> scores = orchestration.compute(new URI(Config.datasetUriBerlin), metrics);

		assertEquals(2, scores.size());
		assertTrue(scores.get(metricDescription) > 0);
		assertTrue(scores.get(licenseSpecifiedMetric) > 0);
	}

	/**
	 * Tests calculation using multiple datasets.
	 */
	@Test
	public void testMultipleDatasets() {

		// Configure endpoint
		Orchestration orchestration = new Orchestration();
		orchestration.getConfiguration().setSparqlQueryEndpoint(Config.sparqlQueryEndpoint);
		orchestration.getConfiguration().setNamedGraph(Config.namedGraph);
		pingEndpoint();

		// Set metrics
		Set<String> metrics = new HashSet<String>();
		String metricDescription = new DescriptionMetric().toString();
		metrics.add(metricDescription);
		// License metric needs distribution
		String licenseSpecifiedMetric = new LicenseSpecifiedMetric().toString();
		metrics.add(licenseSpecifiedMetric);

		// Process
		int minDatasetsToProcess = 100;
		int processedDatasets = orchestration.compute(0, 40, minDatasetsToProcess, metrics);
		assumeTrue(minDatasetsToProcess < processedDatasets);
	}

	/**
	 * Checks, if host of endpoint answers.
	 */
	private void pingEndpoint() {
		boolean reachable = IoUtils.pingHost(Config.sparqlQueryEndpointHost, Config.sparqlQueryEndpointPort, 100);
		if (!reachable) {
			System.out.println(OrchestrationTest.class.getSimpleName() + ": could not access endpoint "
					+ Config.sparqlQueryEndpoint);
		}
		assumeTrue(reachable);
	}
}