import java.sql.*;

/**
 * export CLASSPATH=".;ClassPath/*" Include current directory and all jars in
 * ClassPath folder
 */

public class DatabaseHandler {
	private Connection database;
	private DatabaseMetaData data;

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
			data = database.getMetaData();
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
	 * @param columns the columns to add with properties
	 */
	public void createTable(String name, String[] columns) {
		try {
			ResultSet result = database.createStatement()
					.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name=\'" + columns[0] + "\';");
			if (result.getString(1) == name) {
				System.out.println("Table " + name + " already exists!!");
				return;
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		StringBuilder statement = new StringBuilder();
		statement.append("CREATE TABLE IF NOT EXISTS " + name + " (\n");
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

	public void insertData(String table, String[] data) {
		try {
			ResultSet result = database.createStatement()
					.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name=\'" + table + "\';");
			if (!result.next()) {
				System.out.println("Table " + table + " does not exist!!");
				return;
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
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
		statement.append("INSERT INTO " + table + " VALUES(" + questionmarks.toString() + ")");
		try {
			PreparedStatement prepState = database.prepareStatement(statement.toString());
			for (int i = 0; i < data.length; i++){
				int choose = 0;
				try{
					float test = Float.parseFloat(data[i]);
					if (test != (int) test){
						choose = 1;
					}

				} catch (NumberFormatException e){
					choose = 2;
				}
				if (choose == 0){
					prepState.setInt(i+1,Integer.parseInt(data[i]));
				} else if (choose == 1){
					prepState.setFloat(i+1,Float.parseFloat(data[i]));
				} else {
					prepState.setString(i+1,data[i]);
				}

			}
			prepState.executeUpdate();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
