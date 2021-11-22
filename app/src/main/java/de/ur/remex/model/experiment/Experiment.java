package de.ur.remex.model.experiment;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Experiment {

    @JsonProperty("groups")
    private final ArrayList<ExperimentGroup> groups;
    @JsonProperty("name")
    private final String name;

    @JsonCreator
    public Experiment(@JsonProperty("name") String name,
                      @JsonProperty("groups") ArrayList<ExperimentGroup> groups) {
        this.name = name;
        this.groups = groups;
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
}
