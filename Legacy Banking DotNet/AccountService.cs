using System;
using System.Collections.Generic;
using System.Net.Http;
using Microsoft.Data.Sqlite;

namespace Banking
{
    public class AccountService
    {
        // Business rules buried as magic numbers
        private const double MIN_BALANCE   = 100.00;
        private const double OVERDRAFT_FEE =  30.00;
        private const double TRANSFER_FEE  =  10.00;

        // Direct instantiation — no dependency injection
        private DatabaseManager _db = DatabaseManager.GetInstance();

        // Static HttpClient — DNS refresh issue (dotnet-006 anti-pattern)
        private static readonly HttpClient _httpClient = new HttpClient();

        // ── Account operations ────────────────────────────────────────

        public int OpenAccount(string owner, string type, double deposit)
        {
            int id = -1;
            SqliteCommand cmd = null;
            try
            {
                // String concatenation SQL — SQL injection vulnerability (dotnet-013)
                string sql = "INSERT INTO ACCOUNT(OWNER,TYPE,BALANCE) VALUES('"
                    + owner + "','" + type + "'," + deposit + ")";

                cmd = _db.GetConnection().CreateCommand();
                cmd.CommandText = sql;
                cmd.ExecuteNonQuery();

                cmd.CommandText = "SELECT last_insert_rowid()";
                id = Convert.ToInt32(cmd.ExecuteScalar());

                RecordTxn(id, "DEPOSIT", deposit, "Initial deposit");
                Console.WriteLine("[Service] Opened " + type + " account #" + id + " for " + owner);
            }
            catch (Exception ex)
            {
                // Catching base Exception — swallows everything
                Console.WriteLine("ERROR OpenAccount: " + ex.Message);
            }
            finally
            {
                if (cmd != null) cmd.Dispose();
            }
            return id;
        }

        public Account FindAccount(int id)
        {
            SqliteCommand    cmd    = null;
            SqliteDataReader reader = null;
            try
            {
                cmd = _db.GetConnection().CreateCommand();
                cmd.CommandText = "SELECT * FROM ACCOUNT WHERE ID=" + id;
                reader = cmd.ExecuteReader();
                if (reader.Read()) return MapAccount(reader);
            }
            catch (Exception ex)
            {
                Console.WriteLine("ERROR FindAccount: " + ex.Message);
            }
            finally
            {
                if (reader != null) reader.Close();
                if (cmd    != null) cmd.Dispose();
            }
            return null;
        }

        public List<Account> AllAccounts()
        {
            var list = new List<Account>();
            SqliteCommand    cmd    = null;
            SqliteDataReader reader = null;
            try
            {
                cmd = _db.GetConnection().CreateCommand();
                cmd.CommandText = "SELECT * FROM ACCOUNT ORDER BY ID";
                reader = cmd.ExecuteReader();
                while (reader.Read()) list.Add(MapAccount(reader));
            }
            catch (Exception ex)
            {
                Console.WriteLine("ERROR AllAccounts: " + ex.Message);
            }
            finally
            {
                if (reader != null) reader.Close();
                if (cmd    != null) cmd.Dispose();
            }
            return list;
        }

        // ── Transaction operations ────────────────────────────────────

        public bool Deposit(int id, double amount, string note)
        {
            Account account = FindAccount(id);
            if (account == null || amount <= 0) return false;

            // Non-atomic: two separate writes, no DB transaction wrapper
            double newBalance = account.Balance + amount;
            UpdateBalance(id, newBalance);
            RecordTxn(id, "DEPOSIT", amount, note);
            Console.WriteLine("[Service] Deposit " + amount + " -> #" + id
                + " | balance: " + newBalance);
            return true;
        }

        public bool Withdraw(int id, double amount, string note)
        {
            Account account = FindAccount(id);
            if (account == null || amount <= 0) return false;

            if (account.Balance - amount < MIN_BALANCE)
            {
                // Silent overdraft fee — customer not notified
                Console.WriteLine("[Service] Insufficient funds on #" + id + ". Overdraft fee applied.");
                UpdateBalance(id, account.Balance - OVERDRAFT_FEE);
                RecordTxn(id, "FEE", OVERDRAFT_FEE, "Overdraft fee");
                return false;
            }

            double newBalance = account.Balance - amount;
            UpdateBalance(id, newBalance);
            RecordTxn(id, "WITHDRAWAL", amount, note);
            Console.WriteLine("[Service] Withdraw " + amount + " <- #" + id
                + " | balance: " + newBalance);
            return true;
        }

