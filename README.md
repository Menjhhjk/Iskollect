<style>
  .root { padding: 1rem 0; font-family: var(--font-mono); font-size: 13px; }
  .section { margin-bottom: 1.5rem; }
  .section-title { font-family: var(--font-sans); font-size: 16px; font-weight: 500; color: var(--color-text-primary); margin: 0 0 10px 0; display: flex; align-items: center; gap: 8px; }
  .tree { background: var(--color-background-secondary); border: 1px solid var(--color-border-tertiary); border-radius: var(--border-radius-lg); padding: 14px 18px; line-height: 2; }
  .tree-line { display: flex; align-items: baseline; gap: 0; white-space: pre; }
  .indent { color: var(--color-border-secondary); }
  .folder { color: var(--color-text-info); font-weight: 500; }
  .file   { color: var(--color-text-primary); }
  .stub   { color: var(--color-text-warning); }
  .comment { color: var(--color-text-tertiary); font-size: 11.5px; margin-left: 10px; font-family: var(--font-sans); font-style: italic; }
  .badge { display: inline-block; font-family: var(--font-sans); font-size: 11px; font-weight: 500; padding: 2px 7px; border-radius: 20px; margin-left: 6px; vertical-align: middle; }
  .badge-stub   { background: #FAEEDA; color: #854F0B; }
  .badge-ready  { background: #EAF3DE; color: #3B6D11; }
  .badge-todo   { background: #E6F1FB; color: #185FA5; }

  .branches { display: grid; grid-template-columns: 1fr 1fr; gap: 10px; }
  .branch-card { background: var(--color-background-secondary); border: 1px solid var(--color-border-tertiary); border-radius: var(--border-radius-md); padding: 10px 14px; }
  .branch-name { font-family: var(--font-mono); font-size: 12.5px; font-weight: 500; color: var(--color-text-info); margin-bottom: 4px; }
  .branch-desc { font-size: 12px; color: var(--color-text-secondary); line-height: 1.5; }

  .commit-row { display: flex; gap: 10px; align-items: flex-start; padding: 7px 0; border-bottom: 1px solid var(--color-border-tertiary); }
  .commit-row:last-child { border-bottom: none; }
  .commit-tag { font-family: var(--font-mono); font-size: 11px; font-weight: 500; padding: 2px 8px; border-radius: 4px; white-space: nowrap; min-width: 60px; text-align: center; }
  .tag-feat   { background: #EAF3DE; color: #3B6D11; }
  .tag-fix    { background: #FCEBEB; color: #A32D2D; }
  .tag-stub   { background: #FAEEDA; color: #854F0B; }
  .tag-chore  { background: #F1EFE8; color: #5F5E5A; }
  .tag-test   { background: #E6F1FB; color: #185FA5; }
  .commit-text { font-size: 13px; color: var(--color-text-primary); line-height: 1.5; }
  .commit-sub  { font-size: 11.5px; color: var(--color-text-secondary); margin-top: 2px; }

  .legend { display: flex; gap: 12px; flex-wrap: wrap; margin-top: 6px; }
  .legend-item { display: flex; align-items: center; gap: 5px; font-size: 12px; color: var(--color-text-secondary); }
  .legend-dot { width: 9px; height: 9px; border-radius: 50%; flex-shrink: 0; }
</style>
<div class="root">

  <div class="section">
    <div class="section-title"><i class="ti ti-folder" aria-hidden="true"></i> Repository structure</div>
    <div class="tree">
      <div class="tree-line"><span class="folder">iskollect/</span></div>
      <div class="tree-line"><span class="indent">├── </span><span class="folder">src/com/iskollect/</span></div>
      <div class="tree-line"><span class="indent">│   ├── </span><span class="folder">model/</span><span class="comment">domain POJOs</span></div>
      <div class="tree-line"><span class="indent">│   │   ├── </span><span class="file">InOutLog.java</span><span class="badge badge-ready">ready</span></div>
      <div class="tree-line"><span class="indent">│   │   ├── </span><span class="file">LogResult.java</span><span class="badge badge-ready">ready</span></div>
      <div class="tree-line"><span class="indent">│   │   ├── </span><span class="stub">Student.java</span><span class="badge badge-todo">reg. module</span></div>
      <div class="tree-line"><span class="indent">│   │   ├── </span><span class="stub">Transaction.java</span><span class="badge badge-todo">reg. module</span></div>
      <div class="tree-line"><span class="indent">│   │   ├── </span><span class="stub">Reward.java</span><span class="badge badge-todo">rewards module</span></div>
      <div class="tree-line"><span class="indent">│   │   └── </span><span class="stub">RedeemedReward.java</span><span class="badge badge-todo">rewards module</span></div>

      <div class="tree-line"><span class="indent">│   ├── </span><span class="folder">dao/</span><span class="comment">JDBC data access</span></div>
      <div class="tree-line"><span class="indent">│   │   ├── </span><span class="file">InOutLogDAO.java</span><span class="badge badge-ready">ready</span></div>
      <div class="tree-line"><span class="indent">│   │   ├── </span><span class="stub">StudentDAO.java</span><span class="badge badge-todo">reg. module</span></div>
      <div class="tree-line"><span class="indent">│   │   ├── </span><span class="stub">TransactionDAO.java</span><span class="badge badge-todo">reg. module</span></div>
      <div class="tree-line"><span class="indent">│   │   ├── </span><span class="stub">RewardDAO.java</span><span class="badge badge-todo">rewards module</span></div>
      <div class="tree-line"><span class="indent">│   │   └── </span><span class="stub">RedeemedRewardDAO.java</span><span class="badge badge-todo">rewards module</span></div>

      <div class="tree-line"><span class="indent">│   ├── </span><span class="folder">service/</span><span class="comment">business logic</span></div>
      <div class="tree-line"><span class="indent">│   │   ├── </span><span class="file">InOutService.java</span><span class="badge badge-ready">ready</span></div>
      <div class="tree-line"><span class="indent">│   │   ├── </span><span class="stub">AuthService.java</span><span class="badge badge-todo">reg. module</span></div>
      <div class="tree-line"><span class="indent">│   │   ├── </span><span class="stub">BottleService.java</span><span class="badge badge-todo">bottle module</span></div>
      <div class="tree-line"><span class="indent">│   │   ├── </span><span class="stub">PointsService.java</span><span class="badge badge-todo">bottle module</span></div>
      <div class="tree-line"><span class="indent">│   │   ├── </span><span class="stub">StreakService.java</span><span class="badge badge-todo">bottle module</span></div>
      <div class="tree-line"><span class="indent">│   │   ├── </span><span class="stub">BadgeService.java</span><span class="badge badge-todo">bottle module</span></div>
      <div class="tree-line"><span class="indent">│   │   ├── </span><span class="stub">RewardService.java</span><span class="badge badge-todo">rewards module</span></div>
      <div class="tree-line"><span class="indent">│   │   └── </span><span class="stub">TransactionService.java</span><span class="badge badge-todo">rewards module</span></div>

      <div class="tree-line"><span class="indent">│   ├── </span><span class="folder">util/</span><span class="comment">shared utilities</span></div>
      <div class="tree-line"><span class="indent">│   │   ├── </span><span class="file">DBConnection.java</span><span class="badge badge-ready">ready</span></div>
      <div class="tree-line"><span class="indent">│   │   ├── </span><span class="file">StudentValidator.java</span><span class="badge badge-stub">stub</span></div>
      <div class="tree-line"><span class="indent">│   │   ├── </span><span class="stub">SessionManager.java</span><span class="badge badge-todo">reg. module</span></div>
      <div class="tree-line"><span class="indent">│   │   ├── </span><span class="stub">PasswordUtil.java</span><span class="badge badge-todo">reg. module</span></div>
      <div class="tree-line"><span class="indent">│   │   └── </span><span class="stub">CouponGenerator.java</span><span class="badge badge-todo">rewards module</span></div>

      <div class="tree-line"><span class="indent">│   ├── </span><span class="folder">exception/</span></div>
      <div class="tree-line"><span class="indent">│   │   ├── </span><span class="file">DatabaseException.java</span><span class="badge badge-ready">ready</span></div>
      <div class="tree-line"><span class="indent">│   │   ├── </span><span class="file">DuplicateLogException.java</span><span class="badge badge-ready">ready</span></div>
      <div class="tree-line"><span class="indent">│   │   ├── </span><span class="file">InvalidInputException.java</span><span class="badge badge-ready">ready</span></div>
      <div class="tree-line"><span class="indent">│   │   ├── </span><span class="stub">AuthException.java</span><span class="badge badge-todo">reg. module</span></div>
      <div class="tree-line"><span class="indent">│   │   └── </span><span class="stub">InsufficientPointsException.java</span><span class="badge badge-todo">rewards module</span></div>

      <div class="tree-line"><span class="indent">│   ├── </span><span class="folder">scheduler/</span></div>
      <div class="tree-line"><span class="indent">│   │   └── </span><span class="stub">WeeklyResetScheduler.java</span><span class="badge badge-todo">bottle module</span></div>

      <div class="tree-line"><span class="indent">│   └── </span><span class="folder">controller/</span><span class="comment">JavaFX controllers (frontend)</span></div>
      <div class="tree-line"><span class="indent">│       ├── </span><span class="stub">InOutController.java</span><span class="badge badge-todo">UI module</span></div>
      <div class="tree-line"><span class="indent">│       └── </span><span class="stub">... (others per SAD)</span></div>

      <div class="tree-line"><span class="indent">├── </span><span class="folder">resources/</span></div>
      <div class="tree-line"><span class="indent">│   ├── </span><span class="file">config.properties</span><span class="comment">DB creds — gitignored</span></div>
      <div class="tree-line"><span class="indent">│   └── </span><span class="file">config.properties.example</span><span class="comment">template committed</span></div>

      <div class="tree-line"><span class="indent">├── </span><span class="folder">sql/</span></div>
      <div class="tree-line"><span class="indent">│   ├── </span><span class="file">01_create_inout_logs.sql</span><span class="badge badge-ready">ready</span></div>
      <div class="tree-line"><span class="indent">│   └── </span><span class="stub">02_create_students.sql</span><span class="badge badge-todo">reg. module</span></div>

      <div class="tree-line"><span class="indent">├── </span><span class="folder">test/com/iskollect/</span></div>
      <div class="tree-line"><span class="indent">│   └── </span><span class="file">InOutServiceTest.java</span><span class="badge badge-ready">ready</span></div>

      <div class="tree-line"><span class="indent">├── </span><span class="file">pom.xml</span><span class="comment">or build.gradle</span></div>
      <div class="tree-line"><span class="indent">├── </span><span class="file">.gitignore</span></div>
      <div class="tree-line"><span class="indent">└── </span><span class="file">README.md</span></div>
    </div>
    <div class="legend" style="margin-top:10px">
      <div class="legend-item"><span class="legend-dot" style="background:#3B6D11"></span> Built and ready</div>
      <div class="legend-item"><span class="legend-dot" style="background:#854F0B"></span> Stub (compiles, no real logic yet)</div>
      <div class="legend-item"><span class="legend-dot" style="background:#185FA5"></span> Placeholder (file not created yet — next module's job)</div>
    </div>
  </div>

  <div class="section">
    <div class="section-title"><i class="ti ti-git-branch" aria-hidden="true"></i> Branch strategy</div>
    <div class="branches">
      <div class="branch-card">
        <div class="branch-name">main</div>
        <div class="branch-desc">Stable, tested code only. Never commit directly. Merge via PR when a module is complete and passing.</div>
      </div>
      <div class="branch-card">
        <div class="branch-name">dev</div>
        <div class="branch-desc">Integration branch. All feature branches merge here first. Acts as the staging area before main.</div>
      </div>
      <div class="branch-card">
        <div class="branch-name">feature/inout-monitoring</div>
        <div class="branch-desc">Current branch — the files built today live here. Merge into dev when InOutServiceTest passes.</div>
      </div>
      <div class="branch-card">
        <div class="branch-name">feature/student-registration</div>
        <div class="branch-desc">Next branch. Replaces StudentValidator stub. Depends on StudentDAO and AuthService.</div>
      </div>
      <div class="branch-card">
        <div class="branch-name">feature/bottle-submission</div>
        <div class="branch-desc">BottleService, PointsService, StreakService, BadgeService, WeeklyResetScheduler.</div>
      </div>
      <div class="branch-card">
        <div class="branch-name">feature/rewards-redemption</div>
        <div class="branch-desc">RewardService, RedeemedRewardDAO, CouponGenerator, InsufficientPointsException.</div>
      </div>
    </div>
  </div>

  <div class="section">
    <div class="section-title"><i class="ti ti-git-commit" aria-hidden="true"></i> Commit message convention</div>
    <div class="tree" style="font-family: var(--font-sans)">
      <div class="commit-row">
        <span class="commit-tag tag-feat">feat</span>
        <div><div class="commit-text">add InOutLog model with EventType, EntryMethod, LogStatus enums</div></div>
      </div>
      <div class="commit-row">
        <span class="commit-tag tag-feat">feat</span>
        <div><div class="commit-text">add InOutLogDAO with full CRUD and duplicate-window query</div></div>
      </div>
      <div class="commit-row">
        <span class="commit-tag tag-feat">feat</span>
        <div><div class="commit-text">add InOutService — manual entry, duplicate check, UNRESOLVED fallback</div></div>
      </div>
      <div class="commit-row">
        <span class="commit-tag tag-stub">stub</span>
        <div><div class="commit-text">add StudentValidator stub — always returns true until registration module</div><div class="commit-sub">TODO: swap with StudentDAO.findById() when feature/student-registration merges</div></div>
      </div>
      <div class="commit-row">
        <span class="commit-tag tag-chore">chore</span>
        <div><div class="commit-text">add DBConnection singleton and config.properties.example</div></div>
      </div>
      <div class="commit-row">
        <span class="commit-tag tag-chore">chore</span>
        <div><div class="commit-text">add sql/01_create_inout_logs.sql — DDL without FK constraint</div><div class="commit-sub">FK to students will be added in sql/03_add_fk_inout_student.sql by registration module</div></div>
      </div>
      <div class="commit-row">
        <span class="commit-tag tag-test">test</span>
        <div><div class="commit-text">add InOutServiceTest — covers valid log, duplicate window, invalid input</div></div>
      </div>
    </div>
  </div>

</div>
