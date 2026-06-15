using System;
using Microsoft.Data.Sqlite;

namespace Banking
{
    // Non-thread-safe singleton — classic legacy anti-pattern
    public class DatabaseManager
    {
        private static DatabaseManager _instance = null;
        private SqliteConnection _connection    = null;

        // Hard-coded connection string (should come from config)
        private const string CONNECTION_STRING = "Data Source=:memory:;Cache=Shared";

        private DatabaseManager() { }

        public static DatabaseManager GetInstance()
        {
            if (_instance == null)
                _instance = new DatabaseManager();
            return _instance;
        }

        public SqliteConnection GetConnection()
        {
            try
            {
                if (_connection == null)
                {
                    _connection = new SqliteConnection(CONNECTION_STRING);
                    _connection.Open();
                }
            }
            catch (Exception ex)
            {
                Console.WriteLine("DB connection error: " + ex.Message);
            }
            return _connection;
        }

        public void Initialize()
        {
            SqliteCommand cmd = null;
            try
            {
                cmd = GetConnection().CreateCommand();

                cmd.CommandText =
                    "CREATE TABLE IF NOT EXISTS ACCOUNT (" +
                    "ID      INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "OWNER   TEXT," +
                    "TYPE    TEXT," +
                    "BALANCE REAL," +
                    "STATUS  TEXT DEFAULT 'ACTIVE')";
                cmd.ExecuteNonQuery();

                cmd.CommandText =
                    "CREATE TABLE IF NOT EXISTS TXN (" +
                    "ID         INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "ACCOUNT_ID INTEGER," +
                    "KIND       TEXT," +
                    "AMOUNT     REAL," +
                    "NOTE       TEXT," +
                    "TXN_DATE   TEXT DEFAULT (datetime('now')))";
                cmd.ExecuteNonQuery();

                Console.WriteLine("Schema ready.");
            }
            catch (Exception ex)
            {
                Console.WriteLine("Schema error: " + ex.Message);
            }
            finally
            {
                // Manual cleanup — no using/try-with-resources
                if (cmd != null) cmd.Dispose();
            }
        }
    }
}
