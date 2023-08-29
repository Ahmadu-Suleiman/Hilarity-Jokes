package com.meta4projects.hilarityjokes.room;

import java.util.ArrayList;

public class Jokes {

    private int currentPage;
    private int previousPage;
    private int nextPage;
    private int totalPages;
    private ArrayList<Joke> jokes;

    public Jokes(int currentPage, int previousPage, int nextPage, int totalPages, ArrayList<Joke> jokes) {
        this.currentPage = currentPage;
        this.previousPage = previousPage;
        this.nextPage = nextPage;
        this.totalPages = totalPages;
        this.jokes = jokes;
    }

    public Jokes() {
        jokes = new ArrayList<>();
    }

    public ArrayList<Joke> getJokes() {
        return jokes;
    }

    public void setJokes(ArrayList<Joke> jokes) {
        this.jokes = jokes;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getPreviousPage() {
        return previousPage;
    }

    public void setPreviousPage(int previousPage) {
        this.previousPage = previousPage;
    }

    public int getNextPage() {
        return nextPage;
    }

    public void setNextPage(int nextPage) {
        this.nextPage = nextPage;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
}
