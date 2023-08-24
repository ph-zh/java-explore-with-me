package ru.practicum.mainservice.constants;

import org.springframework.data.domain.Sort;

public class Constants {
    public static final Sort SORT_BY_ID_DESC = Sort.by(Sort.Direction.DESC, "id");
    public static final Sort SORT_BY_ID_ASC = Sort.by(Sort.Direction.ASC, "id");
}
