package de.ur.remex.model.experiment;

import java.util.ArrayList;

public class Experiment {

    private String name;
    private ArrayList<ExperimentGroup> groups;

    public Experiment(String name) {
        this.name = name;
        groups = new ArrayList<>();
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
