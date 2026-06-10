package com.iskollect.prototype;

import com.iskollect.exception.DatabaseException;
import com.iskollect.model.Coupon;
import com.iskollect.model.User;
import com.iskollect.service.AuthService;
import com.iskollect.service.CouponService;
import com.iskollect.util.DBConnection;
import com.iskollect.util.SessionManager;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HighFidelityPrototype extends Application {
    private static final String NIL = "NIL";
    private static final int BYPASS_USER_ID = 0;

    private final CouponService couponService = new CouponService();
    private final AuthService authService = new AuthService();

    private BorderPane root;
    private UiData data = UiData.empty();
    private String activePage = "Dashboard";
    private Label dbStatusLabel;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        root = new BorderPane();
        root.getStyleClass().add("shell");
        Scene scene = new Scene(root, 1320, 860);
        scene.getStylesheets().add(getClass().getResource("/styles/iskollect-highfi.css").toExternalForm());
        showLogin();
        stage.setTitle("Iskollect High-Fidelity Prototype");
        stage.setScene(scene);
        stage.show();
    }

    private void showLogin() {
        VBox card = authCard();
        TextField email = new TextField();
        email.setPromptText("iskolar@iskolarngbayan.pup.edu.ph");
        PasswordField password = new PasswordField();
        password.setPromptText("Password");
        Label status = smallCopy("Use a registered account, or bypass login for prototype testing.");

        card.getChildren().addAll(
                brandBlock(),
                heading("Log in to Iskollect", "page-title"),
                smallCopy("Garbage Recycling Rewards System"),
                field("PUP Webmail", email),
                field("Password", password),
                primary("Log In", () -> {
                    try {
                        boolean ok = authService.login(email.getText().trim(), password.getText());
                        if (ok) {
                            loadAndShow("Dashboard");
                        } else {
                            status.setText("Login failed. Check credentials or use bypass for testing.");
                        }
                    } catch (Exception e) {
                        status.setText("Login unavailable: " + safeMessage(e));
                    }
                }),
                secondary("Bypass Login for Testing", () -> {
                    User user = new User();
                    user.setUserId(BYPASS_USER_ID);
                    user.setUsername("Prototype Iskolar");
                    user.setName("Prototype Iskolar");
                    user.setWebmail("nil@iskolarngbayan.pup.edu.ph");
                    user.setAccountStatus("prototype");
                    SessionManager.setSession(user);
                    loadAndShow("Dashboard");
                }),
                secondary("Create Account", this::showRegister),
                secondary("Forgot Password", this::showForgotPassword),
                status
        );
        root.setLeft(null);
        root.setCenter(center(card));
    }

    private void showRegister() {
        VBox card = authCard();
        card.getChildren().addAll(
                brandBlock(),
                heading("Create Account", "page-title"),
                field("Name", new TextField("")),
                field("PUP Webmail", new TextField("")),
                field("Password", new PasswordField()),
                primary("Continue to Verification", this::showVerifyCode),
                secondary("Back to Login", this::showLogin)
        );
        root.setCenter(center(card));
    }

    private void showForgotPassword() {
        VBox card = authCard();
        card.getChildren().addAll(
                brandBlock(),
                heading("Reset Password", "page-title"),
                smallCopy("Enter your PUP webmail and a verification code will be sent for authentication."),
                field("PUP Webmail", new TextField("")),
                primary("Send Reset Code", this::showVerifyCode),
                secondary("Back to Login", this::showLogin)
        );
        root.setCenter(center(card));
    }

    private void showVerifyCode() {
        VBox card = authCard();
        card.getChildren().addAll(
                brandBlock(),
                heading("Verify Code", "page-title"),
                smallCopy("Check your email. Code expires in 5:00."),
                field("Verification Code", new TextField("")),
                primary("Verify", this::showResetPassword),
                secondary("Back", this::showLogin)
        );
        root.setCenter(center(card));
    }

    private void showResetPassword() {
        VBox card = authCard();
        card.getChildren().addAll(
                brandBlock(),
                heading("Set New Password", "page-title"),
                field("New Password", new PasswordField()),
                field("Confirm Password", new PasswordField()),
                primary("Set New Password", this::showLogin),
                smallCopy("At least 8 characters, contains a number, contains a special character.")
        );
        root.setCenter(center(card));
    }

    private void loadAndShow(String pageName) {
        data = UiData.load(couponService);
        showShell(pageName);
    }

    private void showShell(String pageName) {
        activePage = pageName;
        root.setLeft(sidebar());
        root.setCenter(scroll(switch (pageName) {
            case "Bottle Records" -> bottleRecordsPage();
            case "Leaderboard" -> leaderboardPage();
            case "Rewards Catalog" -> rewardsPage();
            case "Transaction History" -> transactionsPage();
            case "Profile" -> profilePage(false);
            case "In/Out Logs" -> inOutPage();
            default -> dashboardPage();
        }));
        updateDbStatus();
    }

    private VBox sidebar() {
        VBox box = new VBox();
        box.getStyleClass().add("sidebar");
        box.setPrefWidth(260);
        dbStatusLabel = pill(data.connected ? "Supabase connected" : "Supabase offline");
        Button submit = new Button("+ Submit Bottles");
        submit.getStyleClass().add("sidebar-submit");
        submit.setOnAction(e -> showSubmitDialog());
        box.getChildren().addAll(
                brandBlock(),
                centered(submit),
                nav("Dashboard"),
                nav("Bottle Records"),
                nav("Leaderboard"),
                nav("Rewards Catalog"),
                nav("Transaction History"),
                nav("Profile"),
                nav("In/Out Logs"),
                new Separator(),
                dbStatusLabel
        );
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        box.getChildren().addAll(spacer, danger("Log-out", this::showLogout));
        return box;
    }

    private VBox dashboardPage() {
        VBox page = page("Pagbati, " + currentName() + "!", "Track active streaks, badge progress, coupons, and total points at a glance.");
        page.getChildren().add(topActions());

        HBox layout = new HBox(18);
        VBox left = new VBox(16);
        left.getChildren().add(metricGrid(List.of(
                new Metric(data.totalBottles, "Total Bottles Collected", "bottle\npng/svg"),
                new Metric(data.totalPoints, "Total Points", "coin\npng/svg"),
                new Metric(data.todayInOutCount, "Streak", "fire\npng/svg"),
                new Metric(data.currentBadge, "Badge", "badge\npng/svg")
        )));
        left.getChildren().add(recentTransactions());
        HBox.setHgrow(left, Priority.ALWAYS);
        layout.getChildren().addAll(left, leaderboardPreview());
        page.getChildren().add(layout);
        return page;
    }

    private VBox bottleRecordsPage() {
        VBox page = page("Bottle Collection Records", "Review your complete collection history and environmental impact over time.");
        page.getChildren().add(topActions());
        page.getChildren().add(bottleProgressBanner());
        HBox body = new HBox(16);
        VBox left = new VBox(14);
        left.getChildren().add(filterTabs("Day", "Week", "Month", "Year"));
        left.getChildren().add(table(
                List.of("Submission Date", "User", "Bottles Submitted", "Points Earned"),
                data.bottleRows
        ));
        HBox.setHgrow(left, Priority.ALWAYS);
        body.getChildren().addAll(left, badgeHistoryPanel());
        page.getChildren().add(body);
        return page;
    }

    private VBox leaderboardPage() {
        VBox page = page("Campus Leaderboards", "Ranked by total raw bottles and current point balance.");
        page.getChildren().add(topActions());
        HBox podium = new HBox(18);
        podium.setAlignment(Pos.BOTTOM_CENTER);
        List<List<String>> rows = data.leaderboardRows;
        podium.getChildren().addAll(
                podium("2", rowValue(rows, 1, 1), rowValue(rows, 1, 2), rowValue(rows, 1, 3)),
                podium("1", rowValue(rows, 0, 1), rowValue(rows, 0, 2), rowValue(rows, 0, 3)),
                podium("3", rowValue(rows, 2, 1), rowValue(rows, 2, 2), rowValue(rows, 2, 3))
        );
        page.getChildren().add(podium);
        page.getChildren().add(table(List.of("Rank", "Student", "Bottles", "Points", "Badge"), data.leaderboardRows));
        return page;
    }

    private VBox rewardsPage() {
        VBox page = page("Rewards Catalog", "Exchange accumulated points for school supplies, snacks, and meals.");
        page.getChildren().add(topActions());
        page.getChildren().add(balanceBanner());
        GridPane rewards = new GridPane();
        rewards.setHgap(18);
        rewards.setVgap(18);
        int index = 0;
        for (Coupon coupon : data.coupons) {
            rewards.add(rewardCard(coupon), index % 2, index / 2);
            index++;
        }
        if (data.coupons.isEmpty()) {
            rewards.add(emptyState("No coupons found in Supabase.", "The coupons table is reachable but currently has no data."), 0, 0);
        }
        page.getChildren().add(rewards);
        return page;
    }

    private VBox transactionsPage() {
        VBox page = page("Transaction History", "A complete log of redemptions, active coupons, and point deductions.");
        page.getChildren().add(topActions());
        page.getChildren().add(metrics(List.of(new Metric(data.totalPoints, "Total Points You Earned", "coin\npng/svg"), new Metric(data.redemptionCount, "Total Coupons You Redeemed", "ticket\npng/svg"))));

        HBox filters = new HBox(10);
        filters.setAlignment(Pos.CENTER_LEFT);
        ComboBox<String> type = new ComboBox<>();
        type.getItems().addAll("All coupon types", "School Supplies", "Snack V1", "Snack V2", "Lunch");
        type.getSelectionModel().selectFirst();
        ComboBox<String> status = new ComboBox<>();
        status.getItems().addAll("All status", "pending", "claimed", "redeemed");
        status.getSelectionModel().selectFirst();
        filters.getChildren().addAll(type, status, new DatePicker(LocalDate.now()), new DatePicker(LocalDate.now()), secondary("Clear Filters", () -> { }));
        page.getChildren().add(filters);
        page.getChildren().add(table(List.of("#", "Date", "Coupon", "Points", "Code", "Status"), data.redemptionRows));
        return page;
    }

    private VBox profilePage(boolean passwordMode) {
        VBox page = page("Student Profile", "Manage personal information and account preferences.");
        page.getChildren().add(topActions(passwordMode ? "Cancel Password Change" : "Save Changes", () -> showShell("Profile")));
        HBox profile = new HBox(18);
        profile.getChildren().add(profileSummary());
        VBox form = card();
        form.setPrefWidth(590);
        form.getChildren().addAll(
                heading("Personal Information", "section-title"),
                field("Display Name", new TextField(currentName())),
                field("Username", new TextField(currentUsername())),
                field("Age", new TextField(NIL)),
                smallCopy("PUP webmail cannot be changed from this screen.")
        );
        if (passwordMode) {
            form.getChildren().addAll(new Separator(), field("Current Password", new PasswordField()), field("New Password", new PasswordField()), field("Confirm New Password", new PasswordField()), primary("Update Password", () -> showShell("Profile")));
        } else {
            form.getChildren().add(secondary("Change Password", () -> root.setCenter(scroll(profilePage(true)))));
        }
        profile.getChildren().add(form);
        page.getChildren().add(profile);
        return page;
    }

    private VBox inOutPage() {
        VBox page = page("Ingress and Egress Logs", "Monitor gate activity using the Supabase inout_logs table.");
        page.getChildren().add(topActions("Refresh Data", () -> loadAndShow("In/Out Logs")));
        page.getChildren().add(metrics(List.of(new Metric(data.todayInOutCount, "Logs Today", "gate\npng/svg"), new Metric(String.valueOf(data.inOutRows.size()), "Recent Rows", "logs\npng/svg"))));
        page.getChildren().add(table(List.of("Log ID", "User", "Action", "Performed At", "Notes"), data.inOutRows));
        return page;
    }

    private void showSubmitDialog() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        VBox box = card();
        box.setPadding(new Insets(24));
        TextField count = new TextField();
        count.setPromptText("Number of bottles");
        Label note = smallCopy(currentUserId() > 0 ? "Submit against the logged-in user." : "NIL: bypass sessions cannot write bottle records.");
        box.getChildren().addAll(heading("Submit Bottle", "section-title"), smallCopy("Enter bottles physically brought to the collection point today."), field("Bottles", count), note, new HBox(10, secondary("Cancel", dialog::close), primary("Submit", () -> {
            if (currentUserId() <= 0) {
                note.setText("NIL: create or log in with a real account before writing records.");
            } else {
                note.setText("Submit wiring is ready for BottleService integration.");
            }
        })));
        Scene scene = new Scene(box, 460, 320);
        scene.getStylesheets().add(getClass().getResource("/styles/iskollect-highfi.css").toExternalForm());
        dialog.setTitle("Submit Bottle");
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    private VBox page(String title, String copy) {
        VBox page = new VBox(18);
        page.getStyleClass().add("page");
        page.getChildren().addAll(heading(title, "page-title"), smallCopy(copy));
        return page;
    }

    private HBox topActions() {
        return topActions(null, null);
    }

    private HBox topActions(String action, Runnable runnable) {
        HBox bar = new HBox(12);
        bar.getStyleClass().add("top-line");
        bar.setAlignment(Pos.CENTER_LEFT);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        bar.getChildren().addAll(spacer, pill("June 18, 2026 11:11pm"), secondary("Refresh", () -> loadAndShow(activePage)));
        if (action != null && runnable != null) {
            bar.getChildren().add(primary(action, runnable));
        }
        return bar;
    }

    private VBox hero() {
        VBox box = new VBox(12);
        box.getStyleClass().add("hero-panel");
        HBox content = new HBox(20);
        content.setAlignment(Pos.CENTER_LEFT);
        VBox text = new VBox(8, heading("Tapat ko, LinISKO.", "hero-title"), heroCopy("A cleaner campus, one verified bottle submission at a time."));
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        content.getChildren().addAll(text, spacer, illustration("bottle\nbadge\ncoin", 180, 120));
        box.getChildren().add(content);
        return box;
    }

    private VBox bottleProgressBanner() {
        VBox box = card();
        double progress = parseNumber(data.weeklyBottles) / 50.0;
        ProgressBar bar = new ProgressBar(Math.min(1, progress));
        bar.setMaxWidth(Double.MAX_VALUE);
        HBox content = new HBox(14);
        content.setAlignment(Pos.CENTER_LEFT);
        HBox bottles = new HBox(8);
        for (int i = 0; i < 4; i++) {
            bottles.getChildren().add(illustration("bottle\npng/svg", 74, 138));
        }
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        VBox text = new VBox(6, heading(data.weeklyBottles + " of bottles", "metric-value"), smallCopy("how many more needed for next badge level."));
        content.getChildren().addAll(bottles, spacer, text);
        box.getChildren().addAll(content, bar, smallCopy("Milestone to achieve " + data.currentBadge));
        return box;
    }

    private VBox leaderboardPreview() {
        VBox box = card();
        box.setPrefWidth(286);
        box.getChildren().add(heading("Leaderboard", "section-title"));
        if (data.leaderboardRows.isEmpty()) {
            box.getChildren().add(emptyState("NIL", "No users found yet."));
        } else {
            for (int i = 0; i < Math.min(3, data.leaderboardRows.size()); i++) {
                List<String> row = data.leaderboardRows.get(i);
                box.getChildren().add(smallCopy(row.get(0) + ". " + row.get(1) + " - " + row.get(2) + " bottles"));
            }
        }
        box.getChildren().add(secondary("See all ranking", () -> showShell("Leaderboard")));
        return box;
    }

    private VBox recentTransactions() {
        VBox box = card();
        box.setPrefWidth(590);
        box.getChildren().addAll(heading("Recent Transactions", "section-title"), table(List.of("#", "Date", "Coupon", "Points", "Status"), trimColumns(data.redemptionRows, 5)));
        return box;
    }

    private VBox badgeHistoryPanel() {
        VBox box = card();
        box.setPrefWidth(286);
        box.getChildren().add(heading("Badge History", "section-title"));
        for (int i = 0; i < 4; i++) {
            box.getChildren().add(new HBox(10, illustration("Badge", 54, 54), smallCopy("Badge\nDate"), smallCopy("Verification")));
        }
        box.getChildren().add(secondary("See all badge history", () -> { }));
        return box;
    }

    private VBox podium(String rank, String name, String bottles, String points) {
        VBox box = card();
        box.setAlignment(Pos.CENTER);
        box.setPrefWidth("1".equals(rank) ? 246 : 206);
        box.setMinHeight("1".equals(rank) ? 214 : 184);
        box.getChildren().addAll(illustration("profile", "1".equals(rank) ? 108 : 78, "1".equals(rank) ? 108 : 78), heading(nil(name), "section-title"), smallCopy(nil(points) + " pts"), smallCopy(nil(bottles) + " bottles"));
        return box;
    }

    private VBox rewardCard(Coupon coupon) {
        VBox box = card();
        box.setPrefWidth(438);
        box.setMinHeight(184);
        box.getChildren().addAll(
                new HBox(12, illustration(couponIcon(coupon.getName()), 80, 80), heading(formatPoints(coupon.getPointsRequired()) + " PTS", "metric-value")),
                heading(coupon.getName(), "section-title"),
                smallCopy(couponDescription(coupon.getName())),
                primary("Redeem", () -> redeemCoupon(coupon))
        );
        return box;
    }

    private void redeemCoupon(Coupon coupon) {
        if (currentUserId() <= 0) {
            alert("NIL", "Bypass sessions cannot redeem because there is no persisted user row yet.");
            return;
        }
        var result = couponService.redeem(currentUserId(), coupon.getCouponId());
        alert(result.isSuccess() ? "Coupon Redeemed" : "Redemption Failed", result.getMessage());
        loadAndShow("Rewards Catalog");
    }

    private GridPane table(List<String> headers, List<List<String>> rows) {
        GridPane grid = new GridPane();
        grid.getStyleClass().add("table");
        grid.setMaxWidth(Double.MAX_VALUE);
        for (int i = 0; i < headers.size(); i++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setHgrow(Priority.ALWAYS);
            cc.setPercentWidth(100.0 / headers.size());
            grid.getColumnConstraints().add(cc);
            Label head = new Label(headers.get(i).toUpperCase(Locale.ROOT));
            head.getStyleClass().add("table-head");
            head.setMaxWidth(Double.MAX_VALUE);
            grid.add(head, i, 0);
        }
        List<List<String>> visibleRows = rows.isEmpty() ? List.of(nilRow(headers.size())) : rows;
        for (int r = 0; r < visibleRows.size(); r++) {
            for (int c = 0; c < headers.size(); c++) {
                Label cell = new Label(c < visibleRows.get(r).size() ? nil(visibleRows.get(r).get(c)) : NIL);
                if (NIL.equals(cell.getText())) {
                    cell.getStyleClass().add("nil");
                }
                cell.getStyleClass().add("table-cell-lite");
                cell.setWrapText(true);
                cell.setMaxWidth(Double.MAX_VALUE);
                grid.add(cell, c, r + 1);
            }
        }
        return grid;
    }

    private List<String> nilRow(int count) {
        List<String> row = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            row.add(NIL);
        }
        return row;
    }

    private HBox metrics(List<Metric> metrics) {
        HBox row = new HBox(14);
        for (Metric metric : metrics) {
            row.getChildren().add(metricCard(metric));
        }
        return row;
    }

    private GridPane metricGrid(List<Metric> metrics) {
        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(16);
        for (int i = 0; i < metrics.size(); i++) {
            grid.add(metricCard(metrics.get(i)), i % 2, i / 2);
        }
        return grid;
    }

    private VBox metricCard(Metric metric) {
        HBox top = new HBox(12);
        top.setAlignment(Pos.CENTER_LEFT);
        top.getChildren().addAll(illustration(metric.icon(), 74, 108), new Region(), heading(nil(metric.value()), "metric-value"));
        HBox.setHgrow(top.getChildren().get(1), Priority.ALWAYS);
        VBox box = new VBox(5);
        box.getStyleClass().add("metric-card");
        box.getChildren().addAll(top, heading(metric.label(), "metric-label"));
        return box;
    }

    private HBox filterTabs(String... tabs) {
        HBox row = new HBox(18);
        row.getStyleClass().add("card");
        for (String tab : tabs) {
            row.getChildren().add(smallCopy(tab));
        }
        return row;
    }

    private VBox balanceBanner() {
        VBox box = card();
        HBox row = new HBox(18);
        row.setAlignment(Pos.CENTER_LEFT);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        VBox text = new VBox(2, heading("your current balance", "section-title"), heading(userPoints() + " points", "balance-value"), smallCopy("Redeeming a coupon deducts the selected point cost."));
        row.getChildren().addAll(illustration("coin\npng/svg", 116, 116), spacer, text);
        box.getChildren().add(row);
        return box;
    }

    private VBox profileSummary() {
        VBox box = card();
        box.setPrefWidth(286);
        box.setAlignment(Pos.TOP_CENTER);
        box.getChildren().addAll(
                illustration("profile", 112, 112),
                heading(currentName(), "profile-name"),
                smallCopy("@" + currentUsername()),
                smallCopy("badge level"),
                new Separator(),
                heading("PUP WEBMAIL", "field-label"),
                smallCopy(currentEmail()),
                heading("MEMBER SINCE", "field-label"),
                smallCopy("September 2024"),
                heading("TOTAL BOTTLES COLLECTED", "field-label"),
                smallCopy(data.totalBottles),
                heading("COUPONS REDEEMED", "field-label"),
                smallCopy(data.redemptionCount)
        );
        return box;
    }

    private VBox emptyState(String title, String copy) {
        VBox box = card();
        box.setAlignment(Pos.CENTER);
        box.getChildren().addAll(heading(title, "metric-value"), smallCopy(copy));
        return box;
    }

    private VBox card() {
        VBox box = new VBox(12);
        box.getStyleClass().add("card");
        return box;
    }

    private VBox authCard() {
        VBox card = new VBox();
        card.getStyleClass().add("auth-card");
        card.setMinWidth(365);
        card.setPrefWidth(365);
        card.setMaxWidth(365);
        card.setFillWidth(true);
        return card;
    }

    private StackPane center(VBox card) {
        StackPane pane = new StackPane(card);
        pane.getStyleClass().add("auth-root");
        pane.setPadding(new Insets(0, 110, 0, 0));
        StackPane.setAlignment(card, Pos.CENTER_RIGHT);
        return pane;
    }

    private ScrollPane scroll(VBox content) {
        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        return scroll;
    }

    private VBox brandBlock() {
        VBox box = new VBox(2);
        box.getStyleClass().add("sidebar-brand");
        Label brand = heading("ISKollect", "brand");
        Label subtitle = heading("Garbage Recycling Rewards System", "brand-small");
        box.getChildren().addAll(brand, subtitle);
        return box;
    }

    private StackPane centered(Button button) {
        StackPane pane = new StackPane(button);
        pane.setPadding(new Insets(0, 0, 8, 0));
        return pane;
    }

    private Button nav(String pageName) {
        Button button = new Button(pageName);
        button.getStyleClass().add("nav-button");
        if (pageName.equals(activePage)) {
            button.getStyleClass().add("nav-active");
        }
        button.setOnAction(e -> showShell(pageName));
        return button;
    }

    private VBox field(String label, TextField field) {
        VBox box = new VBox(6);
        field.setMaxWidth(Double.MAX_VALUE);
        box.getChildren().addAll(heading(label, "field-label"), field);
        return box;
    }

    private Label heading(String text, String styleClass) {
        Label label = new Label(text);
        label.getStyleClass().add(styleClass);
        label.setWrapText(true);
        return label;
    }

    private Label smallCopy(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("page-copy");
        label.setWrapText(true);
        return label;
    }

    private Label heroCopy(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("hero-copy");
        label.setWrapText(true);
        return label;
    }

    private Label pill(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("status-pill");
        return label;
    }

    private Label illustration(String text, double width, double height) {
        Label label = new Label(text);
        label.getStyleClass().add("illustration");
        label.setPrefSize(width, height);
        label.setWrapText(true);
        return label;
    }

    private Button primary(String text, Runnable action) {
        Button button = new Button(text);
        button.getStyleClass().add("primary-button");
        button.setOnAction(e -> action.run());
        return button;
    }

    private Button secondary(String text, Runnable action) {
        Button button = new Button(text);
        button.getStyleClass().add("secondary-button");
        button.setOnAction(e -> action.run());
        return button;
    }

    private Button danger(String text, Runnable action) {
        Button button = new Button(text);
        button.getStyleClass().add("danger-button");
        button.setOnAction(e -> action.run());
        return button;
    }

    private void showLogout() {
        try {
            if (currentUserId() > 0) {
                authService.logout();
            }
        } catch (Exception ignored) {
        }
        SessionManager.clearSession();
        VBox card = authCard();
        card.setAlignment(Pos.CENTER);
        card.getChildren().addAll(heading("Successfully Logged Out", "page-title"), smallCopy("See you again! Thanks for supporting a cleaner and greener campus."), primary("Return to Login", this::showLogin));
        root.setLeft(null);
        root.setCenter(center(card));
    }

    private void alert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void updateDbStatus() {
        if (dbStatusLabel != null) {
            dbStatusLabel.setText(data.connected ? "Supabase connected" : "Supabase offline");
        }
    }

    private int currentUserId() {
        User user = SessionManager.getCurrentUser();
        return user == null ? BYPASS_USER_ID : user.getUserId();
    }

    private String currentName() {
        User user = SessionManager.getCurrentUser();
        return user == null ? "Iskolar" : nil(user.getName());
    }

    private String currentUsername() {
        User user = SessionManager.getCurrentUser();
        return user == null ? NIL : nil(user.getUsername());
    }

    private String currentEmail() {
        User user = SessionManager.getCurrentUser();
        return user == null ? NIL : nil(user.getWebmail());
    }

    private String userPoints() {
        User user = SessionManager.getCurrentUser();
        if (user != null && user.getUserId() > 0) {
            return formatPoints(user.getTotalPoints());
        }
        return data.totalPoints;
    }

    private static String nil(String value) {
        return value == null || value.isBlank() ? NIL : value;
    }

    private static String safeMessage(Exception e) {
        return e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage();
    }

    private static String formatPoints(double points) {
        if (points == Math.rint(points)) {
            return String.valueOf((int) points);
        }
        return String.format(Locale.ROOT, "%.2f", points);
    }

    private static double parseNumber(String value) {
        try {
            return Double.parseDouble(value.replaceAll("[^0-9.]", ""));
        } catch (Exception e) {
            return 0;
        }
    }

    private static String couponDescription(String name) {
        String normalized = name.toLowerCase(Locale.ROOT);
        if (normalized.contains("suppl")) {
            return "Ballpen, bond paper, pencil, eraser, correction tape, and similar school items.";
        }
        if (normalized.contains("snack") && normalized.contains("v2")) {
            return "Street food items such as fishball, kikiam, and kwek-kwek.";
        }
        if (normalized.contains("snack")) {
            return "Biscuits, breads, chips, and similar light snacks.";
        }
        if (normalized.contains("lunch")) {
            return "A full meal with rice redeemable at a campus food partner.";
        }
        return "Coupon details are currently NIL in the database.";
    }

    private static String couponIcon(String name) {
        String normalized = name.toLowerCase(Locale.ROOT);
        if (normalized.contains("suppl")) {
            return "SCHOOL\nSUPPLIES";
        }
        if (normalized.contains("lunch")) {
            return "LUNCH\nVOUCHER";
        }
        return "SNACK\nVOUCHER";
    }

    private static String rowValue(List<List<String>> rows, int row, int col) {
        if (row >= rows.size() || col >= rows.get(row).size()) {
            return NIL;
        }
        return rows.get(row).get(col);
    }

    private static List<List<String>> trimColumns(List<List<String>> rows, int columns) {
        List<List<String>> trimmed = new ArrayList<>();
        for (List<String> row : rows) {
            trimmed.add(row.subList(0, Math.min(columns, row.size())));
        }
        return trimmed;
    }

    private record Metric(String value, String label, String icon) {
    }

    private static final class UiData {
        private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ROOT);

        private boolean connected;
        private String totalBottles = NIL;
        private String totalPoints = NIL;
        private String redemptionCount = NIL;
        private String weeklyBottles = NIL;
        private String currentBadge = NIL;
        private String todayInOutCount = NIL;
        private List<Coupon> coupons = List.of();
        private List<List<String>> leaderboardRows = List.of();
        private List<List<String>> bottleRows = List.of();
        private List<List<String>> redemptionRows = List.of();
        private List<List<String>> inOutRows = List.of();

        private static UiData empty() {
            return new UiData();
        }

        private static UiData load(CouponService couponService) {
            UiData data = new UiData();
            data.coupons = couponService.getAllCoupons();
            try {
                Connection conn = DBConnection.getInstance().getConnection();
                data.connected = true;
                data.totalBottles = scalar(conn, "SELECT COALESCE(SUM(raw_bottle_count), 0) FROM users");
                data.totalPoints = scalar(conn, "SELECT COALESCE(SUM(total_points), 0) FROM users");
                data.redemptionCount = scalar(conn, "SELECT COUNT(*) FROM redemptions");
                data.weeklyBottles = scalar(conn, "SELECT COALESCE(SUM(bottles_collected), 0) FROM bottle_records WHERE week_start_date = DATE_TRUNC('week', CURRENT_DATE)::date");
                data.todayInOutCount = scalar(conn, "SELECT COUNT(*) FROM inout_logs WHERE performed_at::date = CURRENT_DATE");
                data.currentBadge = badgeFor(parseNumber(data.weeklyBottles));
                data.leaderboardRows = queryRows(conn,
                        "SELECT ROW_NUMBER() OVER (ORDER BY raw_bottle_count DESC, total_points DESC, username ASC) AS rank, username, raw_bottle_count, total_points FROM users ORDER BY raw_bottle_count DESC, total_points DESC, username ASC LIMIT 10",
                        5,
                        rs -> List.of(
                                rs.getString("rank"),
                                rs.getString("username"),
                                rs.getString("raw_bottle_count"),
                                formatPoints(rs.getDouble("total_points")),
                                badgeFor(rs.getDouble("raw_bottle_count"))
                        ));
                data.bottleRows = queryRows(conn,
                        "SELECT br.collection_date, u.username, br.bottles_collected, COALESCE(SUM(pl.points_change) FILTER (WHERE pl.points_change > 0), 0) AS points FROM bottle_records br JOIN users u ON br.user_id = u.user_id LEFT JOIN points_ledger pl ON pl.ref_id = br.record_id AND pl.user_id = br.user_id GROUP BY br.record_id, br.collection_date, u.username, br.bottles_collected ORDER BY br.collection_date DESC, br.record_id DESC LIMIT 10",
                        4,
                        rs -> List.of(
                                rs.getDate("collection_date").toLocalDate().format(DATE),
                                rs.getString("username"),
                                rs.getString("bottles_collected"),
                                formatPoints(rs.getDouble("points"))
                        ));
                data.redemptionRows = queryRows(conn,
                        "SELECT rd.redemption_id, rd.redemption_date, c.coupon_name, c.points_required, rd.coupon_code, rd.status FROM redemptions rd JOIN coupons c ON rd.coupon_id = c.coupon_id ORDER BY rd.redemption_date DESC, rd.redemption_id DESC LIMIT 10",
                        6,
                        rs -> List.of(
                                rs.getString("redemption_id"),
                                rs.getDate("redemption_date").toLocalDate().format(DATE),
                                rs.getString("coupon_name"),
                                formatPoints(rs.getDouble("points_required")) + " PTS",
                                rs.getString("coupon_code"),
                                rs.getString("status")
                        ));
                data.inOutRows = queryRows(conn,
                        "SELECT log_id, user_id, action, performed_at, notes FROM inout_logs ORDER BY performed_at DESC LIMIT 10",
                        5,
                        rs -> List.of(
                                rs.getString("log_id"),
                                rs.getString("user_id"),
                                rs.getString("action"),
                                rs.getTimestamp("performed_at").toLocalDateTime().toString(),
                                nil(rs.getString("notes"))
                        ));
            } catch (Exception e) {
                data.connected = false;
            }
            return data;
        }

        private static String scalar(Connection conn, String sql) throws SQLException {
            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String value = rs.getString(1);
                    return value == null || value.isBlank() ? NIL : value;
                }
                return NIL;
            }
        }

        private static List<List<String>> queryRows(Connection conn, String sql, int expectedColumns, RowMapper mapper)
                throws SQLException {
            List<List<String>> rows = new ArrayList<>();
            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rows.add(mapper.map(rs));
                }
            }
            return rows;
        }

        private static String badgeFor(double bottles) {
            if (bottles >= 31) {
                return "Constellation";
            }
            if (bottles >= 21) {
                return "Gold";
            }
            if (bottles >= 11) {
                return "Emerald";
            }
            if (bottles >= 6) {
                return "Silver";
            }
            if (bottles > 0) {
                return "Bronze";
            }
            return NIL;
        }
    }

    @FunctionalInterface
    private interface RowMapper {
        List<String> map(ResultSet rs) throws SQLException;
    }
}
