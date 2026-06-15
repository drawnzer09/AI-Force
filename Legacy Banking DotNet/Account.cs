namespace Banking
{
    public class Account
    {
        // String constants instead of an enum
        public const string TYPE_CHECKING = "CHECKING";
        public const string TYPE_SAVINGS  = "SAVINGS";
        public const string STATUS_ACTIVE = "ACTIVE";
        public const string STATUS_FROZEN = "FROZEN";

        // Verbose backing fields — no auto-properties, no records
        private int    _id;
        private string _owner;
        private string _type;
        private double _balance;   // double for money — should be decimal
        private string _status;

        public Account() { }

        public Account(int id, string owner, string type, double balance, string status)
        {
            _id      = id;
            _owner   = owner;
            _type    = type;
            _balance = balance;
            _status  = status;
        }

        // Explicit getters/setters (pre-C# 3.0 style)
        public int    Id      { get { return _id; }      set { _id = value; }      }
        public string Owner   { get { return _owner; }   set { _owner = value; }   }
        public string Type    { get { return _type; }    set { _type = value; }    }
        public double Balance { get { return _balance; } set { _balance = value; } }
        public string Status  { get { return _status; }  set { _status = value; }  }

        public override string ToString()
        {
            return "Account[Id=" + _id + ", Owner=" + _owner +
                   ", Type=" + _type + ", Balance=" + _balance + "]";
        }
    }
}
