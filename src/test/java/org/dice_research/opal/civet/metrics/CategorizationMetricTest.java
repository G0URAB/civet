package org.dice_research.opal.civet.metrics;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.DCAT;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.SKOS;
import org.dice_research.opal.test_cases.OpalTestCases;
import org.dice_research.opal.test_cases.TestCase;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests {@link CategorizationMetric}.
 * 
 * @author Adrian Wilke
 */
public class CategorizationMetricTest {

	/**
	 * Tests cases.
	 */
	@Test
	public void testCases() throws Exception {
		CategorizationMetric metric = new CategorizationMetric();

		String datasetUri = "https://example.org/dataset";
		Resource dataset = ResourceFactory.createResource(datasetUri);

		Literal keywordA = ResourceFactory.createPlainLiteral("keywordA");
		Literal keywordB = ResourceFactory.createPlainLiteral("keywordB");

		Resource theme = ResourceFactory.createResource("https://example.org/topic");
		Statement themeAndType = ResourceFactory.createStatement(theme, RDF.type, SKOS.Concept);

		Model model = ModelFactory.createDefaultModel();
		model.add(dataset, RDF.type, DCAT.Dataset);

		// only dataset

		Assert.assertEquals("No categorization", 0, metric.compute(model, datasetUri).intValue());

		// 1 keyword of correct type

		model.addLiteral(dataset, DCAT.keyword, keywordA);
		Assert.assertEquals("1 keyword", 2, metric.compute(model, datasetUri).intValue());

		// 2 keywords of correct type

		model.addLiteral(dataset, DCAT.keyword, keywordB);
		Assert.assertEquals("2 keywords", 3, metric.compute(model, datasetUri).intValue());

		// 1 keyword of correct type, 1 not

		model.remove(dataset, DCAT.keyword, keywordB);
		model.add(dataset, DCAT.keyword, theme);
		Assert.assertEquals("1 keyword, 1 keyword of incorrect type", 2, metric.compute(model, datasetUri).intValue());

		// 1 topic of correct type

		model.remove(dataset, DCAT.keyword, keywordA);
		model.remove(dataset, DCAT.keyword, theme);
		model.add(dataset, DCAT.theme, theme);
		model.add(themeAndType);
		Assert.assertEquals("1 theme", 2, metric.compute(model, datasetUri).intValue());

		// 1 topic not of correct type

		model.remove(dataset, DCAT.theme, theme);
		model.remove(themeAndType);
		model.add(dataset, DCAT.theme, keywordA);
		Assert.assertEquals("1 theme of incorrect type", 1, metric.compute(model, datasetUri).intValue());

		// 5 stars

		model.remove(dataset, DCAT.theme, keywordA);
		model.addLiteral(dataset, DCAT.keyword, keywordA);
		model.addLiteral(dataset, DCAT.keyword, keywordB);
		model.add(dataset, DCAT.theme, theme);
		model.add(themeAndType);
		Assert.assertEquals("2 correct keywords, 1 correct theme", 5, metric.compute(model, datasetUri).intValue());
	}

	/**
	 * Tests example dataset.
	 */
	@Test
	public void testDatasets() throws Exception {

		TestCase testCase = OpalTestCases.getTestCase("opal-2019-06-24", "edp-corine-iceland");

		// Compute stars
		CategorizationMetric metric = new CategorizationMetric();
		Integer stars = metric.compute(testCase.getModel(), testCase.getDatasetUri());

		// Keywords given: 2 stars
		// Keywords are literals: +1 star
		// Theme available: +1 star
		Assert.assertEquals(testCase.getDatasetUri(), 4, stars.intValue());
	}

}