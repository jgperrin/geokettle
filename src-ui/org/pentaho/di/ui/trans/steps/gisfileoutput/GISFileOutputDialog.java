/**********************************************************************
 **                                                                   **
 **               This code belongs to the KETTLE project.            **
 **                                                                   **
 ** Kettle, from version 2.2 on, is released into the public domain   **
 ** under the Lesser GNU Public License (LGPL).                       **
 **                                                                   **
 ** For more details, please read the document LICENSE.txt, included  **
 ** in this project                                                   **
 **                                                                   **
 ** http://www.kettle.be                                              **
 ** info@kettle.be                                                    **
 **                                                                   **
 **********************************************************************/

 
/*
 * Created on 2008-01-27
 * jmathieu, edube
 *
 */

package org.pentaho.di.ui.trans.steps.gisfileoutput;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDialogInterface;
import org.pentaho.di.trans.steps.gisfileoutput.GISFileOutputMeta;
import org.pentaho.di.trans.steps.gisfileoutput.Messages;
import org.pentaho.di.ui.core.dialog.ErrorDialog;
import org.pentaho.di.ui.core.widget.TextVar;
import org.pentaho.di.ui.trans.step.BaseStepDialog;

public class GISFileOutputDialog extends BaseStepDialog implements StepDialogInterface{

	final static private String[] GISFILE_FILTER_EXT = new String[] {"*.shp;*.SHP", "*"};
	
	private Label        wlFileName;
	private Button       wbFileName;
	private TextVar      wFileName;
	private FormData     fdlFileName, fdbFileName, fdFileName;
	
	private Label wlFileField;
	private Button wFileField;
	private FormData fdlFileField,fdFileField;
      
    private Label wlFileNameField;
    private CCombo wFileNameField;
    private FormData fdFileNameField,fdlFileNameField;
	
	private GISFileOutputMeta input;
	private boolean backupChanged;

	public GISFileOutputDialog(Shell parent, Object out, TransMeta tr, String sname){
		super(parent, (BaseStepMeta)out, tr, sname);
		input=(GISFileOutputMeta)out;
	}

	public String open(){
		Shell parent = getParent();
		Display display = parent.getDisplay();

		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MAX | SWT.MIN);
 		props.setLook(shell);
        setShellImage(shell, input);

