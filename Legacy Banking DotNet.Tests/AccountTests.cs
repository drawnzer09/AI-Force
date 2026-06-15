using Banking;
using NUnit.Framework;

namespace Banking.Tests;

[TestFixture]
public sealed class AccountTests
{
    [Test]
    public void Constants_ShouldExposeExpectedLegacyStringValues()
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
    public void DefaultConstructor_ShouldCreateAccountWithDefaultValues()
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
    public void ParameterizedConstructor_ShouldAssignAllProvidedValues()
    {
        var account = new Account(
            id: 101,
            owner: "Jane Doe",
            type: Account.TYPE_SAVINGS,
            balance: 1500.75d,
            status: Account.STATUS_ACTIVE);

        Assert.Multiple(() =>
        {
            Assert.That(account.Id, Is.EqualTo(101));
            Assert.That(account.Owner, Is.EqualTo("Jane Doe"));
            Assert.That(account.Type, Is.EqualTo(Account.TYPE_SAVINGS));
            Assert.That(account.Balance, Is.EqualTo(1500.75d));
            Assert.That(account.Status, Is.EqualTo(Account.STATUS_ACTIVE));
        });
    }

    [Test]
    public void PropertySetters_ShouldUpdateAccountValues()
    {
        var account = new Account();

        account.Id = 202;
        account.Owner = "John Smith";
        account.Type = Account.TYPE_CHECKING;
        account.Balance = -25.50d;
        account.Status = Account.STATUS_FROZEN;

        Assert.Multiple(() =>
        {
            Assert.That(account.Id, Is.EqualTo(202));
            Assert.That(account.Owner, Is.EqualTo("John Smith"));
            Assert.That(account.Type, Is.EqualTo(Account.TYPE_CHECKING));
            Assert.That(account.Balance, Is.EqualTo(-25.50d));
            Assert.That(account.Status, Is.EqualTo(Account.STATUS_FROZEN));
        });
    }

    [Test]
    [SetCulture("en-US")]
    public void ToString_ShouldReturnExpectedLegacyFormat()
    {
        var account = new Account(
            id: 303,
            owner: "Alice Brown",
            type: Account.TYPE_SAVINGS,
            balance: 2345.67d,
            status: Account.STATUS_ACTIVE);

        var result = account.ToString();

        Assert.That(
            result,
            Is.EqualTo("Account[Id=303, Owner=Alice Brown, Type=SAVINGS, Balance=2345.67]"));
    }

    [Test]
    [SetCulture("en-US")]
    public void ToString_ShouldReflectUpdatedPropertyValues()
    {
        var account = new Account
        {
            Id = 404,
            Owner = "Updated Owner",
            Type = Account.TYPE_CHECKING,
            Balance = 999.99d,
            Status = Account.STATUS_FROZEN
        };

        var result = account.ToString();

        Assert.Multiple(() =>
        {
            Assert.That(
                result,
                Is.EqualTo("Account[Id=404, Owner=Updated Owner, Type=CHECKING, Balance=999.99]"));

            Assert.That(result, Does.Not.Contain("FROZEN"));
            Assert.That(result, Does.Not.Contain("Status"));
        });
    }
}
