package at.srfg.kmt.ehealth.phrs.jsf.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.component.UIInput;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

/*
 http://www.icefaces.org/JForum/posts/list/7071.page
 */
public class JsfFormUtil {
	/**
	 * Top down approach
	 * Not in use...
	 * @param form, if null then search top down for forms from UIViewRoot
	 * @return
	 */
	public static final boolean reset(UIForm form) {
		if (form != null) {
			clearSubmittedValues(form);
		} else {
			UIViewRoot viewRoot = (UIViewRoot) FacesContext
					.getCurrentInstance().getViewRoot();

			// UIForm form2 = findForm(viewRoot);
			List<UIForm> forms = findForms(viewRoot);
			if (forms != null && !forms.isEmpty()) {
				for (UIForm theForm : forms) {
					clearSubmittedValues(theForm);
				}
			}
		}
		return true;
		// return Constants.CANCEL;
	}
/**
 * In use...
 */
	public static void clearSubmittedValues(Object obj) {
		if (obj == null) {
			return;
		}		
		if (obj instanceof UIComponent == false) {
			return;
		}

		Iterator<UIComponent> chld = ((UIComponent) obj).getFacetsAndChildren();
		while (chld.hasNext()) {
			clearSubmittedValues(chld.next());
		}
		if (obj instanceof UIInput) {
			((UIInput) obj).setSubmittedValue(null);
			((UIInput) obj).setValue(null);
			((UIInput) obj).setLocalValueSet(false);
			((UIInput) obj).resetValue();
		}
	}

