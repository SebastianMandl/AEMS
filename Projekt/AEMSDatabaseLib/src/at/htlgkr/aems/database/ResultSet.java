package at.htlgkr.aems.database;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class ResultSet implements Iterable<Object[]> {
	
	private HashMap<Integer, Object[]> resultSet;
	
	public ResultSet(java.sql.ResultSet resultSet) throws SQLException {
		this.resultSet = new HashMap<>();
		while(resultSet.next()) {
			ArrayList<Object> list = new ArrayList<>();
			
			try {
				for(int index = 1; true; index++) {
					list.add(resultSet.getObject(index));
				}
			} catch(Exception e) {
				this.resultSet.put(resultSet.getRow() - 1, list.toArray(new Object[list.size()]));
				continue;
			}
		}
	}
	
	public Object[] getRow(int index) {
		return resultSet.get(index);
	}
	
	public Object getObject(int row, int col) {
		if(col >= resultSet.get(row).length) {
			return null;
		}
		return resultSet.get(row)[col];
	}
	
	public String getString(int row, int col) throws NumberFormatException {
		return getObject(row, col).toString();
	}
	
	public float getInteger(int row, int col) throws NumberFormatException {
		return Integer.parseInt(getString(row, col));
	}
	
	public float getFloat(int row, int col) throws NumberFormatException {
		return Float.parseFloat(getString(row, col));
	}
	
	public double getDouble(int row, int col) throws NumberFormatException {
		return Double.parseDouble(getString(row, col));
	}

	@Override
	public Iterator<Object[]> iterator() {
		return new ResultSetIterator(this);
	}
	
	public class ResultSetIterator implements Iterator<Object[]> {

		private ResultSet resultSet;
		private int currentIndex;
		
		public ResultSetIterator(ResultSet resultSet) {
			this.resultSet = resultSet;
		}
		
		@Override
		public boolean hasNext() {
			return currentIndex < resultSet.resultSet.size(); // works because of no gaps between the rows (row numers are consecutive)
		}

		@Override
		public Object[] next() {
			return resultSet.getRow(currentIndex++);
		}
		
	}
	
}
