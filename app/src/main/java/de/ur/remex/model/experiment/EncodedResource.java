package de.ur.remex.model.experiment;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class EncodedResource {

    private final String fileName;
    private final String base64String;

    @JsonCreator
    public EncodedResource(@JsonProperty("fileName") String fileName,
                           @JsonProperty("base64String") String base64String) {
        this.fileName = fileName;
        this.base64String = base64String;
    }

    public String getFileName() {
        return fileName;
    }

    public String getBase64String() {
        return base64String;
    }
}
