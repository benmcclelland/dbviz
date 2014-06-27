package gui.controllers;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;
import java.util.ArrayList;

import gui.models.AbstractModel;
import gui.views.AbstractView;

/**
 * Controller that acts as middle man between views and models.  Controller
 * notifies views of change in models and vice versa.  Views and models must
 * register with the controller to be notified of changes.
 */
public abstract class AbstractController implements PropertyChangeListener {

	private ArrayList<AbstractView> registeredViews;
	private ArrayList<AbstractModel> registeredModels;

	/**
	 * Default constructor
	 */
	public AbstractController() {
        registeredViews = new ArrayList<AbstractView>();
        registeredModels = new ArrayList<AbstractModel>();
    }

	/**
	 * Adds a model to the list of registered models
	 * @param model model to register
	 */
	public void addModel(AbstractModel model) {
        registeredModels.add(model);
        model.addPropertyChangeListener(this);
    }

	/**
	 * Removes a model from the list of registered models
	 * @param model model to remove
	 */
    public void removeModel(AbstractModel model) {
        registeredModels.remove(model);
        model.removePropertyChangeListener(this);
    }

    /**
     * Add a view to the list of registered views
     * @param view view to register
     */
	public void addView(AbstractView view) {
        registeredViews.add(view);
    }

	/**
	 * Remove a view from the list of registered views
	 * @param view view to remove
	 */
	public void removeView(AbstractView view) {
        registeredViews.remove(view);
    }

	/**
	 * Notify registered views of change in model
	 * @param evt event that caused change in model
	 */
	public void propertyChange(PropertyChangeEvent evt) {
        for (AbstractView view: registeredViews) {
            view.modelPropertyChange(evt);
        }
    }

    /**
     * Inspects each registered model to see if it is the owner of the
     * property that has changed.  If it is, then set that property.
     *
     * @param propertyName name of the property
     * @param newValue new value of the property
     */
    protected void setModelProperty(String propertyName, Object newValue) {
        for (AbstractModel model: registeredModels) {
            try {
            	// check if model has property; if so, get appropriate method
                Method method = model.getClass().getMethod("set"+propertyName, 
                		new Class[] { newValue.getClass() } );
                method.invoke(model, newValue);
            } catch (Exception ex) {
                //  Handle exception.
            	ex.printStackTrace();
            }
        }
    }

}