package com.example.theultimateuser.repository;

import com.example.theultimateuser.dto.UserDTO;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Implementation of {@link UserRepository} that utilizes a CSV file as the primary data store
 * Handles the serialization and deserialization of {@link UserDTO} records
 */
@Repository
public class CsvUserRepository implements UserRepository {
    private final File userCsvFile = new File("data/magmutualuserinfo.csv");
    private final File modifiedFile = new File("data/modifieduserinfo.csv");
    private final CsvMapper mapper;
    private final CsvSchema schema;

    public CsvUserRepository() {
        this.mapper = new CsvMapper();
        this.schema = mapper.schemaFor(UserDTO.class).withHeader().withColumnSeparator(',');
        this.mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
    }

    @Override
    public List<UserDTO> readAllUsers() {
        File fileToRead = (modifiedFile.exists() && modifiedFile.length() > 0) ? modifiedFile : userCsvFile;
        try {
            MappingIterator<UserDTO> iterator = mapper.readerFor(UserDTO.class)
                    .with(schema)
                    .readValues(fileToRead);
            return iterator.readAll();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read the mag mutual user csv file " +  fileToRead.getName());
        }
    }

    @Override
    public void saveAllUsers(List<UserDTO> users) {
       try {
           mapper.writer(schema.withHeader())
                   .writeValue(modifiedFile, users);
       } catch (IOException e) {
           throw new RuntimeException("Failed to save updates to modified mag mutual user file " +  modifiedFile.getName());
       }
    }
}
