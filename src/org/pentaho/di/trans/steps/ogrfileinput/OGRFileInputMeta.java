package org.pentaho.di.trans.steps.ogrfileinput;

import java.util.List;
import java.util.Map;

import org.pentaho.di.core.CheckResult;
import org.pentaho.di.core.CheckResultInterface;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.Counter;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.fileinput.FileInputList;
import org.pentaho.di.core.geospatial.OGRReader;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMeta;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.ogrfileinput.Messages;
import org.w3c.dom.Node;


public class OGRFileInputMeta extends BaseStepMeta implements StepMetaInterface
{
	private String 	gisFileName;
	private String 	connectionString;
	private String layerName;
	private String 	spatialFilter;
	private String 	attributeFilter;
	private int 	rowLimit;
	private boolean skipFailureAdded,rowNrAdded;
	private String  rowNrField;


	public OGRFileInputMeta()
	{
		super(); // allocate BaseStepMeta
	}

	/**
	 * @return Returns the connectionString.
	 */
	public String getConnectionString() {
		return connectionString;
	}

	/**
	 * @param connectionString The connectionString to set.
	 */
	public void setConnectionString(String connectionString) {
		this.connectionString = connectionString;
	}
	
	/**
	 * @return Returns the layerName.
	 */
	public String getLayerName() {
		return layerName;
	}

	/**
	 * @param layerName The layerName to set.
	 */
	public void setLayerName(String layerName) {
		this.layerName = layerName;
	}

	/**
	 * @return Returns the spatialFilter.
	 */
	public String getSpatialFilter() {
		return spatialFilter;
	}

	/**
	 * @param spatialFilter The spatialFilter to set.
	 */
	public void setSpatialFilter(String spatialFilter) {
		this.spatialFilter = spatialFilter;
	}

	/**
	 * @return Returns the attributeFilter.
	 */
	public String getAttributeFilter() {
		return attributeFilter;
	}

	/**
	 * @param attributeFilter The attributeFilter to set.
	 */
	public void setAttributeFilter(String attributeFilter) {
		this.attributeFilter = attributeFilter;
	}

	/**
	 * @return Returns the gisFileName.
	 */
	public String getGisFileName()
	{
		return gisFileName;
	}

	/**
	 * @param gisFileName The gisFileName to set.
	 */
	public void setGisFileName(String gisFileName)
	{
		this.gisFileName = gisFileName;
	}

	/**
	 * @return Returns the rowLimit.
	 */
	public int getRowLimit()
	{
		return rowLimit;
	}

	/**
	 * @param rowLimit The rowLimit to set.
	 */
	public void setRowLimit(int rowLimit)
	{
		this.rowLimit = rowLimit;
	}

	/**
	 * @return Returns the rowNrField.
	 */
	public String getRowNrField()
	{
		return rowNrField;
	}

	/**
	 * @param rowNrField The rowNrField to set.
	 */
	public void setRowNrField(String rowNrField)
	{
		this.rowNrField = rowNrField;
	}

	/**
	 * @return Returns the rowNrAdded.
	 */
	public boolean isRowNrAdded()
	{
		return rowNrAdded;
	}

	/**
	 * @param rowNrAdded The rowNrAdded to set.
	 */
	public void setRowNrAdded(boolean rowNrAdded)
	{
		this.rowNrAdded = rowNrAdded;
	}

	/**
	 * @return Returns the skipFailureAdded.
	 */
	public boolean isSkipFailureAdded() {
		return skipFailureAdded;
	}

	/**
	 * @param skipFailureAdded The skipFailureAdded to set.
	 */
	public void setSkipFailureAdded(boolean skipFailureAdded) {
		this.skipFailureAdded = skipFailureAdded;
	}

	public void loadXML(Node stepnode, List<DatabaseMeta> databases, Map<String, Counter> counters) throws KettleXMLException {
		readData(stepnode);
	}

	public Object clone()
	{
		OGRFileInputMeta retval = (OGRFileInputMeta)super.clone();
		return retval;
	}

