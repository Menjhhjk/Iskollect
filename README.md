# Iskollect

Iskollect is a Java desktop application for a bottle-based garbage recycling rewards system. It uses a 3-tier architecture:

- Presentation layer: JavaFX controllers and FXML views
- Business logic layer: service classes and model objects
- Data access layer: JDBC DAOs connected to PostgreSQL

This repository currently contains the backend and controller scaffolding for bottle submission, points calculation, rewards redemption, reports, transaction history, weekly reset scheduling, and ingress/egress logging.

## Technology Stack

- Java 17
- JavaFX
- JDBC
- PostgreSQL
- Maven

## Database

The project is PostgreSQL-specific.

Connection settings are read from:

```text
resources/config.properties
```

Default template:

```properties
db.url=jdbc:postgresql://localhost:5432/iskollect_db
db.user=postgres
db.password=
```

The PostgreSQL schema is in:

```text
sql/00_create_core_schema_postgresql.sql
```

The schema uses PostgreSQL-compatible features such as:

- `GENERATED ALWAYS AS IDENTITY`
- `BOOLEAN`
- `DOUBLE PRECISION`
- `ON CONFLICT`
- `timestamp::date`

## Project Structure

```text
Iskollect/
├── pom.xml
├── README.md
├── SESSION_UPDATES.md
├── resources/
│   └── config.properties
├── sql/
│   ├── 00_create_core_schema_postgresql.sql
│   └── 01_create_inout_logs.sql
│
├── src/com/iskollect/
│   ├── controller/
│   │   ├── BottleSubmitController.java       [Bottle Submission and Points]
│   │   ├── DashboardController.java          [Reports]
│   │   ├── InOutController.java              [Ingress and Egress Logging]
│   │   ├── ProfileController.java            [Bottle Submission and Points]
│   │   ├── RedeemController.java             [Rewards and Redemption]
│   │   ├── RewardsController.java            [Rewards and Redemption]
│   │   └── TransactionController.java        [Transaction History]
│   │
│   ├── dao/
│   │   ├── InOutLogDAO.java                  [Ingress and Egress Logging]
│   │   ├── RedeemedRewardDAO.java            [Rewards and Redemption]
│   │   ├── RewardDAO.java                    [Rewards and Redemption]
│   │   ├── StudentDAO.java                   [Bottle Submission and Points]
│   │   └── TransactionDAO.java               [Transaction History]
│   │
│   ├── exception/
│   │   ├── AuthException.java                [Future Authentication Module]
│   │   ├── DatabaseException.java            [Shared Infrastructure]
│   │   ├── DuplicateLogException.java        [Ingress and Egress Logging]
│   │   ├── InsufficientPointsException.java  [Rewards and Redemption]
│   │   └── InvalidInputException.java        [Shared Infrastructure]
│   │
│   ├── model/
│   │   ├── InOutLog.java                     [Ingress and Egress Logging]
│   │   ├── LogResult.java                    [Ingress and Egress Logging]
│   │   ├── RedeemedReward.java               [Rewards and Redemption]
│   │   ├── RedeemResult.java                 [Rewards and Redemption]
│   │   ├── ReportResult.java                 [Reports]
│   │   ├── Reward.java                       [Rewards and Redemption]
│   │   ├── Student.java                      [Bottle Submission and Points]
│   │   ├── SubmitResult.java                 [Bottle Submission and Points]
│   │   ├── Transaction.java                  [Transaction History]
│   │   └── TransactionHistory.java           [Transaction History]
│   │
│   ├── scheduler/
│   │   └── WeeklyResetScheduler.java         [Weekly Reset]
│   │
│   ├── service/
│   │   ├── BadgeService.java                 [Bottle Submission and Points]
│   │   ├── BottleService.java                [Bottle Submission and Points]
│   │   ├── InOutService.java                 [Ingress and Egress Logging]
│   │   ├── PointsService.java                [Bottle Submission and Points]
│   │   ├── ReportService.java                [Reports]
│   │   ├── RewardService.java                [Rewards and Redemption]
│   │   ├── StreakService.java                [Bottle Submission and Points]
│   │   └── TransactionService.java           [Transaction History]
│   │
│   └── util/
│       ├── CouponGenerator.java              [Rewards and Redemption]
│       ├── DBConnection.java                 [Shared Infrastructure]
│       ├── SessionManager.java               [Future Authentication Module]
│       └── StudentValidator.java             [Bottle Submission and Points]
│
└── test/com/iskollect/
    └── InOutServiceTest.java
```

## Implemented Modules

### Ingress and Egress Logging

- Manual ingress and egress event logging
- Duplicate event detection
- Daily log retrieval
- `LogResult` value object for controller feedback

### Bottle Submission and Points

- Bottle count validation
- Base points calculation: `bottles * 0.5`
- Streak bonus calculation
- Weekly badge bonus calculation
- Transaction insertion with point breakdown
- Student point and weekly stat updates

### Rewards and Redemption

- Rewards catalog retrieval
- Atomic redemption flow using JDBC transactions
- Coupon code generation
- Points deduction
- Redemption history retrieval

### Transaction History

- Bottle submissions and redemptions merged into one history
- Filtering by today, current week, current month, or current year

### Reports

`ReportService` supports:

- Bottle summary by student and date range
- Weekly leaderboard
- Points ledger
- Redemption report
- System summary

Report methods return `ReportResult` and convert database errors into failure results.

### Weekly Reset

`WeeklyResetScheduler` resets weekly bottle and streak data every 7 days. It stores the last reset date in the `system_config` table.

## Build and Test

Compile:

```bash
mvn -q -DskipTests compile
```

Run tests:

```bash
mvn -q test
```

Both commands passed after the latest updates.

## IDE Notes

VS Code should import the Maven project automatically. The repository includes:

```text
.vscode/settings.json
```

If JavaFX imports show as unresolved in VS Code, run:

```text
Java: Clean Java Language Server Workspace
```

Then reload the window and allow Maven dependencies to be imported.

## Known Integration Notes

- Authentication and registration are still treated as a separate module.
- `AuthException` is a stub for the registration module.
- `StudentValidator` remains a stub and currently returns `true`.
- JavaFX FXML files are expected to be wired separately to the controller fields and methods.
- `resources/config.properties` may need local database credentials before running the application.