		ModifyListener lsMod = new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				input.setChanged();
			}
		};
		backupChanged = input.hasChanged();
		
		FormLayout formLayout = new FormLayout();
		formLayout.marginWidth = Const.FORM_MARGIN;
		formLayout.marginHeight = Const.FORM_MARGIN;

		shell.setLayout(formLayout);
		shell.setText(Messages.getString("GISFileOutputDialog.Dialog.Title")); //$NON-NLS-1$

		int middle = props.getMiddlePct();
		int margin = Const.MARGIN;

		// Stepname line
		wlStepname = new Label(shell, SWT.RIGHT);
		wlStepname.setText(Messages.getString("System.Label.StepName")); //$NON-NLS-1$
		props.setLook(wlStepname);
		fdlStepname = new FormData();
		fdlStepname.left = new FormAttachment(0, 0);
		fdlStepname.right = new FormAttachment(middle, -margin);
		fdlStepname.top = new FormAttachment(0, margin);
		wlStepname.setLayoutData(fdlStepname);
		wStepname = new Text(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		wStepname.setText(stepname);
		props.setLook(wStepname);
		wStepname.addModifyListener(lsMod);
		fdStepname = new FormData();
		fdStepname.left = new FormAttachment(middle, 0);
		fdStepname.top = new FormAttachment(0, margin);
		fdStepname.right = new FormAttachment(100, 0);
		wStepname.setLayoutData(fdStepname);

		// FileName line
		wlFileName = new Label(shell, SWT.RIGHT);
		wlFileName.setText(Messages.getString("System.Label.Filename")); //$NON-NLS-1$
 		props.setLook(wlFileName);
		fdlFileName=new FormData();
		fdlFileName.left = new FormAttachment(0, 0);
		fdlFileName.top  = new FormAttachment(wStepname, margin);
		fdlFileName.right= new FormAttachment(middle, -margin);
		wlFileName.setLayoutData(fdlFileName);

		wbFileName=new Button(shell, SWT.PUSH| SWT.CENTER);
 		props.setLook(wbFileName);
		wbFileName.setText(Messages.getString("System.Button.Browse")); //$NON-NLS-1$
		fdbFileName=new FormData();
		fdbFileName.right= new FormAttachment(100, 0);
		fdbFileName.top  = new FormAttachment(wStepname, margin);
		wbFileName.setLayoutData(fdbFileName);
		
		wFileName=new TextVar(transMeta, shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
 		props.setLook(wFileName);
		wFileName.addModifyListener(lsMod);
		fdFileName=new FormData();
		fdFileName.left = new FormAttachment(middle, 0);
		fdFileName.right= new FormAttachment(wbFileName, -margin);
		fdFileName.top  = new FormAttachment(wStepname, margin);
		wFileName.setLayoutData(fdFileName);
		
		//Is FileName defined in a Field				        
	    wlFileField=new Label(shell, SWT.RIGHT);
	    wlFileField.setText(Messages.getString("GISFileOutputDialog.FilenameInField.Label"));
        props.setLook(wlFileField);
        fdlFileField=new FormData();
        fdlFileField.left = new FormAttachment(0, 0);
        fdlFileField.right = new FormAttachment(middle, -margin);
        fdlFileField.top  = new FormAttachment(wFileName, margin*2);
        wlFileField.setLayoutData(fdlFileField);
        
        wFileField=new Button(shell, SWT.CHECK);
        wFileField.setToolTipText(Messages.getString("GISFileOutputDialog.FilenameInField.Tooltip"));
	    props.setLook(wFileField);
	    fdFileField=new FormData();
	    fdFileField.right  = new FormAttachment(100, 0);
	    fdFileField.top   = new FormAttachment(wFileName, margin);
	    fdFileField.left   = new FormAttachment(middle, 0);
	    wFileField.setLayoutData(fdFileField);
	    wFileField.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent arg0)
            {
            	activeFileField();
            	input.setChanged();
            }
        }
        );
        
		// FileName field
		wlFileNameField=new Label(shell, SWT.RIGHT);
        wlFileNameField.setText(Messages.getString("GISFileOutputDialog.FilenameField.Label"));
        props.setLook(wlFileNameField);
        fdlFileNameField=new FormData();
        fdlFileNameField.left = new FormAttachment(0, 0);
        fdlFileNameField.top  = new FormAttachment(wFileField,2* margin);
        fdlFileNameField.right= new FormAttachment(middle, -margin);
        wlFileNameField.setLayoutData(fdlFileNameField);
        
        
        wFileNameField=new CCombo(shell, SWT.BORDER | SWT.READ_ONLY);
        wFileNameField.setEditable(true);
        props.setLook(wFileNameField);
        wFileNameField.addModifyListener(lsMod);
        fdFileNameField=new FormData();
        fdFileNameField.left = new FormAttachment(middle, 0);
        fdFileNameField.top  = new FormAttachment(wFileField, margin);
        fdFileNameField.right= new FormAttachment(100, -margin);
        wFileNameField.setLayoutData(fdFileNameField);
        wFileNameField.addFocusListener(new FocusListener()
            {
                public void focusLost(org.eclipse.swt.events.FocusEvent e){
                }
            
                public void focusGained(org.eclipse.swt.events.FocusEvent e){
                    Cursor busy = new Cursor(shell.getDisplay(), SWT.CURSOR_WAIT);
                    shell.setCursor(busy);
                    setFileField();
                    shell.setCursor(null);
                    busy.dispose();
                }
            }
        );                       
		
		// Some buttons
		wOK=new Button(shell, SWT.PUSH);
		wOK.setText(Messages.getString("System.Button.OK")); //$NON-NLS-1$
		wCancel=new Button(shell, SWT.PUSH);
		wCancel.setText(Messages.getString("System.Button.Cancel")); //$NON-NLS-1$
		
		setButtonPositions(new Button[] { wOK, wCancel }, margin, null);

		// Add listeners
		lsCancel   = new Listener() { public void handleEvent(Event e) { cancel(); } };
		lsOK       = new Listener() { public void handleEvent(Event e) { ok();     } };
		
		wCancel.addListener(SWT.Selection, lsCancel);
        wOK.addListener    (SWT.Selection, lsOK    );
		
		lsDef=new SelectionAdapter() { public void widgetDefaultSelected(SelectionEvent e) { ok(); } };
		
		wStepname.addSelectionListener( lsDef );

		wFileName.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent arg0){
				wFileName.setToolTipText(transMeta.environmentSubstitute(wFileName.getText()));
			}
		});	

		wbFileName.addSelectionListener
		(
			new SelectionAdapter()
			{
				public void widgetSelected(SelectionEvent e) 
				{
					FileDialog dialog = new FileDialog(shell, SWT.OPEN);
					dialog.setFilterExtensions(GISFILE_FILTER_EXT); //$NON-NLS-1$ //$NON-NLS-2$
					if (wFileName.getText()!=null)
					{
						dialog.setFileName(wFileName.getText());
					}
						
					dialog.setFilterNames(new String[] {Messages.getString("GISFileOutputDialog.Filter.SHPFiles"), Messages.getString("System.FileType.AllFiles")}); //$NON-NLS-1$ //$NON-NLS-2$
					
					if (dialog.open()!=null)
					{
						String str = dialog.getFilterPath()+Const.FILE_SEPARATOR+dialog.getFileName();
						wFileName.setText(str);
					}
				}
			}
		);
		
		// Detect X or ALT-F4 or something that kills this window...
		shell.addShellListener(	new ShellAdapter() { public void shellClosed(ShellEvent e) { cancel(); } } );
		
		getData();
		input.setChanged(changed);

		// Set the shell size, based upon previous time...
		setSize();
		activeFileField();
		shell.open();
		while (!shell.isDisposed()){
				if (!display.readAndDispatch()) display.sleep();
		}
		return stepname;
	}
	
	/**
	 * Copy information from the meta-data Output to the dialog fields.
	 */ 
	public void getData(){
		if (!input.isFileNameInField() ) {
			if (input.getFileName() != null){
				wFileName.setText(input.getFileName());
				wFileName.setToolTipText(transMeta.environmentSubstitute(input.getFileName()));
			}
		}else{
			wFileField.setSelection(true);
			if(input.getFileNameField() !=null)
				wFileNameField.setText(input.getFileNameField());		
		}

		wStepname.selectAll();
	}
	
	
	private void setFileField(){
		try{
	        String field=  wFileNameField.getText();
			wFileNameField.removeAll();
				
			RowMetaInterface r = transMeta.getPrevStepFields(stepname);
			if (r!=null){
		    	r.getFieldNames();
			    for (int i=0;i<r.getFieldNames().length;i++){	
		        	wFileNameField.add(r.getFieldNames()[i]);									
				}
			}
			if(field!=null) wFileNameField.setText(field);		
		}catch(KettleException ke){
			new ErrorDialog(shell, Messages.getString("GISFileOutputDialog.FailedToGetFields.DialogTitle"), Messages.getString("GISFileOutputDialog.FailedToGetFields.DialogMessage"), ke); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	private void activeFileField(){
		wlFileNameField.setEnabled(wFileField.getSelection());
		wFileNameField.setEnabled(wFileField.getSelection());	
		wlFileName.setEnabled(!wFileField.getSelection());		
		wFileName.setEnabled(!wFileField.getSelection());
		wbFileName.setEnabled(!wFileField.getSelection());
	}
	
	private void cancel(){
		stepname=null;
		input.setChanged(backupChanged);
		dispose();
	}
	
	public void getInfo(GISFileOutputMeta oneMeta) throws KettleStepException{
		// copy info to Meta class (input)
		oneMeta.setFileName( wFileName.getText() );
		oneMeta.setFileNameInField(wFileField.getSelection());
		oneMeta.setFileNameField(wFileNameField.getText());	
	}
	
	private void ok(){
		try{
			stepname = wStepname.getText(); // return value
			getInfo(input);
			dispose();
		}
		catch(KettleStepException e){
			MessageBox mb = new MessageBox(shell, SWT.OK | SWT.ICON_ERROR );
			mb.setMessage(e.toString());
			mb.setText(Messages.getString("System.Warning")); //$NON-NLS-1$
			mb.open();
			
			// Close anyway!
			dispose();
		}
	}	
}