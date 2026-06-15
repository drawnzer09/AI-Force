using System;
using System.Collections.Generic;
using System.Text;
using Newtonsoft.Json;

namespace Banking
{
    public class Report
    {
        // Non-thread-safe mutable static state
        private static string _lastRunDate = null;

        public static void Accounts(List<Account> list)
        {
            Console.WriteLine("+----+------------------+----------+-------------+--------+");
            Console.WriteLine("| ID | Owner            | Type     |     Balance | Status |");
            Console.WriteLine("+----+------------------+----------+-------------+--------+");
            foreach (Account a in list)
            {
                // string.Format instead of interpolation (pre-C# 6 style)
                Console.WriteLine(string.Format("| {0,-2} | {1,-16} | {2,-8} | {3,11:F2} | {4,-6} |",
                    a.Id, a.Owner, a.Type, a.Balance, a.Status));
            }
            Console.WriteLine("+----+------------------+----------+-------------+--------+");
        }

        public static void Transactions(int accountId, List<string[]> rows)
        {
            Console.WriteLine("Transactions for account #" + accountId + ":");
            Console.WriteLine("+-------------+----------+------------------------------------+");
            Console.WriteLine("| Kind        |   Amount | Note                               |");
            Console.WriteLine("+-------------+----------+------------------------------------+");
            foreach (string[] r in rows)
            {
                Console.WriteLine(string.Format("| {0,-11} | {1,8:F2} | {2,-34} |",
                    r[0], double.Parse(r[1]), r[2]));
            }
            Console.WriteLine("+-------------+----------+------------------------------------+");
        }

        public static void Statement(string owner, List<Account> all)
        {
            // DateTime.Now — not timezone-aware, not DateTimeOffset (dotnet migration target)
            _lastRunDate = DateTime.Now.ToString("dd.MM.yyyy HH:mm");

            Console.WriteLine("=== Statement  |  " + owner + "  |  " + _lastRunDate + " ===");

            double total = 0;
            foreach (Account a in all)
            {
                if (a.Owner == owner)
                {
                    // String concatenation in loop — O(n) allocations (dotnet-022)
                    Console.WriteLine("  " + a.Type + " #" + a.Id + "  EUR " + string.Format("{0:N2}", a.Balance));
                    total += a.Balance;
                }
            }
            Console.WriteLine("  Total             EUR " + string.Format("{0:N2}", total));
            Console.WriteLine(new string('=', 55));
        }

        // Newtonsoft.Json instead of System.Text.Json (dotnet-009)
        public static void ExportJson(List<Account> accounts)
        {
            // StringBuilder used manually instead of being done by the serializer
            StringBuilder sb = new StringBuilder();
            sb.Append("--- Account Export (JSON) ---\n");

            string json = JsonConvert.SerializeObject(accounts, Formatting.Indented,
                new JsonSerializerSettings
                {
                    NullValueHandling = NullValueHandling.Ignore
                });

            sb.Append(json);
            Console.WriteLine(sb.ToString());
        }
    }
}
