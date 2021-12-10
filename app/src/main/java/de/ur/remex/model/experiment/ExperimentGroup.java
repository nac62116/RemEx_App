package de.ur.remex.model.experiment;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

/*
MIT License

Copyright (c) 2021 Colin Nash

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

@JsonIgnoreProperties(ignoreUnknown = true)
public class ExperimentGroup {

    @JsonProperty("name")
    private final String name;
    @JsonProperty("startTimeInMillis")
    private long startTimeInMillis;
    @JsonProperty("surveys")
    private final ArrayList<Survey> surveys;

    @JsonCreator
    public ExperimentGroup(@JsonProperty("name") String name,
                           @JsonProperty("startTimeInMillis") long startTimeInMillis,
                           @JsonProperty("surveys") ArrayList<Survey> surveys) {
        this.name = name;
        this.startTimeInMillis = startTimeInMillis;
        this.surveys = surveys;
    }

    public void setStartTimeInMillis(long startTimeInMillis) {
        this.startTimeInMillis = startTimeInMillis;
    }

    public long getStartTimeInMillis() {
        return startTimeInMillis;
    }

    public Survey getFirstSurvey() {
        for (Survey survey: surveys) {
            if (survey.getPreviousSurveyId() == 0) {
                return survey;
            }
        }
        return null;
    }

    public Survey getSurveyById(int surveyId) {
        for (Survey survey: surveys) {
            if (surveyId == survey.getId()) {
                return survey;
            }
        }
        return null;
    }

    public ArrayList<Survey> getSurveys() {
        return surveys;
    }

    public String getName() {
        return name;
    }
}
