package org.eclipse.ant.internal.ui;/* * (c) Copyright IBM Corp. 2000, 2001. * All Rights Reserved. */import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.widgets.Listener;
import java.util.*;import org.apache.tools.ant.Target;import org.eclipse.ant.core.EclipseProject;import org.eclipse.core.resources.IFile;
import org.eclipse.swt.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

public class AntLaunchWizardPage extends WizardPage implements ICheckStateListener {
	private Vector selectedTargets = new Vector();
	private CheckboxTableViewer listViewer;
	private EclipseProject project;
	private TargetsListLabelProvider labelProvider = new TargetsListLabelProvider();
	private String initialTargetSelections[];
	private Button showLogOnSuccess;
	
	private final static int SIZING_SELECTION_WIDGET_HEIGHT = 200;
	private final static int SIZING_SELECTION_WIDGET_WIDTH = 200;
	
	public AntLaunchWizardPage(EclipseProject project) {
		super("execute ant script",Policy.bind("wizard.executeAntScriptTitle"),null);
		this.project = project;
	}
	
	public void checkStateChanged(CheckStateChangedEvent e) {
		Target checkedTarget = (Target)e.getElement();
		if (e.getChecked())
			selectedTargets.addElement(checkedTarget);
		else
			selectedTargets.removeElement(checkedTarget);
			
		labelProvider.setSelectedTargets(selectedTargets);
		listViewer.refresh();
	}
	
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		composite.setLayout(new GridLayout());
		composite.setLayoutData(new GridData(
		GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL));
		
		new Label(composite,SWT.NONE).setText(Policy.bind("wizard.availableTargetsLabel"));
		
		listViewer = new CheckboxTableViewer(composite,SWT.BORDER | SWT.CHECK);
		GridData data = new GridData(GridData.FILL_BOTH);
		data.heightHint = SIZING_SELECTION_WIDGET_HEIGHT;
		data.widthHint = SIZING_SELECTION_WIDGET_WIDTH;
		listViewer.setSorter(new ViewerSorter() {
			public int compare(Viewer viewer,Object o1,Object o2) {
				return ((Target)o1).getName().compareTo(((Target)o2).getName());
			}
		});
		
		listViewer.getTable().setLayoutData(data);
		listViewer.setLabelProvider(labelProvider);
		listViewer.setContentProvider(TargetsListContentProvider.getInstance());
		listViewer.setInput(project);
		
		new Label(composite,SWT.NONE).setText(Policy.bind("wizard.argumentsLabel"));
		Text argumentsField = new Text(composite,SWT.BORDER);
		argumentsField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		showLogOnSuccess = new Button(composite, SWT.CHECK);
		showLogOnSuccess.setText(Policy.bind("wizard.displayLogLabel"));

		restorePreviousSelectedTargets();
		listViewer.addCheckStateListener(this);
		listViewer.refresh();
		setControl(composite);
	}
	
	public Vector getSelectedTargets() {
		return (Vector)selectedTargets.clone();
	}
	
	protected void restorePreviousSelectedTargets() {
		if (initialTargetSelections == null)
			return;
		
		Vector result = new Vector();
		Object availableTargets[] = TargetsListContentProvider.getInstance().getElements(project);
		for (int i = 0; i < initialTargetSelections.length; i++) {
			String currentTargetName = initialTargetSelections[i];
			for (int j = 0; j < availableTargets.length; j++) {
				if (((Target)availableTargets[j]).getName().equals(currentTargetName)) {
					result.addElement(availableTargets[j]);
					listViewer.setChecked(availableTargets[j],true);
					continue;
				}
			}
		}
		
		selectedTargets = result;
		labelProvider.setSelectedTargets(selectedTargets);
	}

	public void setInitialTargetSelections(String value[]) {
		initialTargetSelections = value;
	}
	
}
