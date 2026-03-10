package com.example.contact_manager;

import com.example.contact_manager.config.AppConfig;
import com.example.contact_manager.model.Contact;
import com.example.contact_manager.service.ContactService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        
        try (AnnotationConfigApplicationContext context = 
                new AnnotationConfigApplicationContext(AppConfig.class)) {

            ContactService contactService = context.getBean(ContactService.class);
            
            Scanner scanner = new Scanner(System.in);
            
            while (true) {
                printMenu();
                String choice = scanner.nextLine();
                
                switch (choice) {
                    case "1":
                        loadContactsFromCsv(contactService, scanner);
                        break;
                    case "2":
                        showAllContacts(contactService);
                        break;
                    case "3":
                        System.out.println("До свидания!");
                        return;
                    default:
                        System.out.println("Неверный выбор. Пожалуйста, выберите 1, 2 или 3.");
                }
                
                System.out.println("\nНажмите Enter для продолжения...");
                scanner.nextLine();
            }
        }
    }
    
    private static void printMenu() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("МЕНЕДЖЕР КОНТАКТОВ");
        System.out.println("=".repeat(50));
        System.out.println("1. Загрузить контакты из CSV-файла");
        System.out.println("2. Показать все контакты");
        System.out.println("3. Выход");
        System.out.print("Выберите действие (1-3): ");
    }
    
    private static void loadContactsFromCsv(ContactService contactService, Scanner scanner) {
        System.out.println("\n--- ЗАГРУЗКА КОНТАКТОВ ИЗ CSV ---");
        System.out.println("Формат файла: Имя Фамилия,Номер телефона,Email");
        System.out.println("Пример: Иван Иванов,+71234567890,iivanov@gmail.com");
        System.out.print("Введите путь к CSV-файлу: ");
        
        String filePath = scanner.nextLine().trim();
        
        try {
            System.out.println("Загрузка файла...");
            long startTime = System.currentTimeMillis();
            
            int count = contactService.loadContactsFromCsv(filePath);
            
            long endTime = System.currentTimeMillis();
            double seconds = (endTime - startTime) / 1000.0;
            
            System.out.println(" Успешно загружено " + count + " контактов за " + seconds + " сек.");
            
        } catch (IOException e) {
            System.err.println(" Ошибка при чтении файла: " + e.getMessage());
            System.err.println("Проверьте, что файл существует и доступен для чтения.");
        } catch (Exception e) {
            System.err.println(" Непредвиденная ошибка: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void showAllContacts(ContactService contactService) {
        System.out.println("\n--- ВСЕ КОНТАКТЫ В БАЗЕ ДАННЫХ ---");
        
        List<Contact> contacts = contactService.getAllContacts();
        
        if (contacts.isEmpty()) {
            System.out.println(" Контактов пока нет.");
            System.out.println("Загрузите контакты из CSV-файла.");
        } else {
            System.out.println(" Найдено контактов: " + contacts.size());
            System.out.println("-".repeat(80));
            System.out.printf("%-5s %-25s %-20s %-30s%n", "ID", "Имя Фамилия", "Телефон", "Email");
            System.out.println("-".repeat(80));
            
            for (Contact contact : contacts) {
                String fullName = contact.getFirstName() + " " + contact.getLastName();
                System.out.printf("%-5d %-25s %-20s %-30s%n",
                        contact.getId(),
                        truncate(fullName, 25),
                        truncate(contact.getPhoneNumber(), 20),
                        truncate(contact.getEmail(), 30));
            }
            System.out.println("-".repeat(80));
        }
    }
    
    private static String truncate(String str, int length) {
        if (str == null) return "";
        if (str.length() <= length) return str;
        return str.substring(0, length - 3) + "...";
    }
}