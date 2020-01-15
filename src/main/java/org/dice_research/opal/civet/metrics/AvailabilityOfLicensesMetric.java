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
 * The AvailabilityOfLicensesMetric provides a rating to a dataset
 * based on the number of available licenses/rights in a dataset.
 * 
 * This metric first checks if the provided dataset has any rights
 * or license information available in the form of predicates 
 * dct:rights or dct:license respectively.
 * 
 * If license/rights information not available at dataset then check 
 * all the available distributions in a dataset for license information
 * and based on that a star rating is provided.
 * 
 * @see https://www.w3.org/TR/vocab-dcat-1/#Property:catalog_license
 * @see https://www.w3.org/TR/vocab-dcat-1/#Property:distribution_rights
 * 
 * @author Gourab Sahu
 */
public class AvailabilityOfLicensesMetric implements Metric {

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
	private static final String DESCRIPTION = 
			 "If dataset has rights/license info then award 5 stars "
			+ "Else If dataset has no rights/license but all distributions in dataset have licenses/rights then give 5 star"
			+ "Else If dataset has no rights/license but less than 100% and more than or equal to 75% dataset's distribution has then 4 stars are awarded"
			+ "Else If dataset has no rights/license but less than 75% and more than or equal to 50% dataset's distribution has then 3 stars are awarded"
			+ "Else If dataset has no rights/license but less than 50% and more than or equal to 25% dataset's distribution has then 2 stars are awarded"
			+ "Else If dataset has no rights/license but less than 25% and more than  0% dataset's distribution has then 1 star is awarded"
			+ "Else if no License at all then 0 star is awarded.";

	@Override
	public Integer compute(Model model, String datasetUri) throws Exception {

		LOGGER.info("Processing dataset " + datasetUri);

		Resource dataset = ResourceFactory.createResource(datasetUri);

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
		NodeIterator LicensesIterator = model.listObjectsOfProperty(dataset,DCTerms.license);
		NodeIterator RightsIterator = model.listObjectsOfProperty(dataset,DCTerms.rights);
		if (LicensesIterator.hasNext()) {
           RDFNode licensObject = LicensesIterator.nextNode();
		   if(!(licensObject.toString().isEmpty()) && isValidLicenseURL(licensObject.toString()))
			   NumberOfLicensesInDataset++;   
		}
		else if (RightsIterator.hasNext()) {
	           RDFNode RightsObject = RightsIterator.nextNode();
			   if(!(RightsObject.toString().isEmpty()) && isValidLicenseURL(RightsObject.toString()))
				   NumberOfLicensesInDataset++;   
			}
		
		
		if (NumberOfLicensesInDataset == 0) {
			// This means there is no license/rights info in dct:dataset, we need to check
			// each dct:distributions

			NodeIterator DistributionsIteratorLicense = model.listObjectsOfProperty(dataset,DCAT.distribution);

				while (DistributionsIteratorLicense.hasNext()) {

					TotalDistributions++;
					Resource Distribution = (Resource) DistributionsIteratorLicense.nextNode();

					if (Distribution.hasProperty(DCTerms.license)) {
						// Even if license info is given, check if a valid URI

						if (isValidLicenseURL(Distribution.getProperty(DCTerms.license).getObject().toString()))
							TotalDistributionsWithLicense++;
					}
				}

			if (TotalDistributionsWithLicense == 0 || TotalDistributionsWithLicense != TotalDistributions)

			{

				/*
				 * If control came here means, there is no dct:license keyword in available
				 * distributions or there maybe both dct:license and dct:rights keyword. Check for rights here.
				 */
				NodeIterator DistributionsIteratorRights = model.listObjectsOfProperty(dataset,DCAT.distribution);

				while (DistributionsIteratorRights.hasNext()) {

					Resource Distribution = (Resource) DistributionsIteratorRights.nextNode();

					if (Distribution.hasProperty(DCTerms.rights)) {
						// Rights indidicate an organisation which has the rights
						//to the distribution has range dct:RightsStatement
						if (!(Distribution.getProperty(DCTerms.rights).getObject().toString().isEmpty()) && 
								!Distribution.getProperty(DCTerms.rights).getObject().isAnon()) {
							TotalDistributionsWithRights++;
							System.out.println("TEST");
						}
					}
				}
			}
		}

		if (NumberOfLicensesInDataset > 0)
			score = 5;
		else {
			 /**
			  * if TotalDistributionsWithLicense == 0 and TotalDistributionsWithRights == 0
			  * then score = 0
			  */

			    if (TotalDistributionsWithLicense > 0 || TotalDistributionsWithRights > 0) {
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