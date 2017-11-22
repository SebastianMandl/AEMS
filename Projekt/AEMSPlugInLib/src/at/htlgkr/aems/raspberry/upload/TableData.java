package at.htlgkr.aems.raspberry.upload;

import java.util.ArrayList;
import java.util.Iterator;

public class TableData implements Iterable<ColumnData> {

	private String tableName;
	private ArrayList<ColumnData> data;
	
	public TableData(String tableName) {
		this.tableName = tableName;
		data = new ArrayList<>();
	}
	
	public TableData addData(String columnName, String value) {
		data.add(new ColumnData(columnName, value));
		return this;
	}
	
	public String getTableName() {
		return tableName;
	}

	@Override
	public Iterator<ColumnData> iterator() {
		return new TableDataIterator(data);
	}
	
	private class TableDataIterator implements Iterator<ColumnData> {

		private ArrayList<ColumnData> data;
		private int index;
		
		public TableDataIterator(ArrayList<ColumnData> data) {
			this.data = data;
		}
		
		@Override
		public boolean hasNext() {
			return index < data.size();
		}

		@Override
		public ColumnData next() {
			if(hasNext())
				return data.get(index++);
			return null;
		}
		
	}
	
	
}
