
package excel2db.service.impl;

import java.util.Arrays;
import java.util.HashSet;

import excel2db.service.GenerateFileList;
import org.springframework.beans.factory.annotation.Value;

public class GenerateFileListImpl implements GenerateFileList {

    @Value("${input.files}")
    String inputFilesList;

    @Override
    public HashSet<String> generateFileList() {

        HashSet<String> fileList = null;
        if (!inputFilesList.isEmpty()) {
            String[] fileNameArray = inputFilesList.split(",");
            // hash set to eliminate duplicates
            fileList = new HashSet<>(Arrays.asList(fileNameArray));
        }
        return fileList;

    }
}
