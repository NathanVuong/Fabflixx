let searchForm= $("#search-submit");

function handleSearchResults(searchEvent) {

    searchEvent.preventDefault();

    console.log("did search");

    window.location.replace("search-result.html?" + "title=" + $("#title").val() + "&year=" +
        $("#year").val() + "&director=" + $("#director").val() + "&star=" + $("#star").val() +
        "&movie_number=25&order=tara&page=1" );
}

searchForm.submit(handleSearchResults);
