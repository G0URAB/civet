package org.dice_research.opal.civet;

import org.dice_research.opal.civet.example.ExampleTest;
import org.dice_research.opal.civet.metrics.CategorizationMetricTest;
import org.dice_research.opal.civet.metrics.MetadataQualityMetricTest;
import org.dice_research.opal.civet.metrics.MultipleSerializationsMetricTest;
import org.dice_research.opal.civet.metrics.UpdateRateMetricTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({

		// Civet

		MetricComputationTest.class,

		// Metrics

		CategorizationMetricTest.class,

		MultipleSerializationsMetricTest.class,

		UpdateRateMetricTest.class,

		// Metric aggregation

		MetadataQualityMetricTest.class,

		// Minimal working example

		ExampleTest.class

})

public class AllTests {

}