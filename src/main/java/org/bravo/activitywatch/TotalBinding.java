package org.bravo.activitywatch;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.binding.LongBinding;
import javafx.beans.property.LongProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import org.bravo.activitywatch.entity.ActivityDO;

class TotalBinding extends LongBinding {

    // Reference to our observable list 
    private final ObservableList<ActivityDO> boundList;

    // Array of currently observed properties of elements of our list
    private LongProperty[] observedProperties = {};

    // Listener that has to call rebinding in response of any change in observable list
    private final ListChangeListener<ActivityDO> BOUND_LIST_CHANGE_LISTENER
            = (ListChangeListener.Change<? extends ActivityDO> change) -> {
                refreshBinding();
            };

    TotalBinding(ObservableList<ActivityDO> boundList) {
        this.boundList = boundList;
        boundList.addListener(BOUND_LIST_CHANGE_LISTENER);
        refreshBinding();
    }

    @Override
    protected long computeValue() {
        int i = 0;
        for (LongProperty bp : observedProperties) {
            i += bp.get();
        }

        return i;
    }

    @Override
    public void dispose() {
        boundList.removeListener(BOUND_LIST_CHANGE_LISTENER);
        unbind(observedProperties);
    }

    private void refreshBinding() {
        // Clean old properties from IntegerBinding's inner listener
        unbind(observedProperties);

        // Load new properties    
        List<LongProperty> tmplist = new ArrayList<>();
        boundList.stream().map((boundList1) -> boundList1.getTimeProperty()).forEach((integerProperty) -> {
            tmplist.add(integerProperty);
        });

        observedProperties = tmplist.toArray(new LongProperty[0]);

        // Bind IntegerBinding's inner listener to all new properties
        super.bind(observedProperties);

        // Invalidate binding to generate events
        // Eager/Lazy recalc depends on type of listeners attached to this instance
        // see IntegerBinding sources
        this.invalidate();
    }
}