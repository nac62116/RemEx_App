package de.ur.remex.model.experiment.questionnaire;

import java.util.ArrayList;

public class PointOfTimeQuestion extends Question {

    private int nextQuestionId;
    // Has to contain at least one of PointOfTimeType.DATE or PointOfTimeType.DAYTIME
    private ArrayList<PointOfTimeType> pointOfTimeTypes;

    public PointOfTimeQuestion() {
        type = QuestionType.POINT_OF_TIME;
        pointOfTimeTypes = new ArrayList<>();
    }

    public int getNextQuestionId() {
        return nextQuestionId;
    }

    public void setNextQuestionId(int nextQuestionId) {
        this.nextQuestionId = nextQuestionId;
    }

    public ArrayList<String> getPointOfTimeTypeNames() {
        ArrayList<String> pointOfTimeTypeNames = new ArrayList<>();
        for (PointOfTimeType pointOfTimeType: pointOfTimeTypes) {
            pointOfTimeTypeNames.add(pointOfTimeType.name());
        }
        return pointOfTimeTypeNames;
    }

    public void addPointOfTimeType(PointOfTimeType pointOfTimeType) {
        if (!pointOfTimeTypes.contains(pointOfTimeType)) {
            pointOfTimeTypes.add(pointOfTimeType);
        }
    }
}
