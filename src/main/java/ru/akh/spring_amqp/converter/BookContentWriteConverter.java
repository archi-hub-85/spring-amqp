package ru.akh.spring_amqp.converter;

import org.springframework.core.convert.converter.Converter;

import ru.akh.spring_amqp.schema.BookContent;
import ru.akh.spring_amqp.schema.ObjectFactory;

public class BookContentWriteConverter implements Converter<ru.akh.spring_amqp.dto.BookContent, BookContent> {

    public static final BookContentWriteConverter INSTANCE = new BookContentWriteConverter();

    private final ObjectFactory factory = new ObjectFactory();

    private BookContentWriteConverter() {
    }

    @Override
    public BookContent convert(ru.akh.spring_amqp.dto.BookContent source) {
        BookContent bookContent = factory.createBookContent();
        bookContent.setId(source.getId());
        bookContent.setFileName(source.getFileName());
        bookContent.setMimeType(source.getMimeType());
        bookContent.setContent(source.getContent());
        bookContent.setSize(source.getSize());

        return bookContent;
    }

}
