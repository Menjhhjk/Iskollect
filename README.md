# ISKOLLECT PROJECT STRUCTURE

## REPOSITORY STRUCTURE

iskollect/
├── src/com/iskollect/
│   ├── model/                         # domain POJOs
│   │   ├── InOutLog.java              [READY]
│   │   ├── LogResult.java             [READY]
│   │   ├── Student.java               [TODO - registration module]
│   │   ├── Transaction.java           [TODO - registration module]
│   │   ├── Reward.java                [TODO - rewards module]
│   │   └── RedeemedReward.java        [TODO - rewards module]
│
│   ├── dao/                           # JDBC data access
│   │   ├── InOutLogDAO.java           [READY]
│   │   ├── StudentDAO.java            [TODO - registration module]
│   │   ├── TransactionDAO.java        [TODO - registration module]
│   │   ├── RewardDAO.java             [TODO - rewards module]
│   │   └── RedeemedRewardDAO.java    [TODO - rewards module]
│
│   ├── service/                       # business logic
│   │   ├── InOutService.java          [READY]
│   │   ├── AuthService.java           [TODO - registration module]
│   │   ├── BottleService.java         [TODO - bottle module]
│   │   ├── PointsService.java         [TODO - bottle module]
│   │   ├── StreakService.java         [TODO - bottle module]
│   │   ├── BadgeService.java          [TODO - bottle module]
│   │   ├── RewardService.java         [TODO - rewards module]
│   │   └── TransactionService.java    [TODO - rewards module]
│
│   ├── util/                          # shared utilities
│   │   ├── DBConnection.java          [READY]
│   │   ├── StudentValidator.java      [STUB]
│   │   ├── SessionManager.java        [TODO - registration module]
│   │   ├── PasswordUtil.java          [TODO - registration module]
│   │   └── CouponGenerator.java       [TODO - rewards module]
│
│   ├── exception/
│   │   ├── DatabaseException.java             [READY]
│   │   ├── DuplicateLogException.java         [READY]
│   │   ├── InvalidInputException.java         [READY]
│   │   ├── AuthException.java                 [TODO - registration module]
│   │   └── InsufficientPointsException.java   [TODO - rewards module]
│
│   ├── scheduler/
│   │   └── WeeklyResetScheduler.java  [TODO - bottle module]
│
│   └── controller/                    # JavaFX controllers
│       ├── InOutController.java       [TODO - UI module]
│       └── ... (others per SAD)
│
├── resources/
│   ├── config.properties              # gitignored DB credentials
│   └── config.properties.example      # committed template
│
├── sql/
│   ├── 01_create_inout_logs.sql       [READY]
│   └── 02_create_students.sql         [TODO - registration module]
│
├── test/com/iskollect/
│   └── InOutServiceTest.java          [READY]
│
├── pom.xml
├── .gitignore
└── README.md

## LEGEND

[READY] = Built and functional
[STUB]  = Compiles but contains temporary logic
[TODO]  = Planned for future module development

## BRANCH STRATEGY

main
Stable and tested code only.
Never commit directly.
Merge through Pull Requests only.

dev
Integration/staging branch.
Feature branches merge here first.

feature/inout-monitoring
Current development branch.
Merge after InOutServiceTest passes.

feature/student-registration
Handles StudentDAO, AuthService,
and replacement of StudentValidator stub.

feature/bottle-submission
Handles BottleService, PointsService,
StreakService, BadgeService,
WeeklyResetScheduler.

feature/rewards-redemption
Handles RewardService,
RedeemedRewardDAO,
CouponGenerator,
InsufficientPointsException.
