package ru.practicum.mainservice.pagination;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class OffsetBasedPageRequest implements Pageable {

    private int offset;

    private int limit;

    // Constructor could be expanded if sorting is needed
    private Sort sort;

    public OffsetBasedPageRequest(int offset, int limit, Sort sort) {
        if (limit < 1) {
            throw new IllegalArgumentException("Limit must not be less than one!");
        }
        if (offset < 0) {
            throw new IllegalArgumentException("Offset index must not be less than zero!");
        }
        this.limit = limit;
        this.offset = offset;
        this.sort = sort;
    }

    @Override
    public int getPageNumber() {
        return offset / limit;
    }

    @Override
    public int getPageSize() {
        return limit;
    }

    @Override
    public long getOffset() {
        return offset;
    }

    @Override
    public Sort getSort() {
        return sort;
    }

    @Override
    public Pageable next() {
        // Typecast possible because number of entries cannot be bigger than integer (primary key is integer)
        return new OffsetBasedPageRequest(getPageSize(), (int) (getOffset() + getPageSize()), sort);
    }

    public Pageable previous() {
        // The integers are positive. Subtracting does not let them become bigger than integer.
        return hasPrevious() ?
                new OffsetBasedPageRequest(getPageSize(), (int) (getOffset() - getPageSize()), sort) : this;
    }

    @Override
    public Pageable previousOrFirst() {
        return hasPrevious() ? previous() : first();
    }

    @Override
    public Pageable first() {
        return new OffsetBasedPageRequest(getPageSize(), 0, sort);
    }

    @Override
    public Pageable withPage(int pageNumber) {
        return new OffsetBasedPageRequest(getPageSize() * pageNumber, getPageSize(), sort);
    }

    @Override
    public boolean hasPrevious() {
        return offset > limit;
    }
}
