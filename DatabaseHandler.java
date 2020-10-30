import java.sql.*;
import java.util.ArrayList;

/**
 * export CLASSPATH=".;ClassPath/*" Include current directory and all jars in
 * ClassPath folder
 */

public class DatabaseHandler {
	private Connection database;
	// private DatabaseMetaData data;

	public DatabaseHandler() throws Exception {
		throw new Exception("No name or path specified for the database!");
	}

	/**
	 * @param path the path to the database in the current directiory, if in the
	 *             current directory make path="" i.e. path =/path/to/
	 * @param name name of database including .db i.e. name = name.db
	 */
	public DatabaseHandler(String path, String name) {
		openConnection(path, name);
	}

	private void openConnection(String path, String name) {
		try {
			database = DriverManager.getConnection("jdbc:sqlite:" + path + name);
			// data = database.getMetaData();
			// System.out.println("Opened database successfully");
		} catch (Exception e) {
			System.out.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
	}

	/**
	 * Create a new table with column values and properities as specified in the
	 * columns array The elements of columns should all be valid SQL statements
	 * 
	 * @param name    the name of the table, can include spaces now
	 * @param columns the columns to add with properties
	 */
	public void createTable(String name, String[] columns) {
		try {
			ResultSet result = database.createStatement()
					.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name=\'" + name + "\';");
			if (!result.isClosed() && result.getString(1).equals(name)) {
				// System.out.println("Table " + name + " already exists!!");
				return;
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		StringBuilder statement = new StringBuilder();
		statement.append("CREATE TABLE IF NOT EXISTS [" + name + "] (\n");
		for (int i = 0; i < columns.length; i++) {
			statement.append("	" + columns[i]);
			if (i < columns.length - 1)
				statement.append(",\n");
			else
				statement.append("\n");
		}
		statement.append(");");

		try {
			database.createStatement().execute(statement.toString());
		} catch (SQLException e) {
			System.out.println("Creating table failed! Columns contained invalid properties.\n" + e.getSQLState());
			System.exit(0);
		}
	}

	/**
	 * Inserts a new row of data to a table.
	 * 
	 * @param table The name of the table
	 * @param data  an array of data, no spaces at the begining or end of the
	 *              elements. Each index of the array corresponds to a column in the
	 *              sql table. Data must be in the same order as table
	 */
	public void insertRow(String table, String[] data) {
		try {
			ResultSet result = database.createStatement()
					.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name=\'" + table + "\';");
			if (result.isClosed()) {
				System.out.println("Table " + table + " does not exist!!");
				return;
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		StringBuilder statement = new StringBuilder();
		StringBuilder questionmarks = new StringBuilder();
		for (int i = 0; i < data.length; i++) {
			if (i < data.length - 1)
				questionmarks.append("?,");
			else
				questionmarks.append("?");
		}
		statement.append("INSERT INTO [" + table + "] VALUES(" + questionmarks.toString() + ")");
		try {
			PreparedStatement prepState = database.prepareStatement(statement.toString());
			for (int i = 0; i < data.length; i++) {
				int choose = 0;
				try {
					float test = Float.parseFloat(data[i]);
					if (test != (int) test) {
						choose = 1;
					}

				} catch (NumberFormatException e) {
					choose = 2;
				}
				if (choose == 0) {
					if (data[i].indexOf(".") < 0)
						prepState.setInt(i + 1, Integer.parseInt(data[i]));
					else
						prepState.setInt(i + 1, Integer.parseInt(data[i].substring(0, data[i].indexOf("."))));
				} else if (choose == 1) {
					prepState.setFloat(i + 1, Float.parseFloat(data[i]));
				} else {
					prepState.setString(i + 1, data[i]);
				}

			}
			prepState.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Query data from the database
	 * 
	 * @param table  The name of the table to select
	 * @param column The name of the column to select
	 * @param extras A valid SQL command string. Can include WHERE, ORDER BY, etc.
	 * @return an ArrayList of all the data returned by the query or null if the
	 *         query failed
	 */
	public ArrayList<String> selectData(String table, String column, String extras) {
		StringBuilder statement = new StringBuilder();
		statement.append("SELECT " + column + " FROM [" + table + "]");
		if (extras != null) {
			statement.append(" " + extras);
		}

		try {
			ResultSet result = database.createStatement().executeQuery(statement.toString());
			ArrayList<String> data = new ArrayList<String>();
			while (result.next()) {
				data.add(result.getString(1));
			}
			return data;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/** Delete rows from a table
	 * 
	 * @param table Tbe name of the table to delete from
	 * @param extras Specify WHERE, ORDER BY, etc. If using LIKE, need to add "" around string to match ex. \"%abc%\"
	 */
	public void deleteData(String table, String extras) {
		StringBuilder statement = new StringBuilder();
		statement.append("DELETE FROM [" + table + "] ");
		if (extras != null) {
			statement.append(extras);
		}
		try {
			database.createStatement().execute(statement.toString());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
