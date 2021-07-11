package ru.akh.spring_amqp;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import ru.akh.spring_amqp.dao.BookRepository;
import ru.akh.spring_amqp.dto.Author;
import ru.akh.spring_amqp.dto.Book;
import ru.akh.spring_amqp.dto.BookContent;
import ru.akh.spring_amqp.sender.BookServiceClient;

@SpringBootTest
@Import(ClientConfig.class)
@ActiveProfiles("embedded")
public class BookServiceTest extends AbstractTest {

    @MockBean
    private BookRepository repository;

    @Autowired
    private BookServiceClient client;

    @Test
    public void testGet() {
        long id = 1;

        Book book = createBook(id, "title1", 2021, 2L, "name1");
        Mockito.when(repository.get(id)).thenReturn(book);

        Book actualBook = client.get(id);
        assertEquals(book, actualBook, "book.");
    }

    @Test
    public void testPutBook() {
        long id = 1;
        Book book = createBook(id, "title", 2021, 1L, "author");
        Mockito.when(repository.put(Mockito.any())).thenReturn(id);

        long result = client.put(book);
        Assertions.assertEquals(id, result, "result");
    }

    @Test
    public void testGetTopBooks() {
        Book book1 = createBook(1L, "title1", 2021, 1L, "name1");
        Book book2 = createBook(2L, "title2", 2022, 2L, "name2");
        Mockito.when(repository.getTopBooks(Book.Field.ID, 2))
                .thenReturn(Arrays.asList(book1, book2));

        List<Book> result = client.getTopBooks(Book.Field.ID, 2);
        Assertions.assertEquals(2, result.size(), "result.size");
        assertEquals(book1, result.get(0), "book1.");
        assertEquals(book2, result.get(1), "book2.");
    }

    @Test
    public void testGetBooksByAuthor() {
        String author = "author";
        Book book1 = createBook(1L, "title1", 2021, 1L, author);
        Book book2 = createBook(2L, "title2", 2022, 1L, author);
        Mockito.when(repository.getBooksByAuthor(author))
                .thenReturn(Arrays.asList(book1, book2));

        List<Book> result = client.getBooksByAuthor(author);
        Assertions.assertEquals(2, result.size(), "result.size");
        assertEquals(book1, result.get(0), "book1.");
        assertEquals(book2, result.get(1), "book2.");
    }

    @Test
    public void testGetContent() throws Exception {
        long id = 1;
        BookContent content = createBookContent(id, "test.txt", "test content");
        Mockito.when(repository.getContent(id)).thenReturn(content);

        BookContent actualContent = client.getContent(id);
        assertEquals(content, actualContent, "bookContent.");
    }

    @Test
    public void testPutContent() throws Exception {
        BookContent content = createBookContent(2, "test.txt", "test content");

        client.putContent(content);

        ArgumentCaptor<BookContent> captor = ArgumentCaptor.forClass(BookContent.class);
        Mockito.verify(repository).putContent(captor.capture());
        BookContent actualContent = captor.getValue();
        assertEquals(content, actualContent, "bookContent.");
    }

    private static Book createBook(Long id, String title, int year, Long authorId, String authorName) {
        Book book = new Book();
        book.setId(id);
        book.setTitle(title);
        book.setYear(year);

        Author author = new Author();
        author.setId(authorId);
        author.setName(authorName);
        book.setAuthor(author);

        return book;
    }

    private static BookContent createBookContent(long id, String fileName, String content) {
        BookContent bookContent = new BookContent();
        bookContent.setId(id);
        bookContent.setFileName(fileName);
        bookContent.setMimeType("text/plain");
        byte[] contentBytes = content.getBytes(StandardCharsets.UTF_8);
        bookContent.setContent(contentBytes);
        bookContent.setSize(contentBytes.length);

        return bookContent;
    }

    private static void assertEquals(Book expected, Book actual, String messagePrefix) {
        Assertions.assertEquals(expected.getId(), actual.getId(), messagePrefix + "id");
        Assertions.assertEquals(expected.getTitle(), actual.getTitle(), messagePrefix + "title");
        Assertions.assertEquals(expected.getYear(), actual.getYear(), messagePrefix + "year");
        Assertions.assertNotNull(actual.getAuthor(), messagePrefix + "author");
        Assertions.assertEquals(expected.getAuthor().getId(), actual.getAuthor().getId(), messagePrefix + "author.id");
        Assertions.assertEquals(expected.getAuthor().getName(), actual.getAuthor().getName(),
                messagePrefix + "author.name");
    }

    private static void assertEquals(BookContent expected, BookContent actual, String messagePrefix) {
        Assertions.assertEquals(expected.getFileName(), actual.getFileName(), messagePrefix + "fileName");
        Assertions.assertEquals(expected.getMimeType(), actual.getMimeType(), messagePrefix + "mimeType");
        Assertions.assertArrayEquals(expected.getContent(), actual.getContent(), messagePrefix + "content");
        Assertions.assertEquals(expected.getSize(), actual.getSize(), messagePrefix + "size");
    }

}
