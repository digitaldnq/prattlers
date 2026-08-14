package by.doni.core.web.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
public class PageResponse<T> {

    private List<T> data;

    private Integer totalPages = 0;
}
