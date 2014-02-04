package org.bravo.activitywatch;

import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;


class RefreshEvent extends Event {
    
	private static final long serialVersionUID = 1L;
	
	public static final EventType<RefreshEvent> REFRESH_REQUEST = new EventType<RefreshEvent>(ANY, "BUTTON_PRESSED");
    
    public RefreshEvent() {
        this(REFRESH_REQUEST);
    }
    
    public RefreshEvent(EventType<? extends Event> arg0) {
        super(arg0);
    }
    public RefreshEvent(Object arg0, EventTarget arg1, EventType<? extends Event> arg2) {
        super(arg0, arg1, arg2);
    }  
}