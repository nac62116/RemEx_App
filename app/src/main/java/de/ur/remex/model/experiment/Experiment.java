package de.ur.remex.model.experiment;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class Experiment {

    private final ArrayList<ExperimentGroup> groups;

    @JsonCreator
    public Experiment(@JsonProperty("name") String name, @JsonProperty("groups") ArrayList<ExperimentGroup> groups) {
        this.groups = groups;
    }

    public void addExperimentGroup(ExperimentGroup group) {
        groups.add(group);
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
}