        public bool Transfer(int fromId, int toId, double amount, string note)
        {
            Console.WriteLine("[Service] Transfer " + amount + " from #" + fromId + " to #" + toId);

            // Race condition: no wrapping DB transaction (dotnet-013)
            bool ok = Withdraw(fromId, amount, "Transfer out: " + note);
            if (ok)
            {
                Deposit(toId, amount, "Transfer in: " + note);
                Account updated = FindAccount(fromId);
                if (updated != null)
                {
                    UpdateBalance(fromId, updated.Balance - TRANSFER_FEE);
                    RecordTxn(fromId, "FEE", TRANSFER_FEE, "Transfer fee");
                }
            }
            return ok;
        }

        public void ApplyInterest(int id, double annualRate)
        {
            Account account = FindAccount(id);
            if (account == null || account.Type != Account.TYPE_SAVINGS) return;

            double interest = account.Balance * (annualRate / 12);
            UpdateBalance(id, account.Balance + interest);
            RecordTxn(id, "INTEREST", interest, "Monthly interest @ " + (annualRate * 100) + "% p.a.");
            Console.WriteLine("[Service] Interest " + interest + " applied to savings #" + id);
        }

        public List<string[]> TxnHistory(int accountId)
        {
            var rows = new List<string[]>();
            SqliteCommand    cmd    = null;
            SqliteDataReader reader = null;
            try
            {
                cmd = _db.GetConnection().CreateCommand();
                cmd.CommandText = "SELECT KIND,AMOUNT,NOTE,TXN_DATE FROM TXN WHERE ACCOUNT_ID="
                    + accountId + " ORDER BY TXN_DATE DESC";
                reader = cmd.ExecuteReader();
                while (reader.Read())
                {
                    rows.Add(new string[]
                    {
                        reader.GetString(0),
                        reader.GetDouble(1).ToString(),
                        reader.GetString(2),
                        reader.GetString(3)
                    });
                }
            }
            catch (Exception ex)
            {
                Console.WriteLine("ERROR TxnHistory: " + ex.Message);
            }
            finally
            {
                if (reader != null) reader.Close();
                if (cmd    != null) cmd.Dispose();
            }
            return rows;
        }

        // Blocking call on async method — deadlock risk in ASP.NET Framework (dotnet-020)
        public string GetExchangeRate(string currency)
        {
            try
            {
                return _httpClient.GetStringAsync(
                    "https://open.er-api.com/v6/latest/" + currency).Result;
            }
            catch (Exception)
            {
                return "{}";
            }
        }

        // Legacy switch statement — no switch expression (dotnet-016)
        public string ClassifyTransaction(string kind)
        {
            switch (kind)
            {
                case "DEPOSIT":    return "Credit";
                case "WITHDRAWAL": return "Debit";
                case "FEE":        return "Bank charge";
                case "INTEREST":   return "Interest credit";
                case "TRANSFER":   return "Fund transfer";
                default:           return "Other";
            }
        }

        // ── Private helpers ───────────────────────────────────────────

        private void UpdateBalance(int id, double balance)
        {
            SqliteCommand cmd = null;
            try
            {
                cmd = _db.GetConnection().CreateCommand();
                cmd.CommandText = "UPDATE ACCOUNT SET BALANCE=" + balance + " WHERE ID=" + id;
                cmd.ExecuteNonQuery();
            }
            catch (Exception ex)
            {
                Console.WriteLine("ERROR UpdateBalance: " + ex.Message);
            }
            finally
            {
                if (cmd != null) cmd.Dispose();
            }
        }

        private void RecordTxn(int accountId, string kind, double amount, string note)
        {
            SqliteCommand cmd = null;
            try
            {
                cmd = _db.GetConnection().CreateCommand();
                cmd.CommandText = "INSERT INTO TXN(ACCOUNT_ID,KIND,AMOUNT,NOTE) VALUES("
                    + accountId + ",'" + kind + "'," + amount + ",'" + note + "')";
                cmd.ExecuteNonQuery();
            }
            catch (Exception ex)
            {
                Console.WriteLine("ERROR RecordTxn: " + ex.Message);
            }
            finally
            {
                if (cmd != null) cmd.Dispose();
            }
        }

        private Account MapAccount(SqliteDataReader r)
        {
            return new Account(
                Convert.ToInt32(r["ID"]),
                r["OWNER"].ToString(),
                r["TYPE"].ToString(),
                Convert.ToDouble(r["BALANCE"]),
                r["STATUS"].ToString()
            );
        }
    }
}
