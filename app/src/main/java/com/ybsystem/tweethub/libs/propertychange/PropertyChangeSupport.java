package com.ybsystem.tweethub.libs.propertychange;

public class PropertyChangeSupport {

    private PropertyChangeListener propertyChangeListener;

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.propertyChangeListener = listener;
    }

    public void firePropertyChange() {
        this.propertyChangeListener.propertyChange();
    }

}
