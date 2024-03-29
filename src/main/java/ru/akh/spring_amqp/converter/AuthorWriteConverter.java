package ru.akh.spring_amqp.converter;

import org.springframework.core.convert.converter.Converter;

import ru.akh.spring_amqp.schema.Author;
import ru.akh.spring_amqp.schema.ObjectFactory;

public class AuthorWriteConverter implements Converter<ru.akh.spring_amqp.dto.Author, Author> {

    public static final AuthorWriteConverter INSTANCE = new AuthorWriteConverter();

    private final ObjectFactory factory = new ObjectFactory();

    private AuthorWriteConverter() {
    }

    @Override
    public Author convert(ru.akh.spring_amqp.dto.Author source) {
        Author author = factory.createAuthor();
        author.setId(source.getId());
        author.setName(source.getName());

        return author;
    }

}
