package org.mylibrary.core.dataTransfer;

import java.util.Date;

public record LendDetails(BookDetails book, Date date, int id) {
}
