# Iskollect Session Updates

This document summarizes the changes made in the latest development session. It is written as a focused changelog for advisor review.

## Session Scope

The session implemented the PostgreSQL-oriented backend and controller scaffolding for the Iskollect bottle-based recycling rewards system. The work followed the provided SAD instructions while adapting the database layer to PostgreSQL instead of MySQL.

## Major Decisions

- The project was changed to PostgreSQL-specific JDBC configuration.
- Maven was configured as the build system for Java 17, JavaFX, and the PostgreSQL JDBC driver.
- Existing ingress and egress logic was preserved, but its DAO date queries were adjusted for PostgreSQL.
- JavaFX controllers were kept thin: they call services and update UI fields only.
- Registration and authentication were not fully implemented because they are identified as a separate module.

## Files Added

### Models

- `Student.java`
- `Transaction.java`
- `Reward.java`
- `RedeemedReward.java`
- `SubmitResult.java`
- `RedeemResult.java`
- `TransactionHistory.java`
- `ReportResult.java`

These classes represent the main database-backed entities and immutable result objects used by services and controllers.

### DAOs

- `StudentDAO.java`
- `TransactionDAO.java`
- `RewardDAO.java`
- `RedeemedRewardDAO.java`

The DAO classes use `PreparedStatement` and `DBConnection.getInstance().getConnection()`. SQL was written for PostgreSQL compatibility.

### Services

- `PointsService.java`
- `StreakService.java`
- `BadgeService.java`
- `BottleService.java`
- `RewardService.java`
- `TransactionService.java`
- `ReportService.java`

These services implement point calculation, bottle submission, badge and streak handling, redemption, transaction history, and reporting workflows.

### Utilities

- `SessionManager.java`
- `CouponGenerator.java`

`SessionManager` stores the current student in memory. `CouponGenerator` creates 12-character uppercase coupon codes from UUID values.

### Scheduler

- `WeeklyResetScheduler.java`

The scheduler reads and writes `system_config.last_weekly_reset`, resets weekly student stats, and uses PostgreSQL `ON CONFLICT` for the configuration upsert.

### Controllers

- `DashboardController.java`
- `BottleSubmitController.java`
- `RewardsController.java`
- `RedeemController.java`
- `TransactionController.java`
- `ProfileController.java`
- `InOutController.java`

The controllers are JavaFX-facing classes. They contain no SQL and delegate business workflows to the service or DAO layer.

### Exceptions

- `InsufficientPointsException.java`
- `AuthException.java`

`AuthException` is intentionally a stub for the future registration and authentication module.

### Configuration and Build Files

- `pom.xml`
- `.vscode/settings.json`
- `resources/config.properties`
- `sql/00_create_core_schema_postgresql.sql`

The Maven file now includes JavaFX and PostgreSQL dependencies. VS Code settings were added so the Java language server imports Maven dependencies automatically.

## Files Updated

### `DBConnection.java`

Updated from MySQL driver loading to PostgreSQL driver loading:

```java
Class.forName("org.postgresql.Driver");
```

The expected JDBC URL is now:

```text
jdbc:postgresql://localhost:5432/iskollect_db
```

### `InOutLogDAO.java`

Updated date filtering queries to PostgreSQL-compatible syntax:

```sql
timestamp::date
```

The table documentation was also adjusted away from MySQL-specific types such as `AUTO_INCREMENT` and `DATETIME`.

### `InOutServiceTest.java`

Fixed the package declaration from:

```java
package test.com.iskollect;
```

to:

```java
package com.iskollect;
```

This matches the test source root and folder path.

## PostgreSQL Schema

The new PostgreSQL schema file is:

```text
sql/00_create_core_schema_postgresql.sql
```

It creates:

- `students`
- `transactions`
- `rewards`
- `redeemed_rewards`
- `inout_logs`
- `system_config`

It also seeds the reward catalog:

- Supplies Coupon
- Snack V1 Coupon
- Snack V2 Coupon
- Lunch Coupon

## Verification Performed

The following commands were run successfully:

```bash
mvn -q -DskipTests compile
mvn -q test
```

A search was also performed for common MySQL leftovers such as:

- `jdbc:mysql`
- `com.mysql`
- `AUTO_INCREMENT`
- `DATETIME`
- `TINYINT`

No remaining matches were found in the checked source, SQL, resource, or Maven files.

## Remaining Work

- Build or connect the JavaFX FXML views.
- Replace `StudentValidator` with real student lookup logic after the registration module is available.
- Implement the full authentication and registration module separately.
- Confirm local PostgreSQL credentials in `resources/config.properties`.
- Run the PostgreSQL schema against the actual development database.
- Add broader unit and integration tests for DAO and service behavior.

## Notes

The session produced a compiling backend-oriented implementation. The database direction is now PostgreSQL-specific, and the Maven build verifies that the source compiles with the declared JavaFX and PostgreSQL dependencies. The main intentional gap is authentication and registration, which remains separate by project instruction.