package ru.akh.spring_amqp.receiver;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import ru.akh.spring_amqp.converter.BookContentReadConverter;
import ru.akh.spring_amqp.converter.BookContentWriteConverter;
import ru.akh.spring_amqp.converter.BookReadConverter;
import ru.akh.spring_amqp.converter.BookWriteConverter;
import ru.akh.spring_amqp.dao.BookRepository;
import ru.akh.spring_amqp.dto.Book;
import ru.akh.spring_amqp.dto.BookContent;
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

@Component
@RabbitListener(queues = "${ru.akh.spring-amqp.queue}")
public class BookServiceListener {

    private final BookRepository repository;

    private final ObjectFactory factory = new ObjectFactory();

    public BookServiceListener(BookRepository repository) {
        this.repository = repository;
    }

    @RabbitHandler
    public GetResponse get(GetRequest request) {
        Book book = repository.get(request.getId());

        GetResponse response = factory.createGetResponse();
        response.setResult(BookWriteConverter.INSTANCE.convert(book));

        return response;
    }

    @RabbitHandler
    public PutResponse put(PutRequest request) {
        Book book = BookReadConverter.INSTANCE.convert(request.getBook());
        long id = repository.put(book);

        PutResponse response = factory.createPutResponse();
        response.setResult(id);
        return response;
    }

    @RabbitHandler
    public GetTopBooksResponse getTopBooks(GetTopBooksRequest request) {
        Book.Field field = Book.Field.valueOf(request.getField().name());
        List<Book> books = repository.getTopBooks(field, request.getLimit());

        GetTopBooksResponse response = factory.createGetTopBooksResponse();
        if (!books.isEmpty()) {
            response.getResult()
                    .addAll(books.stream().map(BookWriteConverter.INSTANCE::convert).collect(Collectors.toList()));
        }
        return response;
    }

    @RabbitHandler
    public GetBooksByAuthorResponse getBooksByAuthor(GetBooksByAuthorRequest request) {
        List<Book> books = repository.getBooksByAuthor(request.getAuthor());

        GetBooksByAuthorResponse response = factory.createGetBooksByAuthorResponse();
        if (!books.isEmpty()) {
            response.getResult()
                    .addAll(books.stream().map(BookWriteConverter.INSTANCE::convert).collect(Collectors.toList()));
        }
        return response;
    }

    @RabbitHandler
    public GetContentResponse getContent(GetContentRequest request) {
        BookContent bookContent = repository.getContent(request.getId());

        GetContentResponse response = factory.createGetContentResponse();
        response.setResult(BookContentWriteConverter.INSTANCE.convert(bookContent));
        return response;
    }

    @RabbitHandler
    public void putContent(PutContentRequest request) {
        BookContent bookContent = BookContentReadConverter.INSTANCE.convert(request.getContent());
        repository.putContent(bookContent);
    }

}
