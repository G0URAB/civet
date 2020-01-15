package org.dice_research.opal.civet.metrics;

import org.apache.jena.rdf.model.Model;
import org.dice_research.opal.civet.TestData;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests {@link UpdateRateMetric}.
 */
public class UpdateRateMetricTest {

	TestData testdata;
	final String TEST_FILE = "TestDataUpdateRateMetric.ttl";
	Model model;

	@Before
	public void setUp() throws Exception {
		testdata = new TestData();
		model = testdata.getModel(TEST_FILE);
	}

	@Test
	public void testStarNull() throws Exception {

		final String TEST_DATASET = "http://projekt-opal.de/dataset/https___"
				+ "europeandataportal_eu_set_data_debat_dorientations_budgetaires_2019";

		UpdateRateMetric metric = new UpdateRateMetric();
		Integer stars = metric.compute(model, TEST_DATASET);
		Assert.assertNull(TEST_FILE, stars);

	}

	@Test
	public void testStar1() throws Exception {

		final String TEST_DATASET = "http://projekt-opal.de/dataset/https___"
				+ "europeandataportal_eu_set_data_declaration_du_profil_acheteur_35";

		UpdateRateMetric metric = new UpdateRateMetric();
		Integer stars = metric.compute(model, TEST_DATASET);
		Assert.assertEquals(TEST_FILE, 1, stars.intValue());
	}

	@Test
	public void testStar2() throws Exception {

		final String TEST_DATASET = "http://projekt-opal.de/dataset/https___europeandataportal_eu_set_data_"
				+ "defibrillateurs_presents_sur_la_commune_de_sixt_sur_aff_en_2018";

		UpdateRateMetric metric = new UpdateRateMetric();
		Integer stars = metric.compute(model, TEST_DATASET);
		Assert.assertEquals(TEST_FILE, 2, stars.intValue());
	}

	@Test
	public void testStar3() throws Exception {

		final String TEST_DATASET = "http://projekt-opal.de/dataset/https___"
				+ "europeandataportal_eu_set_data_de68f4fc_2886_4775_9537_2bf21d81dff7";

		UpdateRateMetric metric = new UpdateRateMetric();
		Integer stars = metric.compute(model, TEST_DATASET);
		Assert.assertEquals(TEST_FILE, 3, stars.intValue());

	}

	@Test
	public void testStar4() throws Exception {

		final String TEST_DATASET = "http://projekt-opal.de/dataset/https___"
				+ "europeandataportal_eu_set_data_ded2b43b_abec_49b1_9eb1_44d418ec7fb4";

		UpdateRateMetric metric = new UpdateRateMetric();
		Integer stars = metric.compute(model, TEST_DATASET);
		Assert.assertEquals(TEST_FILE, 4, stars.intValue());
	}

	@Test
	public void testStar5() throws Exception {

		final String TEST_DATASET = "http://projekt-opal.de/dataset/https___"
				+ "europeandataportal_eu_set_data_de7414f5fdc7eee3_85c8205f_27eae5db_ff90930e_bd567a43a3521eb8b2250d88";

		UpdateRateMetric metric = new UpdateRateMetric();
		Integer stars = metric.compute(model, TEST_DATASET);
		Assert.assertEquals(TEST_FILE, 5, stars.intValue());
	}

}