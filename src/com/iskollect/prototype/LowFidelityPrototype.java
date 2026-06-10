package com.iskollect.prototype;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
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

import java.time.LocalDate;
import java.util.List;

public class LowFidelityPrototype extends Application {
    private BorderPane root;
    private VBox sidebar;
    private String activePage = "Dashboard";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        root = new BorderPane();
        root.getStyleClass().add("app-shell");

        Scene scene = new Scene(root, 1280, 820);
        scene.getStylesheets().add(getClass().getResource("/styles/iskollect-lowfi.css").toExternalForm());

        showLogin();
        stage.setTitle("Iskollect Low-Fidelity JavaFX Prototype");
        stage.setScene(scene);
        stage.show();
    }

    private void showLogin() {
        VBox card = authCard();
        card.getChildren().addAll(
                brandBlock(),
                title("Log In", "auth-title"),
                labeledField("PUP Webmail", "iskolar@iskolarngbayan.pup.edu.ph"),
                labeledPassword("Password", "Password"),
                linkButton("Forgot Password?", this::showForgotPassword),
                primary("Log In", () -> showShell("Dashboard")),
                secondary("Bypass Login for Testing", () -> showShell("Dashboard")),
                secondary("Create Account", this::showRegister)
        );
        root.setLeft(null);
        root.setCenter(centeredAuth(card));
    }

    private void showRegister() {
        VBox card = authCard();
        card.getChildren().addAll(
                brandBlock(),
                title("Create Account", "auth-title"),
                labeledField("Name", "First Name, Middle I., Last Name"),
                labeledField("PUP Webmail", "iskolar@iskolarngbayan.pup.edu.ph"),
                labeledPassword("Password", "Password"),
                primary("Sign Up", this::showVerifyCode),
                linkButton("Already have an account? Log In", this::showLogin)
        );
        root.setLeft(null);
        root.setCenter(centeredAuth(card));
    }

    private void showForgotPassword() {
        VBox card = authCard();
        card.getChildren().addAll(
                brandBlock(),
                title("Reset Password", "auth-title"),
                copy("Enter your official PUP webmail address and we will send a verification code for authentication."),
                labeledField("PUP Webmail", "iskolar@iskolarngbayan.pup.edu.ph"),
                primary("Send Reset Code", this::showVerifyCode),
                linkButton("Remembered it? Back to Login", this::showLogin)
        );
        root.setLeft(null);
        root.setCenter(centeredAuth(card));
    }

    private void showVerifyCode() {
        VBox card = authCard();
        card.getChildren().addAll(
                brandBlock(),
                title("Verify Code", "auth-title"),
                copy("Check your email. We sent a 6-digit code to iskolar@iskolarngbayan.pup.edu.ph."),
                labeledField("Verification Code", "000000"),
                copy("Code expires in 5:00"),
                primary("Verify", this::showResetPassword),
                linkButton("Didn't get a code? Resend", () -> { }),
                secondary("Back", this::showLogin)
        );
        root.setLeft(null);
        root.setCenter(centeredAuth(card));
    }

    private void showResetPassword() {
        VBox card = authCard();
        card.getChildren().addAll(
                brandBlock(),
                title("Set New Password", "auth-title"),
                labeledPassword("New Password", "New Password"),
                labeledPassword("Confirm Password", "Confirm Password"),
                checklist("At least 8 characters", "Contains a number", "Contains a special character", "Password match"),
                primary("Set New Password", this::showLogin),
                copy("You will be redirected to the login page shortly.")
        );
        root.setLeft(null);
        root.setCenter(centeredAuth(card));
    }

    private void showShell(String page) {
        activePage = page;
        sidebar = buildSidebar();
        root.setLeft(sidebar);
        switch (page) {
            case "Bottle Records" -> root.setCenter(scroll(bottleRecordsPage()));
            case "Leaderboard" -> root.setCenter(scroll(leaderboardPage()));
            case "Rewards Catalog" -> root.setCenter(scroll(rewardsPage()));
            case "Transaction History" -> root.setCenter(scroll(transactionHistoryPage()));
            case "Profile" -> root.setCenter(scroll(profilePage(false)));
            default -> root.setCenter(scroll(dashboardPage()));
        }
    }

    private VBox buildSidebar() {
        VBox box = new VBox();
        box.getStyleClass().add("sidebar");
        box.setPrefWidth(250);
        box.getChildren().addAll(
                brandBlock(),
                nav("Dashboard"),
                nav("Bottle Records"),
                nav("Leaderboard"),
                nav("Rewards Catalog"),
                nav("Transaction History"),
                nav("Profile")
        );
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        box.getChildren().addAll(spacer, secondary("Log-out", this::showLogout));
        return box;
    }

    private VBox dashboardPage() {
        VBox page = page("Pagbati, Iskolar ng Bayan!", "Your central hub. Track your active streaks, current badge progress, and total points at a glance.");
        page.getChildren().add(headerActions());
        page.getChildren().add(metrics(List.of(
                new Metric("##", "Total Bottles Collected"),
                new Metric("##", "Total Points"),
                new Metric("10 pts", "Streak Silver Badge")
        )));

        HBox row = new HBox(16);
        row.getChildren().addAll(leaderboardPreview(), recentTransactions(), reminderCard());
        page.getChildren().add(row);
        return page;
    }

    private VBox bottleRecordsPage() {
        VBox page = page("Bottle Collection Records", "Review your complete collection history and track your environmental impact over time.");
        page.getChildren().add(headerActions());
        page.getChildren().add(bottleStrip());

        HBox row = new HBox(16);
        row.getChildren().addAll(progressCard(), badgeHistoryCard());
        page.getChildren().add(row);

        HBox filters = new HBox(8, tab("Day", true), tab("Week", false), tab("Month", false), tab("Year", false));
        page.getChildren().add(filters);
        page.getChildren().add(table(
                List.of("Submission Date", "Bottles Submitted", "Points Earned"),
                List.of(
                        List.of("Today", "12 bottles", "6 pts"),
                        List.of("Yesterday", "8 bottles", "4 pts"),
                        List.of("2 days ago", "10 bottles", "5 pts"),
                        List.of("3 days ago", "5 bottles", "2.5 pts"),
                        List.of("4 days ago", "7 bottles", "3.5 pts")
                )
        ));
        return page;
    }

    private VBox leaderboardPage() {
        VBox page = page("Campus Leaderboards", "See where you stand! Ranked strictly by the total number of raw bottles contributed this week.");
        page.getChildren().add(headerActions());

        HBox podium = new HBox(16);
        podium.getChildren().addAll(
                podiumCard("2", "Precious Peligrino", "48 bottles", "10 pts"),
                podiumCard("1", "Heavenlee Morales", "56 bottles", "10 pts"),
                podiumCard("3", "Clint Commendador", "43 bottles", "10 pts")
        );
        page.getChildren().add(podium);
        page.getChildren().add(table(
                List.of("Rank", "Student", "Bottles This Week", "Badge + Bonus Pts"),
                List.of(
                        List.of("1", "Heavenlee Morales", "56", "Gold Badge + 10 PTS"),
                        List.of("2", "Precious Peligrino", "48", "Gold Badge + 10 PTS"),
                        List.of("3", "Clint Commendador", "43", "Emerald Badge + 5 PTS"),
                        List.of("4", "Aaron Benavidez", "31", "Emerald Badge + 5 PTS"),
                        List.of("5", "Marc Luzong", "25", "Silver Badge + 3 PTS"),
                        List.of("6", "Mikaela Ludovico", "18", "Silver Badge + 3 PTS")
                )
        ));
        return page;
    }

    private VBox rewardsPage() {
        VBox page = page("Rewards Catalog", "You've earned it. Exchange your accumulated points for school supplies, snacks, and meals here.");
        page.getChildren().add(headerActions());
        page.getChildren().add(metrics(List.of(new Metric("143 points", "Your current balance"), new Metric("0", "Active coupons"))));
        page.getChildren().add(copy("Redeeming a coupon deducts the selected coupon point cost from your balance."));

        GridPane grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(16);
        grid.add(rewardCard("School Supplies", "10 PTS", "Ballpen, bond paper, pencil, eraser, correction tape, and similar items."), 0, 0);
        grid.add(rewardCard("Snack Voucher V1", "30 PTS", "Biscuits, breads, chips, and similar light snacks."), 1, 0);
        grid.add(rewardCard("Snack Voucher V2", "50 PTS", "Street food items such as fishball, kikiam, and kwek-kwek."), 2, 0);
        grid.add(rewardCard("Lunch Voucher", "100 PTS", "A full meal with rice redeemable at a campus food partner."), 3, 0);
        page.getChildren().add(grid);
        return page;
    }

    private VBox transactionHistoryPage() {
        VBox page = page("Transaction History", "A complete, detailed log of your past redemptions, active coupons, and point deductions.");
        page.getChildren().add(headerActions());
        page.getChildren().add(metrics(List.of(new Metric("##", "Total Points You Earned"), new Metric("##", "Total Coupons You Redeemed"))));

        HBox filters = new HBox(10);
        filters.setAlignment(Pos.CENTER_LEFT);
        ComboBox<String> couponTypeFilter = new ComboBox<>();
        couponTypeFilter.getItems().addAll("All coupon types", "School Supplies", "Snack V1", "Snack V2", "Lunch");
        couponTypeFilter.getSelectionModel().selectFirst();
        ComboBox<String> statusFilter = new ComboBox<>();
        statusFilter.getItems().addAll("All status", "Pending", "Fulfilled", "Redeemed");
        statusFilter.getSelectionModel().selectFirst();
        filters.getChildren().addAll(couponTypeFilter, statusFilter, new DatePicker(LocalDate.now()), new DatePicker(LocalDate.now()), secondary("Clear Filters", () -> { }));
        page.getChildren().add(filters);
        page.getChildren().add(table(
                List.of("#", "Date & Time", "Coupon Type", "Points", "Status", "Unique Code"),
                List.of(
                        List.of("001", "May 18, 2026 10:32AM", "Supply Coupon", "10 PTS", "Pending", "ISK-2026-0134"),
                        List.of("002", "May 18, 2026 2:14PM", "Snack V1 Coupon", "30 PTS", "Fulfilled", "ISK-2026-0090"),
                        List.of("003", "May 10, 2026 2:14PM", "Lunch Coupon", "100 PTS", "Redeemed", "ISK-2026-0055")
                )
        ));
        return page;
    }

    private VBox profilePage(boolean passwordMode) {
        VBox page = page("Student Profile", "Manage your personal information, update your display name, and adjust your account preferences.");
        page.getChildren().add(headerActions(passwordMode ? "Cancel Password Change" : "Save Changes", () -> showShell("Profile")));

        HBox overview = new HBox(16);
        overview.getChildren().addAll(
                card("Heavenlee Khim Morales", "@heavenlyyboddy_\nPUP Webmail\nheavenleekhimmorales@iskolarngbayan.pup.edu.ph"),
                metricCard(new Metric("September 2024", "Member Since")),
                metricCard(new Metric("##", "Total Bottles Collected")),
                metricCard(new Metric("##", "Coupons Redeemed"))
        );
        page.getChildren().add(overview);

        VBox form = cardBox();
        form.getChildren().addAll(title("Personal Information", "section-title"), copy("Changes saved immediately."), labeledField("Display Name", "Heavenlee Khim F. Morales"), labeledField("Username", "@heavenlyyboddy_"), labeledField("Age", "20"), copy("PUP webmail cannot be changed."));
        if (passwordMode) {
            form.getChildren().addAll(new Separator(), labeledPassword("Current Password", "Enter Current Password"), labeledPassword("New Password", "Min. 8 characters, 1 number, 1 special char"), labeledPassword("Confirm New Password", "Repeat New Password"), primary("Update Password", () -> showShell("Profile")));
        } else {
            form.getChildren().add(secondary("Change Password", () -> root.setCenter(scroll(profilePage(true)))));
        }
        page.getChildren().add(form);
        return page;
    }

    private void showSubmitDialog() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        VBox box = cardBox();
        box.setPadding(new Insets(24));
        box.getChildren().addAll(
                title("Submit Bottle", "section-title"),
                copy("Enter the number of bottles you are submitting at the collection point today."),
                copy("Only submit bottles you have physically brought to the collection point."),
                labeledField("Number of Bottles", "##"),
                new HBox(10, secondary("Cancel", dialog::close), primary("Submit", dialog::close))
        );
        Scene scene = new Scene(box, 420, 310);
        scene.getStylesheets().add(getClass().getResource("/styles/iskollect-lowfi.css").toExternalForm());
        dialog.setScene(scene);
        dialog.setTitle("Submit Bottle");
        dialog.showAndWait();
    }

    private void showLogout() {
        root.setLeft(null);
        VBox card = authCard();
        card.setAlignment(Pos.CENTER);
        card.getChildren().addAll(title("Successfully Logged Out", "auth-title"), copy("See you again! Thanks for supporting a cleaner and greener campus."), primary("Return to Login", this::showLogin));
        root.setCenter(centeredAuth(card));
    }

    private HBox headerActions() {
        return headerActions("+ Submit Bottles", this::showSubmitDialog);
    }

    private HBox headerActions(String actionText, Runnable action) {
        HBox bar = new HBox(12);
        bar.getStyleClass().add("top-bar");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        bar.getChildren().addAll(copy("June 18, 2026 11:11pm"), spacer, primary(actionText, action));
        return bar;
    }

    private Button nav(String name) {
        Button button = new Button(name);
        button.getStyleClass().add("nav-button");
        if (name.equals(activePage)) {
            button.getStyleClass().add("nav-button-active");
        }
        button.setOnAction(e -> showShell(name));
        return button;
    }

    private VBox page(String title, String copy) {
        VBox page = new VBox(18);
        page.getStyleClass().add("page");
        page.getChildren().addAll(title(title, "page-title"), copy(copy));
        return page;
    }

    private ScrollPane scroll(VBox content) {
        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        return scroll;
    }

    private VBox authCard() {
        VBox card = new VBox();
        card.getStyleClass().add("auth-card");
        return card;
    }

    private StackPane centeredAuth(VBox card) {
        StackPane pane = new StackPane(card);
        pane.getStyleClass().add("auth-pane");
        return pane;
    }

    private VBox brandBlock() {
        VBox box = new VBox(2);
        Label brand = new Label("ISKollect");
        brand.getStyleClass().add("brand-mark");
        Label subtitle = new Label("Garbage Recycling Rewards System");
        subtitle.getStyleClass().add("brand-subtitle");
        Label tagline = new Label("Tapat ko, LinISKO.");
        tagline.getStyleClass().add("muted");
        box.getChildren().addAll(brand, subtitle, tagline);
        return box;
    }

    private Label title(String text, String styleClass) {
        Label label = new Label(text);
        label.getStyleClass().add(styleClass);
        label.setWrapText(true);
        return label;
    }

    private Label copy(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("page-copy");
        label.setWrapText(true);
        return label;
    }

    private VBox labeledField(String label, String prompt) {
        VBox box = new VBox(6);
        Label l = title(label, "small-title");
        TextField field = new TextField();
        field.setPromptText(prompt);
        box.getChildren().addAll(l, field);
        return box;
    }

    private VBox labeledPassword(String label, String prompt) {
        VBox box = new VBox(6);
        Label l = title(label, "small-title");
        PasswordField field = new PasswordField();
        field.setPromptText(prompt);
        box.getChildren().addAll(l, field);
        return box;
    }

    private VBox checklist(String... items) {
        VBox box = new VBox(4);
        for (String item : items) {
            CheckBox cb = new CheckBox(item);
            cb.setSelected(true);
            box.getChildren().add(cb);
        }
        return box;
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

    private Button linkButton(String text, Runnable action) {
        Button button = new Button(text);
        button.getStyleClass().add("outline-button");
        button.setOnAction(e -> action.run());
        return button;
    }

    private HBox metrics(List<Metric> metrics) {
        HBox box = new HBox(14);
        for (Metric metric : metrics) {
            box.getChildren().add(metricCard(metric));
        }
        return box;
    }

    private VBox metricCard(Metric metric) {
        VBox box = new VBox(4);
        box.getStyleClass().add("metric-card");
        box.getChildren().addAll(title(metric.value(), "metric-value"), title(metric.label(), "metric-label"));
        return box;
    }

    private VBox cardBox() {
        VBox box = new VBox(12);
        box.getStyleClass().add("card");
        return box;
    }

    private VBox card(String heading, String body) {
        VBox box = cardBox();
        box.getChildren().addAll(title(heading, "section-title"), copy(body));
        return box;
    }

    private VBox leaderboardPreview() {
        VBox box = card("Leaderboard", "Top 3 by total points");
        box.setPrefWidth(330);
        box.getChildren().addAll(
                copy("1. Heavenlee Morales - 52 bottles - 10 pts"),
                copy("2. Precious Peligrino - 48 bottles - 10 pts"),
                copy("3. Clint Commendador - 43 bottles - 10 pts"),
                secondary("See all ranking", () -> showShell("Leaderboard"))
        );
        return box;
    }

    private VBox recentTransactions() {
        VBox box = cardBox();
        box.setPrefWidth(520);
        box.getChildren().addAll(title("Recent Transactions", "section-title"), table(
                List.of("#", "Date & Time", "Coupon Type", "Points", "Status"),
                List.of(
                        List.of("001", "May 18, 10:32AM", "Supply Coupon", "10 PTS", "Pending"),
                        List.of("002", "May 18, 2:14PM", "Snack V1", "30 PTS", "Fulfilled"),
                        List.of("003", "May 10, 12:00PM", "Lunch", "100 PTS", "Redeemed")
                )
        ));
        return box;
    }

    private VBox reminderCard() {
        VBox box = card("Reminder", "Target the bottle milestone to level up your weekly badge.");
        box.setPrefWidth(260);
        box.getChildren().addAll(placeholder("bottle png/svg", 110, 90), placeholder("badge png/svg", 110, 90));
        return box;
    }

    private HBox bottleStrip() {
        HBox strip = new HBox(12);
        strip.getChildren().add(title("## of bottles", "section-title"));
        for (int i = 0; i < 4; i++) {
            strip.getChildren().add(placeholder("bottle\npng/svg", 120, 90));
        }
        return strip;
    }

    private VBox progressCard() {
        VBox box = cardBox();
        box.setPrefWidth(450);
        ProgressBar bar = new ProgressBar(0.68);
        bar.setMaxWidth(Double.MAX_VALUE);
        box.getChildren().addAll(title("Badge Progress", "section-title"), copy("Milestone to achieve next badge level."), bar, copy("68% complete"));
        return box;
    }

    private VBox badgeHistoryCard() {
        VBox box = cardBox();
        box.setPrefWidth(520);
        box.getChildren().addAll(title("Badge History", "section-title"), copy("All weekly badges since you joined."), table(
                List.of("Week", "Date Range", "Badge Earned", "Total Bottles"),
                List.of(
                        List.of("20", "May 12-18, 2026", "Constellation (Lv.5)", "35 bottles"),
                        List.of("19", "May 5-11, 2026", "Emerald", "24 bottles"),
                        List.of("18", "Apr 28-May 4, 2026", "Emerald (Lv.3)", "15 bottles")
                )
        ));
        return box;
    }

    private VBox podiumCard(String rank, String name, String bottles, String points) {
        VBox box = cardBox();
        box.setPrefWidth(300);
        box.setAlignment(Pos.CENTER);
        box.getChildren().addAll(placeholder("MEDAL", 90, 70), title("#" + rank, "metric-value"), title(name, "section-title"), copy(bottles), copy(points));
        return box;
    }

    private VBox rewardCard(String name, String points, String description) {
        VBox box = cardBox();
        box.setPrefWidth(250);
        box.getChildren().addAll(placeholder("supply/snack\npng/svg", 210, 120), title(name, "section-title"), title(points, "metric-value"), copy(description), primary("Redeem", () -> { }));
        return box;
    }

    private Label placeholder(String text, double width, double height) {
        Label label = new Label(text);
        label.getStyleClass().add("placeholder");
        label.setPrefSize(width, height);
        label.setWrapText(true);
        return label;
    }

    private Label tab(String text, boolean active) {
        Label label = new Label(text);
        label.getStyleClass().add("screen-tab");
        if (active) {
            label.getStyleClass().add("screen-tab-active");
        }
        return label;
    }

    private GridPane table(List<String> headers, List<List<String>> rows) {
        GridPane grid = new GridPane();
        grid.getStyleClass().add("table-grid");
        grid.setMaxWidth(Double.MAX_VALUE);
        for (int i = 0; i < headers.size(); i++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setHgrow(Priority.ALWAYS);
            cc.setPercentWidth(100.0 / headers.size());
            grid.getColumnConstraints().add(cc);
            Label header = new Label(headers.get(i).toUpperCase());
            header.getStyleClass().add("table-header");
            header.setMaxWidth(Double.MAX_VALUE);
            grid.add(header, i, 0);
        }
        for (int r = 0; r < rows.size(); r++) {
            List<String> row = rows.get(r);
            for (int c = 0; c < headers.size(); c++) {
                Label cell = new Label(c < row.size() ? row.get(c) : "");
                cell.getStyleClass().add("table-cell-lite");
                cell.setWrapText(true);
                cell.setMaxWidth(Double.MAX_VALUE);
                grid.add(cell, c, r + 1);
            }
        }
        return grid;
    }

    private record Metric(String value, String label) {
    }
}
