package com.iskollect;

import java.sql.Connection;
import com.iskollect.util.DBConnection;

public class TestDatabaseConnection {

    public static void main(String[] args) {
        try {
            Connection conn =
                DBConnection.getInstance().getConnection();

            System.out.println("SUCCESS");
            System.out.println("Connected to:");
            System.out.println(conn.getMetaData().getURL());

        } catch (Exception e) {
            System.out.println("FAILED");
            e.printStackTrace();
        }
    }
}