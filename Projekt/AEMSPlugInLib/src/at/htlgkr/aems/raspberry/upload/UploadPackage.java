package at.htlgkr.aems.raspberry.upload;

import java.util.ArrayList;

public class UploadPackage {

	private final ArrayList<TableData> DATA = new ArrayList<>();
	
	public UploadPackage addData(TableData data) {
		DATA.add(data);
		return this;
	}
	
	public TableData[] getTableData() {
		return DATA.toArray(new TableData[DATA.size()]);
	}
	
}
