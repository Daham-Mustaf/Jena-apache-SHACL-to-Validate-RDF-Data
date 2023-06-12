package shacl.validator;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.vocabulary.RDFS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.topbraid.jenax.util.JenaUtil;
import org.topbraid.shacl.validation.ValidationUtil;
import org.topbraid.shacl.vocabulary.SH;

public class ShaclValidation {
    private static final Logger logger = LoggerFactory.getLogger(ShaclValidation.class);
    private static final Marker WTF_MARKER = MarkerFactory.getMarker("WTF");
    private final Path basePath;

    public ShaclValidation(Path basePath) {
        this.basePath = basePath;
    }

    public void validate(String dataFileName, String shapeFileName) {
        try {
            String dataFilePath = getFileAbsolutePath("src/main/resources/", dataFileName);
            String shapeFilePath = getFileAbsolutePath("src/main/resources/", shapeFileName);

            Model dataModel = readModelFromFile(dataFilePath);
            Model shapeModel = readModelFromFile(shapeFilePath);

            Resource reportResource = ValidationUtil.validateModel(dataModel, shapeModel, true);
            boolean conforms = reportResource.getProperty(SH.conforms).getBoolean();
            logger.trace("Conforms = {}", conforms);

            if (conforms) {
                String dataFileNameWithoutExtension = removeFileExtension(dataFileName);
                String reportFilePath = getFileAbsolutePath("src/main/reports/", dataFileNameWithoutExtension + "_conform_report.ttl");
                createReportFile(reportFilePath, "Data of " + dataFileName + " validated successfully!");
            } else {
                String dataFileNameWithoutExtension = removeFileExtension(dataFileName);
                String reportFilePath = getFileAbsolutePath("src/main/reports/", dataFileNameWithoutExtension + "_validation_report.ttl");
                writeModelToFile(reportFilePath, reportResource.getModel());
                System.out.println("See the violation report. The data has not been conformed!");
            }
        } catch (Throwable t) {
            logger.error(WTF_MARKER, t.getMessage(), t);
        }
    }

    private String getFileAbsolutePath(String directory, String fileName) {
        return basePath.resolve(directory + fileName).toAbsolutePath().toString();
    }

    private Model readModelFromFile(String filePath) {
        Model model = JenaUtil.createDefaultModel();
        model.read("file:" + filePath);
        return model;
    }

    private String removeFileExtension(String fileName) {
        int extensionIndex = fileName.lastIndexOf(".");
        if (extensionIndex != -1) {
            return fileName.substring(0, extensionIndex);
        }
        return fileName;
    }

    private void createReportFile(String filePath, String message) throws IOException {
        File reportFile = new File(filePath);
        reportFile.createNewFile();
        try (OutputStream reportOutputStream = new FileOutputStream(reportFile)) {
            Model reportModel = JenaUtil.createDefaultModel();
            reportModel.createResource().addProperty(RDFS.comment, message);
            RDFDataMgr.write(reportOutputStream, reportModel, RDFFormat.TURTLE);
        }
        System.out.println("Data validated successfully!");
    }

    private void writeModelToFile(String filePath, Model model) throws IOException {
        File reportFile = new File(filePath);
        reportFile.createNewFile();
        try (OutputStream reportOutputStream = new FileOutputStream(reportFile)) {
            RDFDataMgr.write(reportOutputStream, model, RDFFormat.TURTLE);
        }
    }

    public static void main(String[] args) {
        Path basePath = Paths.get(".").toAbsolutePath().normalize();
        ShaclValidation validator = new ShaclValidation(basePath);

        String dataFileName = "person.ttl";
        String shapeFileName = "personShape.ttl";
        validator.validate(dataFileName, shapeFileName);
    }
}



