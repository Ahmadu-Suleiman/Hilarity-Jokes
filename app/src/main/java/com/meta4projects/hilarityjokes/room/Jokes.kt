package com.meta4projects.hilarityjokes.room

class Jokes {
    var currentPage = 0
    var previousPage = 0
    var nextPage = 0
    var totalPages = 0
    var jokes: ArrayList<Joke>

    constructor(currentPage: Int, previousPage: Int, nextPage: Int, totalPages: Int, jokes: ArrayList<Joke>) {
        this.currentPage = currentPage
        this.previousPage = previousPage
        this.nextPage = nextPage
        this.totalPages = totalPages
        this.jokes = jokes
    }

    constructor() {
        jokes = ArrayList()
    }
}