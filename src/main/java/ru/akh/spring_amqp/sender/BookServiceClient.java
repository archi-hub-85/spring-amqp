package ru.akh.spring_amqp.sender;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.amqp.core.AmqpTemplate;

import ru.akh.spring_amqp.converter.BookContentReadConverter;
import ru.akh.spring_amqp.converter.BookContentWriteConverter;
import ru.akh.spring_amqp.converter.BookReadConverter;
import ru.akh.spring_amqp.converter.BookWriteConverter;
import ru.akh.spring_amqp.dto.Book;
import ru.akh.spring_amqp.dto.BookContent;
import ru.akh.spring_amqp.schema.BookField;
import ru.akh.spring_amqp.schema.GetBooksByAuthorRequest;
import ru.akh.spring_amqp.schema.GetBooksByAuthorResponse;
import ru.akh.spring_amqp.schema.GetContentRequest;
import ru.akh.spring_amqp.schema.GetContentResponse;
import ru.akh.spring_amqp.schema.GetRequest;
import ru.akh.spring_amqp.schema.GetResponse;
import ru.akh.spring_amqp.schema.GetTopBooksRequest;
import ru.akh.spring_amqp.schema.GetTopBooksResponse;
import ru.akh.spring_amqp.schema.ObjectFactory;
import ru.akh.spring_amqp.schema.PutContentRequest;
import ru.akh.spring_amqp.schema.PutRequest;
import ru.akh.spring_amqp.schema.PutResponse;

public class BookServiceClient {

    private final AmqpTemplate template;

    private final ObjectFactory factory = new ObjectFactory();

    public BookServiceClient(AmqpTemplate template) {
        this.template = template;
    }

    private void send(Object request) {
        template.convertAndSend(request);
    }

    @SuppressWarnings("unchecked")
    private <T> T sendAndReceive(Object request, Class<T> responseClass) {
        return (T) template.convertSendAndReceive(request);
    }

    public Book get(long id) {
        GetRequest request = factory.createGetRequest();
        request.setId(id);

        GetResponse response = sendAndReceive(request, GetResponse.class);
        return BookReadConverter.INSTANCE.convert(response.getResult());
    }

    public long put(Book book) {
        PutRequest request = factory.createPutRequest();
        request.setBook(BookWriteConverter.INSTANCE.convert(book));

        PutResponse response = sendAndReceive(request, PutResponse.class);
        return response.getResult();
    }

    public List<Book> getTopBooks(Book.Field field, int limit) {
        GetTopBooksRequest request = factory.createGetTopBooksRequest();
        request.setField(BookField.valueOf(field.name()));
        request.setLimit(limit);

        GetTopBooksResponse response = sendAndReceive(request, GetTopBooksResponse.class);
        return response.getResult().stream().map(BookReadConverter.INSTANCE::convert).collect(Collectors.toList());
    }

    public List<Book> getBooksByAuthor(String author) {
        GetBooksByAuthorRequest request = factory.createGetBooksByAuthorRequest();
        request.setAuthor(author);

        GetBooksByAuthorResponse response = sendAndReceive(request, GetBooksByAuthorResponse.class);
        return response.getResult().stream().map(BookReadConverter.INSTANCE::convert).collect(Collectors.toList());
    }

    public BookContent getContent(long id) {
        GetContentRequest request = factory.createGetContentRequest();
        request.setId(id);

        GetContentResponse response = sendAndReceive(request, GetContentResponse.class);
        return BookContentReadConverter.INSTANCE.convert(response.getResult());
    }

    public void putContent(BookContent content) {
        PutContentRequest request = factory.createPutContentRequest();
        request.setContent(BookContentWriteConverter.INSTANCE.convert(content));

        send(request);
    }

}
