using Banking;
using NUnit.Framework;

namespace Banking.Tests;

[TestFixture]
public sealed class AccountTests
{
    [Test]
    public void DefaultConstructor_ShouldInitializeClrDefaultValues()
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
    public void ParameterizedConstructor_ShouldPopulateAllProperties()
    {
        var account = new Account(
            id: 101,
            owner: "Jane Doe",
            type: Account.TYPE_CHECKING,
            balance: 2500.75d,
            status: Account.STATUS_ACTIVE);

        Assert.Multiple(() =>
        {
            Assert.That(account.Id, Is.EqualTo(101));
            Assert.That(account.Owner, Is.EqualTo("Jane Doe"));
            Assert.That(account.Type, Is.EqualTo(Account.TYPE_CHECKING));
            Assert.That(account.Balance, Is.EqualTo(2500.75d));
            Assert.That(account.Status, Is.EqualTo(Account.STATUS_ACTIVE));
        });
    }

    [Test]
    public void Properties_ShouldAllowGetAndSet()
    {
        var account = new Account();

        account.Id = 202;
        account.Owner = "John Smith";
        account.Type = Account.TYPE_SAVINGS;
        account.Balance = 9876.54d;
        account.Status = Account.STATUS_FROZEN;

        Assert.Multiple(() =>
        {
            Assert.That(account.Id, Is.EqualTo(202));
            Assert.That(account.Owner, Is.EqualTo("John Smith"));
            Assert.That(account.Type, Is.EqualTo(Account.TYPE_SAVINGS));
            Assert.That(account.Balance, Is.EqualTo(9876.54d));
            Assert.That(account.Status, Is.EqualTo(Account.STATUS_FROZEN));
        });
    }

    [Test]
    public void Constants_ShouldExposeExpectedStringValues()
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
    [SetCulture("en-US")]
    public void ToString_ShouldReturnExpectedFormat()
    {
        var account = new Account(
            id: 303,
            owner: "Alice Walker",
            type: Account.TYPE_CHECKING,
            balance: 123.45d,
            status: Account.STATUS_ACTIVE);

        var result = account.ToString();

        Assert.That(
            result,
            Is.EqualTo("Account[Id=303, Owner=Alice Walker, Type=CHECKING, Balance=123.45]"));
    }

    [Test]
    [SetCulture("en-US")]
    public void ToString_ShouldReflectUpdatedPropertyValues()
    {
        var account = new Account();

        account.Id = 404;
        account.Owner = "Updated Owner";
        account.Type = Account.TYPE_SAVINGS;
        account.Balance = 456.78d;
        account.Status = Account.STATUS_FROZEN;

        var result = account.ToString();

        Assert.That(
            result,
            Is.EqualTo("Account[Id=404, Owner=Updated Owner, Type=SAVINGS, Balance=456.78]"));
    }

    [Test]
    [SetCulture("en-US")]
    public void ToString_ShouldNotIncludeStatus()
    {
        var account = new Account(
            id: 505,
            owner: "Status Hidden",
            type: Account.TYPE_CHECKING,
            balance: 10.0d,
            status: Account.STATUS_FROZEN);

        var result = account.ToString();

        Assert.Multiple(() =>
        {
            Assert.That(result, Does.Not.Contain(Account.STATUS_FROZEN));
            Assert.That(result, Does.Not.Contain("Status"));
            Assert.That(result, Is.EqualTo("Account[Id=505, Owner=Status Hidden, Type=CHECKING, Balance=10]"));
        });
    }

    [Test]
    public void Constructor_ShouldAllowNegativeBalance_WhenNoValidationExists()
    {
        var account = new Account(
            id: 606,
            owner: "Overdrawn Customer",
            type: Account.TYPE_CHECKING,
            balance: -25.50d,
            status: Account.STATUS_ACTIVE);

        Assert.That(account.Balance, Is.EqualTo(-25.50d));
    }

    [Test]
    public void Constructor_ShouldAllowUnknownTypeAndStatus_WhenNoValidationExists()
    {
        var account = new Account(
            id: 707,
            owner: "Flexible Customer",
            type: "MONEY_MARKET",
            balance: 1000.00d,
            status: "PENDING_REVIEW");

        Assert.Multiple(() =>
        {
            Assert.That(account.Type, Is.EqualTo("MONEY_MARKET"));
            Assert.That(account.Status, Is.EqualTo("PENDING_REVIEW"));
        });
    }

    [Test]
    public void Properties_ShouldAllowNullStringValues_WhenNoValidationExists()
    {
        var account = new Account(
            id: 808,
            owner: "Temporary Owner",
            type: Account.TYPE_SAVINGS,
            balance: 50.00d,
            status: Account.STATUS_ACTIVE);

        account.Owner = null!;
        account.Type = null!;
        account.Status = null!;

        Assert.Multiple(() =>
        {
            Assert.That(account.Owner, Is.Null);
            Assert.That(account.Type, Is.Null);
            Assert.That(account.Status, Is.Null);
        });
    }

    [Test]
    [SetCulture("en-US")]
    public void ToString_ShouldHandleNullStringProperties_UsingCurrentImplementation()
    {
        var account = new Account(
            id: 909,
            owner: null!,
            type: null!,
            balance: 0.0d,
            status: null!);

        var result = account.ToString();

        Assert.That(
            result,
            Is.EqualTo("Account[Id=909, Owner=, Type=, Balance=0]"));
    }
}
