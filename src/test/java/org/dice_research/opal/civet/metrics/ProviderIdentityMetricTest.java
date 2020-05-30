package org.dice_research.opal.civet.metrics;

import org.dice_research.opal.civet.TestData;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests {@link AvailabilityOfLicensesMetric}.
 * 
 * @author Gourab Sahu
 */
public class ProviderIdentityMetricTest {

	TestData testdata;

	/*
	 *  Dataset has no dct:publisher and no accessURL at distributions but 
	 *  the dataset has a landingPage which as per Data Catalog can be 
	 *  treated as publisher information ---> 5 stars
	 */
	private static final String TestCase_5stars = "TestCaseForPI_5Stars.ttl";
	private static final String TEST_dataset_5stars = "http://projekt-opal.de/dataset/https___ckan_govdata_de_0af72fb1_b058_4de4_b99e_369a5a463f24";
	
	
	/*
	 * 4 stars: dct:Publisher is not of FOAF:Person or Organization or Agent
	 */
	private static final String TestCase_4stars = "TestCaseForPI_4Stars__synthetic.ttl";
	private static final String TEST_dataset_4stars = "http://projekt-opal.de/dataset/_mcloudde_vieljhrlicherasterdesmittlerenvegetationsbeginnsindeutschland4";
	
	
	/*
	 * The dataset has no dcat:landingPage, no dct:publisher and out of 
	 * 2 distributions only 1 has dcat:accessURL. As per Data Catalog, 
	 * accessURL can be used as landingPage is distributions are accessed 
	 * only through accessURL.
	 */
	private static final String TestCase_3stars = "TestCaseForPI_3Stars__synthetic.ttl";
	private static final String TEST_dataset_3stars = "http://projekt-opal.de/dataset/https___ckan_govdata_de_34a3e19c_323f_474a_b21b_b03be924093a3";
	
	
	/*
	 * Dataset has no publisher info. 2 out of 5 distributions have a valid access_url.
	 */
	private static final String TestCase_2stars = "TestCaseForPI_2Stars__synthetic.ttl";
	private static final String TEST_dataset_2stars = "http://projekt-opal.de/dataset/https___europeandataportal_eu_set_data_464819ee_0d2a_4ca4_b5a4_026e34488404";
	
	
	
	
	/*
	 * Dataset has no publisher info. 1 out of 5 distributions have a valid access_url.
	 */
	private static final String TestCase_1stars = "TestCaseForPI_1Stars__synthetic.ttl";
	private static final String TEST_dataset_1stars = "http://projekt-opal.de/dataset/https___europeandataportal_eu_set_data_2013_03_visas_delivres_aux_etudiants1";
	
	
	/*
	 * No publisher information at all
	 */
	private static final String TestCase_0stars = "TestCaseForPI_0Stars__synthetic.ttl";
	private static final String TEST_dataset_0stars = "http://projekt-opal.de/dataset/_mcloudde_vieljhrlicherasterdesmittlerenvegetationsbeginnsindeutschland";

	@Before
	public void setUp() throws Exception {
		testdata = new TestData();
	}
	
	@Test
	public void testCase0() throws Exception {

		ProviderIdentityMetric metric = new ProviderIdentityMetric();
		Integer stars = metric.compute(testdata.getModel(TestCase_0stars), TEST_dataset_0stars);
		Assert.assertEquals("Provider Identity Test: Test Case 0 stars", 0, stars.intValue());
	}
	
	@Test
	public void testCase1() throws Exception {

		ProviderIdentityMetric metric = new ProviderIdentityMetric();
		Integer stars = metric.compute(testdata.getModel(TestCase_1stars), TEST_dataset_1stars);
		Assert.assertEquals("Provider Identity Test: Test Case 1 stars", 1, stars.intValue());
	}
	
	@Test
	public void testCase2() throws Exception {

		ProviderIdentityMetric metric = new ProviderIdentityMetric();
		Integer stars = metric.compute(testdata.getModel(TestCase_2stars), TEST_dataset_2stars);
		Assert.assertEquals("Provider Identity Test: Test Case 2 stars", 2, stars.intValue());
	}
	
	
	@Test
	public void testCase3() throws Exception {

		ProviderIdentityMetric metric = new ProviderIdentityMetric();
		Integer stars = metric.compute(testdata.getModel(TestCase_3stars), TEST_dataset_3stars);
		Assert.assertEquals("Provider Identity Test: Test Case 3 stars", 3, stars.intValue());
	}

	
	@Test
	public void testCase4() throws Exception {

		ProviderIdentityMetric metric = new ProviderIdentityMetric();
		Integer stars = metric.compute(testdata.getModel(TestCase_4stars), TEST_dataset_4stars);
		Assert.assertEquals("Provider Identity Test: Test Case 4 stars", 4, stars.intValue());
	}
	
	
	@Test
	public void testCase5() throws Exception {

		ProviderIdentityMetric metric = new ProviderIdentityMetric();
		Integer stars = metric.compute(testdata.getModel(TestCase_5stars), TEST_dataset_5stars);
		Assert.assertEquals("Provider Identity Test: Test Case 5 stars", 5, stars.intValue());
	}


}
