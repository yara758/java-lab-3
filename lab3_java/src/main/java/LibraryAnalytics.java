import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

public class LibraryAnalytics {

    private List<Visitor> visitors;

    public LibraryAnalytics(List<Visitor> visitors) {
        this.visitors = visitors;
    }

    // Задание 1
    public void printVisitors() {
        System.out.println("Список посетителей:");
        visitors.forEach(v -> System.out.println(v.getName() + " " + v.getSurname()));
        System.out.println("Количество посетителей: " + visitors.size());
    }

    // Задание 2
    public void printUniqueBooks() {
        Set<Book> uniqueBooks = visitors.stream()
                .flatMap(v -> v.getFavoriteBooks().stream())
                .collect(Collectors.toSet());
        System.out.println("Список уникальных книг:");
        uniqueBooks.forEach(b -> System.out.println(b.getName() + " by " + b.getAuthor()));
        System.out.println("Количество уникальных книг: " + uniqueBooks.size());
    }

    // Задание 3
    public void printBooksSortedByYear() {
        List<Book> sortedBooks = visitors.stream()
                .flatMap(v -> v.getFavoriteBooks().stream())
                .distinct()  // Удаляем дубликаты (нужно переопределить equals() и hashCode() в классе Book)
                .sorted(Comparator.comparingInt(Book::getPublishingYear))
                .collect(Collectors.toList());

        System.out.println("Список книг, отсортированных по году издания (без повторений):");
        sortedBooks.forEach(b -> System.out.println(b.getName() + " (" + b.getPublishingYear() + ")"));
    }

    // Задание 4
    public void checkIfJaneAustenExists() {
        boolean hasJaneAusten = visitors.stream()
                .flatMap(v -> v.getFavoriteBooks().stream())
                .anyMatch(b -> "Jane Austen".equals(b.getAuthor()));
        System.out.println("Есть ли книга автора Jane Austen в избранном: " + hasJaneAusten);
    }

    // Задание 5
    public void printMaxFavoriteBooksCount() {
        int maxBooks = visitors.stream()
                .mapToInt(v -> v.getFavoriteBooks().size())
                .max()
                .orElse(0);
        System.out.println("Максимальное число добавленных в избранное книг: " + maxBooks);
    }

    // Задание 6
    public void sendSmsToSubscribedVisitors() {
        double averageBooks = visitors.stream()
                .mapToInt(v -> v.getFavoriteBooks().size())
                .average()
                .orElse(0);
        List<SmsMessage> smsMessages = visitors.stream()
                .filter(Visitor::isSubscribed)
                .map(v -> {
                    int bookCount = v.getFavoriteBooks().size();
                    String message;
                    if (bookCount > averageBooks) {
                        message = "You are a bookworm!";
                    } else if (bookCount < averageBooks) {
                        message = "Read more!";
                    } else {
                        message = "Fine.";
                    }
                    return new SmsMessage(v.getPhone(), message);
                })
                .collect(Collectors.toList());
        System.out.println("SMS сообщения:");
        smsMessages.forEach(sms -> System.out.println(sms.getPhone() + ": " + sms.getMessage()));
    }

    // Метод для загрузки данных из JSON-файла
    public static List<Visitor> loadVisitorsFromJson(String filePath) {
        Gson gson = new Gson();
        List<Visitor> visitors = new ArrayList<>();
        try (FileReader reader = new FileReader(filePath)) {
            Type listType = new TypeToken<ArrayList<Visitor>>() {}.getType();
            visitors = gson.fromJson(reader, listType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return visitors;
    }

    public static void main(String[] args) {
        List<Visitor> visitors = loadVisitorsFromJson("src/main/resources/books.json");
        LibraryAnalytics analytics = new LibraryAnalytics(visitors);

        analytics.printVisitors(); // Задание 1
        analytics.printUniqueBooks(); // Задание 2
        analytics.printBooksSortedByYear(); // Задание 3
        analytics.checkIfJaneAustenExists(); // Задание 4
        analytics.printMaxFavoriteBooksCount(); // Задание 5
        analytics.sendSmsToSubscribedVisitors(); // Задание 6
    }
}