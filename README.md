# SHACL Validator Jena-apache to RDF Data

The SHACL Validator is a Java application that validates a data graph against a shape graph using the SHACL (Shapes Constraint Language) specification. It utilizes the Apache Jena library for RDF processing and the TopBraid SHACL API for validation.

## Prerequisites

- Java Development Kit (JDK) 8 or higher
- Apache Maven

## Usage

1. Save your data graph file and shape graph file in the `src/main/resources/` directory of the project.
2. Run the `ShaclValidation` class as a Java application.
3. The validation report will be generated and saved in the `src/main/reports/` directory.

## Configuration

You can modify the file paths by updating the following variables in the `ShaclValidation` class:

- `dataFileName`: The name of the data graph file.
- `shapeFileName`: The name of the shape graph file.

Please ensure that both files are in the `src/main/resources/` directory or provide the correct relative path if they are located elsewhere.

## Output

- If the data graph conforms to the shape graph, a conform report file will be generated in the `src/main/reports/` directory. The file will have the name format: `dataFileName_conform_report.ttl`.
- If the data graph does not conform to the shape graph, a validation report file will be generated in the `src/main/reports/` directory. The file will have the name format: `dataFileName_validation_report.ttl`.

The report files are in Turtle format and contain information about the validation results.

## Contributing

Contributions to this project are welcome! If you find any issues or have suggestions for improvements, please feel free to open an issue or submit a pull request.





