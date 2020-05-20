package org.dice_research.opal.civet.metrics;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.SimpleSelector;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.sparql.vocabulary.FOAF;
import org.apache.jena.vocabulary.DCAT;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.RDF;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dice_research.opal.civet.Metric;
import org.dice_research.opal.common.vocabulary.Opal;

/**
 * The ProviderIdentity gives a star rating to a dataset based on any available
 * publisher of the dataset which is provided through the predicate
 * dct:publisher. If dct:publisher predicate is not present or empty then check
 * for a landing page in the dataset which is given by a predicate
 * dcat:landingPage. If still no publisher is found then check for access URL in
 * each distributions which is given by a predicate dcat:accessURL. Award a 0
 * star, in case of no availability of a pub- lisher.
 * 
 * @author Gourab Sahu
 */
public class ProviderIdentityMetric implements Metric {

	static int publisher_score = 0;

	public static boolean isValidURL(String checkURL) {
		/*
		 * Here we check whether the URL of foaf:homepage is a valid URL or not.
		 */
		try {
			new URL(checkURL).toURI();
			return true;
		}

		catch (Exception e) {
			return false;
		}
	}

	public static void evaluatePublisher(Resource publisher) {

		boolean publisher_is_a_foaf_resource = publisher.hasProperty(RDF.type, FOAF.Agent) ? true
				: publisher.hasProperty(RDF.type, FOAF.Person) ? true
						: publisher.hasProperty(RDF.type, FOAF.Organization) ? true
								: publisher.hasProperty(RDF.type, FOAF.Group) ? true : false;
		String foaf_name = publisher.hasProperty(FOAF.name) ? publisher.getProperty(FOAF.name).getObject().toString()
				: "";

		/*
		 * If a publisher is of type foaf:org or foaf:person and has an non-empty name
		 * then 5 stars are awarded.
		 */
		if (publisher_is_a_foaf_resource && !foaf_name.isEmpty())
			publisher_score = 5;
		/*
		 * If resource is not a FOAF:organisation or FOAF:Person or FOAF:Agent but has a
		 * publisher then 4 stars are awarded for not following DCAT recommendations.
		 */
		else if (!publisher_is_a_foaf_resource && !foaf_name.isEmpty())
			publisher_score = 4;

		/*
		 * Last case check if dct:publisher has been given in the form of a URL. 4 stars
		 * for not following DCAT recommendations.
		 */
		else if (isValidURL(publisher.toString()))
			publisher_score = 4;

	}

	private static final Logger LOGGER = LogManager.getLogger();
	private static final String DESCRIPTION = "If dataset has a Publisher which is of type FOAF.person or FOAF.organization "
			+ "or FOAF.Agent then 5 atars are awarded "

			+ "If dataset has a Publisher which is not of type FOAF.person or FOAF.organization "
			+ "or FOAF.Agent then 4 atars are awarded "

			+ "If dct:publisher predicate does not provide a publisher then in that case if "
			+ "dcat:Landingpage predicate provides a valid URL then award 5 stars. Landing page " + "has a FOAF range."

			+ "As a last resort check if all distributions have a access URL and based on the "
			+ "percentage of availability of accessURL award a star rating ";

	@Override
	public Integer compute(Model model, String datasetUri) throws Exception {

		LOGGER.info("Processing dataset " + datasetUri);

		Resource dataset = ResourceFactory.createResource(datasetUri);

		NodeIterator publishers = model.listObjectsOfProperty(dataset, DCTerms.publisher);

		while (publishers.hasNext()) {

			RDFNode publisher = publishers.next();

			if (publisher.isAnon() || publisher.isURIResource()) {
				evaluatePublisher((Resource) publisher);
				if (publisher_score == 5)
					break;
			}
		}

		/*
		 * If publisher_score=0 then check if dcat:landingPage is available in
		 * dcat:catalog
		 */
		if (publisher_score == 0) {

			// It could be possible that more than 1 landing pages exist in a dataset.
			NodeIterator landingpages = model.listObjectsOfProperty(dataset, DCAT.landingPage);

			while (landingpages.hasNext()) {

				Object landing_page = landingpages.next();

				if (isValidURL(landing_page.toString())) {
					publisher_score = 5;
					break;
				}

			}
		}

		// If LandingPage is not there then check for AccessURL in the distribution
		if (publisher_score == 0) {

			int number_of_access_url = 0;
			int number_of_distributions = 0;

			NodeIterator distributions = model.listObjectsOfProperty(dataset, DCAT.distribution);

			while (distributions.hasNext()) {

				number_of_distributions++;
				RDFNode distribution = distributions.next();
				if (distribution.isResource()) {
					Resource distribution_resource = (Resource) distribution;
					if (distribution_resource.hasProperty(DCAT.accessURL)) {
						if (isValidURL(distribution_resource.getProperty(DCAT.accessURL).getObject().toString()))
							number_of_access_url++;
					}
				}

			}

			int TotalPercentageOfAccessURL = (number_of_access_url * 100) / number_of_distributions;

			if (TotalPercentageOfAccessURL == 100)
				publisher_score = 5;
			else if (TotalPercentageOfAccessURL < 100 && TotalPercentageOfAccessURL >= 75)
				publisher_score = 4;
			else if (TotalPercentageOfAccessURL < 75 && TotalPercentageOfAccessURL >= 50)
				publisher_score = 3;
			else if (TotalPercentageOfAccessURL < 50 && TotalPercentageOfAccessURL >= 25)
				publisher_score = 2;
			else if (TotalPercentageOfAccessURL < 25 && TotalPercentageOfAccessURL > 0)
				publisher_score = 1;

		}
		System.out.println("score:" + publisher_score);
		return publisher_score;

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