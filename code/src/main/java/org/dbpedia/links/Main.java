package org.dbpedia.links;

import org.aksw.rdfunit.io.reader.RdfReader;
import org.aksw.rdfunit.io.reader.RdfReaderException;
import org.aksw.rdfunit.io.reader.RdfStreamReader;
import org.apache.jena.rdf.model.Model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.stream;


/**
 * @author Dimitris Kontokostas
 * @since 22/4/2016 5:10 μμ
 */
public class Main {

    public static void main(String[] args) throws Exception {

        File f = new File("./").getParentFile();
        List<File> allFilesInRepo = getAllFilesInFolderOrFile(f);

        checkRdfSyntax(allFilesInRepo);

        checkDBpediaAsSubject(allFilesInRepo);

        checkMetadataFiles(allFilesInRepo);



    }

    private static void checkRdfSyntax(List<File> filesList) {
        filesList.stream().forEach(file -> {
            String fileName = file.getAbsolutePath();
            if (fileName.endsWith("nt") || fileName.endsWith("ttl")) {
                RdfReader reader = new RdfStreamReader(fileName);
                try {
                    Model model = reader.read();
                } catch (RdfReaderException e) {
                    throw new RuntimeException("Syntax error in file:" + fileName, e);

                    //Syntax error reading file...
                }
            }

        });
    }

    private static void checkDBpediaAsSubject(List<File> filesList) {
        filesList.stream().forEach(file -> {
            String fileName = file.getAbsolutePath();
            if (fileName.endsWith("links.nt")) {  // TODO make sure we check everything
                RdfReader reader = new RdfStreamReader(fileName);
                try {
                    Model model = reader.read();
                    model.listSubjects()
                            .forEachRemaining( subject -> {
                                if (!subject.toString().contains("dbpedia.org/")) {
                                    throw new RuntimeException("File " + fileName + " does not have a dbpedia URI as subject");
                                }
                            });
                } catch (RdfReaderException e) {
                    throw new RuntimeException("Syntax error in file:" + fileName, e);

                    //Syntax error reading file...
                }
            }

        });
    }


    private static void checkMetadataFiles(List<File> filesList) {
        filesList.stream().forEach(file -> {
            String fileName = file.getAbsolutePath();
            if (fileName.endsWith("metadata.ttl")) {
                // TODO RDFUnit check
            }

        });
    }

    private static List<File> getAllFilesInFolderOrFile (File input)
    {
        List<File> fileList = new ArrayList<>();
        if(input.isDirectory()) {
            stream(input.listFiles()).forEach(file ->  {

                if (file.isDirectory()) {
                    fileList.addAll(getAllFilesInFolderOrFile(file));
                } else {
                    fileList.add(file);
                }
            });
        }
        if(input.isFile())
        {
            fileList.add(input);
        }

        return fileList;
    }
}