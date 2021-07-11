package ru.akh.spring_amqp.converter;

import org.springframework.core.convert.converter.Converter;

import ru.akh.spring_amqp.schema.BookContent;

public class BookContentReadConverter implements Converter<BookContent, ru.akh.spring_amqp.dto.BookContent> {

    public static final BookContentReadConverter INSTANCE = new BookContentReadConverter();

    private BookContentReadConverter() {
    }

    @Override
    public ru.akh.spring_amqp.dto.BookContent convert(BookContent source) {
        ru.akh.spring_amqp.dto.BookContent bookContent = new ru.akh.spring_amqp.dto.BookContent();
        bookContent.setId(source.getId());
        bookContent.setFileName(source.getFileName());
        bookContent.setMimeType(source.getMimeType());
        bookContent.setContent(source.getContent());
        bookContent.setSize(source.getSize());

        return bookContent;
    }

}
