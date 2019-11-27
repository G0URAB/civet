package org.dice_research.opal.civet.metrics;

import java.net.URL;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.SimpleSelector;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.DCAT;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.RDF;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dice_research.opal.civet.Metric;
import org.dice_research.opal.common.vocabulary.Opal;

/**
 * The CategorizationMetric awards stars based on the number of keywords of a
 * dataset.
 * 
 * @author Adrian Wilke
 */
public class KnownLicenseMetric implements Metric {

	public static boolean isValidLicenseURL(String LicenseURL) {
		/*
		 * new URL tries to create a new URL with provided license URL. If invalid
		 * License URL then catch exception and return false.
		 */
		try {
			new URL(LicenseURL).toURI();
			return true;
		}

		catch (Exception e) {
			return false;
		}
	}

	private static final Logger LOGGER = LogManager.getLogger();
	private static final String DESCRIPTION = "Extract License/rights information from dataset or it's distributions "
			+ "If dataset has rights/license info then award 5 star "
			+ "Else If dataset has no rights/license but more than 100% of dataset's distribution has then give 5 star"
			+ "Else If dataset has no rights/license but less than 100% and more than 60% dataset's distribution has then 4 stars are awarded"
			+ "Else If dataset has no rights/license but less than 60% and more than 40% dataset's distribution has then 3 stars are awarded"
			+ "Else If dataset has no rights/license but less than 40% and more than 20% dataset's distribution has then 2 stars are awarded"
			+ "Else If dataset has no rights/license but less than 20% and more than  0% dataset's distribution has then 1 stars are awarded"
			+ "Else if no License at all then 0 star is awarded.";

	@Override
	public Integer compute(Model model, String datasetUri) throws Exception {

		LOGGER.info("Processing dataset " + datasetUri);

		Resource dataset = ResourceFactory.createResource(datasetUri);

		// NodeIterator nodeIterator = model.listObjectsOfProperty(dataset,
		// DCAT.keyword);

		// For Score calculation
		int TotalDistributions = 0;
		int TotalDistributionsWithLicense = 0;
		int TotalDistributionsWithRights = 0;

		// score to return
		int score = 0;

		/*
		 * For dcat:dataset, we will first check if license is given or not. If not
		 * check anything with 'right'.
		 */
		int NumberOfLicensesInDataset = 0;
		StmtIterator DatasetIterator = model.listStatements(new SimpleSelector(null, RDF.type, DCAT.Dataset));
		if (DatasetIterator.hasNext()) {
			Statement DataSetSentence = DatasetIterator.nextStatement();
			Resource DataSet = DataSetSentence.getSubject();
			// System.out.println(DataSet.toString());
			if (DataSet.hasProperty(DCTerms.license)
					&& !(DataSet.getProperty(DCTerms.license).getObject().toString().isEmpty())) {
				if (isValidLicenseURL(DataSet.getProperty(DCTerms.license).getObject().toString()))
					NumberOfLicensesInDataset++;
			} else if (DataSet.hasProperty(DCTerms.rights)
					&& !(DataSet.getProperty(DCTerms.rights).getObject().toString().isEmpty())) {
				if (isValidLicenseURL(DataSet.getProperty(DCTerms.rights).getObject().toString()))
					NumberOfLicensesInDataset++;
			}
		}

		if (NumberOfLicensesInDataset == 0) {
			// This means there is no license/rights info in dct:dataset, we need to check
			// each dct:distributions

			NodeIterator DistributionsIteratorLicense = model.listObjectsOfProperty(DCAT.distribution);

			if (DistributionsIteratorLicense.hasNext()) {

				while (DistributionsIteratorLicense.hasNext()) {

					TotalDistributions++;
					Resource Distribution = (Resource) DistributionsIteratorLicense.nextNode();

					if (Distribution.hasProperty(DCTerms.license)) {
						// Even if license info is given, check if a valid URI

						if (isValidLicenseURL(Distribution.getProperty(DCTerms.license).getObject().toString()))
							TotalDistributionsWithLicense++;
					}
				}
			}

			if (TotalDistributionsWithLicense == 0 || TotalDistributionsWithLicense != TotalDistributions)

			{

				/*
				 * If control came here means, there is no dct:license keyword in available
				 * distributions or there maybe both dct:license and dct:rights keyword. Check for rights here.
				 */
				NodeIterator DistributionsIteratorRights = model.listObjectsOfProperty(DCAT.distribution);

				while (DistributionsIteratorRights.hasNext()) {

					Resource Distribution = (Resource) DistributionsIteratorRights.nextNode();

					if (Distribution.hasProperty(DCTerms.rights)) {
						// Even if license info is given, check if a valid URI
						if (isValidLicenseURL(Distribution.getProperty(DCTerms.rights).getObject().toString()))
							TotalDistributionsWithRights++;
					}
				}
			}
		}

		if (NumberOfLicensesInDataset > 0)
			score = 5;
		else {
			if (TotalDistributionsWithLicense == 0 && TotalDistributionsWithRights == 0)
				score = 0;
			else if (TotalDistributionsWithLicense > 0 || TotalDistributionsWithRights > 0) {
				int TotalKnownLicenses = TotalDistributionsWithLicense + TotalDistributionsWithRights;

				int EvaluationInPercentage = ((TotalKnownLicenses * 100) / TotalDistributions);

				if (EvaluationInPercentage == 100)
					score = 5;
				else if (EvaluationInPercentage < 100 && EvaluationInPercentage >= 75)
					score = 4;
				else if (EvaluationInPercentage < 75 && EvaluationInPercentage >= 50)
					score = 3;
				else if (EvaluationInPercentage < 50 && EvaluationInPercentage >= 25)
					score = 2;
				else if (EvaluationInPercentage < 25 && EvaluationInPercentage > 0)
					score = 1;
				else if (EvaluationInPercentage==0)
					score = 0;
			}
		}

		return score;
	}

	@Override
	public String getDescription() {
		return DESCRIPTION;
	}

	@Override
	public String getUri() throws Exception {
		return Opal.OPAL_METRIC_CATEGORIZATION.getURI();
	}

}