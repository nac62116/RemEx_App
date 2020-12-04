package de.ur.remex.utilities;

import java.util.Observable;

public class ActivityObservable extends Observable {

    public void notifyExperimentController(ActivityEvent event){
        super.setChanged();
        super.notifyObservers(event);
    }
}