	/**
	 * This is a top down search for forms
	 * 
	 * @param component
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static List<UIForm> findForms(Object component) {
		if ((component == null) || (component instanceof UIComponent == false)) {
			return null;
		}

		List<UIForm> forms = new ArrayList<UIForm>();
		// Iterator<UIComponent> chld = ((UIComponent)
		// component).getFacetsAndChildren();
		List chld = ((UIComponent) component).getChildren();
		if (chld.size() != 0) {
			for (int i = 0; i < chld.size(); i++) {
				List<UIForm> subForms = findForms(chld.get(i));

				if (subForms != null && !subForms.isEmpty()) {
					forms.addAll(subForms);
				}
			}
		}

		if (component instanceof UIForm) {
			forms.add((UIForm) component);
		}
		return forms;
	}

	/**
	 * Find enclosing form by walking up the tree Find parent form of a button
	 * e.g. a reset or cancel button
	 * 
	 */
	/**
	 * 
	 * @param renderRepsonse
	 * @param event
	 * 
	 *            reset button should have reset prefix and form Id 'FormId'
	 */
	public UIComponent findFormComponent(UIComponent component) {
		if (component != null) {

			boolean found = false;
			UIComponent theComponent = component;
			// String className;
			try {
				int count = 0;
				while (!found) {
					count++;
					if (theComponent != null) {
						// className = theComponent.getClass().getSimpleName();
						if (theComponent instanceof UIForm) {
							found = true;
							return theComponent;
						}
					} else {
						found = true;
						continue;
					}
					if (count > 20) {
						found = true;
						continue;
					}
					theComponent = theComponent.getParent();
				}
			} catch (Exception e) {
				System.out.println("exception" + e);
			}
		}
		return null;
	}
	/*
	 * TODO USe tomahawk to reset values
	 * https://cwiki.apache.org/confluence/display
	 * /MYFACES/Clear+Input+Components
	 * 
	 * Overview
	 * 
	 * Below are a number of possible solutions. Force a new View
	 * 
	 * Call this method from the action method of the immediate command
	 * component:
	 * 
	 * public void refresh() { FacesContext context =
	 * FacesContext.getCurrentInstance(); Application application =
	 * context.getApplication(); ViewHandler viewHandler =
	 * application.getViewHandler(); UIViewRoot viewRoot =
	 * viewHandler.createView(context, context .getViewRoot().getViewId());
	 * context.setViewRoot(viewRoot); context.renderResponse(); //Optional }
	 * 
	 * This causes the current View tree to be discarded and a fresh one
	 * created. The new components of course then have no submitted values, and
	 * so fetch their displayed values via their value-bindings.
	 * 
	 * Note: If you call this method (in its current form) from the
	 * valueChangeListener of an immediate component, be sure to call the
	 * value's setter manually first. Otherwise, you will lose the new value
	 * entirely, as the lifecycle is skipped and the view is recreated, leaving
	 * the new value stored in neither place.
	 * 
	 * Note: If you want to navigate to another page then omit the optional
	 * context.renderResponse(); and save the view with
	 * application.getStateManager().saveSerializedView(context);. This will
	 * avoid session to reference to the old view when you go back to the page
	 * (tested only in JSF RI 1.1.01).
	 * 
	 * In addition to immediate components, this method can be useful when
	 * working with multiple forms or subForms. <<BR>> Omit the last line
	 * "context.renderResponse()", when you don't want to skip the other
	 * life-cycle phases. Delete Components Holding Unwanted State
	 * 
	 * Find the parent component of the problem inputs, and call
	 * 
	 * parentComponent.getChildren().clear();
	 * 
	 * During the render phase, new instances of these child components will
	 * then be created, while other components will not be affected.
	 * 
	 * This is effectively the same as the above solution, but discards a
	 * selected subset of components rather than the UI!ViewRoot.
	 * 
	 * Obtaining the parent component to discard can be done via binding.
	 * Alternatively, the "action listener" form of callback can be used for the
	 * command; this is passed an !ActionEvent from which the command component
	 * that was clicked can be found. A call to "findComponent" can be made on
	 * this to locate the desired parent component by id, or other similar
	 * solutions. Explicitly clear submitted value
	 * 
	 * For each component that you want to reset, specify a binding attribute so
	 * that the backing bean can access them. From the immediate component's
	 * action method, call:
	 * 
	 * component.setSubmittedValue(null); // The following is only needed for
	 * immediate input components // but it won't do any harm in other
	 * situations.. component.setValue(null); component.setLocalValueSet(false);
	 * 
	 * This will cause that component to refetch its value via its value-binding
	 * when rendered. In JSF 1.2, a resetValue() convenience function was added
	 * for just this set of calls.
	 * 
	 * This only affects specific components, though. If you want to clear a
	 * whole form, you can bind some parent component, then explicitly walk the
	 * tree of child components testing each one for type !EditableValueHolder,
	 * and if so performing the above action. You could even do this by fetching
	 * the UI!ViewRoot component, and not need to use any component bindings at
	 * all.
	 * 
	 * There may be some complications with walking the component tree when the
	 * page contains a UIData component with input fields. The UIData uses a
	 * single set of UIComponent objects to represent all the rows in the table,
	 * and stores the state of each row separately from the components
	 * themselves. It is probably therefore necessary to do something like this
	 * to reset editable components within table rows:
	 * 
	 * for(int i=0; i<uiDataComponent.rows(); ++i) {
	 * uiDataComponent.setRowIndex(i); // walk the child components, resetting
	 * any submitted value } uiDataComponent.setRowIndex(-1);
	 * 
	 * MyFaces Trinidad includes a <tr:resetActionListener> tag that automates
	 * this strategy of walking the tree, including tables.
	 */
	
	//http://stackoverflow.com/questions/3933786/jsf-2-bean-validation-validation-failed-empty-values-are-replaced-with-las
	public static void newForm(ActionEvent ae) {
	   // contact = new Contact();
	   // contact.setBirthday(new Date()); //for testing only

	    resetForm(ae.getComponent());
	}

	private static void resetForm(UIComponent uiComponent) {
	    //get form component
	    UIComponent parentComponent = uiComponent.getParent();
	    if (uiComponent instanceof UIForm)
	        resetFields(uiComponent);
	    else if (parentComponent != null)
	        resetForm(parentComponent);
	    else
	        resetFields(uiComponent);

	}

	private static  void resetFields(UIComponent baseComponent) {
	    for (UIComponent c : baseComponent.getChildren()) {
	        if (c.getChildCount() > 0)
	            resetFields(c);

	        if (c instanceof UIInput)
	            ((UIInput) c).resetValue();
	    }
	}

}
