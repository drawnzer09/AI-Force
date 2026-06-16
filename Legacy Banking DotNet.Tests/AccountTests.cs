using NUnit.Framework;

namespace Banking.Tests
{
    [TestFixture]
    public sealed class AccountTests
    {
        [Test]
        public void AccountConstants_ShouldExposeExpectedValues()
        {
            Assert.Multiple(() =>
            {
                Assert.That(Account.TYPE_CHECKING, Is.EqualTo("CHECKING"));
                Assert.That(Account.TYPE_SAVINGS, Is.EqualTo("SAVINGS"));
                Assert.That(Account.STATUS_ACTIVE, Is.EqualTo("ACTIVE"));
                Assert.That(Account.STATUS_FROZEN, Is.EqualTo("FROZEN"));
            });
        }

        [Test]
        public void DefaultConstructor_ShouldInitializePropertiesToDefaultValues()
        {
            var account = new Account();

            Assert.Multiple(() =>
            {
                Assert.That(account.Id, Is.EqualTo(0));
                Assert.That(account.Owner, Is.Null);
                Assert.That(account.Type, Is.Null);
                Assert.That(account.Balance, Is.EqualTo(0.0d));
                Assert.That(account.Status, Is.Null);
            });
        }

        [Test]
        public void ParameterizedConstructor_ShouldAssignAllProperties()
        {
            const int id = 101;
            const string owner = "Jane Doe";
            const string type = Account.TYPE_CHECKING;
            const double balance = 2500.75d;
            const string status = Account.STATUS_ACTIVE;

            var account = new Account(id, owner, type, balance, status);

            Assert.Multiple(() =>
            {
                Assert.That(account.Id, Is.EqualTo(id));
                Assert.That(account.Owner, Is.EqualTo(owner));
                Assert.That(account.Type, Is.EqualTo(type));
                Assert.That(account.Balance, Is.EqualTo(balance));
                Assert.That(account.Status, Is.EqualTo(status));
            });
        }

        [Test]
        public void PropertySetters_ShouldUpdateValues()
        {
            var account = new Account
            {
                Id = 202,
                Owner = "John Smith",
                Type = Account.TYPE_SAVINGS,
                Balance = 9876.50d,
                Status = Account.STATUS_FROZEN
            };

            Assert.Multiple(() =>
            {
                Assert.That(account.Id, Is.EqualTo(202));
                Assert.That(account.Owner, Is.EqualTo("John Smith"));
                Assert.That(account.Type, Is.EqualTo(Account.TYPE_SAVINGS));
                Assert.That(account.Balance, Is.EqualTo(9876.50d));
                Assert.That(account.Status, Is.EqualTo(Account.STATUS_FROZEN));
            });
        }

        [Test]
        public void ToString_ShouldReturnExpectedFormat()
        {
            const int id = 303;
            const string owner = "Alice Johnson";
            const string type = Account.TYPE_CHECKING;
            const double balance = 123.45d;
            const string status = Account.STATUS_ACTIVE;

            var account = new Account(id, owner, type, balance, status);

            var expected =
                "Account[Id=" + id +
                ", Owner=" + owner +
                ", Type=" + type +
                ", Balance=" + balance + "]";

            var actual = account.ToString();

            Assert.That(actual, Is.EqualTo(expected));
        }

        [Test]
        public void ToString_ShouldReflectUpdatedPropertyValues()
        {
            var account = new Account(1, "Initial Owner", Account.TYPE_CHECKING, 10.0d, Account.STATUS_ACTIVE)
            {
                Id = 404,
                Owner = "Updated Owner",
                Type = Account.TYPE_SAVINGS,
                Balance = 4321.99d,
                Status = Account.STATUS_FROZEN
            };

            var expected =
                "Account[Id=" + account.Id +
                ", Owner=" + account.Owner +
                ", Type=" + account.Type +
                ", Balance=" + account.Balance + "]";

            var actual = account.ToString();

            Assert.That(actual, Is.EqualTo(expected));
        }

        [Test]
        public void ToString_ShouldNotIncludeStatusBasedOnCurrentImplementation()
        {
            var account = new Account(
                id: 505,
                owner: "Frozen Account Owner",
                type: Account.TYPE_SAVINGS,
                balance: 1000.0d,
                status: Account.STATUS_FROZEN);

            var actual = account.ToString();

            Assert.Multiple(() =>
            {
                Assert.That(actual, Does.Not.Contain("Status="));
                Assert.That(actual, Does.Not.Contain(Account.STATUS_FROZEN));
            });
        }
    }
}
