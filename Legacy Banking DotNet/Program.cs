using System;
using System.Collections.Generic;

namespace Banking
{
    class Program
    {
        static void Main(string[] args)
        {
            Console.WriteLine("=== Legacy Core Banking System v1.0 (.NET) ===");

            // Direct instantiation everywhere — no dependency injection (dotnet-008)
            DatabaseManager.GetInstance().Initialize();
            AccountService svc = new AccountService();

            // Open accounts
            int chk1 = svc.OpenAccount("Hans Mueller", Account.TYPE_CHECKING, 5000.00);
            int sav1  = svc.OpenAccount("Hans Mueller", Account.TYPE_SAVINGS,  20000.00);
            int chk2  = svc.OpenAccount("Anna Schmidt", Account.TYPE_CHECKING, 3500.00);

            // Transactions
            svc.Deposit  (chk1,         2500.00, "Monthly salary");
            svc.Withdraw (chk1,          800.00, "ATM withdrawal");
            svc.Withdraw (chk1,          450.00, "Online purchase");
            svc.Transfer (chk1, chk2,   1200.00, "Rent payment");
            svc.Deposit  (sav1,         1000.00, "Bonus");
            svc.ApplyInterest(sav1, 0.03);                    // 3% p.a.
            svc.Withdraw (chk2,         5000.00, "Large withdrawal — triggers overdraft fee");

            // Reports
            Console.WriteLine("\n--- All Accounts ---");
            List<Account> all = svc.AllAccounts();
            Report.Accounts(all);

            Console.WriteLine("\n--- Transactions: Checking #" + chk1 + " ---");
            Report.Transactions(chk1, svc.TxnHistory(chk1));

            Console.WriteLine("\n--- Transactions: Savings #" + sav1 + " ---");
            Report.Transactions(sav1, svc.TxnHistory(sav1));

            Console.WriteLine("\n--- Statement: Hans Mueller ---");
            Report.Statement("Hans Mueller", all);

            Console.WriteLine("\n--- Statement: Anna Schmidt ---");
            Report.Statement("Anna Schmidt", all);

            Console.WriteLine("\n--- JSON Export ---");
            Report.ExportJson(all);
        }
    }
}
