package vn.com.lifesup.hackathon.util;


import org.modelmapper.*;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.module.jsr310.Jsr310Module;
import org.springframework.util.CollectionUtils;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class MapUtil {
    private ModelMapper modelMapper;

    public MapUtil() {
        modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STANDARD)
                .setSkipNullEnabled(true);
        modelMapper.registerModule(new Jsr310Module());
        modelMapper.createTypeMap(String.class, Instant.class);
        modelMapper.addConverter(toStringInstant);
        modelMapper.getTypeMap(String.class, Instant.class).setProvider(InstantProvider);
        /*convert Instant to String */
        modelMapper.createTypeMap(Instant.class, String.class);
        modelMapper.addConverter(toInstantString);
        modelMapper.getTypeMap(Instant.class, String.class).setProvider(StringProvider);
    }

    public MapUtil(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public <T, E> T mapEntityToDto(E entity, Class<T> clazz) {
        return modelMapper.map(entity, clazz);
    }

    public <T, E> T mapDtoToEntity(E dto, Class<T> clazz) {
        return modelMapper.map(dto, clazz);
    }

    public <T, E> void mapData(T source, E destination) {
        modelMapper.map(source, destination);
    }

    public <T, E> List<T> mapList(List<E> inputData, Class<T> clazz) {
        return CollectionUtils.isEmpty(inputData) ?
                Collections.emptyList() :
                inputData.stream()
                        .map(i -> modelMapper.map(i, clazz))
                        .collect(Collectors.toList());
    }

    Provider<LocalDate> localDateProvider = new AbstractProvider<LocalDate>() {
        @Override
        public LocalDate get() {
            return LocalDate.now();
        }
    };

    Converter<String, LocalDate> toStringDate = new AbstractConverter<String, LocalDate>() {
        @Override
        protected LocalDate convert(String source) {
            DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate localDate = LocalDate.parse(source, format);
            return localDate;
        }
    };

    /*String to Instant*/
    Provider<Instant> InstantProvider = new AbstractProvider<Instant>() {
        @Override
        public Instant get() {
            return Instant.now();
        }
    };

    Converter<String, Instant> toStringInstant = new AbstractConverter<String, Instant>() {
        @Override
        protected Instant convert(String source) {
            if (source == null) return null;
            return CommonUtil.convertToInstant(source, source.length() == Constants.DATETIME_FORMAT.length());
        }
    };

    Provider<String> StringProvider = new AbstractProvider<String>() {
        @Override
        public String get() {
            return Instant.now().toString();
        }
    };

    Converter<Instant, String> toInstantString = new AbstractConverter<Instant, String>() {
        @Override
        protected String convert(Instant source) {
            if (source == null) return null;
            return source.toString();
        }
    };

}