	private void readData(Node stepnode)
	throws KettleXMLException
	{
		try
		{
			gisFileName        = XMLHandler.getTagValue(stepnode, "file_gis"); //$NON-NLS-1$
			connectionString   = XMLHandler.getTagValue(stepnode, "connection_string"); //$NON-NLS-1$
			layerName          = XMLHandler.getTagValue(stepnode, "layer_name"); //$NON-NLS-1$
			spatialFilter      = XMLHandler.getTagValue(stepnode, "spatial_filter"); //$NON-NLS-1$
			attributeFilter    = XMLHandler.getTagValue(stepnode, "attribute_filter"); //$NON-NLS-1$
			skipFailureAdded   = "Y".equalsIgnoreCase(XMLHandler.getTagValue(stepnode, "skip_failure")); //$NON-NLS-1$ //$NON-NLS-2$
			rowLimit           = Const.toInt(XMLHandler.getTagValue(stepnode, "limit"), 0); //$NON-NLS-1$
			rowNrAdded         = "Y".equalsIgnoreCase(XMLHandler.getTagValue(stepnode, "add_rownr")); //$NON-NLS-1$ //$NON-NLS-2$
			rowNrField         = XMLHandler.getTagValue(stepnode, "field_rownr"); //$NON-NLS-1$

		}
		catch(Exception e)
		{
			throw new KettleXMLException(Messages.getString("OGRFileInputMeta.Exception.UnableToReadStepInformationFromXML"), e); //$NON-NLS-1$
		}
	}

	public void setDefault()
	{
		gisFileName      = null;
		connectionString = null;
		layerName        = null;
		spatialFilter    = null;
		attributeFilter  = null;
		skipFailureAdded = false;
		rowLimit         = 0;
		rowNrAdded       = false;
		rowNrField       = null;
	}


	public RowMetaInterface getOutputFields(FileInputList files, String cnxString, String name)
	throws KettleStepException
	{
		RowMetaInterface rowMeta = new RowMeta();

		if (files.nrOfFiles()==0 && cnxString==null)
		{
			throw new KettleStepException(Messages.getString("OGRFileInputMeta.Exception.NoFilesFoundToProcess")); //$NON-NLS-1$
		}

		// Take the first file to determine what the layout is...
		//
		OGRReader ogrReader = null;
		try
		{
			if (files.nrOfFiles()!=0) {	
				String ogr_path = files.getFile(0).getURL().getPath();

				if (Const.isWindows()) {
					ogr_path = ogr_path.substring(3).replace('/', '\\');
					ogrReader = new OGRReader(ogr_path, layerName, spatialFilter, attributeFilter, skipFailureAdded);
				} else {
					ogr_path = ogr_path.substring(2);
					ogrReader = new OGRReader(ogr_path, layerName, spatialFilter, attributeFilter, skipFailureAdded);
				}
			}

			if (cnxString!=null && !(cnxString.trim().equals("")))
				ogrReader = new OGRReader(cnxString, layerName, spatialFilter, attributeFilter, skipFailureAdded);

			ogrReader.open();
			RowMetaInterface add = ogrReader.getFields();
			for (int i=0;i<add.size();i++)
			{
				ValueMetaInterface v=add.getValueMeta(i);
				v.setOrigin(name);
			}
			rowMeta.addRowMeta( add );
		}
		catch(Exception ke)
		{
			throw new KettleStepException(Messages.getString("OGRFileInputMeta.Exception.UnableToReadMetaDataFromGISFile"), ke); //$NON-NLS-1$
		}
		finally
		{
			if (ogrReader!=null) ogrReader.close();
		}

		if (rowNrAdded && rowNrField!=null && rowNrField.length()>0)
		{
			ValueMetaInterface rnr = new ValueMeta(rowNrField, ValueMetaInterface.TYPE_INTEGER);
			rnr.setOrigin(name);
			rowMeta.addValueMeta(rnr);
		}

		return rowMeta;
	}

