package de.ur.remex.model.experiment;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class Experiment {

    private final ArrayList<ExperimentGroup> groups;
    private final String name;
    private final ArrayList<EncodedResource> encodedResources;

    @JsonCreator
    public Experiment(@JsonProperty("name") String name,
                      @JsonProperty("groups") ArrayList<ExperimentGroup> groups,
                      @JsonProperty("encodedResources") ArrayList<EncodedResource> encodedResources) {
        this.name = name;
        this.groups = groups;
        this.encodedResources = encodedResources;
    }

    public ExperimentGroup getExperimentGroupByName(String groupName) {
        for (ExperimentGroup group: groups) {
            if (group.getName().equals(groupName)) {
                return group;
            }
        }
        return null;
    }

    public ArrayList<ExperimentGroup> getExperimentGroups() {
        return groups;
    }

    public String getName() {
        return name;
    }

    public ArrayList<EncodedResource> getEncodedResources() {
        return encodedResources;
    }
}
