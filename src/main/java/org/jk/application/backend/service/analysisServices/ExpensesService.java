package org.jk.application.backend.service.analysisServices;

import org.jk.application.backend.model.Expense;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ExpensesService {

    private final static String filePath = "src/main/resources/files/expenses";
    private static int lastId;

    public ExpensesService() {
    }

    public static List<Expense> getExpenses() {
        List<Expense> expenses = new ArrayList<>();

        try {
            FileReader fileReader = new FileReader(getFile());

            int i;
            StringBuilder line = new StringBuilder();
            while ((i = fileReader.read()) != -1) {
                char c = (char) i;

                if (c == '\n') {
                    expenses.add(getExpense(line.substring(0, line.length()).split(";")));
                    line = new StringBuilder();
                } else {
                    line.append(c);
                }
            }

            fileReader.close();

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return expenses;
    }

    public static void addExpense(Expense expense) {
        try {
            FileWriter fileWriter = new FileWriter(getFile(), true);
            fileWriter.write((lastId + 1) + ";" + expense.getDate() + ";" + expense.getName() + ";" + expense.getPrice() + "\n");
            fileWriter.close();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static void removeExpense(List<Integer> ids) {
        try {
            FileReader fileReader = new FileReader(getFile());

            int i;
            boolean contains = false;
            StringBuilder file = new StringBuilder();
            StringBuilder line = new StringBuilder();
            while ((i = fileReader.read()) != -1) {
                char c = (char) i;

                if (c == '\n') {
                    for (int id : ids) {
                        if (contains)
                            continue;
                        contains = line.toString().split(";")[0].equals(String.valueOf(id));
                    }
                    if (!contains) {
                        file.append(line).append("\n");
                    }
                    contains = false;
                    line = new StringBuilder();
                } else {
                    line.append(c);
                }
            }
            fileReader.close();

            FileWriter fileWriter = new FileWriter(getFile());
            fileWriter.write(file.toString());
            fileWriter.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private static File getFile() {
        File[] files = new File(ExpensesService.filePath).listFiles();
        if (Objects.nonNull(files) && files.length < 2) {
            return files[0];
        } else {
            return new File("null");
        }
    }

    private static Expense getExpense(String[] arr) {
        int id = getInt(arr[0]);

        String[] dateArr = arr[1].split("-");
        LocalDate date = LocalDate.of(getInt(dateArr[0]), getInt(dateArr[1]), getInt(dateArr[2]));
        double price = Double.parseDouble(arr[3]);
        lastId = id;
        return new Expense(id, date, arr[2], price);
    }

    private static int getInt(String str) {
        return Integer.parseInt(str);
    }
}