	@Override
	public void getFields(RowMetaInterface row, String name, RowMetaInterface[] info, StepMeta nextStep, VariableSpace space) throws KettleStepException {

		FileInputList fileList = getTextFileList(space);
		if (fileList.nrOfFiles()==0 && (getConnectionString()==null || getConnectionString().trim().equals("")))
		{
			throw new KettleStepException(Messages.getString("XBaseInputMeta.Exception.NoFilesFoundToProcess")); //$NON-NLS-1$
		}

		row.addRowMeta( getOutputFields(fileList, getConnectionString(), name) );
	}


	public String getXML()
	{
		StringBuffer retval=new StringBuffer();

		retval.append("    " + XMLHandler.addTagValue("file_gis",    gisFileName)); //$NON-NLS-1$ //$NON-NLS-2$
		retval.append("    " + XMLHandler.addTagValue("connection_string",    connectionString)); //$NON-NLS-1$ //$NON-NLS-2$
		retval.append("    " + XMLHandler.addTagValue("layer_name",    layerName));
		retval.append("    " + XMLHandler.addTagValue("spatial_filter",    spatialFilter)); //$NON-NLS-1$ //$NON-NLS-2$
		retval.append("    " + XMLHandler.addTagValue("attribute_filter",    attributeFilter)); //$NON-NLS-1$ //$NON-NLS-2$
		retval.append("    " + XMLHandler.addTagValue("skip_failure",skipFailureAdded)); //$NON-NLS-1$ //$NON-NLS-2$
		retval.append("    " + XMLHandler.addTagValue("limit",       rowLimit)); //$NON-NLS-1$ //$NON-NLS-2$
		retval.append("    " + XMLHandler.addTagValue("add_rownr",   rowNrAdded)); //$NON-NLS-1$ //$NON-NLS-2$
		retval.append("    " + XMLHandler.addTagValue("field_rownr", rowNrField)); //$NON-NLS-1$ //$NON-NLS-2$

		return retval.toString();
	}

	public void readRep(Repository rep, long id_step, List<DatabaseMeta> databases, Map<String, Counter> counters)
	throws KettleException
	{
		try
		{
			gisFileName      = rep.getStepAttributeString (id_step, "file_gis"); //$NON-NLS-1$
			connectionString = rep.getStepAttributeString (id_step, "connection_string"); //$NON-NLS-1$
			layerName        = rep.getStepAttributeString (id_step, "layer_name");
			spatialFilter    = rep.getStepAttributeString (id_step, "spatial_filter"); //$NON-NLS-1$
			attributeFilter  = rep.getStepAttributeString (id_step, "attribute_filter"); //$NON-NLS-1$
			skipFailureAdded = rep.getStepAttributeBoolean(id_step, "skip_failure"); //$NON-NLS-1$
			rowLimit         = (int)rep.getStepAttributeInteger(id_step, "limit"); //$NON-NLS-1$
			rowNrAdded       = rep.getStepAttributeBoolean(id_step, "add_rownr"); //$NON-NLS-1$
			rowNrField       = rep.getStepAttributeString (id_step, "field_rownr"); //$NON-NLS-1$

		}
		catch(Exception e)
		{
			throw new KettleException(Messages.getString("OGRFileInputMeta.Exception.UnexpectedErrorReadingMetaDataFromRepository"), e); //$NON-NLS-1$
		}
	}

	public void saveRep(Repository rep, long id_transformation, long id_step)
	throws KettleException
	{
		try
		{
			rep.saveStepAttribute(id_transformation, id_step, "file_gis",          gisFileName); //$NON-NLS-1$
			rep.saveStepAttribute(id_transformation, id_step, "connection_string", connectionString); //$NON-NLS-1$
			rep.saveStepAttribute(id_transformation, id_step, "layer_name",        layerName);
			rep.saveStepAttribute(id_transformation, id_step, "spatial_filter",    spatialFilter); //$NON-NLS-1$
			rep.saveStepAttribute(id_transformation, id_step, "attribute_filter",  attributeFilter); //$NON-NLS-1$
			rep.saveStepAttribute(id_transformation, id_step, "skip_failure",      skipFailureAdded); //$NON-NLS-1$
			rep.saveStepAttribute(id_transformation, id_step, "limit",             rowLimit); //$NON-NLS-1$
			rep.saveStepAttribute(id_transformation, id_step, "add_rownr",         rowNrAdded); //$NON-NLS-1$
			rep.saveStepAttribute(id_transformation, id_step, "field_rownr",       rowNrField); //$NON-NLS-1$

		}
		catch(Exception e)
		{
			throw new KettleException(Messages.getString("OGRFileInputMeta.Exception.UnableToSaveMetaDataToRepository")+id_step, e); //$NON-NLS-1$
		}
	}

