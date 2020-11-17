package de.ur.remex.utilities;

import java.util.Observable;

public class ActivityObservable extends Observable {

    public void notifyExperimentController(String eventType){
        super.setChanged();
        super.notifyObservers(eventType);
    }
}
