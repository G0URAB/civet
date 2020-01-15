package org.dice_research.opal.civet.metrics;

import java.util.Arrays;
import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.DCTerms;
import org.dice_research.opal.civet.Metric;
import org.dice_research.opal.common.vocabulary.Opal;

/**
 * The UpdateRateMetric awards stars based on how often the dataset is updated.
 * 
 * Values are based on the Dublin Core Collection Description Frequency
 * Vocabulary as well on values defined by europa.eu.
 * 
 * Values of europa.eu: ANNUAL ANNUAL_2 ANNUAL_3 BIENNIAL BIMONTHLY BIWEEKLY
 * CONT DAILY DAILY_2 IRREG MONTHLY MONTHLY_2 MONTHLY_3 NEVER OP_DATPRO
 * QUARTERLY TRIENNIAL UNKNOWN UPDATE_CONT WEEKLY_2 WEEKLY_3 QUINQUENNIAL
 * DECENNIAL HOURLY QUADRENNIAL BIHOURLY TRIHOURLY BIDECENNIAL TRIDECENNIAL
 * 
 * Values of dublincore.org: freq:triennial freq:biennial freq:annual
 * freq:semiannual freq:threeTimesAYear freq:quarterly freq:bimonthly
 * freq:monthly freq:semimonthly freq:biweekly freq:threeTimesAMonth freq:weekly
 * freq:semiweekly freq:threeTimesAWeek freq:daily freq:continuous
 * freq:irregular
 * 
 * @see https://www.w3.org/TR/vocab-dcat/#temporal-properties
 * @see https://www.dublincore.org/specifications/dublin-core/collection-description/frequency/
 * @see http://publications.europa.eu/resource/authority/frequency
 * 
 * @author Vikrant Singh, Adrian Wilke
 */

public class UpdateRateMetric implements Metric {

	private static final String DESCRIPTION = "Computes the update rate/frequency. "
			+ "If data is updated at least weekly, 5 stars are awarded. "
			+ "If data is updated at least monthly, 4 stars are awarded."
			+ "If data is updated at least four times a year, 3 stars are awarded. "
			+ "If data is updated at least once a year, 2 stars are awarded. "
			+ "If data is updated sometimes, 1 star is awarded.";

	protected static List<String> listUnknown = Arrays.asList(new String[] { "UNKNOWN" });

	protected static List<String> listNo = Arrays.asList(new String[] { "OP_DATPRO", "NEVER" });

	protected static List<String> listSometimes = Arrays.asList(new String[] { "IRREG", "BIENNIAL", "TRIENNIAL",
			"DECENNIAL", "TRIDECENNIAL", "BIDECENNIAL", "QUINQUENNIAL" });

	protected static List<String> listYearly = Arrays.asList(new String[] { "ANNUAL", "THREETIMESAYEAR" });

	protected static List<String> listQuaterly = Arrays
			.asList(new String[] { "QUARTERLY", "BIMONTHLY", "QUADRENNIAL" });

	protected static List<String> listMonthly = Arrays
			.asList(new String[] { "MONTHLY", "THREETIMESAMONTH", "BIWEEKLY" });

	protected static List<String> listWeekly = Arrays.asList(
			new String[] { "HOURLY", "WEEKLY", "CONT", "DAILY", "UPDATE_CONT", "THREETIMESAWEEK", "CONTINUOUS" });

	@Override
	public Integer compute(Model model, String datasetUri) throws Exception {
		Resource dataset = ResourceFactory.createResource(datasetUri);
		Statement statement = model.getProperty(dataset, DCTerms.accrualPeriodicity);
		String updateRate;
		if (statement == null) {
			return null;
		} else {
			updateRate = String.valueOf(statement.getObject()).toUpperCase();
		}

		if (stringContainsListEntry(updateRate, listUnknown)) {
			return null;
		}

		else if (stringContainsListEntry(updateRate, listNo)) {
			return 0;
		}

		else if (stringContainsListEntry(updateRate, listSometimes)) {
			return 1;
		}

		else if (stringContainsListEntry(updateRate, listYearly)) {
			return 2;
		}

		else if (stringContainsListEntry(updateRate, listQuaterly)) {
			return 3;
		}

		else if (stringContainsListEntry(updateRate, listMonthly)) {
			return 4;
		}

		else if (stringContainsListEntry(updateRate, listWeekly)) {
			return 5;
		}

		else {
			return null;
		}
	}

	protected boolean stringContainsListEntry(String string, List<String> list) {
		for (String listString : list) {
			if (string.contains(listString)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String getDescription() {
		return DESCRIPTION;
	}

	@Override
	public String getUri() throws Exception {
		return Opal.OPAL_METRIC_UPDATE_RATE.getURI();
	}
}