	public void check(List<CheckResultInterface> remarks, TransMeta transMeta, StepMeta stepMeta, RowMetaInterface prev,
			String[] input, String[] output, RowMetaInterface info)
	{
		CheckResult cr;

		if (gisFileName==null && (connectionString==null || connectionString.trim().equals("")))
		{		
			cr = new CheckResult(CheckResult.TYPE_RESULT_ERROR, Messages.getString("OGRFileInputMeta.Remark.PleaseSelectFileToUse"), stepMeta); //$NON-NLS-1$
			remarks.add(cr);
		}
		else
		{
			cr = new CheckResult(CheckResult.TYPE_RESULT_OK, Messages.getString("OGRFileInputMeta.Remark.FileToUseIsSpecified"), stepMeta); //$NON-NLS-1$
			remarks.add(cr);

			OGRReader ogrReader = null;
			try
			{
				if (gisFileName!=null) {
					String ogr_path = getURLfromFileName(transMeta.environmentSubstitute(gisFileName)).getPath();
					if (Const.isWindows()) {
						ogr_path = ogr_path.substring(3).replace('/', '\\');
						ogrReader = new OGRReader(ogr_path, layerName, spatialFilter, attributeFilter, skipFailureAdded);
					} else {
						ogr_path = ogr_path.substring(2);
						ogrReader = new OGRReader(ogr_path, layerName, spatialFilter, attributeFilter, skipFailureAdded);
					}
				}
				
				if (connectionString!=null && !(connectionString.trim().equals(""))) {
					ogrReader = new OGRReader(connectionString, layerName, spatialFilter, attributeFilter, skipFailureAdded);
				}

				ogrReader.open();
				cr = new CheckResult(CheckResult.TYPE_RESULT_OK, Messages.getString("OGRFileInputMeta.Remark.FileExistsAndCanBeOpened"), stepMeta); //$NON-NLS-1$
				remarks.add(cr);

				RowMetaInterface r = ogrReader.getFields();

				cr = new CheckResult(CheckResult.TYPE_RESULT_OK, r.size()+Messages.getString("OGRFileInputMeta.Remark.OutputFieldsCouldBeDetermined"), stepMeta); //$NON-NLS-1$
				remarks.add(cr);
			}
			catch(KettleException ke)
			{
				cr = new CheckResult(CheckResult.TYPE_RESULT_ERROR, Messages.getString("OGRFileInputMeta.Remark.NoFieldsCouldBeFoundInFileBecauseOfError")+Const.CR+ke.getMessage(), stepMeta); //$NON-NLS-1$
				remarks.add(cr);
			}
			finally
			{
				if (ogrReader != null) ogrReader.close();
			}
		}
	}

	public StepInterface getStep(StepMeta stepMeta, StepDataInterface stepDataInterface, int cnr, TransMeta tr, Trans trans)
	{
		return new OGRFileInput(stepMeta, stepDataInterface, cnr, tr, trans);
	}

	public StepDataInterface getStepData()
	{
		return new OGRFileInputData();
	}

	public FileInputList getTextFileList(VariableSpace space)
	{
		return FileInputList.createFileList(space, new String[] { gisFileName }, new String[] { null }, new String[] { "N" });
	}

	private java.net.URL getURLfromFileName(String filename) {
		try {
			return (new java.io.File(filename)).toURI().toURL();
		}
		catch (java.net.MalformedURLException urle) {
			// logError(Messages.getString("GISFileInput.Log.Error.MalformedURL"));
		}
		return null;
	}

}
