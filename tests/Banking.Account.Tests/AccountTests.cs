using System.Globalization;
using Banking;
using NUnit.Framework;

namespace Banking.Account.Tests;

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
    public void DefaultConstructor_ShouldInitializePropertiesToClrDefaults()
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
            owner: "Alice",
            type: Account.TYPE_CHECKING,
            balance: 1250.75d,
            status: Account.STATUS_ACTIVE);

        Assert.Multiple(() =>
        {
            Assert.That(account.Id, Is.EqualTo(101));
            Assert.That(account.Owner, Is.EqualTo("Alice"));
            Assert.That(account.Type, Is.EqualTo(Account.TYPE_CHECKING));
            Assert.That(account.Balance, Is.EqualTo(1250.75d));
            Assert.That(account.Status, Is.EqualTo(Account.STATUS_ACTIVE));
        });
    }

    [Test]
    public void Properties_ShouldAllowMutationAndReturnUpdatedValues()
    {
        var account = new Account();

        account.Id = 202;
        account.Owner = "Bob";
        account.Type = Account.TYPE_SAVINGS;
        account.Balance = 999.99d;
        account.Status = Account.STATUS_FROZEN;

        Assert.Multiple(() =>
        {
            Assert.That(account.Id, Is.EqualTo(202));
            Assert.That(account.Owner, Is.EqualTo("Bob"));
            Assert.That(account.Type, Is.EqualTo(Account.TYPE_SAVINGS));
            Assert.That(account.Balance, Is.EqualTo(999.99d));
            Assert.That(account.Status, Is.EqualTo(Account.STATUS_FROZEN));
        });
    }

    [Test]
    public void StringProperties_ShouldAllowNullAssignments_BecauseNoValidationExists()
    {
        var account = new Account(
            id: 1,
            owner: "Initial Owner",
            type: Account.TYPE_CHECKING,
            balance: 100.0d,
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
    public void TypeAndStatus_ShouldAllowUnknownStringValues_BecauseNoValidationExists()
    {
        var account = new Account();

        account.Type = "MONEY_MARKET";
        account.Status = "CLOSED";

        Assert.Multiple(() =>
        {
            Assert.That(account.Type, Is.EqualTo("MONEY_MARKET"));
            Assert.That(account.Status, Is.EqualTo("CLOSED"));
        });
    }

    [TestCase(-100.00d)]
    [TestCase(0.00d)]
    [TestCase(100.00d)]
    [TestCase(999999999.99d)]
    [TestCase(double.PositiveInfinity)]
    [TestCase(double.NegativeInfinity)]
    public void Balance_ShouldAllowNegativeAndSpecialDoubleValues_BecauseNoValidationExists(double balance)
    {
        var account = new Account();

        account.Balance = balance;

        Assert.That(account.Balance, Is.EqualTo(balance));
    }

    [Test]
    public void Balance_ShouldAllowNaN_BecauseNoValidationExists()
    {
        var account = new Account();

        account.Balance = double.NaN;

        Assert.That(double.IsNaN(account.Balance), Is.True);
    }

    [Test]
    public void ToString_ShouldReturnLegacyFormat_UsingCurrentPropertyValues()
    {
        var originalCulture = CultureInfo.CurrentCulture;

        try
        {
            CultureInfo.CurrentCulture = CultureInfo.InvariantCulture;

            var account = new Account(
                id: 42,
                owner: "Charlie",
                type: Account.TYPE_SAVINGS,
                balance: 1234.56d,
                status: Account.STATUS_ACTIVE);

            var result = account.ToString();

            Assert.That(
                result,
                Is.EqualTo("Account[Id=42, Owner=Charlie, Type=SAVINGS, Balance=1234.56]"));
        }
        finally
        {
            CultureInfo.CurrentCulture = originalCulture;
        }
    }

    [Test]
    public void ToString_ShouldReflectUpdatedPropertyValues()
    {
        var originalCulture = CultureInfo.CurrentCulture;

        try
        {
            CultureInfo.CurrentCulture = CultureInfo.InvariantCulture;

            var account = new Account();

            account.Id = 77;
            account.Owner = "Dana";
            account.Type = Account.TYPE_CHECKING;
            account.Balance = 500.25d;
            account.Status = Account.STATUS_FROZEN;

            var result = account.ToString();

            Assert.That(
                result,
                Is.EqualTo("Account[Id=77, Owner=Dana, Type=CHECKING, Balance=500.25]"));
        }
        finally
        {
            CultureInfo.CurrentCulture = originalCulture;
        }
    }

    [Test]
    public void ToString_ShouldNotIncludeStatus_BecauseLegacyImplementationOmitsIt()
    {
        var account = new Account(
            id: 88,
            owner: "Eve",
            type: Account.TYPE_CHECKING,
            balance: 10.0d,
            status: Account.STATUS_FROZEN);

        var result = account.ToString();

        Assert.Multiple(() =>
        {
            Assert.That(result, Does.Not.Contain("Status"));
            Assert.That(result, Does.Not.Contain(Account.STATUS_FROZEN));
        });
    }

    [Test]
    public void ToString_ShouldUseCurrentCultureForBalance_BecauseDoubleConcatenationUsesCurrentCulture()
    {
        var originalCulture = CultureInfo.CurrentCulture;

        try
        {
            CultureInfo.CurrentCulture = CultureInfo.GetCultureInfo("fr-FR");

            var account = new Account(
                id: 5,
                owner: "François",
                type: Account.TYPE_SAVINGS,
                balance: 12.5d,
                status: Account.STATUS_ACTIVE);

            var result = account.ToString();

            Assert.That(
                result,
                Is.EqualTo("Account[Id=5, Owner=François, Type=SAVINGS, Balance=12,5]"));
        }
        finally
        {
            CultureInfo.CurrentCulture = originalCulture;
        }
    }
}

[TestFixture]
public sealed class AccountBusinessRuleSkeletonTests
{
    [Test]
    [Ignore("No US/FR acceptance criteria or implementation was provided for rejecting unsupported account types.")]
    public void Constructor_ShouldRejectUnsupportedAccountType_WhenRequirementIsDefined()
    {
        Assert.Inconclusive("Business rule not determinable from provided source code.");
    }

    [Test]
    [Ignore("No US/FR acceptance criteria or implementation was provided for rejecting unsupported account statuses.")]
    public void Constructor_ShouldRejectUnsupportedAccountStatus_WhenRequirementIsDefined()
    {
        Assert.Inconclusive("Business rule not determinable from provided source code.");
    }

    [Test]
    [Ignore("No deposit method or acceptance criteria was provided.")]
    public void Deposit_ShouldIncreaseBalance_WhenDepositFeatureIsImplemented()
    {
        Assert.Inconclusive("Deposit behavior is not implemented in Account.cs.");
    }

    [Test]
    [Ignore("No withdrawal method or acceptance criteria was provided.")]
    public void Withdraw_ShouldDecreaseBalance_WhenWithdrawalFeatureIsImplemented()
    {
        Assert.Inconclusive("Withdrawal behavior is not implemented in Account.cs.");
    }

    [Test]
    [Ignore("No transaction restriction behavior for frozen accounts was provided.")]
    public void FrozenAccount_ShouldRejectTransactions_WhenRequirementIsDefined()
    {
        Assert.Inconclusive("Frozen account transaction behavior is not implemented in Account.cs.");
    }
}
