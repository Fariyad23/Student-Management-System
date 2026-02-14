package com.fariyad.an.St_management.ui;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class StudentUI extends JFrame {

    private JTable table;
    private DefaultTableModel model;
    private JTextField nameField, emailField;

    // ✅ Change only this if your controller mapping is different
    // Examples:
    // "http://localhost:8081/student"
    // "http://localhost:8081/students"
    // "http://localhost:8081/api/student"
    private final String BASE_URL = "http://localhost:8080/student";

    public StudentUI() {
        setTitle("🎓 Student Management System");
        setSize(850, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 248, 255));
        add(panel);

        JLabel title = new JLabel("Student Management System", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 26));
        title.setForeground(Color.WHITE);
        title.setOpaque(true);
        title.setBackground(new Color(70, 130, 180));
        title.setPreferredSize(new Dimension(100, 55));
        panel.add(title, BorderLayout.NORTH);

        model = new DefaultTableModel(new String[]{"ID", "Name", "Email"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        table = new JTable(model);
        table.setRowHeight(26);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        bottomPanel.setBackground(new Color(224, 255, 255));

        nameField = new JTextField(12);
        emailField = new JTextField(16);

        JButton addBtn = new JButton("Add Student");
        JButton loadBtn = new JButton("Load Students");
        JButton deleteBtn = new JButton("Delete Student");
        JButton clearBtn = new JButton("Clear");

        styleButton(addBtn, new Color(60, 179, 113));
        styleButton(loadBtn, new Color(30, 144, 255));
        styleButton(deleteBtn, new Color(220, 20, 60));
        styleButton(clearBtn, new Color(255, 140, 0));

        bottomPanel.add(new JLabel("Name:"));
        bottomPanel.add(nameField);

        bottomPanel.add(new JLabel("Email:"));
        bottomPanel.add(emailField);

        bottomPanel.add(addBtn);
        bottomPanel.add(loadBtn);
        bottomPanel.add(deleteBtn);
        bottomPanel.add(clearBtn);

        panel.add(bottomPanel, BorderLayout.SOUTH);

        addBtn.addActionListener(this::addStudent);
        loadBtn.addActionListener(e -> loadStudents());
        deleteBtn.addActionListener(e -> deleteStudent());
        clearBtn.addActionListener(e -> clearFields());

        setVisible(true);

        // Load on start
        loadStudents();
    }

    private void styleButton(JButton button, Color bg) {
        button.setBackground(bg);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 13));
        button.setFocusPainted(false);
    }

    private void clearFields() {
        nameField.setText("");
        emailField.setText("");
        nameField.requestFocus();
    }

    private void loadStudents() {
        try {
            model.setRowCount(0);

            HttpURLConnection conn = openConnection(BASE_URL, "GET");
            int code = conn.getResponseCode();
            String response = readResponse(conn, code);

            if (code >= 200 && code < 300) {
                if (response == null || response.trim().isEmpty()) {
                    // empty list
                    return;
                }

                JSONArray array = new JSONArray(response);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);

                    long id = obj.optLong("id", 0);
                    String name = obj.optString("name", "");
                    String email = obj.optString("email", "");

                    model.addRow(new Object[]{id, name, email});
                }
            } else {
                showError("Load failed", code, response);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading students:\n" + ex.getMessage());
        }
    }

    private void addStudent(ActionEvent e) {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();

        if (name.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill Name and Email!");
            return;
        }

        try {
            HttpURLConnection conn = openConnection(BASE_URL, "POST");
            conn.setDoOutput(true);

            JSONObject json = new JSONObject();
            json.put("name", name);
            json.put("email", email);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(json.toString().getBytes(StandardCharsets.UTF_8));
            }

            int code = conn.getResponseCode();
            String response = readResponse(conn, code);

            if (code >= 200 && code < 300) {
                JOptionPane.showMessageDialog(this, "Student Added Successfully!");
                clearFields();
                loadStudents();
            } else {
                showError("Add failed", code, response);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error adding student:\n" + ex.getMessage());
        }
    }

    private void deleteStudent() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a student first!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this student?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            long id = Long.parseLong(model.getValueAt(row, 0).toString());

            HttpURLConnection conn = openConnection(BASE_URL + "/" + id, "DELETE");
            int code = conn.getResponseCode();
            String response = readResponse(conn, code);

            if (code >= 200 && code < 300) {
                JOptionPane.showMessageDialog(this, "Student Deleted Successfully!");
                loadStudents();
            } else {
                showError("Delete failed", code, response);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error deleting student:\n" + ex.getMessage());
        }
    }

    private HttpURLConnection openConnection(String url, String method) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestMethod(method);
        conn.setConnectTimeout(8000);
        conn.setReadTimeout(8000);

        // ✅ Helpful headers
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        return conn;
    }

    private void showError(String title, int code, String response) {
        String msg = title + "\nHTTP " + code + "\n\n" + (response == null ? "" : response);
        JOptionPane.showMessageDialog(this, msg);
    }

    private String readResponse(HttpURLConnection conn, int code) throws IOException {
        InputStream is = (code >= 200 && code < 300) ? conn.getInputStream() : conn.getErrorStream();
        if (is == null) return "";
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) sb.append(line);
            return sb.toString();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(StudentUI::new);
    }
}
