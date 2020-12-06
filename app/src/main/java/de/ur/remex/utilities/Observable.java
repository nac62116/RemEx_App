package de.ur.remex.utilities;

public class Observable extends java.util.Observable {

    public void notifyExperimentController(Event event){
        super.setChanged();
        super.notifyObservers(event);
    }
}
