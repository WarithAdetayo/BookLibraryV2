package org.mylibrary.core.dataTransfer;

public record BookDetails(String isbn, String title, String author, String description, int copies, Boolean isInCatalogue) {
}
