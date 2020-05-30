[![Codacy Badge](https://app.codacy.com/project/badge/Grade/90ef5ca5918242e9bba1859d96c06110)](https://www.codacy.com/gh/pg-dp/civet?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=pg-dp/civet&amp;utm_campaign=Badge_Grade)

# OPAL Civet

OPAL metadata quality component.


## Usage with Apache Maven

Add the following lines to your `pom.xml` configuration file:

	<dependencies>
		<dependency>
			<groupId>org.dice-research.opal</groupId>
			<artifactId>civet</artifactId>
			<version>[2,3)</version>
		</dependency>
	</dependencies>
	
	<repositories>
		<repository>
			<id>maven.aksw.internal</id>
			<name>AKSW Repository</name>
			<url>http://maven.aksw.org/archiva/repository/internal</url>
		</repository>
		<repository>
			<id>maven.aksw.snapshots</id>
			<name>AKSW Snapshot Repository</name>
			<url>http://maven.aksw.org/archiva/repository/snapshots</url>
		</repository>
	</repositories>

Available versions are listed at [maven.aksw.org](https://maven.aksw.org/archiva/#advancedsearch~internal/org.dice-research.opal~civet~~~~~30).


## Example

```Java
import java.io.File;
import org.apache.jena.rdf.model.Model;
import org.dice_research.opal.civet.Civet;
import org.dice_research.opal.common.utilities.FileHandler;

public class Example {

	/**
	 * Computes quality metric scores (measurements).
	 * 
	 * @param turtleInputFile  A TURTLE file to read
	 * @param turtleOutputFile A TURTLE file to write results
	 * @param datasetUri       A URI of a dcat:Dataset inside the TURTLE data
	 * 
	 * @see https://www.w3.org/TR/turtle/
	 * @see https://www.w3.org/TR/vocab-dcat/
	 */
	public void evaluateMetadata(File turtleInputFile, File turtleOutputFile, String datasetUri) throws Exception {

		// Load TURTLE file into model
		Model model = FileHandler.importModel(turtleInputFile);

		Civet civet = new Civet();

		// If existing measurements should be removed
		// (optional method call, default: true)
		civet.setRemoveMeasurements(true);

		// If it should be logged, if a measurement could not be computed
		// (optional method call, default: true)
		civet.setLogNotComputed(true);

		// Update model
		model = civet.process(model, datasetUri);

		// Write updated model into TURTLE file
		FileHandler.export(turtleOutputFile, model);
	}
}

```

Example input:

```
<https://example.org/>
        a       <http://www.w3.org/ns/dcat#Dataset> ;
        <http://www.w3.org/ns/dcat#keyword>
                "keywordB" , "keywordA" ;
        <http://www.w3.org/ns/dcat#theme>
                <https://example.org/theme> .

<https://example.org/theme>
        a       <http://www.w3.org/2004/02/skos/core#Concept> .
```

Example output:

```
<https://example.org/>
        a       <http://www.w3.org/ns/dcat#Dataset> ;
        <http://www.w3.org/ns/dcat#keyword>
                "keywordA" , "keywordB" ;
        <http://www.w3.org/ns/dcat#theme>
                <https://example.org/theme> ;
        <http://www.w3.org/ns/dqv#hasQualityMeasurement>
                [ a       <http://www.w3.org/ns/dqv#QualityMeasurement> ;
                  <http://www.w3.org/ns/dqv#isMeasurementOf>
                          <http://metric.projekt-opal.de/MetadataQuality> ;
                  <http://www.w3.org/ns/dqv#value>
                          3
                ] ;
        <http://www.w3.org/ns/dqv#hasQualityMeasurement>
                [ a       <http://www.w3.org/ns/dqv#QualityMeasurement> ;
                  <http://www.w3.org/ns/dqv#isMeasurementOf>
                          <http://metric.projekt-opal.de/MultipleSerializations> ;
                  <http://www.w3.org/ns/dqv#value>
                          0
                ] ;
        <http://www.w3.org/ns/dqv#hasQualityMeasurement>
                [ a       <http://www.w3.org/ns/dqv#QualityMeasurement> ;
                  <http://www.w3.org/ns/dqv#isMeasurementOf>
                          <http://metric.projekt-opal.de/Categorization> ;
                  <http://www.w3.org/ns/dqv#value>
                          5
                ] .

<https://example.org/theme>
        a       <http://www.w3.org/2004/02/skos/core#Concept> .
```


## Note

Civet version 1 can be found at [branch civet-version-1](https://github.com/projekt-opal/civet/tree/civet-version-1).


## Credits

[Data Science Group (DICE)](https://dice-research.org/) at [Paderborn University](https://www.uni-paderborn.de/)

This work has been supported by the German Federal Ministry of Transport and Digital Infrastructure (BMVI) in the project [Open Data Portal Germany (OPAL)](http://projekt-opal.de/) (funding code 19F2028A).
